package com.example.child_monitoring_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.child_monitoring_app.ui.presentation.addChild.AddChildScreen
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageScreen
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageViewModel
import com.example.child_monitoring_app.ui.presentation.appUsage.PhoneNumberList
import com.example.child_monitoring_app.ui.presentation.appUsage.ShowChildCallHistory
import com.example.child_monitoring_app.ui.presentation.browser.BrowserHistoryScreen
import com.example.child_monitoring_app.ui.presentation.component.MainScaffold
import com.example.child_monitoring_app.ui.presentation.dashBoard.ChildDashBoardScreen
import com.example.child_monitoring_app.ui.presentation.dashBoard.ChildListScreen
import com.example.child_monitoring_app.ui.presentation.dashBoard.DashBoardMainScreen
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
            MainScaffold(navController, "Sign Up") {
                ParentSignupScreen(authViewModel, navController)
            }
        }

        composable(Screen.ChildLogin.route) {
            MainScaffold(navController, "Child Login") {
                ChildLoginScreen(authViewModel, navController)
            }
        }

        composable(Screen.ParentLogin.route) {
            MainScaffold(navController, "Login") {
                ParentLoginScreen(authViewModel, navController)
            }
        }

        composable(Screen.DashBoard.route) {
            MainScaffold(
                navController,
                "DashBoard",
                showFloatingButton = true,
                showBackButton = false
            ) {
                DashBoardMainScreen(
                    Modifier.padding(it),
                    appUsageViewModel,
                    authViewModel,
                    navController
                )
            }
        }

        composable(Screen.AddChild.route) {
            MainScaffold(navController, "Add Child") {
                AddChildScreen(authViewModel)
            }
        }

        composable(Screen.AppUsage.route) {
            MainScaffold(navController, "App Usage") {
                val hasPermission = appUsageViewModel.hasPermission
                if (hasPermission) {
                    AppUsageScreen(appUsageViewModel, authViewModel, Modifier.padding(it))
                } else {
                    appUsageViewModel.requestPermission()
                }
            }
        }

        composable(Screen.CallHistory.route) {
            MainScaffold(navController, "Call Log History") {
                ShowChildCallHistory(appUsageViewModel, authViewModel, Modifier.padding(it))
            }
        }

        composable(Screen.ShowChildList.route) {
            MainScaffold(
                navController,
                "Child List",
                showFloatingButton = true,
                showBackButton = false
            ) {
                ChildListScreen(authViewModel, Modifier.padding(it)) {
                    navController.navigate(Screen.DashBoard.route)
                }
            }
        }

        composable(Screen.LocationScreen.route) {
            ShowLocationScreen()
        }

        composable(Screen.ChildDashBoard.route) {
            MainScaffold(navController, "DashBoard") {
                ChildDashBoardScreen(authViewModel)
            }
        }

        composable(Screen.PhoneNumberList.route) {
            MainScaffold(navController, "Contact List") {
                PhoneNumberList(
                    modifier = Modifier.padding(it),
                    authViewModel = authViewModel,
                    appUsageViewModel = appUsageViewModel
                )
            }
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
    object ShowChildList : Screen("show_child_list")
    object LocationScreen : Screen("show_location")
    object PhoneNumberList : Screen("phone_number_list")
    object BrowserHistory : Screen("browser_history_list")
}