package com.example.child_monitoring_app.features.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.core.style_guide.AquaGradient
import com.example.child_monitoring_app.core.style_guide.GrayGradient
import com.example.child_monitoring_app.core.style_guide.GreenGradient
import com.example.child_monitoring_app.core.style_guide.OrangeGradient
import com.example.child_monitoring_app.core.style_guide.PinkGradient
import com.example.child_monitoring_app.core.style_guide.SkyBlueGradient
import com.example.child_monitoring_app.core.style_guide.VioletGradient
import com.example.child_monitoring_app.core.style_guide.YellowGradient
import network.chaintech.sdpcomposemultiplatform.sdp


@Composable
fun FeaturesScreen(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {

    val features = listOf(
        MonitoringFeature(
            "App Usage", R.drawable.baseline_apps_24, Screen.AppUsage.route,
            YellowGradient
        ),
        MonitoringFeature(
            "Call History", R.drawable.ic_contact, Screen.CallHistory.route,
            GreenGradient
        ),
        MonitoringFeature(
            "Contacts",
            R.drawable.ic_call,
            Screen.PhoneNumberList.route,
            PinkGradient
        ),
        MonitoringFeature(
            "Browser History", R.drawable.baseline_web_24, Screen.BrowserHistory.route,
            SkyBlueGradient
        ),
        MonitoringFeature(
            "Location",
            R.drawable.baseline_location_on_24,
            Screen.LocationScreen.route,
            VioletGradient
        ),
        MonitoringFeature(
            "App Blocker", R.drawable.baseline_app_blocking_24, Screen.AppBlocker.route,
            OrangeGradient
        ),
        MonitoringFeature(
            "Web Blocker", R.drawable.baseline_block_24, Screen.WebBlocker.route,
            GrayGradient
        ),
        MonitoringFeature(
            "App Launch Counter", R.drawable.baseline_touch_app_24, Screen.AppLaunch.route,
            AquaGradient
        ),
    )


    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.sdp)
                .nestedScroll(remember { object : NestedScrollConnection {} }),
            content = {
                items(features) { feature ->
                    FeatureCardWithImage(feature) {
                        onNavigate(feature.route)
                    }
                }
            }
        )
    }

}