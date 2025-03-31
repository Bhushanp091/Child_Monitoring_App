package com.example.child_monitoring_app.ui.presentation.login

import android.content.Context
import android.net.Uri
import com.example.child_monitoring_app.ui.domain.model.ChildData
import com.example.child_monitoring_app.ui.data.FirebaseAuthManager
import com.example.child_monitoring_app.ui.presentation.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel : BaseViewModel() {
    private val authManager = FirebaseAuthManager()


    suspend fun login(email: String, password: String,context:Context): Result<String> {
        return withContext(Dispatchers.IO) {
            authManager.login(email, password,context)
        }
    }

    suspend fun signUp(email: String, password: String,name: String): Result<String> {
        return withContext(Dispatchers.IO) {
            authManager.signUpParent(email, password,name)
        }
    }

    suspend fun saveUser(parentId: String, name: String, age: Int, username: String, password: String,imageUri:Uri?): Result<String> {
        return withContext(Dispatchers.IO) {
            authManager.saveNewChildData(parentId, name, age, username, password,imageUri)
        }
    }

    suspend fun childLogin(username: String, password: String,context: Context): Result<ChildData> {
        return withContext(Dispatchers.IO) {
            authManager.childLogin(username,password,context)
        }
    }

    suspend fun getChildList():List<ChildData>{
        return withContext(Dispatchers.IO){
            authManager.getChildrenList()
        }
    }
}
