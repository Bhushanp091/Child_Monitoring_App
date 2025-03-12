package com.example.child_monitoring_app.ui.presentation.login

import androidx.lifecycle.ViewModel
import com.example.child_monitoring_app.ui.data.ChildData
import com.example.child_monitoring_app.ui.data.FirebaseAuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {
    private val authManager = FirebaseAuthManager()

    suspend fun login(email: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            authManager.login(email, password)
        }
    }

    suspend fun signUp(email: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            authManager.signUp(email, password)
        }
    }

    suspend fun saveUser(name: String, age: Int, username: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            authManager.saveNewChildData(name, age, username, password)
        }
    }

    suspend fun getUser(username: String,password: String): Result<ChildData> {
        return withContext(Dispatchers.IO) {
            authManager.getChildData(username,password)
        }
    }
}
