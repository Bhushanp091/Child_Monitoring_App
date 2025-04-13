package com.example.child_monitoring_app.features.auth

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.child_monitoring_app.core.util.ChildData
import com.example.child_monitoring_app.core.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel : BaseViewModel() {


    /*** Login Screen States ***/
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    val name =  mutableStateOf("")
    var passwordVisible = mutableStateOf(false)
    val isSignUp = mutableStateOf(false)
    var message = mutableStateOf("")
    var isLogIn = mutableStateOf(false)
    var authStatus = mutableStateOf("Not authenticated")
    var username = mutableStateOf("")
    var isAuthenticating  = mutableStateOf(false)
    var authFailed = mutableStateOf(false)
    var confirmPassword = mutableStateOf("")
    var confirmPasswordVisible= mutableStateOf(false)




    suspend fun parentLogin(email: String, password: String, context: Context): Result<String> {
        return withContext(Dispatchers.IO) {
            firebaseManager.parentLogin(email, password, context)
        }
    }

    suspend fun parentSignUp(
        email: String,
        password: String,
        name: String,
        context: Context
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            firebaseManager.parentSignUp(email, password, name, context)
        }
    }

    fun logOut() {
        firebaseManager.logOut()
    }

    private val _status = MutableStateFlow<Result<String>?>(null)
    val status: StateFlow<Result<String>?> = _status

    fun addChild(
        parentId: String,
        name: String,
        age: Int,
        username: String,
        password: String,
    ) {
        viewModelScope.launch {
            val result = firebaseManager.addChild(parentId, name, age, username, password)
            _status.value = result
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


    fun clearData(){
        username.value = ""
        email.value = ""
        password.value = ""
        confirmPassword.value = ""
        name.value = ""
        passwordVisible.value = false
        isSignUp.value = false
        confirmPasswordVisible.value = false
    }
}
