package com.example.child_monitoring_app.ui.presentation.component

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class ToastType {
    SUCCESS, ERROR, WARNING, INFO
}

fun Context.toast(
    message: String,
    type: ToastType = ToastType.INFO,
    duration: Int = android.widget.Toast.LENGTH_SHORT
) {
    CoroutineScope(Dispatchers.Main).launch {
        val toast = android.widget.Toast.makeText(this@toast, message, duration)
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

