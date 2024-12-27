package com.bsoftwares.thebiblequiz.ui.basicviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import com.bsoftwares.thebiblequiz.R

@Composable
fun BasicDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(.85f)
        Column(Modifier.verticalScroll(rememberScrollState())) {
            content()
        }
    }
}

@Composable
fun BasicPositiveNegativeDialog(onDismissRequest: () -> Unit,
                                title: String = stringResource(R.string.warning),
                                dismissible : Boolean = true, // When this is false, you have to update the Dialog on the positive and negative functions
                                dialogIcon: Painter? = painterResource(id = R.drawable.warning_24dp),
                                description: String = stringResource(R.string.emptyString),
                                positiveString: String = stringResource(R.string.yes),
                                negativeString: String? = stringResource(R.string.no),
                                positiveIcon: Painter = painterResource(id = R.drawable.baseline_check_24_bw),
                                negativeIcon: Painter = painterResource(id = R.drawable.baseline_close_24_bw),
                                positiveFunction: () -> Unit = {},
                                negativeFunction: () -> Unit = onDismissRequest,
                                extraUI: @Composable () -> Unit = {}) {
    Dialog(onDismissRequest = { if (dismissible) onDismissRequest() else Unit }) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(.85f)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BasicContainer(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (dialogIcon != null) {
                            Image(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.CenterVertically),
                                painter = dialogIcon,
                                colorFilter = ColorFilter.tint(colorResource(R.color.contrast_color)),
                                contentDescription = null
                            )
                        }
                        BasicText(modifier = Modifier.align(Alignment.CenterVertically), text = title, fontSize = 22)
                    }
                    if (description.isNotEmpty()) {
                        BasicText(text = description)
                    }
                    //It will display extra views
                    extraUI()
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (negativeString != null) {
                    BasicContainer(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                        onClick = {
                            onDismissRequest()
                            negativeFunction()
                        }
                    ) {

                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {

                            Image(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterVertically),
                                painter = negativeIcon,
                                contentDescription = null
                            )

                            BasicText(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 8.dp),
                                text = negativeString,
                            )
                        }
                    }
                }

                BasicContainer(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    onClick = {
                        onDismissRequest()
                        positiveFunction()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {

                        Image(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterVertically),
                            painter = positiveIcon,
                            contentDescription = null
                        )

                        BasicText(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp),
                            text = positiveString,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TestBasicDialog() {
    BasicPositiveNegativeDialog(onDismissRequest = {} , title = "Warning", description = "Test description")
}