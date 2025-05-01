package com.example.child_monitoring_app.features.call_log_history

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.style_guide.Text.RegularText
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.example.child_monitoring_app.core.util.CommonUtil
import com.example.child_monitoring_app.features.app_usage.AppUsageViewModel
import com.example.child_monitoring_app.features.app_usage.CallLogModel
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp


@Composable
fun PhoneNumberList(
    appUsageViewModel: AppUsageViewModel,
    modifier: Modifier
) = with(appUsageViewModel){
    val context = LocalContext.current
    val parenId = SharedPreference.getParentId(context) ?: ""

    LaunchedEffect(Unit) {
        callLogFlag.value = true
        appUsageViewModel.firebaseManager.fetchContactsFromFirebase(parenId,appUsageViewModel.childId.value) {
            if (callLogFlag.value) {
                appUsageViewModel.contactList.value = it
                callLogFlag.value = !callLogFlag.value
            }
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier
        ) {
            items(appUsageViewModel.contactList.value) {
                ContactInfoBoxNew(it)
            }
        }
    }

}


@Composable
fun ContactInfoBoxNew(contact: Contact) {

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.sdp, horizontal = 8.sdp)
                .background(color = Color.White, shape = RoundedCornerShape(5.sdp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(40.sdp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                val initials = contact.name.take(2).uppercase()
                Text(
                    text = initials,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = primaryColor
                )
            }

            Spacer(Modifier.padding(5.sdp))


            // Call Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                SubHeadingText.SemiBold(title = contact.name.ifEmpty { "Unknown" })
                Spacer(Modifier.padding(2.sdp))
                RegularText.Medium(
                    title = contact.phoneNumber,
                )
            }
//            Text(
//                text = CommonUtil.convertTimestampToReadableFormat(callLogModel.date.toLong()),
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Black,
//                fontSize = 10.ssp
//            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .width(12.sdp), color = Color.LightGray.copy(alpha = 0.5f)
        )
    }
}


