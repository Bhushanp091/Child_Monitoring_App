package com.example.child_monitoring_app.ui.presentation.login

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.Screen
import com.example.child_monitoring_app.ui.presentation.component.CommonButton
import com.example.child_monitoring_app.ui.theme.lightBlue
import com.example.child_monitoring_app.ui.theme.white_color
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun PreLoginScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.sdp)
            .background(white_color),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select Login Type", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.sdp))

        Button(
            onClick = { navController.navigate(Screen.ParentLogin.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.sdp)
        ) {
            Text("Login as Parent")
        }

        Button(
            onClick = { navController.navigate(Screen.ChildLogin.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.sdp)
        ) {
            Text("Login as Child")
        }
    }
}


@Composable
fun ParentChildSelectionScreen(
    navController: NavController
) {
    val backgroundColor = Color(0xFFF0F6FF)
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
            )   ,
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 10.dp),
//            horizontalArrangement = Arrangement.Start
//        ) {
//            Text(
//                text = "Get Started",
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.Bold,
//                fontSize = 30.sp,
//                modifier = Modifier
//            )
//        }

//        Text(
//            text = "This is an app that allows Parents\nto gently monitor the safety of their\nchildren.",
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color.Gray,
//            lineHeight = 30.sp,
//            modifier = Modifier
//                .fillMaxWidth(),
//            fontWeight = FontWeight.Medium
//        )

//        Text(
//            text = "Which one are you?",
//            color = Color.Black,
//            lineHeight = 50.sp,
//            modifier = Modifier
//                . fillMaxWidth()
//                .padding(start = 30.dp)
//                .fillMaxWidth(),
//            textAlign = TextAlign.Center,
//            fontWeight = FontWeight.Medium
//        )
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
                onClick = { navController.navigate(Screen.ParentLogin.route) },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.padding(5.dp))
            SelectionButton(
                text = "I'm Child",
                icon = R.drawable.child_image,
                onClick = { navController.navigate(Screen.ChildLogin.route) },
                modifier = Modifier.weight(1f)

            )
        }

        CommonButton(
            text = "Sign Up",
            modifier = Modifier.padding(horizontal = 10.dp).padding(bottom = 20.dp),
            onClick = { navController.navigate(Screen.SignUp.route) },
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
//                Row {
//                    Text(
//                        text = "Which one are you?",
//                        color = Color.White,
//                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier.padding(bottom = 24.dp).fillMaxWidth(),
//                        textAlign = TextAlign.Center
//                    )
//
//                }

            }

            // Placeholder for role selection (you'll implement this next)
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
        // People icons (simplified)
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


