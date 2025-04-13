package com.example.child_monitoring_app.features.app_blocker.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.preference.SharedPreference.saveBlockedWeb
import com.example.child_monitoring_app.core.style_guide.Text.RegularText
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.example.child_monitoring_app.core.util.adultWebsites
import com.example.child_monitoring_app.core.util.socialMediaWebsites
import com.example.child_monitoring_app.features.app_usage.AppUsageViewModel


@Composable
fun WebBlockerScreen(
    modifier: Modifier = Modifier,
    appUsageViewModel: AppUsageViewModel
) {

    val blockedWebsites = remember { mutableListOf<String>() }
    var searchQuery = remember { mutableStateOf("") }
    var blockAdultContent by remember { mutableStateOf(true) }
    var blockSocialMedia by remember { mutableStateOf(false) }
    var blockSocialMediaList = remember { mutableListOf<String>() }
    val context = LocalContext.current
    val parentId = SharedPreference.getParentId(context) ?: ""

    LaunchedEffect(Unit) {
        appUsageViewModel.firebaseManager.fetchBlockedWebFromFirebase(
            parentId,
            appUsageViewModel.childId.value
        ) { it ->
            blockedWebsites += it
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FF))
            .padding(16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search websites...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF9AA1B9)
                )
            },
            trailingIcon = {
                if (searchQuery.value.isNotEmpty()) {
                    IconButton(onClick = { searchQuery.value = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color(0xFF9AA1B9)
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SubHeadingText.SemiBold(title = "Content Filters", textColor = Color(0xFF2A3252))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        RegularText.SemiBold(
                            title = "Block Adult Content",
                            textColor = Color(0xFF2A3252),
                        )
                        SmallText.Medium(
                            title = "Blocks access to adult and explicit websites",
                            textColor = Color(0xFF9AA1B9)
                        )
                    }

                    Switch(
                        checked = blockAdultContent,
                        onCheckedChange = { blockAdultContent = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFFF5252),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Social media filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        RegularText.SemiBold(
                            title = "Block Social Media",
                            textColor = Color(0xFF2A3252),
                        )
                        SmallText.Medium(
                            title = "Blocks access to adult and explicit websites",
                            textColor = Color(0xFF9AA1B9),
                        )
                    }

                    Switch(
                        checked = blockSocialMedia,
                        onCheckedChange = {
                            blockSocialMedia = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2196F3),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            }
        }
        if (blockedWebsites.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SubHeadingText.SemiBold(title = "Blocked Websites", textColor = Color(0xFF2A3252))
                RegularText.Medium(
                    title = "${blockedWebsites.size} sites",
                    textColor = Color(0xFF9AA1B9)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth()
            ) {
                items(blockedWebsites) { website ->
                    SimplifiedBlockedWebsiteItemCard(
                        website = website,
                        onUnblock = {
                            println("Unblocking $it")
                            blockedWebsites.remove(it)
                        }
                    )
                }
            }
        }

        // Bottom button
        Button(
            onClick = {
                val input = searchQuery.value.trim()
                if (input.isNotEmpty() && !blockedWebsites.contains(input)) {
                    blockedWebsites.add(input)
                }
                val combinedList = blockedWebsites.toMutableSet()
                if (blockAdultContent) combinedList.addAll(adultWebsites)
                if (blockSocialMedia) combinedList.addAll(socialMediaWebsites)
                appUsageViewModel.firebaseManager.uploadBlockedWebList(
                    appUsageViewModel.childId.value,
                    combinedList.toList()
                ) {
                    searchQuery.value = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = if (searchQuery.value.isEmpty()) "Add Website to Block"
                else "Block (${searchQuery.value})",
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun SimplifiedBlockedWebsiteItemCard(
    website: String,
    onUnblock: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF0F0)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                SubHeadingText.SemiBold(title = website)
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RegularText.Medium(title = website)
                }
            }
            IconButton(
                onClick = { onUnblock(website) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE0E0))
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "{website}",
                    tint = Color(0xFFFF5252)
                )
            }
        }
    }
}