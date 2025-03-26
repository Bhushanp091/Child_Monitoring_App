package com.example.child_monitoring_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.child_monitoring_app.ui.presentation.addChild.AddChildScreen
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageScreen
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageViewModel
import com.example.child_monitoring_app.ui.presentation.appUsage.CallLogHistoryScreen
import com.example.child_monitoring_app.ui.presentation.appUsage.PhoneNumberList
import com.example.child_monitoring_app.ui.presentation.appUsage.ShowChildCallHistory
import com.example.child_monitoring_app.ui.presentation.browser.BrowserHistoryScreen
import com.example.child_monitoring_app.ui.presentation.dashBoard.ChildDashBoardScreen
import com.example.child_monitoring_app.ui.presentation.dashBoard.DashBoardMainScreen
import com.example.child_monitoring_app.ui.presentation.dashBoard.GetUserScreen
import com.example.child_monitoring_app.ui.presentation.location.ShowLocationScreen
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import com.example.child_monitoring_app.ui.presentation.login.ParentChildSelectionScreen
import com.example.child_monitoring_app.ui.presentation.login.child_login.ChildLoginScreen
import com.example.child_monitoring_app.ui.presentation.login.ui.ParentLoginScreen
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
//        startDestination = Screen.DashBoard.route
    ) {

        composable(Screen.PreLogin.route) {
            ParentChildSelectionScreen(navController)
        }

        composable(Screen.SignUp.route) {
            ParentSignupScreen(authViewModel,navController)
        }

        composable(Screen.ChildLogin.route) {
            ChildLoginScreen(authViewModel,navController)
        }

        composable(Screen.ParentLogin.route) {
            ParentLoginScreen(authViewModel,navController)
        }

        composable(Screen.DashBoard.route) {
            DashBoardMainScreen(appUsageViewModel,authViewModel,navController)
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
            ShowChildCallHistory(appUsageViewModel)
//            CallLogHistoryScreen(appUsageViewModel){}
        }



        composable(Screen.LocationScreen.route) {
            ShowLocationScreen()
        }

        composable(Screen.ChildDashBoard.route) {
            ChildDashBoardScreen(authViewModel)
        }

        composable(Screen.PhoneNumberList.route) {
            PhoneNumberList()
        }

        composable(Screen.BrowserHistory.route) {
            BrowserHistoryScreen()
        }



    }


}


sealed class Screen(val route: String) {
    object DashBoard : Screen("dash_board")
    object ChildDashBoard : Screen("child_dash_board")
    object CallHistory : Screen("call_history")
    object AppUsage : Screen("app_usage")
    object ParentLogin : Screen("login")
    object SignUp : Screen("sign_up")
    object PreLogin : Screen("pre_login")
    object ChildLogin : Screen("child_login")
    object AddChild : Screen("add_Child")
    object LocationScreen:Screen("show_location")
    object PhoneNumberList:Screen("phone_number_list")
    object BrowserHistory:Screen("browser_history_list")
}