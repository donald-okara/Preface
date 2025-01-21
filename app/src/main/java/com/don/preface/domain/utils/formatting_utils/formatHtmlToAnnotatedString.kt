package com.don.preface.domain.utils.formatting_utils

import android.text.Spanned
import android.text.style.CharacterStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans

fun formatHtmlToAnnotatedString(html: String): AnnotatedString {
    // Parse the HTML description
    val spanned: Spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)

    // Convert Spanned to AnnotatedString
    return buildAnnotatedString {
        // Traverse the spans in the spanned text
        val spans: Array<out CharacterStyle> = spanned.getSpans(0, spanned.length)
        var lastEnd = 0

        spans.forEach { span ->
            // Get the start and end indices of the current span
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)

            // Append text before the current span
            if (lastEnd < start) {
                append(spanned.subSequence(lastEnd, start))
            }

            // Apply styles based on span type
            when (span) {
                is android.text.style.StyleSpan -> {
                    withStyle(style = when (span.style) {
                        android.graphics.Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                        android.graphics.Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                        else -> SpanStyle()
                    }) {
                        append(spanned.subSequence(start, end))
                    }
                }
                is android.text.style.UnderlineSpan -> {
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        append(spanned.subSequence(start, end))
                    }
                }
                is android.text.style.URLSpan -> {
                    pushStringAnnotation(tag = "URL", annotation = span.url)
                    withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.LineThrough)) {
                        append(spanned.subSequence(start, end))
                    }
                    pop()
                }
                else -> {
                    // If it's a different type of span, just append it as is
                    append(spanned.subSequence(start, end))
                }
            }

            lastEnd = end // Update the last end to the current span's end
        }

        // Append any remaining text after the last span
        if (lastEnd < spanned.length) {
            append(spanned.subSequence(lastEnd, spanned.length))
        }
    }
}
