package com.example.child_monitoring_app.ui.data

import android.content.Context
import android.util.Log
import com.example.child_monitoring_app.ui.presentation.appUsage.CallLogModel
import com.example.child_monitoring_app.ui.presentation.appUsage.CallType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager (){
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String,context: Context): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val parentId = authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

            // Store Parent ID in SharedPreferences
            SharedPreference.saveParentIdLocally(context,parentId)
            Result.success("Login successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpParent(email: String, password: String, name: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val parentId = authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

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



    suspend fun saveNewChildData(parentId: String, name: String, age: Int, username: String, password: String): Result<String> {
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
        val parentId = FirebaseAuth.getInstance().currentUser?.uid
        if (parentId == null) {
            Log.e("Firestore", "Parent not logged in")
            return emptyList()
        }

        return try {
            val snapshot = FirebaseFirestore.getInstance()
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


    // 🔹 Retrieve user data from Firestore (by username)
//    suspend fun getChildData(parentId: String, username: String, password: String): Result<ChildData> {
//        return try {
//            val docRef = firestore.collection("parents").document(parentId)
//                .collection("children").whereEqualTo("username", username)
//                .whereEqualTo("password", password)
//                .get().await()
//
//            if (!docRef.isEmpty) {
//                val child = docRef.documents[0].toObject(ChildData::class.java)
//                if (child != null) Result.success(child) else Result.failure(Exception("Child not found"))
//            } else {
//                Result.failure(Exception("Child not found"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    suspend fun getChildData(username: String, password: String): Result<ChildData> {
        val parents = firestore.collection("parents").get().await()

        for (parent in parents) {
            val snapshot = parent.reference.collection("children")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get().await()

            if (!snapshot.isEmpty) {
                val child = snapshot.documents[0].toObject(ChildData::class.java)
                return Result.success(child!!)
            }
        }
        return Result.failure(Exception("Child not found"))
    }

    suspend fun saveCallLogs(parentId: String, childUsername: String, callLogs: List<CallLogModel>): Result<String> {
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

    // 🔹 Retrieve Call Logs for Child
    suspend fun getCallLogs(parentId: String, childUsername: String): Result<List<CallLogModel>> {
        return try {
            val snapshot = firestore.collection("parents").document(parentId)
                .collection("children").document(childUsername).get().await()

            val callLogs = snapshot["data.callLogs"] as? List<Map<String, Any>> ?: emptyList()

            val parsedLogs = callLogs.map { log ->
                CallLogModel(
                    name = log["name"] as String,
                    number = log["number"] as String,
                    type = when(log["type"] as String) {
                        "INCOMING" -> CallType.RECEIVED
                        "OUTGOING" -> CallType.MADE
                        "MISSED" -> CallType.MISSED
                        else -> CallType.UNKNOWN
                    },
                    date = log["date"] as String,
                    duration = log["duration"] as String
                )
            }

            Result.success(parsedLogs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}




class FirebaseAuthService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Sign Up Parent
    fun signUpParent(email: String, password: String, name: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    if (userId != null) {
                        val parentData = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "childrenCount" to 0
                        )
                        db.collection("parents").document(userId).set(parentData)
                            .addOnSuccessListener { onResult(true, null) }
                            .addOnFailureListener { onResult(false, it.message) }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Login Parent
    fun loginParent(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Add Child to Firestore
    fun addChild(parentId: String, name: String, age: Int, gender: String, username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val childData = hashMapOf(
            "name" to name,
            "age" to age,
            "gender" to gender,
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

        db.collection("parents").document(parentId).collection("children").document(username)
            .set(childData)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }
}
