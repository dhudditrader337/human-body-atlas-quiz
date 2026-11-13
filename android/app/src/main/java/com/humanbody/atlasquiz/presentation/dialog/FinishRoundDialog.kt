package com.humanbody.atlasquiz.presentation.dialog

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.humanbody.atlasquiz.R

@Composable
fun FinishRoundDialog(onCancel: () -> Unit, onFinish: () -> Unit) {
    AlertDialog(onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.finish_title)) },
        text = { Text(stringResource(R.string.finish_message)) },
        confirmButton = { TextButton(onFinish) { Text(stringResource(R.string.finish)) } },
        dismissButton = { TextButton(onCancel) { Text(stringResource(R.string.keep_learning)) } })
}

@Composable
fun PauseDialog(onResume: () -> Unit, onMenu: () -> Unit) {
    AlertDialog(onDismissRequest = onResume,
        title = { Text(stringResource(R.string.pause_title)) },
        text = { Text(stringResource(R.string.pause_message)) },
        confirmButton = { TextButton(onResume) { Text(stringResource(R.string.resume)) } },
        dismissButton = { TextButton(onMenu) { Text(stringResource(R.string.menu)) } })
}
