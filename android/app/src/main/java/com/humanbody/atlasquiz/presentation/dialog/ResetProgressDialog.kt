package com.humanbody.atlasquiz.presentation.dialog

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.humanbody.atlasquiz.R

@Composable
fun ResetProgressDialog(onCancel: () -> Unit, onReset: () -> Unit) {
    AlertDialog(onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.reset_title)) },
        text = { Text(stringResource(R.string.reset_message)) },
        confirmButton = { TextButton(onReset) { Text(stringResource(R.string.reset)) } },
        dismissButton = { TextButton(onCancel) { Text(stringResource(R.string.cancel)) } })
}
