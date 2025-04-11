package com.example.child_monitoring_app.core.style_guide.Text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.example.child_monitoring_app.core.style_guide.POPPINS
import com.example.child_monitoring_app.core.style_guide.ROBOTO
import network.chaintech.sdpcomposemultiplatform.ssp

object SubHeadingText {

    @Composable
    operator fun invoke(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        brush: Brush? = null,
        onClick: (() -> Unit?)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 13.ssp,
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
    fun SemiBold(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = POPPINS,
        textAlign: TextAlign = TextAlign.Start,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        brush: Brush? = null,
        onClick: (() -> Unit?)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 13.ssp,
            textColor = textColor,
            fontFamily = getFontFamilySemiBold(fontStyle),
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
    fun Bold(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        brush: Brush? = null,
        onClick: (() -> Unit?)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 13.ssp,
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
    fun BoldWithSpannable(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        brush: Brush? = null,
        inlineContent: Map<String, InlineTextContent>?,
        annotatedString: AnnotatedString? = null,
        onClick: (() -> Unit?)? = null
    ) {
        if (annotatedString == null) {
            GeneralTextView(
                title = title,
                fontSize = 13.ssp,
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
        } else {
            Text(
                annotatedString,
                inlineContent = inlineContent ?: mapOf(),
                fontSize = 13.ssp,
                color = textColor,
                textAlign = textAlign,
                maxLines = maxLines,
                lineHeight = lineHeight,
                overflow = overflow,
                fontFamily = getFontFamilyBold(fontStyle),
                modifier = if (onClick != null) modifier.clickable { onClick.invoke() } else modifier
            )
        }
    }


    @Composable
    fun Medium(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        brush: Brush? = null,
        onClick: (() -> Unit?)? = null
    ) {
        GeneralTextView(
            title = title,
            fontSize = 13.ssp,
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
    fun MediumUnderLined(
        modifier: Modifier = Modifier,
        title: String,
        fontStyle: String = ROBOTO,
        textAlign: TextAlign = TextAlign.Start,
        textColor: Color = Color.Black,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        maxLines: Int = Int.MAX_VALUE,
        brush: Brush? = null,
        onClick: () -> Unit
    ) {
        GeneralTextView(
            title = title,
            fontSize = 13.ssp,
            textColor = textColor,
            fontFamily = getFontFamilyMedium(fontStyle),
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