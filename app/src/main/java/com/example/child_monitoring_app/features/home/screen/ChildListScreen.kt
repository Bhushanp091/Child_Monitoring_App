package com.example.child_monitoring_app.features.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.core.preference.SharedPreference.saveChildUsernameLocally
import com.example.child_monitoring_app.core.style_guide.Text.RegularText
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.example.child_monitoring_app.core.ui.BaseViewModel
import com.example.child_monitoring_app.core.util.ChildData
import com.example.child_monitoring_app.features.app_usage.AppUsageViewModel
import com.example.child_monitoring_app.features.auth.AuthViewModel
import com.example.child_monitoring_app.features.home.HomeViewModel
import com.example.child_monitoring_app.features.theme.primaryBlue
import kotlinx.coroutines.launch
@Composable
fun ChildListScreen(
    authViewModel: AuthViewModel,
    appUsageViewModel: AppUsageViewModel,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) = with(homeViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                childList.value = authViewModel.getChildList()
                println("Child List ${childList.value}")
                loadingChilList.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Error Occurred"
                loadingChilList.value = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFF0F4FF), Color(0xFFFFFFFF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEEF2FF)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFF4A6FFF),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        RegularText.Medium(
                            title = "Select a Child to monitor",
                            textColor = Color(0xFF2A3252)
                        )

                        SmallText.Medium(
                            title = "Tap on a child profile to view their activity dashboard",
                            textColor = Color(0xFF9AA1B9)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Child List or Loading
            if (loadingChilList.value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4A6FFF),
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        RegularText.Medium(
                            title = "Loading profiles...",
                            textColor = Color(0xFF9AA1B9)
                        )
                    }
                }
            } else if (childList.value.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "No Profiles",
                            tint = Color(0xFFBDC1D3),
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SubHeadingText.Medium(
                            title = "No child profiles found",
                            textColor = Color(0xFF9AA1B9)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SmallText.Medium(
                            title = "Add a child profile to start monitoring",
                            textColor = Color(0xFF9AA1B9)
                        )
                    }
                }
            } else {
                // Child list
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RegularText.Medium(
                        title = "${childList.value.size} Profile${if (childList.value.size > 1) "s" else ""}",
                        textColor = Color(0xFF9AA1B9),
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    )

                    childList.value.forEach { childData ->
                        ChildProfileCard(childData) { selectedChild ->
                            appUsageViewModel.childId.value = selectedChild.username
                            appUsageViewModel.childName.value = selectedChild.name
                            authViewModel.childId.value = selectedChild.username
                            saveChildUsernameLocally(context,selectedChild.username)
                            authViewModel.childName.value = selectedChild.name
                            onNavigate(Screen.DashBoard.route)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Add new child button
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Add new child navigation */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A6FFF)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Add Child",
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                RegularText.SemiBold(
                    title = "Add New Child Profile",
                    textColor = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ChildProfileCard(
    childData: ChildData,
    onClick: (ChildData) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(childData) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Profile image with decorative circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF0F4FF),
                                Color(0xFFE4EAFF)
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(4.dp)
            ) {
                ProfileImage(childData.image.toString())
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Child details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                SubHeadingText.SemiBold(
                    title = childData.name,
                    textColor = Color(0xFF2A3252)
                )

                Spacer(modifier = Modifier.height(4.dp))

                SmallText.Medium(
                    title = childData.username,
                    textColor = Color(0xFF9AA1B9)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Arrow icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Select Child",
                tint = Color(0xFF9AA1B9),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProfileImage(base64String: String) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(width = 2.dp, color = Color(0xFFE4EAFF), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.child_image),
            contentDescription = "Child Profile",
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}