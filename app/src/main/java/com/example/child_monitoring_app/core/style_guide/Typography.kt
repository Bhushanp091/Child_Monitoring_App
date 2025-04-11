package com.example.child_monitoring_app.core.style_guide


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.child_monitoring_app.R

const val ROBOTO = "ROBOTO"
const val POPPINS = "POPPINS"
const val SFPRO = "SFPRO"
const val PS = "PS"
const val POETSEN = "POETSEN"
const val SEGOEP = "SEGOEP"

val robotoBold = FontFamily(
    Font(R.font.roboto_bold, FontWeight.Bold)
)
val poppinsBold = FontFamily(
    Font(R.font.poppins_bold, FontWeight.Bold)
)
val sfProBold = FontFamily(
    Font(R.font.sf_pro_bold, FontWeight.Bold)
)
val psBold = FontFamily(
    Font(R.font.ps_bold, FontWeight.Bold)
)

val roboto = FontFamily(
    Font(R.font.roboto, FontWeight.Normal)
)
val poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal)
)
val sfPro = FontFamily(
    Font(R.font.sf_pro, FontWeight.Normal)
)
val ps = FontFamily(
    Font(R.font.ps, FontWeight.Normal)
)

val robotoMedium = FontFamily(
    Font(R.font.roboto_medium, FontWeight.Medium)
)
val poppinsMedium = FontFamily(
    Font(R.font.poppins_medium, FontWeight.Medium)
)
val sfProMedium = FontFamily(
    Font(R.font.sf_pro_medium, FontWeight.Medium)
)
val psMedium = FontFamily(
    Font(R.font.ps_medium, FontWeight.Medium)
)

val poppinsSemiBold = FontFamily(
    Font(R.font.poppins_semibold, FontWeight.SemiBold)
)
val sfProSemiBold = FontFamily(
    Font(R.font.sf_pro_semibold, FontWeight.SemiBold)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)