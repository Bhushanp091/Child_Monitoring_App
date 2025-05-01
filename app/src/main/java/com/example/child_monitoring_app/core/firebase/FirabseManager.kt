package com.example.child_monitoring_app.core.firebase

import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Paint
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.features.app_usage.getAppUsageStats
import com.example.child_monitoring_app.features.call_log_history.Contact
import com.example.child_monitoring_app.features.call_log_history.getCallLogs
import com.example.child_monitoring_app.features.call_log_history.getContacts
import com.example.child_monitoring_app.core.util.ChildData
import com.example.child_monitoring_app.features.app_blocker.AppLaunchModel
import com.example.child_monitoring_app.features.app_usage.AppUsageInfo
import com.example.child_monitoring_app.features.app_usage.CallLogModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FirebaseAuthManager() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()


    suspend fun parentLogin(email: String, password: String, context: Context): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val parentId =
                authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

            SharedPreference.saveParentIdLocally(context, parentId)
            SharedPreference.saveParentLoginState(context, true)
            SharedPreference.saveChildLoginState(context, false)

            Result.success("Login successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun parentSignUp(
        email: String,
        password: String,
        name: String,
        context: Context
    ): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val parentId =
                authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

            val parentData = hashMapOf(
                "name" to name,
                "email" to email,
                "childrenCount" to 0
            )

            firestore.collection("parents").document(parentId).set(parentData).await()
            SharedPreference.saveParentLoginState(context, true)
            SharedPreference.saveChildLoginState(context, false)
            Result.success("Parent signup successful")
        } catch (e: Exception) {
            println("Sign Up Failed $e")
            Result.failure(e)
        }
    }

    suspend fun addChild(
        parentId: String,
        name: String,
        age: Int,
        username: String,
        password: String,
    ): Result<String> {
        try {
            val childData = hashMapOf(
                "name" to name,
                "age" to age.toString(),
                "username" to username,
                "password" to password,
                "data" to hashMapOf(
                    "location" to null,
                    "appUsage" to emptyList<HashMap<String, Any>>(),
                    "contacts" to emptyList<HashMap<String, Any>>(),
                    "callLogs" to emptyList<HashMap<String, Any>>(),
                    "browserHistory" to emptyList<HashMap<String, Any>>(),
                    "appBlock" to emptyList(),
                    "webBlock" to emptyList()
                )
            )
            firestore.collection("parents").document(parentId)
                .collection("children").document(username)
                .set(childData).await()

            return Result.success("Success")
        } catch (e: Exception) {
            println("Add Child Error $e")
            return Result.failure(e)
        }
    }

    suspend fun getChildrenList(): List<ChildData> {
        val parentId = auth.currentUser?.uid
        if (parentId == null) {
            Log.e("Firestore", "Parent not logged in")
            return emptyList()
        }

        return try {
            val snapshot = firestore
                .collection("parents").document(parentId)
                .collection("children").get().await()

            if (snapshot.isEmpty) {
                Log.e("Firestore", "No children found for parent: $parentId")
            }

            snapshot.documents.mapNotNull { doc ->
                Log.d("Firestore", "Fetched Child: ${doc.data}")
                doc.toObject(ChildData::class.java)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching children: ${e.message}")
            emptyList()
        }
    }

    suspend fun childLogin(
        username: String,
        password: String,
        context: Context
    ): Result<ChildData> {
        val parents = firestore.collection("parents").get().await()
        for (parent in parents) {

            val snapshot = parent.reference.collection("children")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get().await()

            if (!snapshot.isEmpty) {
                val document = snapshot.documents[0]  // Get first matching document
                val child = snapshot.documents[0].toObject(ChildData::class.java)
                val childId = document.id
                val parentId = parent.id
                SharedPreference.saveParentIdLocally(context, parentId)
                SharedPreference.saveChildIdLocally(context, childId)
                SharedPreference.saveChildLoginState(context, true)
                return Result.success(child!!)
            }
        }
        return Result.failure(Exception("Child not found"))
    }

    fun uploadCallLogsToFirebase(context: Context, username: String) {
        val callLogs = getCallLogs(context)
        println("CallLogs $callLogs")
        println("Username $username")

        // Step 1: Find the correct parent that has this child
        firestore.collection("parents")
            .get()
            .addOnSuccessListener { parentSnapshot ->
                for (parentDoc in parentSnapshot.documents) {
                    val parentId = parentDoc.id  // Get the parent document ID

                    // Step 2: Find the child document inside this parent's "children" collection
                    firestore.collection("parents").document(parentId)
                        .collection("children")
                        .whereEqualTo("username", username) // Find child by username
                        .get()
                        .addOnSuccessListener { childSnapshot ->
                            if (!childSnapshot.isEmpty) {
                                val childDoc = childSnapshot.documents[0]
                                val childId = childDoc.id  // Get the child document ID

                                // Step 3: Prepare call logs data
                                val callLogsList = callLogs.map { log ->
                                    hashMapOf(
                                        "name" to log.name,
                                        "number" to log.number,
                                        "type" to log.type.toString(),
                                        "duration" to log.duration,
                                        "date" to log.date
                                    )
                                }

                                // Step 4: Update Firestore at the correct path
                                firestore.collection("parents").document(parentId)
                                    .collection("children").document(childId)
                                    .update("data.callLogs", callLogsList)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "Call logs updated successfully")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Firebase", "Error updating call logs: ${it.message}")
                                    }
                            } else {
                                Log.e("Firebase", "No child document found for username: $username")
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Firebase", "Error fetching child document: ${it.message}")
                        }
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching parent document: ${it.message}")
            }
    }

    fun fetchCallLogsFromFirebase(
        parentId: String,
        childId: String,
        onResult: (List<CallLogModel>) -> Unit
    ) {
        println("Call Log history $childId")

        firestore.collection("parents")
            .document(parentId)
            .collection("children")
            .document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val dataMap = document.get("data") as? Map<*, *>
                    val callLogsList = dataMap?.get("callLogs") as? List<Map<String, Any>>

                    val callLogs = callLogsList?.map { log ->
                        CallLogModel(
                            name = log["name"] as? String ?: "",
                            number = log["number"] as? String ?: "",
                            type = log["type"] as? String ?: "",
                            duration = log["duration"] as? String ?: "",
                            date = log["date"] as? String ?: ""
                        )
                    } ?: emptyList()

                    onResult(callLogs)
                } else {
                    Log.e("Firebase", "Child document not found")
                    onResult(emptyList())
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching call logs: ${it.message}")
            }
    }




    // Fixed Firebase Methods for Proper Time Intervals
    fun uploadAppUsageToFirebase(context: Context, username: String, intervalType: String) {
        val calendar = Calendar.getInstance()
        val endTime = System.currentTimeMillis()
        val startTime: Long
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val path = when (intervalType) {
            "daily" -> {
                // Start of the current day (midnight)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                startTime = calendar.timeInMillis
                "appUsageDaily"
            }
            "weekly" -> {
                // Start of the current week (Sunday or Monday depending on locale)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                startTime = calendar.timeInMillis
                "appUsageWeekly"
            }
            else -> { // monthly
                // Start of the current month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                startTime = calendar.timeInMillis
                "appUsageMonthly"
            }
        }

        // Log start and end times for debugging
        Log.d("TimeInterval", "$intervalType: ${dateFormat.format(Date(startTime))} to ${dateFormat.format(Date(endTime))}")

        val appUsageData = getAppUsageStats(context, startTime, endTime)
        val dateKey = dateFormat.format(Date(startTime))

        val usageMap = appUsageData.associateBy { it.packageName }.mapValues { (_, log) ->
            hashMapOf(
                "packageName" to log.packageName,
                "appName" to log.appName,
                "usageTime" to log.usageTime,
                "usageTimeMillis" to log.usageTimeMillis,
                "icon" to log.icon,
                "lastTimeUsed" to log.lastTimeUsed,
                "startDate" to startTime,  // Store the time range for reference
                "endDate" to endTime
            )
        }

        // Upload data to Firebase
        firestore.collection("parents").get().addOnSuccessListener { parentSnapshot ->
            parentSnapshot.forEach { parentDoc ->
                firestore.collection("parents").document(parentDoc.id)
                    .collection("children")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener { childSnapshot ->
                        if (!childSnapshot.isEmpty) {
                            val childId = childSnapshot.documents[0].id
                            val docRef = firestore.collection("parents").document(parentDoc.id)
                                .collection("children").document(childId)

                            docRef.update("data.$path.$dateKey", usageMap.values.toList())
                                .addOnSuccessListener {
                                    Log.d("Firebase", "$intervalType usage for $dateKey uploaded successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firebase", "Error uploading $intervalType usage: ${e.message}")
                                }
                        }
                    }
            }
        }
    }

    fun fetchAppUsageFromFirebase(
        parentId: String,
        childId: String,
        intervalType: String = "monthly", // options: daily, weekly, monthly
        onResult: (List<AppUsageInfo>) -> Unit
    ) {
        val usageField = when (intervalType) {
            "daily" -> "data.appUsageDaily"
            "weekly" -> "data.appUsageWeekly"
            else -> "data.appUsage" // monthly
        }

        Log.d("Firebase", "Fetching $intervalType app usage for child $childId")

        firestore.collection("parents").document(parentId)
            .collection("children").document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val appUsageList = document.get(usageField) as? List<HashMap<String, Any>>

                    val appUsageData = appUsageList?.map { log ->
                        val usageTime = log["usageTime"] as? String ?: "00:00:00"
                        val usageTimeMillis = log["usageTimeMillis"] as? String ?: "0"

                        AppUsageInfo(
                            packageName = log["packageName"] as? String ?: "",
                            appName = log["appName"] as? String ?: "",
                            usageTime = usageTime,
                            usageTimeMillis = usageTimeMillis,
                            icon = log["icon"] as? String ?: "",
                            lastTimeUsed = log["lastTimeUsed"] as? String ?: ""
                        )
                    } ?: emptyList()

                    // Sort by actual usage time
                    val sortedData = appUsageData.sortedByDescending {
                        it.usageTimeMillis.toLongOrNull() ?: 0
                    }

                    Log.d("Firebase", "Retrieved ${sortedData.size} app usage records for $intervalType")
                    onResult(sortedData)
                } else {
                    Log.e("Firebase", "Child document not found")
                    onResult(emptyList())
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching app usage ($intervalType): ${it.message}")
                onResult(emptyList())
            }
    }




    suspend fun uploadContactsToFirebase(context: Context, username: String) {
        val callLogs = getContacts(context)
        println("Contacts $callLogs")
        println("Username $username")

        // Step 1: Find the correct parent that has this child
        firestore.collection("parents")
            .get()
            .addOnSuccessListener { parentSnapshot ->
                for (parentDoc in parentSnapshot.documents) {
                    val parentId = parentDoc.id  // Get the parent document ID

                    // Step 2: Find the child document inside this parent's "children" collection
                    firestore.collection("parents").document(parentId)
                        .collection("children")
                        .whereEqualTo("username", username) // Find child by username
                        .get()
                        .addOnSuccessListener { childSnapshot ->
                            if (!childSnapshot.isEmpty) {
                                val childDoc = childSnapshot.documents[0]
                                val childId = childDoc.id  // Get the child document ID

                                // Step 3: Prepare call logs data
                                val callLogsList = callLogs.map { log ->
                                    hashMapOf(
                                        "name" to log.name,
                                        "phoneNumber" to log.phoneNumber,
                                        "id" to log.id,
                                    )
                                }

                                // Step 4: Update Firestore at the correct path
                                firestore.collection("parents").document(parentId)
                                    .collection("children").document(childId)
                                    .update("data.contacts", callLogsList)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "Contacts updated successfully")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Firebase", "Error updating call logs: ${it.message}")
                                    }
                            } else {
                                Log.e("Firebase", "No child document found for username: $username")
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Firebase", "Error fetching child document: ${it.message}")
                        }
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching parent document: ${it.message}")
            }
    }

    fun fetchContactsFromFirebase(
        parentId: String,
        childId: String,
        onResult: (List<Contact>) -> Unit
    ) {
        if (parentId.isBlank() || childId.isBlank()) {
            Log.e("Firebase", "Invalid parentId or childId")
            onResult(emptyList())
            return
        }

        firestore.collection("parents").document(parentId)
            .collection("children").document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val dataMap = document.get("data") as? Map<*, *>
                    val contactsList = dataMap?.get("contacts") as? List<Map<String, Any>>

                    val contacts = contactsList?.map { contact ->
                        Contact(
                            name = contact["name"] as? String ?: "",
                            id = contact["id"] as? String ?: "",
                            phoneNumber = contact["phoneNumber"] as? String ?: ""
                        )
                    } ?: emptyList()

                    Log.d("Firebase", "Fetched ${contacts.size} contacts")
                    onResult(contacts)
                } else {
                    Log.e("Firebase", "Child document not found")
                    onResult(emptyList())
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching contacts: ${it.message}")
                onResult(emptyList())
            }
    }

    fun uploadChildLocationToFirebase(childId: String, location: LatLng?) {
        firestore.collection("parents")
            .get()
            .addOnSuccessListener { parentSnapshot ->
                for (parentDoc in parentSnapshot.documents) {
                    val parentId = parentDoc.id
                    firestore.collection("parents").document(parentId)
                        .collection("children")
                        .whereEqualTo("username", childId)
                        .get()
                        .addOnSuccessListener { childSnapshot ->
                            if (!childSnapshot.isEmpty) {
                                val childDoc = childSnapshot.documents[0]
                                val child = childDoc.id
                                val locationData = hashMapOf(
                                    "latitude" to location?.latitude,
                                    "longitude" to location?.longitude,
                                    "timestamp" to System.currentTimeMillis()
                                )
                                firestore.collection("parents").document(parentId)
                                    .collection("children").document(childId)
                                    .update("data.location", locationData)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "Location Updated successfully")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Firebase", "Error Updating Location: ${it.message}")
                                    }
                            } else {
                                Log.e("Firebase", "No child document found for username: $childId")
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Firebase", "Error fetching child document: ${it.message}")
                        }
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching parent document: ${it.message}")
            }
    }

    fun fetchChildLocationFromFirebase(
        parentId: String,
        childUsername: String, // use username to find the child
        onResult: (LatLng?) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("parents")
            .document(parentId)
            .collection("children")
            .whereEqualTo("username", childUsername)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val childDoc = querySnapshot.documents[0]
                    val locationMap = childDoc.get("data.location") as? Map<*, *>
                    val lat = locationMap?.get("latitude") as? Double
                    val lng = locationMap?.get("longitude") as? Double

                    if (lat != null && lng != null) {
                        onResult(LatLng(lat, lng))
                    } else {
                        Log.e("Firebase", "Latitude or Longitude is null")
                        onResult(null)
                    }
                } else {
                    Log.e("Firebase", "No child found with username: $childUsername")
                    onResult(null)
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to fetch child: ${it.message}")
                onResult(null)
            }
    }

    fun uploadBlockedAppList(username: String, appList: List<AppUsageInfo>, onResult: () -> Unit) {

        firestore.collection("parents")
            .get()
            .addOnSuccessListener { parentSnapshot ->
                for (parentDoc in parentSnapshot.documents) {
                    val parentId = parentDoc.id
                    firestore.collection("parents").document(parentId)
                        .collection("children")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener { childSnapshot ->
                            if (!childSnapshot.isEmpty) {
                                val childDoc = childSnapshot.documents[0]
                                val childId = childDoc.id

                                val blockedApp = appList.map { log ->
                                    hashMapOf(
                                        "packageName" to log.packageName,
                                        "appName" to log.appName,
                                    )
                                }

                                // Step 4: Update Firestore at the correct path
                                firestore.collection("parents").document(parentId)
                                    .collection("children").document(childId)
                                    .update("data.appBlock", blockedApp)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "AppBlock updated successfully")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Firebase", "Error updating AppUsage: ${it.message}")
                                    }
                            } else {
                                Log.e("Firebase", "No child document found for username: $username")
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Firebase", "Error fetching child document: ${it.message}")
                        }
                }
                onResult()
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching parent document: ${it.message}")
                onResult()
            }
    }

    fun fetchBlockedAppFromFirebase(
        parentId: String,
        childId: String,
        onResult: (List<AppUsageInfo>) -> Unit
    ) {
        firestore.collection("parents").document(parentId)
            .collection("children").document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val appUsageList = document.get("data.appBlock") as? List<HashMap<String, Any>>
                    val appUsageData = appUsageList?.map { log ->
                        AppUsageInfo(
                            packageName = log["packageName"] as? String ?: "",
                            appName = log["appName"] as? String ?: "",
                        )
                    } ?: emptyList()
                    onResult(appUsageData)
                } else {
                    Log.e("Firebase", "Child document not found")
                    onResult(emptyList())
                }
            }
            .addOnFailureListener { Log.e("Firebase", "Error fetching appUsage: ${it.message}") }
    }

    fun uploadBlockedWebList(username: String, appList: List<String>, onResult: () -> Unit) {

        firestore.collection("parents")
            .get()
            .addOnSuccessListener { parentSnapshot ->
                for (parentDoc in parentSnapshot.documents) {
                    val parentId = parentDoc.id
                    firestore.collection("parents").document(parentId)
                        .collection("children")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener { childSnapshot ->
                            if (!childSnapshot.isEmpty) {
                                val childDoc = childSnapshot.documents[0]
                                val childId = childDoc.id

                                val blockedWeb = appList.map { log ->
                                    hashMapOf(
                                        "webName" to log,
                                    )
                                }

                                firestore.collection("parents").document(parentId)
                                    .collection("children").document(childId)
                                    .update("data.webBlock", blockedWeb)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "AppBlock updated successfully")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Firebase", "Error updating AppUsage: ${it.message}")
                                    }
                            } else {
                                Log.e("Firebase", "No child document found for username: $username")
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Firebase", "Error fetching child document: ${it.message}")
                        }
                }
                onResult()
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching parent document: ${it.message}")
                onResult()
            }
    }

    fun fetchBlockedWebFromFirebase(
        parentId: String,
        childId: String,
        onResult: (List<String>) -> Unit
    ) {
        firestore.collection("parents").document(parentId)
            .collection("children").document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val appUsageList = document.get("data.webBlock") as? List<HashMap<String, Any>>
                    val blockedWeb = appUsageList?.map { log ->
                        log["webName"] as? String ?: ""
                    } ?: emptyList<String>()
                    onResult(blockedWeb)
                } else {
                    Log.e("Firebase", "Child document not found")
                    onResult(emptyList())
                }
            }
            .addOnFailureListener { Log.e("Firebase", "Error fetching appUsage: ${it.message}") }
    }

    fun uploadBatteryNetworkData(username: String, isConnected: Boolean, batteryLevel: Int) {
        firestore.collection("parents")
            .get()
            .addOnSuccessListener { parentSnapshot ->
                for (parentDoc in parentSnapshot.documents) {
                    val parentId = parentDoc.id
                    firestore.collection("parents").document(parentId)
                        .collection("children")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener { childSnapshot ->
                            if (!childSnapshot.isEmpty) {
                                val childDoc = childSnapshot.documents[0]
                                val childId = childDoc.id

                                val data = hashMapOf(
                                    "battery" to batteryLevel,
                                    "isConnected" to isConnected
                                )

                                firestore.collection("parents").document(parentId)
                                    .collection("children").document(childId)
                                    .update("data.batteryNetwork", data)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "AppBlock updated successfully")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Firebase", "Error updating AppUsage: ${it.message}")
                                    }
                            } else {
                                Log.e("Firebase", "No child document found for username: $username")
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Firebase", "Error fetching child document: ${it.message}")
                        }
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching parent document: ${it.message}")
            }
    }

    fun fetchBatteryAndNetworkData(
        parentId: String,
        childId: String,
        onResult: (battery: Int, isConnected: Boolean) -> Unit
    ) {
        firestore.collection("parents").document(parentId)
            .collection("children").document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val dataMap = document.get("data.batteryNetwork") as? Map<*, *>
                    val isConnected = dataMap?.get("isConnected") as? Boolean ?: false
                    val battery = (dataMap?.get("battery") as? Long)?.toInt() ?: 0
                    println("Battery Percentage1 $battery $isConnected")
                    onResult(battery, isConnected)
                } else {
                    Log.e("Firebase", "Child document not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching battery/network data: ${exception.message}")
            }
    }

    fun uploadAppLaunchCountFirebase(
        context: Context,
        username: String
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val appLaunchData = SharedPreference.getAppLaunchData(context)
        firestore.collection("parents").get()
            .addOnSuccessListener { parentSnapshot ->
                for (parentDoc in parentSnapshot.documents) {
                    val parentId = parentDoc.id

                    firestore.collection("parents").document(parentId)
                        .collection("children")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener { childSnapshot ->
                            if (!childSnapshot.isEmpty) {
                                val childId = childSnapshot.documents[0].id
                                val appLaunchMap = hashMapOf<String, Any>()

                                for (log in appLaunchData) {
                                    appLaunchMap[log.packageName] = mapOf(
                                        "packageName" to log.packageName,
                                        "count" to log.count
                                    )
                                }

                                firestore.collection("parents").document(parentId)
                                    .collection("children").document(childId)
                                    .update("data.appLaunches", appLaunchMap)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "App Launch Count uploaded.")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Firebase", "Upload failed: ${it.message}")
                                    }

                            } else {
                                Log.e("Firebase", "Child not found.")
                            }
                        }
                }
            }
    }

    fun fetchAppLaunchData(
        parentId: String,
        childId: String,
        onResult: (List<AppLaunchModel>) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("parents").document(parentId)
            .collection("children").document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.get("data.appLaunches") as? Map<*, *>
                    val resultList = mutableListOf<AppLaunchModel>()

                    data?.forEach { (_, value) ->
                        if (value is Map<*, *>) {
                            val packageName = value["packageName"] as? String ?: ""
                            val count = (value["count"] as? Long)?.toInt() ?: 0

                            if (packageName.isNotEmpty()) {
                                resultList.add(AppLaunchModel(packageName, count))
                            }
                        }
                    }

                    onResult(resultList)
                } else {
                    Log.e("Firebase", "Document not found")
                    onResult(emptyList())
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching data: ${it.message}")
                onResult(emptyList())
            }
    }

}
