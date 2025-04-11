package com.example.child_monitoring_app.core.style_guide.Text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.example.child_monitoring_app.core.style_guide.POETSEN
import com.example.child_monitoring_app.core.style_guide.POPPINS
import com.example.child_monitoring_app.core.style_guide.PS
import com.example.child_monitoring_app.core.style_guide.ROBOTO
import com.example.child_monitoring_app.core.style_guide.SEGOEP
import com.example.child_monitoring_app.core.style_guide.SFPRO
import com.example.child_monitoring_app.core.style_guide.poppins
import com.example.child_monitoring_app.core.style_guide.poppinsBold
import com.example.child_monitoring_app.core.style_guide.poppinsMedium
import com.example.child_monitoring_app.core.style_guide.poppinsSemiBold
import com.example.child_monitoring_app.core.style_guide.ps
import com.example.child_monitoring_app.core.style_guide.psBold
import com.example.child_monitoring_app.core.style_guide.psMedium
import com.example.child_monitoring_app.core.style_guide.roboto
import com.example.child_monitoring_app.core.style_guide.robotoBold
import com.example.child_monitoring_app.core.style_guide.robotoMedium
import com.example.child_monitoring_app.core.style_guide.sfPro
import com.example.child_monitoring_app.core.style_guide.sfProBold
import com.example.child_monitoring_app.core.style_guide.sfProMedium
import com.example.child_monitoring_app.core.style_guide.sfProSemiBold
import network.chaintech.sdpcomposemultiplatform.ssp

object RegularText {

    @Composable
    operator fun invoke(
        modifier: Modifier = Modifier,
        title: String,
        fontSize: TextUnit = 11.ssp,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        brush: Brush? = null,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        onClick: (() -> Unit?)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = fontSize,
            textColor = textColor,
            fontFamily = getFontFamilyRegular(fontStyle),
            modifier = modifier,
            textAlign = textAlign,
            maxLines = maxLines,
            lineHeight = lineHeight,
            overflow = overflow,
            brush = brush,
            onClick = onClick
        )
    }

    @Composable
    fun Underlined(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        brush: Brush? = null,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        onClick: (() -> Unit?)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 11.ssp,
            textColor = textColor,
            fontFamily = getFontFamilyRegular(fontStyle),
            modifier = modifier,
            textAlign = textAlign,
            textDecoration = TextDecoration.Underline,
            maxLines = maxLines,
            lineHeight = lineHeight,
            overflow = overflow,
            brush = brush,
            onClick = onClick
        )
    }

    @Composable
    fun Medium(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        brush: Brush? = null,
        textColor: Color = Color.Gray,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        onClick: (() -> Unit?)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 11.ssp,
            textColor = textColor,
            fontFamily = getFontFamilyMedium(fontStyle),
            modifier = modifier,
            textAlign = textAlign,
            maxLines = maxLines,
            lineHeight = lineHeight,
            overflow = overflow,
            brush = brush,
            onClick = onClick
        )
    }

    @Composable
    fun MediumUnderLine(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        brush: Brush? = null,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        onClick: (() -> Unit)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 11.ssp,
            textColor = textColor,
            fontFamily = getFontFamilyMedium(fontStyle),
            modifier = modifier,
            textAlign = textAlign,
            maxLines = maxLines,
            brush = brush,
            textDecoration = TextDecoration.Underline,
            lineHeight = lineHeight,
            overflow = overflow,
            onClick = onClick
        )
    }

    @Composable
    fun SemiBold(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = POPPINS,
        textAlign: TextAlign = TextAlign.Start,
        brush: Brush? = null,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        onClick: (() -> Unit?)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 11.ssp,
            textColor = textColor,
            fontFamily = getFontFamilySemiBold(fontStyle),
            modifier = modifier,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            maxLines = maxLines,
            brush = brush,
            onClick = onClick
        )
    }

    @Composable
    fun Bold(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        brush: Brush? = null,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        onClick: (() -> Unit?)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 11.ssp,
            textColor = textColor,
            fontFamily = getFontFamilyBold(fontStyle),
            modifier = modifier,
            textAlign = textAlign,
            maxLines = maxLines,
            lineHeight = lineHeight,
            overflow = overflow,
            brush = brush,
            onClick = onClick
        )
    }

    @Composable
    fun BoldUnderLine(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        brush: Brush? = null,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        onClick: (() -> Unit)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 11.ssp,
            textColor = textColor,
            fontFamily = getFontFamilyBold(fontStyle),
            modifier = modifier,
            textAlign = textAlign,
            maxLines = maxLines,
            brush = brush,
            lineHeight = lineHeight,
            overflow = overflow,
            textDecoration = TextDecoration.Underline,
            onClick = onClick
        )
    }
}

fun getFontFamilyRegular(fontStyle: String): FontFamily {
    return when (fontStyle) {
        ROBOTO -> roboto
        POPPINS -> poppins
        SFPRO -> sfPro
        PS -> ps
//        POETSEN -> poetsen
//        SEGOEP -> segoe
        else -> roboto
    }
}

fun getFontFamilyMedium(fontStyle: String): FontFamily {
    return when (fontStyle) {
        ROBOTO -> robotoMedium
        POPPINS -> poppinsMedium
        SFPRO -> sfProMedium
        PS -> psMedium
        else -> robotoMedium
    }
}

fun getFontFamilyBold(fontStyle: String): FontFamily {
    return when (fontStyle) {
        ROBOTO -> robotoBold
        POPPINS -> poppinsBold
        SFPRO -> sfProBold
        PS -> psBold
//        SEGOEP -> seg
        else -> robotoBold
    }
}

fun getFontFamilySemiBold(fontStyle: String): FontFamily {
    return when (fontStyle) {
        ROBOTO -> roboto
        POPPINS -> poppinsSemiBold
        SFPRO -> sfProSemiBold
        PS -> ps
        else -> roboto
    }
}


