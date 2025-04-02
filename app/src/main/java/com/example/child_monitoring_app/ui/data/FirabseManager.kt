package com.example.child_monitoring_app.ui.data

import android.app.usage.UsageStatsManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.child_monitoring_app.ui.data.appUsage.getAppUsageStats
import com.example.child_monitoring_app.ui.data.callHistory.Contact
import com.example.child_monitoring_app.ui.data.callHistory.getCallLogs
import com.example.child_monitoring_app.ui.data.callHistory.getContacts
import com.example.child_monitoring_app.ui.domain.model.ChildData
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageInfo
import com.example.child_monitoring_app.ui.presentation.appUsage.CallLogModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class FirebaseAuthManager() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference


    suspend fun login(email: String, password: String, context: Context): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val parentId =
                authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

            SharedPreference.saveParentIdLocally(context, parentId)
            Result.success("Login successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpParent(email: String, password: String, name: String): Result<String> {
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
            Result.success("Parent signup successful")
        } catch (e: Exception) {
            println("Sign Up Failed $e")
            Result.failure(e)
        }
    }


    suspend fun saveNewChildData(
        parentId: String,
        name: String,
        age: Int,
        username: String,
        password: String,
        imageUri: Uri? // Profile Image Uri
    ): Result<String> {
        try {
            val imageUrl = imageUri?.let { uploadProfilePhoto(username, it) } ?: ""

            val childData = hashMapOf(
                "name" to name,
                "age" to age.toString(),
                "username" to username,
                "password" to password,
                "profilePhoto" to imageUrl, // Store Profile Photo URL
                "data" to hashMapOf(
                    "location" to null,
                    "appUsage" to emptyList<HashMap<String, Any>>(),
                    "contacts" to emptyList<HashMap<String, Any>>(),
                    "callLogs" to emptyList<HashMap<String, Any>>(),
                    "browserHistory" to emptyList<HashMap<String, Any>>(),
                    "youtubeHistory" to emptyList<HashMap<String, Any>>()
                )
            )

            firestore.collection("parents").document(parentId)
                .collection("children").document(username)
                .set(childData).await()

            return Result.success("Child data saved successfully")
        } catch (e: Exception) {
            println("Add Child Error $e")
            return Result.failure(e)
        }
    }

    private suspend fun uploadProfilePhoto(username: String, imageUri: Uri): String {
        return try {
            val imageRef = storage.child("profile_photos/$username.jpg")
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            println("Image Upload Error: ${e.message}")
            ""
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
                SharedPreference.saveChildIdLocally(context, childId)//saving child id here
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
        firestore.collection("parents").document(parentId)
            .collection("children").document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val callLogsList = document.get("data.callLogs") as? List<HashMap<String, Any>>
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
            .addOnFailureListener { Log.e("Firebase", "Error fetching call logs: ${it.message}") }
    }

    fun uploadAppUsageToFirebase(context: Context, username: String) {

        var selectedInterval = mutableStateOf(UsageStatsManager.INTERVAL_MONTHLY)
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(selectedInterval.value, -1) // -1 Day, -1 Week, or -1 Month
        val startTime = calendar.timeInMillis

        val appUsageData = getAppUsageStats(context, startTime, endTime)

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
                                val callLogsList = appUsageData.map { log ->
                                    hashMapOf(
                                        "packageName" to log.packageName,
                                        "appName" to log.appName,
                                        "usageTime" to log.usageTime,
                                        "icon" to log.icon,
                                        "lastTimeUsed" to log.lastTimeUsed
                                    )
                                }

                                // Step 4: Update Firestore at the correct path
                                firestore.collection("parents").document(parentId)
                                    .collection("children").document(childId)
                                    .update("data.appUsage", callLogsList)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "AppUsage updated successfully")
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

    fun fetchAppUsageFromFirebase(
        parentId: String,
        childId: String,
        onResult: (List<AppUsageInfo>) -> Unit
    ) {
        firestore.collection("parents").document(parentId)
            .collection("children").document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val appUsageList = document.get("data.appUsage") as? List<HashMap<String, Any>>
                    val appUsageData = appUsageList?.map { log ->
                        AppUsageInfo(
                            packageName = log["packageName"] as? String ?: "",
                            usageTime = log["usageTime"] as? String ?: "",
                            icon = log["icon"] as? String ?: "",
                            lastTimeUsed = log["lastTimeUsed"] as? String ?: "",
                            appName = log["appName"] as? String ?: ""
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
        firestore.collection("parents").document(parentId)
            .collection("children").document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val callLogsList = document.get("data.contacts") as? List<HashMap<String, Any>>
                    val callLogs = callLogsList?.map { log ->
                        Contact(
                            name = log["name"] as? String ?: "",
                            id =  log["id"] as? String ?: "",
                            phoneNumber = log["phoneNumber"] as? String ?: ""
                        )
                    } ?: emptyList()

                    onResult(callLogs)
                } else {
                    Log.e("Firebase", "Child document not found")
                    onResult(emptyList())
                }
            }
            .addOnFailureListener { Log.e("Firebase", "Error fetching call contacts: ${it.message}") }
    }




}
