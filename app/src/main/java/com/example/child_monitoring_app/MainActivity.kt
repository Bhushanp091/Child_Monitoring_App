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
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageScreen
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageViewModel
import com.example.child_monitoring_app.ui.presentation.appUsage.CallLogHistoryScreen
import com.example.child_monitoring_app.ui.presentation.dashBoard.DashBoardMainScreen
import com.example.child_monitoring_app.ui.presentation.dashBoard.GetUserScreen
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import com.example.child_monitoring_app.ui.presentation.login.ChooseLoginScreen
import com.example.child_monitoring_app.ui.presentation.login.child_login.ChildLoginScreen
import com.example.child_monitoring_app.ui.presentation.login.ui.LoginScreen
import com.example.child_monitoring_app.ui.presentation.login.ui.ParentSignupScreen

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
fun Navigation() {


    val navController = rememberNavController()
    val appUsageViewModel: AppUsageViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.PreLogin.route
    ) {

        composable(Screen.PreLogin.route) {
            ChooseLoginScreen(navController)
        }

        composable(Screen.SignUp.route) {
            ParentSignupScreen(authViewModel,navController)
        }

        composable(Screen.ChildLogin.route) {
            ChildLoginScreen(authViewModel,navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Screen.DashBoard.route) {
            DashBoardMainScreen(authViewModel,navController)
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
            CallLogHistoryScreen(appUsageViewModel){}
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
    object SignUp : Screen("sign_up")
    object PreLogin : Screen("pre_login")
    object ChildLogin : Screen("child_login")
    object AddChild : Screen("add_Child")
    object ShowChildData : Screen("show_child_data")
}