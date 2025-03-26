package com.example.child_monitoring_app.ui.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.child_monitoring_app.ui.data.callHistory.getCallLogs
import com.example.child_monitoring_app.ui.presentation.appUsage.CallLogModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String, context: Context): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val parentId =
                authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

            // Store Parent ID in SharedPreferences
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
        password: String
    ): Result<String> {
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
                "youtubeHistory" to emptyList<HashMap<String, Any>>()
            )
        )

        return try {
            firestore.collection("parents").document(parentId)
                .collection("children").document(username)
                .set(childData).await()
            Result.success("Child data saved successfully")
        } catch (e: Exception) {
            println("Add Child Error $e")
            Result.failure(e)
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

    suspend fun getChildId(): String {
        val parentId = auth.currentUser?.uid
        val childId = mutableStateOf("")
        if (parentId == null) {
            Log.e("Firestore", "Parent not logged in")
            return ""
        }

        return try {
            val snapshot = firestore
                .collection("parents").document(parentId)
                .collection("children").get().await()

            if (snapshot.isEmpty) {
                childId.value = snapshot.documents[0].id
                Log.e("Firestore", "No children found for parent: $parentId")
            }

            childId.value

        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching children: ${e.message}")
            ""
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

    suspend fun saveCallLogs(
        parentId: String,
        childUsername: String,
        callLogs: List<CallLogModel>
    ): Result<String> {
        return try {
            firestore.collection("parents").document(parentId)
                .collection("children").document(childUsername)
                .update("data.callLogs", callLogs)
                .await()
            Result.success("Call logs saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
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



    fun fetchCallLogsFromFirebase(parentId: String, childId: String, onResult: (List<CallLogModel>) -> Unit) {
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



}



