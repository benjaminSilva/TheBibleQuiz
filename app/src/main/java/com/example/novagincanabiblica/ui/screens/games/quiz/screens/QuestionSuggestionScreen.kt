package com.example.novagincanabiblica.ui.screens.games.quiz.screens

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.novagincanabiblica.R
import com.example.novagincanabiblica.data.models.quiz.Answer
import com.example.novagincanabiblica.data.models.quiz.Question
import com.example.novagincanabiblica.data.models.quiz.QuestionDifficulty
import com.example.novagincanabiblica.data.models.state.FeedbackMessage
import com.example.novagincanabiblica.ui.basicviews.BasicContainer
import com.example.novagincanabiblica.ui.basicviews.BasicEditText
import com.example.novagincanabiblica.ui.basicviews.BasicText
import com.example.novagincanabiblica.ui.basicviews.FeedbackMessage
import com.example.novagincanabiblica.ui.basicviews.animateAlpha
import com.example.novagincanabiblica.ui.basicviews.animateColor
import com.example.novagincanabiblica.ui.theme.closeToBlack
import com.example.novagincanabiblica.ui.theme.darkGray
import com.example.novagincanabiblica.ui.theme.gray
import com.example.novagincanabiblica.viewmodel.BibleQuizViewModel

@Composable
fun InitSuggestQuestionScreen(viewModel: BibleQuizViewModel) {
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    SuggestQuestionScreen(feedbackMessage = feedbackMessage) {
        viewModel.sendQuestionSuggestion(it)
    }
}

@Composable
fun SuggestQuestionScreen(feedbackMessage: FeedbackMessage, sendSuggestion: (Question) -> Unit) {

    val listOfRadioButtonDifficultyOptions = listOf("Easy", "Medium", "Hard", "Impossible")
    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            listOfRadioButtonDifficultyOptions[0]
        )
    }

    var displayErrorMessageIfNecessary by remember {
        mutableStateOf(false)
    }

    var questionText by remember {
        mutableStateOf("")
    }

    var questionCorrectAnswer by remember {
        mutableStateOf("")
    }

    var questionWrongAnswer1 by remember {
        mutableStateOf("")
    }

    var questionWrongAnswer2 by remember {
        mutableStateOf("")
    }

    var questionWrongAnswer3 by remember {
        mutableStateOf("")
    }

    var bibleVerse by remember {
        mutableStateOf("")
    }

    var questionCreator by remember {
        mutableStateOf("")
    }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage == FeedbackMessage.QuestionSuggestionSent) {
            questionText = ""
            questionCorrectAnswer = ""
            questionWrongAnswer1 = ""
            questionWrongAnswer2 = ""
            questionWrongAnswer3 = ""
            bibleVerse = ""
            questionCreator = ""
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            BasicText(text = "Suggest a Question", fontSize = 36)
            BasicText(text = "Question Text", fontSize = 22)
            BasicEditText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp), text = questionText,
                errorMessage = if (displayErrorMessageIfNecessary && questionText.isEmpty()) "This field is required" else "",

            ) {
                questionText = it
            }
            BasicText(text = "Correct Answer Text", fontSize = 22)
            BasicEditText(
                text = questionCorrectAnswer,
                errorMessage = if (displayErrorMessageIfNecessary && questionCorrectAnswer.isEmpty()) "This field is required" else ""
            ) {
                questionCorrectAnswer = it
            }
            BasicText(text = "First Wrong Answer", fontSize = 22)
            BasicEditText(
                text = questionWrongAnswer1,
                errorMessage = if (displayErrorMessageIfNecessary && questionWrongAnswer1.isEmpty()) "This field is required" else ""
            ) {
                questionWrongAnswer1 = it
            }
            BasicText(text = "Second Wrong Answer", fontSize = 22)
            BasicEditText(
                text = questionWrongAnswer2,
                errorMessage = if (displayErrorMessageIfNecessary && questionWrongAnswer2.isEmpty()) "This field is required" else ""
            ) {
                questionWrongAnswer2 = it
            }
            BasicText(text = "Third Wrong Answer", fontSize = 22)
            BasicEditText(
                text = questionWrongAnswer3,
                errorMessage = if (displayErrorMessageIfNecessary && questionWrongAnswer3.isEmpty()) "This field is required" else ""
            ) {
                questionWrongAnswer3 = it
            }
            Column {
                BasicText(
                    text = "Bible verse with the answer",
                    fontSize = 22
                )
                BasicText(
                    text = stringResource(R.string.question_suggestion_bible_verse_details),
                    fontSize = 12,
                    fontColor = darkGray
                )
            }
            BasicEditText(
                text = bibleVerse,
                errorMessage = if (displayErrorMessageIfNecessary && bibleVerse.isEmpty()) "This field is required" else ""
            ) {
                bibleVerse = it
            }
            BasicText(
                text = "Select Difficulty",
                fontSize = 22
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOfRadioButtonDifficultyOptions.onEach {
                    BasicRadioButton(selected = it == selectedOption, updateRadioButton = {
                        onOptionSelected(it)
                    }) {
                        BasicText(modifier = Modifier.padding(16.dp), text = it)
                    }
                }
            }
            Column {
                BasicText(
                    text = "Your name",
                    fontSize = 22
                )
                BasicText(
                    text = stringResource(R.string.question_suggestion_created_by_details),
                    fontSize = 12,
                    fontColor = darkGray
                )
            }
            BasicEditText(text = questionCreator) {
                questionCreator = it
            }
            SendButton {
                if (requiredEditTextAreNotEmpty(
                        questionText,
                        questionCorrectAnswer,
                        questionWrongAnswer1,
                        questionWrongAnswer2,
                        questionWrongAnswer3,
                        bibleVerse
                    )
                ) {
                    sendSuggestion(
                        Question(
                            question = questionText,
                            listOfAnswers = listOf(
                                Answer(answerText = questionCorrectAnswer, correct = true),
                                Answer(answerText = questionWrongAnswer1, correct = false),
                                Answer(answerText = questionWrongAnswer2, correct = false),
                                Answer(answerText = questionWrongAnswer3, correct = false)
                            ),
                            bibleVerse = bibleVerse,
                            difficulty = QuestionDifficulty.valueOf(selectedOption.uppercase()),
                            createdBy = questionCreator
                        )
                    )
                } else {
                    displayErrorMessageIfNecessary = true
                }
            }
        }
        if (feedbackMessage != FeedbackMessage.NoMessage) {
            FeedbackMessage(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                errorMessage = feedbackMessage
            )
        }
    }
}

