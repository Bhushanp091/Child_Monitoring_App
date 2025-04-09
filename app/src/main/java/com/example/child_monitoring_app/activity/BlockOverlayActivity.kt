package com.example.child_monitoring_app.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class BlockOverlayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set transparent background
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)

        setContent {
            BlockScreen()
        }
    }



}

@Composable
fun BlockScreen(modifier: Modifier = Modifier) {
    Column (
        modifier = Modifier
    ){
        Text("You are Blocked \uD83D\uDE39")
    }
}