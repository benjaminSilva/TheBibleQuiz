package com.example.novagincanabiblica.ui.basicviews

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.novagincanabiblica.ui.theme.closeToBlack
import com.example.novagincanabiblica.ui.theme.wrongPlace
import com.example.novagincanabiblica.ui.theme.zillasFontFamily

@Composable
fun BasicText(
    modifier: Modifier = Modifier,
    text: String?,
    fontSize: Int = 14,
    fontFamily: FontFamily = zillasFontFamily,
    lineHeight: Int = 18,
    fontColor: Color = closeToBlack,
    textAlign: TextAlign = TextAlign.Start
) {
    if (text != null) {
        Text(
            text = text,
            modifier = modifier,
            fontSize = fontSize.sp,
            fontFamily = fontFamily,
            lineHeight = lineHeight.sp,
            color = fontColor,
            textAlign = textAlign
        )
    }
}

@Composable
fun BasicText(
    modifier: Modifier = Modifier,
    text: AnnotatedString?,
    fontSize: Int = 14,
    fontFamily: FontFamily = zillasFontFamily,
    lineHeight: Int = 18,
    fontColor: Color = closeToBlack
) {
    if (text != null) {
        Text(
            text = text,
            modifier = modifier,
            fontSize = fontSize.sp,
            fontFamily = fontFamily,
            lineHeight = lineHeight.sp,
            color = fontColor
        )
    }
}

fun highlightText(text: String, highlightText: String) = buildAnnotatedString {
    var wordToHighlightLowerCase = highlightText.lowercase()
    var countOfOccurrences = countOccurrences(text, searchStr = wordToHighlightLowerCase)
    val wordToHighlightCapitalized = wordToHighlightLowerCase.replaceFirstChar { it.uppercaseChar() }
    var countOfOccurrencesCapitalized = countOccurrences(text, searchStr = wordToHighlightCapitalized)
    var countOfOccurrencesTotal = countOfOccurrences + countOfOccurrencesCapitalized
    var modifiedString = text
    for (i in 0 until countOfOccurrencesTotal) {
        val substringBeforeLower = modifiedString.substringBefore(wordToHighlightLowerCase)
        val subStringBeforeLowerLengh = substringBeforeLower.length
        val substringBeforeCapitalized = modifiedString.substringBefore(wordToHighlightCapitalized)
        val subStringBeforeCapitalizedLengh = substringBeforeCapitalized.length
        val highlightNextCase = if ( subStringBeforeLowerLengh > subStringBeforeCapitalizedLengh) {
            countOfOccurrencesCapitalized -= 1
            wordToHighlightCapitalized
        } else {
            countOfOccurrences -= 1
            wordToHighlightLowerCase
        }
        append(modifiedString.substringBefore(highlightNextCase))
        modifiedString = modifiedString.substringAfter(highlightNextCase)
        withStyle(SpanStyle(background = wrongPlace)) {
            append(highlightNextCase)
        }
    }

    append(modifiedString)
}


fun countOccurrences(str: String, searchStr: String): Int {
    var count = 0
    var startIndex = 0

    while (startIndex < str.length) {
        val index = str.indexOf(searchStr, startIndex)
        if (index >= 0) {
            count++
            startIndex = index + searchStr.length
        } else {
            break
        }
    }

    return count
}

@Composable
fun AutoResizeText(
    text: String,
    fontSizeRange: FontSizeRange,
    modifier: Modifier = Modifier,
    color: Color = closeToBlack,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
) {
    var fontSizeValue by remember { mutableFloatStateOf(fontSizeRange.max.value) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        style = style,
        fontSize = fontSizeValue.sp,
        onTextLayout = {
            if (it.didOverflowHeight && !readyToDraw) {
                val nextFontSizeValue = fontSizeValue - fontSizeRange.step.value
                if (nextFontSizeValue <= fontSizeRange.min.value) {
                    // Reached minimum, set minimum font size and it's readToDraw
                    fontSizeValue = fontSizeRange.min.value
                    readyToDraw = true
                } else {
                    // Text doesn't fit yet and haven't reached minimum text range, keep decreasing
                    fontSizeValue = nextFontSizeValue
                }
            } else {
                // Text fits before reaching the minimum, it's readyToDraw
                readyToDraw = true
            }
        },
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() }
    )
}

data class FontSizeRange(
    val min: TextUnit,
    val max: TextUnit,
    val step: TextUnit = DEFAULT_TEXT_STEP,
) {
    init {
        require(min < max) { "min should be less than max, $this" }
        require(step.value > 0) { "step should be greater than 0, $this" }
    }

    companion object {
        private val DEFAULT_TEXT_STEP = 1.sp
    }
}

@Preview(widthDp = 200, heightDp = 100)
@Preview(widthDp = 200, heightDp = 30)
@Preview(widthDp = 60, heightDp = 30)
@Composable
fun AutoSizeTextPreview() {
    MaterialTheme {
        Surface {
            AutoResizeText(
                text = "A much more annoying text, isnt it gonna break the line",
                fontSizeRange = FontSizeRange(1.sp, 20.sp),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}