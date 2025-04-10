package com.example.child_monitoring_app.features.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.core.util.ChildData
import com.example.child_monitoring_app.features.auth.AuthViewModel
import com.example.child_monitoring_app.features.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun ChildListScreen(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    modifier: Modifier,
    onNavigate: (String) -> Unit
) = with(homeViewModel) {

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                childList.value = authViewModel.getChildList()
                println("Child List $childList")
                loadingChilList.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Error Occurred"
                loadingChilList.value = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        if (loadingChilList.value) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            childList.value.forEach { it ->
                ChildProfileCard(it) { childId ->
                    authViewModel.childId.value = childId
                    authViewModel.childUserName.value = childId
                    onNavigate(Screen.DashBoard.route)
                }
            }
        }
    }
}

@Composable
fun ChildProfileCard(
    childData: ChildData,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(childData.username) }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {


            ProfileImage(childData.image.toString())

            Spacer(modifier = Modifier.width(16.dp)) // Space between image and text

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = childData.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = childData.username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}


@Composable
fun ProfileImage(base64String: String) {
    val bitmap = null

    if (bitmap != null) {
//        Image(
//            bitmap = ,
//            contentDescription = "Profile Picture",
//            modifier = Modifier
//                .size(100.dp)
//                .clip(CircleShape)
//        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.child_image),
            contentDescription = "profile",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
