package com.example.child_monitoring_app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.features.home.screen.AddChildScreen
import com.example.child_monitoring_app.features.app_usage.AppUsageScreen
import com.example.child_monitoring_app.features.app_usage.AppUsageViewModel
import com.example.child_monitoring_app.features.call_log_history.PhoneNumberList
import com.example.child_monitoring_app.features.call_log_history.CallHistoryScreen
import com.example.child_monitoring_app.features.browser_history.BrowserHistoryScreen
import com.example.child_monitoring_app.core.common.MainScaffold
import com.example.child_monitoring_app.features.child.ChildDashBoardScreen
import com.example.child_monitoring_app.features.home.screen.ChildListScreen
import com.example.child_monitoring_app.features.home.screen.HomeScreen
import com.example.child_monitoring_app.features.location.ShowLocationScreen
import com.example.child_monitoring_app.features.location.LocationViewModel
import com.example.child_monitoring_app.features.child.ChildLocationScreen
import com.example.child_monitoring_app.features.auth.AuthViewModel
import com.example.child_monitoring_app.features.auth.screen.FingerPrintScreen
import com.example.child_monitoring_app.features.auth.screen.ChooseUserTypeScreen
import com.example.child_monitoring_app.features.auth.screen.ChildLoginScreen
import com.example.child_monitoring_app.features.auth.screen.ParentLoginScreen
import com.example.child_monitoring_app.features.auth.screen.ParentSignupScreen
import com.example.child_monitoring_app.features.home.HomeViewModel


@Composable
fun Navigation() {

    val navController = rememberNavController()
    val appUsageViewModel: AppUsageViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val context = LocalContext.current


    val startDestinationId = if (SharedPreference.isUserLoggedIn(context)) {
        Screen.FingerPrint.route
    } else {
        Screen.PreLogin.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestinationId
//        startDestination = Screen.BrowserHistory.route
//        startDestination = Screen.DashBoard.route
    ) {

        composable(Screen.PreLogin.route) {
            ChooseUserTypeScreen() { onNavigate(navController, it) }
        }

        composable(Screen.SignUp.route) {
            MainScaffold(navController, "Sign Up") {
                ParentSignupScreen(authViewModel) { onNavigate(navController, it) }
            }
        }

        composable(Screen.ChildLogin.route) {
            MainScaffold(navController, "Child Login") {
                ChildLoginScreen(authViewModel) { onNavigate(navController, it) }
            }
        }

        composable(Screen.ParentLogin.route) {
            MainScaffold(navController, "Login") {
                ParentLoginScreen(authViewModel) { onNavigate(navController, it) }
            }
        }

        composable(Screen.DashBoard.route) {
            MainScaffold(
                navController,
                "DashBoard",
                showFloatingButton = true,
                showBackButton = true
            ) {
                HomeScreen(
                    Modifier.padding(it),
                    authViewModel,
                    navController
                )
            }
        }

        composable(Screen.AddChild.route) {
            MainScaffold(navController, "Add Child") {
                AddChildScreen(authViewModel, homeViewModel) {
                    navController.popBackStack()
                }
            }
        }

        composable(Screen.AppUsage.route) {
            MainScaffold(navController, "App Usage") {
                AppUsageScreen(appUsageViewModel, Modifier.padding(it))
            }
        }

        composable(Screen.CallHistory.route) {
            MainScaffold(navController, "Call Log History") {
                CallHistoryScreen(appUsageViewModel, Modifier.padding(it))
            }
        }

        composable(Screen.ShowChildList.route) {
            MainScaffold(
                navController,
                "Child List",
                showFloatingButton = true,
                showBackButton = false
            ) {
                ChildListScreen(authViewModel, homeViewModel, Modifier.padding(it)) { route ->
                    onNavigate(navController, route)
                }
            }
        }

        composable(Screen.LocationScreen.route) {
            MainScaffold(navController, "Child Location") {
                ShowLocationScreen(locationViewModel, authViewModel)
            }
        }

        composable(Screen.ChildLocationScreen.route) {
            MainScaffold(navController, "Your Location") {
                ChildLocationScreen(locationViewModel)
            }
        }

        composable(Screen.ChildDashBoard.route) {
            MainScaffold(navController, "DashBoard") {
                ChildDashBoardScreen(authViewModel) { onNavigate(navController, it) }
            }
        }

        composable(Screen.PhoneNumberList.route) {
            MainScaffold(navController, "Contact List") {
                PhoneNumberList(
                    modifier = Modifier.padding(it),
                    appUsageViewModel = appUsageViewModel
                )
            }
        }

        composable(Screen.BrowserHistory.route) {
            MainScaffold(navController, "Browser History") {
                BrowserHistoryScreen()
            }
        }

        composable(Screen.FingerPrint.route) {
            FingerPrintScreen(
                modifier = Modifier,
                authViewModel = authViewModel
            ) {
                navController.navigate(Screen.ShowChildList.route)
            }
        }
    }
}


fun onNavigate(navController: NavController, route: String) {
    if (route != Screen.Back.route) {
        navController.navigate(route)
    } else {
        navController.popBackStack()
    }
}

sealed class Screen(val route: String) {
    data object Back : Screen("back")
    data object DashBoard : Screen("dash_board")
    data object FingerPrint : Screen("fingerprint")
    data object ChildDashBoard : Screen("child_dash_board")
    data object CallHistory : Screen("call_history")
    data object AppUsage : Screen("app_usage")
    data object ParentLogin : Screen("login")
    data object SignUp : Screen("sign_up")
    data object PreLogin : Screen("pre_login")
    data object ChildLogin : Screen("child_login")
    data object AddChild : Screen("add_Child")
    data object ShowChildList : Screen("show_child_list")
    data object LocationScreen : Screen("show_child_location")
    data object ChildLocationScreen : Screen("child_location_screen")
    data object PhoneNumberList : Screen("phone_number_list")
    data object BrowserHistory : Screen("browser_history_list")
}

