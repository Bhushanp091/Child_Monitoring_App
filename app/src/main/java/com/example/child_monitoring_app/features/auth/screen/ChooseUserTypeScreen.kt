package com.example.child_monitoring_app.features.auth.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.common.CommonButton
import com.example.child_monitoring_app.core.navigation.Screen

/*** Choose User Type Screen ***/
@Composable
fun ChooseUserTypeScreen(
    onNavigate: (String) -> Unit
) {
    val lightBlue = Color(0xFFE6F2FF)
    val primaryBlue = Color(0xFF2196F3)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryBlue, // Deep Blue
                        lightBlue  // Slightly Darker Blue
                    )
                )
            ),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        OnboardingScreen()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectionButton(
                text = "I'm Parents",
                icon = R.drawable.parents_image,
                onClick = { onNavigate(Screen.ParentLogin.route) },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.padding(5.dp))
            SelectionButton(
                text = "I'm Child",
                icon = R.drawable.child_image,
                onClick = { onNavigate(Screen.ChildLogin.route) },
                modifier = Modifier.weight(1f)

            )
        }

        CommonButton(
            text = "Sign Up",
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(bottom = 20.dp),
            onClick = { onNavigate(Screen.SignUp.route) },
            enabled = true
        )

    }
}

@Composable
fun OnboardingScreen() {
    Box(
        modifier = Modifier
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Headline
            Text(
                text = "Get Started",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 32.dp)
            )

            // Description
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome to Child Monitoring App",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = "Ensure your child's safety with real-time monitoring and comprehensive tracking.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 26.sp
                    )
                )

                Text(
                    text = "Connect, protect, and stay informed about your child's digital activities.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 26.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SelectionButton(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lightBlue = Color(0xFFE6F2FF)
    val primaryBlue = Color(0xFF2196F3)


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .clip(RoundedCornerShape(16.dp))
            .background(lightBlue)
            .aspectRatio(1f)
            .padding(8.dp)

    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(70.dp)
            )
        }
        Text(
            text = text,
            color = primaryBlue,
            fontWeight = FontWeight.Medium
        )
    }
}