fun requiredEditTextAreNotEmpty(
    questionText: String,
    questionCorrectAnswer: String,
    questionWrongAnswer1: String,
    questionWrongAnswer2: String,
    questionWrongAnswer3: String,
    bibleVerse: String
): Boolean =
    questionText.isNotEmpty() && questionCorrectAnswer.isNotEmpty() && questionWrongAnswer1.isNotEmpty() && questionWrongAnswer2.isNotEmpty() && questionWrongAnswer3.isNotEmpty() && bibleVerse.isNotEmpty()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SendButton(sendQuestion: () -> Unit) {

    val haptic = LocalHapticFeedback.current

    var tapStarted by remember {
        mutableStateOf(false)
    }

    var localDensity = LocalDensity.current

    var heightForLoaderView by remember {
        mutableStateOf(0.dp)
    }

    val animateSendingQuestion by animateAlpha(condition = !tapStarted)

    LaunchedEffect(animateSendingQuestion) {
        if (animateSendingQuestion == 1f) {
            sendQuestion()
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    BasicContainer(modifier = Modifier
        .fillMaxWidth()
        .pointerInteropFilter {
            if (it.action == MotionEvent.ACTION_DOWN) {
                tapStarted = true
            }
            if (it.action == MotionEvent.ACTION_CANCEL) {
                tapStarted = false
            }
            if (it.action == MotionEvent.ACTION_UP) {
                tapStarted = false
            }
            true
        }
        .onGloballyPositioned {
            heightForLoaderView = with(localDensity) {
                it.size.height.toDp()
            }
        }) {
        Box(
            modifier = Modifier
                .height(heightForLoaderView)
                .fillMaxWidth(animateSendingQuestion)
                .background(gray)
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                modifier = Modifier.align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.baseline_send_24),
                contentDescription = null
            )
            BasicText(text = "Send question (Hold)", fontSize = 22)
        }

    }
}

@Composable
fun BasicRadioButton(
    selected: Boolean,
    updateRadioButton: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val colorAnimation by animateColor(
        condition = !selected,
        startValue = gray,
        endValue = closeToBlack
    )

    Box(
        modifier = Modifier
            .border(
                width = 2.dp, color = colorAnimation, shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                updateRadioButton()
            })
    {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuestionSuggestion() {
    SuggestQuestionScreen(FeedbackMessage.NoMessage) {

    }
}