package com.example.child_monitoring_app.ui.presentation.component

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Enum for Toast Types
enum class ToastType {
    SUCCESS, ERROR, WARNING, INFO
}

// Toast Extension Function
fun Context.toast(
    message: String,
    type: ToastType = ToastType.INFO,
    duration: Int = android.widget.Toast.LENGTH_SHORT
) {
    CoroutineScope(Dispatchers.Main).launch {
        val toast = android.widget.Toast.makeText(this@toast, message, duration)

        // Customize toast appearance based on type
        toast.view?.let { view ->
            when (type) {
                ToastType.SUCCESS -> view.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                ToastType.ERROR -> view.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
                ToastType.WARNING -> view.setBackgroundColor(android.graphics.Color.parseColor("#FFC107"))
                ToastType.INFO -> view.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"))
            }
        }

        toast.show()
    }
}

// Composable Wrapper for Jetpack Compose
@Composable
fun ToastHost(
    message: String? = null,
    type: ToastType = ToastType.INFO
) {
    val context = LocalContext.current

    LaunchedEffect(message) {
        message?.let {
            context.toast(it, type)
        }
    }
}

// Example Usage in a Composable
