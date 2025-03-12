package com.example.child_monitoring_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.child_monitoring_app.ui.presentation.addChild.AddChildScreen
import com.example.child_monitoring_app.ui.presentation.addChild.GetUserScreen
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageScreen
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageViewModel
import com.example.child_monitoring_app.ui.presentation.appUsage.CallHistoryScreen
import com.example.child_monitoring_app.ui.presentation.dashBoard.DashBoardMainScreen
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import com.example.child_monitoring_app.ui.presentation.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation()
        }
    }
}


@Composable
fun Navigation(modifier: Modifier = Modifier) {


    val navController = rememberNavController()
    val appUsageViewModel: AppUsageViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.DashBoard.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Screen.DashBoard.route) {
            DashBoardMainScreen(navController)
        }

        composable(Screen.AddChild.route) {
            AddChildScreen(authViewModel, navController)
        }

        composable(Screen.AppUsage.route) {
            val hasPermission = appUsageViewModel.hasPermission
            if (hasPermission) {
                AppUsageScreen(appUsageViewModel)
            } else {
                appUsageViewModel.requestPermission()
            }
        }

        composable(Screen.CallHistory.route) {
            CallHistoryScreen(appUsageViewModel)
        }

        composable(Screen.ShowChildData.route) {
            GetUserScreen(authViewModel)
        }
    }


}


sealed class Screen(val route: String) {
    object DashBoard : Screen("dash_board")
    object CallHistory : Screen("call_history")
    object AppUsage : Screen("app_usage")
    object Login : Screen("login")
    object AddChild : Screen("add_Child")
    object ShowChildData : Screen("show_child_data")
}