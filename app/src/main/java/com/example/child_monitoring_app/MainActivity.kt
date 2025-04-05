package com.example.child_monitoring_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.child_monitoring_app.ui.data.SharedPreference
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
import com.example.child_monitoring_app.ui.presentation.location.ChildLocationMap
import com.example.child_monitoring_app.ui.presentation.location.LocationViewModel
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
    val locationViewModel: LocationViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current


    val startDestinationId = if (SharedPreference.isUserLoggedIn(context)) {
        Screen.ShowChildList.route
    } else {
        Screen.PreLogin.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestinationId
//        startDestination = Screen.PreLogin.route
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
                showBackButton = true
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
                AddChildScreen(authViewModel) {
                    navController.popBackStack()
                }
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
            MainScaffold(navController, "Child Location") {
                ChildLocationMap(locationViewModel, authViewModel)
            }
        }

        composable(Screen.ChildLocationScreen.route) {
            MainScaffold(navController, "Your Location") {
                ShowLocationScreen(locationViewModel)
            }
        }

        composable(Screen.ChildDashBoard.route) {
            MainScaffold(navController, "DashBoard") {
                ChildDashBoardScreen(navController, authViewModel)
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
    object LocationScreen : Screen("show_child_location")
    object ChildLocationScreen : Screen("child_location_screen")
    object PhoneNumberList : Screen("phone_number_list")
    object BrowserHistory : Screen("browser_history_list")
}