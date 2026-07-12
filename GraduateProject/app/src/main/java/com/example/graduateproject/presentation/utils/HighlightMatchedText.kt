package com.example.graduateproject.presentation.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

fun highlightMatchedText(text: String, query: String): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(text)

    val startIndex = text.indexOf(query, ignoreCase = true)
    if (startIndex == -1) return AnnotatedString(text)

    return buildAnnotatedString {
        append(text.substring(0, startIndex))
        withStyle(style = SpanStyle(color = Color(0xFF4DB6AC))) {
            append(text.substring(startIndex, startIndex + query.length))
        }
        append(text.substring(startIndex + query.length))
    }
}