package com.example.child_monitoring_app.ui.presentation.login

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.child_monitoring_app.ui.domain.model.ChildData
import com.example.child_monitoring_app.ui.data.FirebaseAuthManager
import com.example.child_monitoring_app.ui.presentation.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel : BaseViewModel() {

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
}
