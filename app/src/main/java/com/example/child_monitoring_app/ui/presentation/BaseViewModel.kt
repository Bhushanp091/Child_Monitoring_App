package com.example.child_monitoring_app.ui.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.child_monitoring_app.ui.data.ChildData
import com.example.child_monitoring_app.ui.data.FirebaseAuthManager
import kotlinx.coroutines.launch


open class BaseViewModel : ViewModel() {
    private val firebaseManager = FirebaseAuthManager()

    fun storeChildData(
        context: Context,
        childId:String
    ) {
        viewModelScope.launch {
            firebaseManager.uploadCallLogsToFirebase(context,childId)
            firebaseManager.uploadAppUsageToFirebase(context,childId)
        }
    }

}