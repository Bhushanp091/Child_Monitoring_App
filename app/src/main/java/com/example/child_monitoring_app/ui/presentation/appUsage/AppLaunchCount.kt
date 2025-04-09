package com.example.child_monitoring_app.ui.presentation.appUsage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.child_monitoring_app.ui.database.app_launch.AppLaunch
import com.example.child_monitoring_app.ui.database.app_launch.AppLaunchViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun AppLaunchScreen(viewModel: AppLaunchViewModel = viewModel()) {


    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val calendarToday = Calendar.getInstance()
    val today = dateFormat.format(calendarToday.time)

    val calendarWeekStart = Calendar.getInstance()
    calendarWeekStart.add(Calendar.DAY_OF_YEAR, -7)
    val weekStart = dateFormat.format(calendarWeekStart.time)

    val calendarMonthStart = Calendar.getInstance()
    calendarMonthStart.set(Calendar.DAY_OF_MONTH, 1)
    val monthStart = dateFormat.format(calendarMonthStart.time)

    viewModel.loadStats(
        date = today,
        weekStart = weekStart,
        weekEnd = today,
        monthStart = monthStart,
        monthEnd = today
    )



    val daily = viewModel.daily.value
    val weekly = viewModel.weekly.value
    val monthly = viewModel.monthly.value

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Daily Launch Count", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        LaunchList(daily)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Weekly Launch Count", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        LaunchList(weekly)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Monthly Launch Count", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        LaunchList(monthly)
    }
}

@Composable
fun LaunchList(data: List<AppLaunch>) {
    if (data.isEmpty()) {
        Text("No data available")
    } else {
        LazyColumn {
            items(data) { item ->
                Text("${item.packageName}: ${item.launchCount} launches")
            }
        }
    }
}
