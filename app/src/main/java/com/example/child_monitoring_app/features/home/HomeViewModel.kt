package com.example.child_monitoring_app.features.home

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.example.child_monitoring_app.core.ui.BaseViewModel
import com.example.child_monitoring_app.core.util.ChildData

class HomeViewModel() : BaseViewModel() {

    var name = mutableStateOf("")
    var age = mutableStateOf("")
    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var message = mutableStateOf("")
    var passwordVisible = mutableStateOf(false)
    var selectedImageUri  =  mutableStateOf<Uri?>(null)
    var childList = mutableStateOf<List<ChildData>>(emptyList())
    var loadingChilList = mutableStateOf(true)
    var errorMessage = mutableStateOf("")
    val isLoading = mutableStateOf(false)


    fun clearData(){
        name.value = ""
        age.value = ""
        username.value = ""
        password.value = ""
        message.value = ""
        isLoading.value = false
        passwordVisible.value = false
    }

}