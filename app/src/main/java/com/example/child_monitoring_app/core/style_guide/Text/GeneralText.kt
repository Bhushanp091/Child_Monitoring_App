package com.example.child_monitoring_app.core.style_guide.Text

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.example.child_monitoring_app.core.style_guide.roboto
import network.chaintech.sdpcomposemultiplatform.ssp

@Composable
fun GeneralTextView(
    modifier: Modifier = Modifier,
    title: String,
    fontSize: TextUnit = 10.ssp,
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = Color.Black,
    maxLines: Int = Int.MAX_VALUE,
    fontFamily: FontFamily? = roboto,
    textDecoration: TextDecoration? = null,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    brush: Brush? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: (() -> Unit?)? = null
) {
    Text(
        text = title,
        fontSize = fontSize,
        color = textColor,
        textAlign = textAlign,
        fontFamily = fontFamily,
        modifier = if (onClick != null) modifier.clickable { onClick.invoke() } else modifier,
        maxLines = maxLines,
        textDecoration = textDecoration,
        style = TextStyle(
            brush = brush,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        ),
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        onTextLayout = onTextLayout,
        overflow = overflow
    )
}