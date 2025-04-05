package com.example.child_monitoring_app.ui.presentation.dashBoard

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.ui.data.BiometricAuthUtil
import com.example.child_monitoring_app.ui.domain.decodeBase64ToBitmap
import com.example.child_monitoring_app.ui.domain.model.ChildData
import com.example.child_monitoring_app.ui.presentation.component.toast
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ChildListScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier,
    onClickNavigate: () -> Unit
) {
    var children by remember { mutableStateOf<List<ChildData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val biometricAuthUtil = remember { BiometricAuthUtil(context) }
    val isBiometricAvailable = remember { biometricAuthUtil.canAuthenticate() }
    var authStatus by remember { mutableStateOf("Not authenticated") }
    val biometricStatus = remember { biometricAuthUtil.getBiometricAvailabilityStatus() }

    LaunchedEffect(Unit) {
        if (isBiometricAvailable) {
            biometricAuthUtil.showBiometricPrompt(
                activity = context as FragmentActivity,
                onSuccess = {
                    authStatus = "Authentication successful"
                },
                onError = { errorCode, errorMessage ->
                    authStatus = "Authentication error: $errorMessage"
                },
                onFailed = {
                    authStatus = "Authentication failed. Please try again."
                }
            )
        } else {
            context.toast("Biometric authentication is not available")
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                children = authViewModel.getChildList()
                println("Child List $children")
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error Occurred"
                isLoading = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        if (isLoading) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            children.forEach { it ->
                ChildProfileCard(it) { childId ->
                    authViewModel.childId.value = childId
                    authViewModel.childUserName.value = childId
                    onClickNavigate()
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
    val bitmap = decodeBase64ToBitmap(base64String)

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
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
