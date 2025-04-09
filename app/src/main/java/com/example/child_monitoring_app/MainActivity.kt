package com.example.child_monitoring_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
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
import com.example.child_monitoring_app.ui.presentation.login.FingerPrintScreen
import com.example.child_monitoring_app.ui.presentation.login.ParentChildSelectionScreen
import com.example.child_monitoring_app.ui.presentation.login.child_login.ChildLoginScreen
import com.example.child_monitoring_app.ui.presentation.login.parent_login.ParentLoginScreen
import com.example.child_monitoring_app.ui.presentation.login.parent_login.ParentSignupScreen


class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation()
//            if(!checkAccessibilityPermission()){
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//            }
        }
    }
    fun checkAccessibilityPermission(): Boolean {
        return try {
            val accessibilityEnabled = Settings.Secure.getInt(
                applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            if (accessibilityEnabled == 0) {
                // Accessibility service is NOT enabled, prompt the user
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                false
            } else {
                // Accessibility service is already enabled
                true
            }
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            false
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
            MainScaffold(navController, "Browser History") {
                BrowserHistoryScreen()
            }
        }

        composable(Screen.FingerPrint.route) {
            FingerPrintScreen {
                navController.navigate(Screen.ShowChildList.route)
            }
        }
    }
}


sealed class Screen(val route: String) {
    object DashBoard : Screen("dash_board")
    object FingerPrint : Screen("fingerprint")
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