package com.bsoftwares.thebiblequiz.ui.screens.games.quiz.screens

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.models.quiz.Answer
import com.bsoftwares.thebiblequiz.data.models.quiz.Question
import com.bsoftwares.thebiblequiz.data.models.quiz.QuestionDifficulty
import com.bsoftwares.thebiblequiz.data.models.quiz.getName
import com.bsoftwares.thebiblequiz.data.models.state.FeedbackMessage
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicContainer
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicEditText
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicScreenBox
import com.bsoftwares.thebiblequiz.ui.basicviews.BasicText
import com.bsoftwares.thebiblequiz.ui.basicviews.animateAlpha
import com.bsoftwares.thebiblequiz.ui.basicviews.animateColor
import com.bsoftwares.thebiblequiz.ui.theme.appBackground
import com.bsoftwares.thebiblequiz.ui.theme.darkGray
import com.bsoftwares.thebiblequiz.ui.theme.emptyString
import com.bsoftwares.thebiblequiz.ui.theme.gray
import com.bsoftwares.thebiblequiz.viewmodel.BibleQuizViewModel

@Composable
fun InitSuggestQuestionScreen(viewModel: BibleQuizViewModel) {
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    BasicScreenBox(feedbackMessage = feedbackMessage) {
        SuggestQuestionScreen(feedbackMessage = feedbackMessage) {
            viewModel.sendQuestionSuggestion(it)
        }
    }
}

@Composable
fun SuggestQuestionScreen(feedbackMessage: FeedbackMessage, sendSuggestion: (Question) -> Unit) {

    val listOfRadioButtonDifficultyOptions = QuestionDifficulty.values()
    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            listOfRadioButtonDifficultyOptions[0]
        )
    }

    var displayErrorMessageIfNecessary by remember {
        mutableStateOf(false)
    }

    var questionText by remember {
        mutableStateOf(emptyString)
    }

    var questionCorrectAnswer by remember {
        mutableStateOf(emptyString)
    }

    var questionWrongAnswer1 by remember {
        mutableStateOf(emptyString)
    }

    var questionWrongAnswer2 by remember {
        mutableStateOf(emptyString)
    }

    var questionWrongAnswer3 by remember {
        mutableStateOf(emptyString)
    }

    var bibleVerse by remember {
        mutableStateOf(emptyString)
    }

    var questionCreator by remember {
        mutableStateOf(emptyString)
    }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage == FeedbackMessage.QuestionSuggestionSent) {
            questionText = emptyString
            questionCorrectAnswer = emptyString
            questionWrongAnswer1 = emptyString
            questionWrongAnswer2 = emptyString
            questionWrongAnswer3 = emptyString
            bibleVerse = emptyString
            questionCreator = emptyString
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            BasicText(text = stringResource(R.string.suggest_a_question), fontSize = 36)
            BasicText(text = stringResource(R.string.question_text), fontSize = 22)
            BasicEditText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp), text = questionText,
                errorMessage = if (displayErrorMessageIfNecessary && questionText.isEmpty()) stringResource(
                    R.string.this_field_is_required
                ) else emptyString
            ) {
                questionText = it
            }
            BasicText(text = stringResource(R.string.correct_answer_text), fontSize = 22)
            BasicEditText(
                text = questionCorrectAnswer,
                errorMessage = if (displayErrorMessageIfNecessary && questionCorrectAnswer.isEmpty()) stringResource(
                    R.string.this_field_is_required
                ) else emptyString
            ) {
                questionCorrectAnswer = it
            }
            BasicText(text = stringResource(R.string.first_wrong_answer), fontSize = 22)
            BasicEditText(
                text = questionWrongAnswer1,
                errorMessage = if (displayErrorMessageIfNecessary && questionWrongAnswer1.isEmpty()) stringResource(
                    R.string.this_field_is_required
                ) else emptyString
            ) {
                questionWrongAnswer1 = it
            }
            BasicText(text = stringResource(R.string.second_wrong_answer), fontSize = 22)
            BasicEditText(
                text = questionWrongAnswer2,
                errorMessage = if (displayErrorMessageIfNecessary && questionWrongAnswer2.isEmpty()) stringResource(
                    R.string.this_field_is_required
                ) else emptyString
            ) {
                questionWrongAnswer2 = it
            }
            BasicText(text = stringResource(R.string.third_wrong_answer), fontSize = 22)
            BasicEditText(
                text = questionWrongAnswer3,
                errorMessage = if (displayErrorMessageIfNecessary && questionWrongAnswer3.isEmpty()) stringResource(
                    R.string.this_field_is_required
                ) else emptyString
            ) {
                questionWrongAnswer3 = it
            }
            Column {
                BasicText(
                    text = stringResource(R.string.bible_verse_with_the_answer),
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
                errorMessage = if (displayErrorMessageIfNecessary && bibleVerse.isEmpty()) stringResource(
                    R.string.this_field_is_required
                ) else emptyString
            ) {
                bibleVerse = it
            }
            BasicText(
                text = stringResource(R.string.select_difficulty),
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
                        BasicText(modifier = Modifier.padding(16.dp), text = it.getName())
                    }
                }
            }
            Column {
                BasicText(
                    text = stringResource(R.string.your_name),
                    fontSize = 22
                )
                BasicText(
                    text = stringResource(R.string.question_suggestion_created_by_details),
                    fontSize = 12,
                    fontColor = darkGray
                )
            }
            BasicEditText(text = questionCreator, keyboardOption = KeyboardOptions(imeAction = ImeAction.Done)) {
                questionCreator = it
            }
            ButtonWithHold(modifier = Modifier.fillMaxWidth(),holdAction = {
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
                            difficulty = QuestionDifficulty.valueOf(selectedOption.name),
                            createdBy = questionCreator
                        )
                    )
                } else {
                    displayErrorMessageIfNecessary = true
                }
            }) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        painter = painterResource(id = R.drawable.baseline_send_24),
                        contentDescription = null
                    )
                    BasicText(text = stringResource(R.string.send_question_hold), fontSize = 22)
                }
            }
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
fun ButtonWithHold(modifier: Modifier = Modifier,holdAction: () -> Unit, content: @Composable BoxScope.() -> Unit) {

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
            holdAction()
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    BasicContainer(modifier = modifier
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
        content()
    }
}

@Composable
fun BasicRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    updateRadioButton: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val colorAnimation by animateColor(
        condition = !selected,
        startValue = appBackground(),
        endValue = gray
    )

    Box(
        modifier = modifier
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