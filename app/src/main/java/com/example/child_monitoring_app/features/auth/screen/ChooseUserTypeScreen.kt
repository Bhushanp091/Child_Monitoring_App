package com.example.child_monitoring_app.features.auth.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.common.CommonButton
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp

///*** Choose User Type Screen ***/
//@Composable
//fun ChooseUserTypeScreen(
//    onNavigate: (String) -> Unit
//) {
//    val lightBlue = Color(0xFFE6F2FF)
//    val primaryBlue = Color(0xFF2196F3)
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        primaryBlue, // Deep Blue
//                        lightBlue  // Slightly Darker Blue
//                    )
//                )
//            ),
//        verticalArrangement = Arrangement.SpaceAround,
//        horizontalAlignment = Alignment.CenterHorizontally
//
//    ) {
//        OnboardingScreen()
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 15.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            SelectionButton(
//                text = "I'm Parents",
//                icon = R.drawable.parents_image,
//                onClick = { onNavigate(Screen.ParentLogin.route) },
//                modifier = Modifier.weight(1f)
//            )
//            Spacer(Modifier.padding(5.dp))
//            SelectionButton(
//                text = "I'm Child",
//                icon = R.drawable.child_image,
//                onClick = { onNavigate(Screen.ChildLogin.route) },
//                modifier = Modifier.weight(1f)
//
//            )
//        }
//
//        CommonButton(
//            text = "Sign Up",
//            modifier = Modifier
//                .padding(horizontal = 10.dp)
//                .padding(bottom = 20.dp),
//            onClick = { onNavigate(Screen.SignUp.route) },
//            enabled = true
//        )
//
//    }
//}
//
//@Composable
//fun OnboardingScreen() {
//    Box(
//        modifier = Modifier
//            .padding(24.dp)
//    ) {
//        Column(
//            modifier = Modifier,
//            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // Headline
//            Text(
//                text = "Get Started",
//                style = MaterialTheme.typography.displaySmall.copy(
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold
//                ),
//                modifier = Modifier.padding(top = 32.dp)
//            )
//
//            // Description
//            Column(
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Text(
//                    text = "Welcome to Child Monitoring App",
//                    style = MaterialTheme.typography.titleLarge.copy(
//                        color = Color.White.copy(alpha = 0.9f),
//                        fontWeight = FontWeight.SemiBold
//                    )
//                )
//
//                Text(
//                    text = "Ensure your child's safety with real-time monitoring and comprehensive tracking.",
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        color = Color.White.copy(alpha = 0.7f),
//                        lineHeight = 26.sp
//                    )
//                )
//
//                Text(
//                    text = "Connect, protect, and stay informed about your child's digital activities.",
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        color = Color.White.copy(alpha = 0.7f),
//                        lineHeight = 26.sp
//                    )
//                )
//            }
//            Spacer(modifier = Modifier.height(32.dp))
//        }
//    }
//}
//
//@Composable
//fun SelectionButton(
//    text: String,
//    icon: Int,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val lightBlue = Color(0xFFE6F2FF)
//    val primaryBlue = Color(0xFF2196F3)
//
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = modifier
//            .clickable(
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() }
//            ) { onClick() }
//            .clip(RoundedCornerShape(16.dp))
//            .background(lightBlue)
//            .aspectRatio(1f)
//            .padding(8.dp)
//
//    ) {
//        Row(
//            horizontalArrangement = Arrangement.Center,
//            modifier = Modifier.padding(bottom = 8.dp)
//        ) {
//            Image(
//                painter = painterResource(id = icon),
//                contentDescription = null,
//                modifier = Modifier.size(70.dp)
//            )
//        }
//        Text(
//            text = text,
//            color = primaryBlue,
//            fontWeight = FontWeight.Medium
//        )
//    }
//}


@Composable
fun ChooseUserTypeScreen(
    onNavigate: (String) -> Unit
) {
    // Theme colors
    val primaryBlue = Color(0xFF1976D2)
    val lightBlue = Color(0xFFBBDEFB)
    val accentColor = Color(0xFF03A9F4)
    val backgroundGradientEnd = Color(0xFFE3F2FD)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryBlue,
                        backgroundGradientEnd
                    )
                )
            )
    ) {
        // Background design elements
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Draw some decorative circles for visual interest
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.8f, size.height * 0.2f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.07f),
                radius = size.width * 0.4f,
                center = Offset(size.width * 0.1f, size.height * 0.85f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.sdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App logo and branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 20.sdp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.sdp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(9.sdp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = "KidGuard logo",
                        tint = primaryBlue,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(6.sdp))

                Text(
                    text = "KidGuard",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    ),
                    letterSpacing = 1.ssp
                )

                SmallText.Medium(
                    title = "Protecting What Matters Most",
                    textColor = Color.White,
                )
            }

            // Onboarding content
            OnboardingContent()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.sdp),
                modifier = Modifier.padding(top = 30.sdp)
            ) {
                Text(
                    text = "Choose your account type",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.sdp),
                    horizontalArrangement = Arrangement.spacedBy(12.sdp)
                ) {
                    UserTypeCard(
                        text = "I'm a Parent",
                        icon = R.drawable.parents_image,
                        onClick = { onNavigate(Screen.ParentLogin.route) },
                        modifier = Modifier.weight(1f)
                    )

                    UserTypeCard(
                        text = "I'm a Child",
                        icon = R.drawable.child_image,
                        onClick = { onNavigate(Screen.ChildLogin.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                SignUpButton(
                    onClick = { onNavigate(Screen.SignUp.route) }
                )
            }
        }
    }
}

@Composable
fun OnboardingContent() {
    Column(
        modifier = Modifier
            .padding(vertical = 20.sdp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        SubHeadingText.SemiBold(
            title = "Get Started",
            textColor = Color.White
        )
        Spacer(modifier = Modifier.height(14.sdp))

        SmallText.Medium(
            title = "Welcome to KidGuard",
            textColor = Color.White
        )

        Spacer(modifier = Modifier.height(6.sdp))

        val features = listOf(
            "Ensure your child's safety with real-time monitoring",
            "Comprehensive activity tracking and alerts",
            "Connect, protect, and stay informed about digital activities"
        )

        features.forEach { feature ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 3.sdp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.sdp)
                )

                Spacer(modifier = Modifier.width(6.sdp))

                SmallText.Medium(
                    title = feature,
                    textColor = Color.White
                )
            }
        }
    }
}

@Composable
fun UserTypeCard(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.sdp else 6.sdp,
        label = "cardElevation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "cardScale"
    )

    Card(
        shape = RoundedCornerShape(16.sdp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.9f)
                .padding(8.sdp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .size(80.sdp)
                    .padding(8.sdp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.sdp))
            SmallText.Medium(
                title = text,
                textColor = Color(0xFF1976D2),
            )
        }
    }
}

@Composable
fun SignUpButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed)
            Color(0xFF03A9F4) else Color(0xFF2196F3),
        label = "buttonColor"
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.sdp),
        contentPadding = PaddingValues(horizontal = 32.sdp, vertical = 12.sdp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.sdp,
            pressedElevation = 2.sdp
        ),
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp)
    ) {
        Text(
            text = "Create an Account",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(vertical = 4.sdp)
        )
    }
}