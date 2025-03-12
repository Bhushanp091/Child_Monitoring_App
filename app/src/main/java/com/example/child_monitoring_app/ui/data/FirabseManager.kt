package com.example.child_monitoring_app.ui.data

import com.example.child_monitoring_app.ui.presentation.appUsage.CallLogModel
import com.example.child_monitoring_app.ui.presentation.appUsage.CallType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success("Login successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success("Signup successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun saveNewChildData(name: String, age: Int, username: String, password: String): Result<String> {
        val userId = firestore.collection("child").document().id // Generate unique ID
        val user = ChildData(name, age.toString(), username, password)

        return try {
            firestore.collection("child").document(userId).set(user).await()
            Result.success("User data saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 Retrieve user data from Firestore (by username)
    suspend fun getChildData(username: String, password: String): Result<ChildData> {
        return try {
            val querySnapshot = firestore.collection("child")
                .whereEqualTo("username", username)
                .whereEqualTo("password",password)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val user = querySnapshot.documents[0].toObject(ChildData::class.java)
                if (user != null) Result.success(user) else Result.failure(Exception("User not found"))
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun saveCallLogs(userId: String, callLogs: List<CallLogModel>): Result<String> {
        return try {
            firestore.collection("users").document(userId)
                .update("callLogs", callLogs)
                .await()
            Result.success("Call logs saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getCallLogs(userId: String): Result<List<CallLogModel>> {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            val callLogs = snapshot["callLogs"] as? List<Map<String, Any>> ?: emptyList()

            val parsedLogs = callLogs.map { log ->
                CallLogModel(
                    name = log["name"] as String,
                    number = log["number"] as String,
                    type = when(log["type"] as String){
                        else -> CallType.UNKNOWN
                    } ,
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
