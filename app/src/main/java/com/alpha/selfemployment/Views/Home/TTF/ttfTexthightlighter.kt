package com.alpha.selfemployment.Views.Home.TTF

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.alpha.selfemployment.ui.theme.green3A8
import com.alpha.selfemployment.ui.theme.lightGreenEBF

fun highlightedText(
    text: String,
    start: Int,
    end: Int
): AnnotatedString {

    return buildAnnotatedString {

        append(text)

        if (start < end) {
            addStyle(
                SpanStyle(
                    background = lightGreenEBF,
                    color = green3A8
                ),
                start,
                end
            )
        }
    }
}