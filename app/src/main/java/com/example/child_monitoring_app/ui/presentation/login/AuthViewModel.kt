package com.example.child_monitoring_app.ui.presentation.login

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.child_monitoring_app.ui.domain.model.ChildData
import com.example.child_monitoring_app.ui.data.FirebaseAuthManager
import com.example.child_monitoring_app.ui.presentation.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel : BaseViewModel() {

    // 🔐 Temporary store for OTPs
    private val emailOtpMap = mutableMapOf<String, String>()

    // 🔥 Firebase manager instance
    //private val firebaseManager = FirebaseAuthManager()

    suspend fun login(email: String, password: String, context: Context): Result<String> {
        return withContext(Dispatchers.IO) {
            firebaseManager.login(email, password, context)
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        context: Context
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            firebaseManager.signUpParent(email, password, name, context)
        }
    }

    fun logOut() {
        firebaseManager.logOut()
    }

    suspend fun saveUser(
        parentId: String,
        name: String,
        age: Int,
        username: String,
        password: String,
        imageUri: Uri?,
        context: Context
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            firebaseManager.saveNewChildData(context, parentId, name, age, username, password, imageUri)
        }
    }

    suspend fun childLogin(
        username: String,
        password: String,
        context: Context
    ): Result<ChildData> {
        return withContext(Dispatchers.IO) {
            firebaseManager.childLogin(username, password, context)
        }
    }

    suspend fun getChildList(): List<ChildData> {
        return withContext(Dispatchers.IO) {
            firebaseManager.getChildrenList()
        }
    }

    suspend fun sendEmailOtp(email: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val otp = generateOtp()
                emailOtpMap[email] = otp
                firebaseManager.sendOtpToEmail(email, otp, context)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun verifyEmailOtp(email: String, enteredOtp: String): Boolean {
        return withContext(Dispatchers.IO) {
            val correctOtp = emailOtpMap[email]
            correctOtp != null && correctOtp == enteredOtp
        }
    }

    private fun generateOtp(): String {
        return (100000..999999).random().toString()
    }
}
