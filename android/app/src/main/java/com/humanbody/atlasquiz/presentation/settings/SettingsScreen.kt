package com.humanbody.atlasquiz.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.di.ServiceLocator
import com.humanbody.atlasquiz.core.theme.DeepBlue
import com.humanbody.atlasquiz.core.ui.*
import com.humanbody.atlasquiz.domain.model.Difficulty
import com.humanbody.atlasquiz.presentation.app.UiEvent
import com.humanbody.atlasquiz.presentation.dialog.ResetProgressDialog

@Composable
fun SettingsRoute(onEvent: (UiEvent) -> Unit) {
    val vm: SettingsViewModel = viewModel(factory = ServiceLocator.factory())
    val state by vm.uiState.collectAsStateWithLifecycle()
    ObserveEvents(vm.events, onEvent)
    SettingsScreen(state, vm::difficulty, vm::haptics, vm::motion, vm::requestReset, vm::save, vm::reset, vm::back)
    if (state.resetDialog) ResetProgressDialog(vm::cancelReset, vm::reset)
}

@Composable
fun SettingsScreen(state: SettingsUiState, onDifficulty: (Difficulty) -> Unit, onHaptics: (Boolean) -> Unit,
    onMotion: (Boolean) -> Unit, onReset: () -> Unit, onRetry: () -> Unit, onRetryReset: () -> Unit, onBack: () -> Unit) {
    val enabled = state.status != SettingsStatus.Saving
    AtlasPage(AtlasBackdrop.Settings) {
        ScreenHeader(stringResource(R.string.settings_title), onBack)
        AtlasSurface {
            Text(stringResource(R.string.difficulty_title), style = MaterialTheme.typography.titleLarge)
            Difficulty.entries.forEach { difficulty ->
                val selected = state.settings.difficulty == difficulty
                val description = stringResource(if (selected) R.string.selected else if (!enabled) R.string.unavailable else R.string.available)
                Row(Modifier.fillMaxWidth().heightIn(min = 64.dp)
                    .selectable(selected, enabled = enabled, role = Role.RadioButton, onClick = { onDifficulty(difficulty) })
                    .semantics { stateDescription = description }.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RadioButton(selected, onClick = null, enabled = enabled)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(difficultyName(difficulty)), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(difficultySummary(difficulty)), style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
                    }
                }
            }
        }
        AtlasSurface {
            Text(stringResource(R.string.comfort_title), style = MaterialTheme.typography.titleLarge)
            ComfortToggle(R.string.haptics_title, R.string.haptics_body, state.settings.haptics, enabled, onHaptics)
            HorizontalDivider()
            ComfortToggle(R.string.motion_title, R.string.motion_body, state.settings.reducedMotion, enabled, onMotion)
        }
        if (state.status != SettingsStatus.Ready) {
            StatusMessage(stringResource(when (state.status) {
                SettingsStatus.Saving -> R.string.settings_saving
                SettingsStatus.Saved -> R.string.settings_saved
                SettingsStatus.Cleared -> R.string.progress_cleared
                SettingsStatus.ResetError -> R.string.reset_error
                SettingsStatus.ReadError -> R.string.settings_read_error
                else -> R.string.settings_error
            }), state.status in listOf(SettingsStatus.Error, SettingsStatus.ResetError, SettingsStatus.ReadError))
            if (state.status == SettingsStatus.Saving) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (state.status == SettingsStatus.Error || state.status == SettingsStatus.ReadError) SecondaryButton(R.string.retry_save, onRetry)
            if (state.status == SettingsStatus.ResetError) SecondaryButton(R.string.retry, onRetryReset)
        }
        AtlasSurface {
            Text(stringResource(R.string.local_title), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.local_body), style = MaterialTheme.typography.bodyLarge)
            SecondaryButton(R.string.reset_progress, onReset, enabled = enabled)
        }
    }
}

@Composable
private fun ComfortToggle(title: Int, body: Int, checked: Boolean, enabled: Boolean, onChange: (Boolean) -> Unit) {
    val state = stringResource(if (checked) R.string.on_state else R.string.off_state)
    val description = stringResource(title)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(body), style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
        Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(state, style = MaterialTheme.typography.bodyLarge)
            Switch(checked, onCheckedChange = onChange, enabled = enabled,
                modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp).semantics {
                    stateDescription = state
                    contentDescription = description
                })
        }
    }
}
