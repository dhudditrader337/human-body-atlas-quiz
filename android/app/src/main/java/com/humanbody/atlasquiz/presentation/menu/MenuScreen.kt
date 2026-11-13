package com.humanbody.atlasquiz.presentation.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.di.ServiceLocator
import com.humanbody.atlasquiz.core.theme.*
import com.humanbody.atlasquiz.core.ui.*
import com.humanbody.atlasquiz.presentation.app.UiEvent

@Composable
fun MenuRoute(onEvent: (UiEvent) -> Unit) {
    val vm: MenuViewModel = viewModel(factory = ServiceLocator.factory())
    val state by vm.uiState.collectAsStateWithLifecycle()
    ObserveEvents(vm.events, onEvent)
    MenuScreen(state, vm::selectSystem, vm::play, vm::explore, vm::settings, vm::retry, vm::result)
}

@Composable
fun MenuScreen(state: MenuUiState, onSelect: (String) -> Unit, onPlay: () -> Unit, onExplore: () -> Unit,
    onSettings: () -> Unit, onRetry: () -> Unit, onResult: (String) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val scrollAll = maxHeight < 640.dp || LocalDensity.current.fontScale > 1.3f
        AtlasBackground(AtlasBackdrop.Menu)
        Column(Modifier.align(Alignment.TopCenter).safeDrawingPadding().widthIn(max = 720.dp).fillMaxSize().padding(horizontal = 20.dp)) {
            Column(if (scrollAll) Modifier.weight(1f).verticalScroll(rememberScrollState()) else Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.atlas_edition), style = MaterialTheme.typography.labelSmall, color = DeepBlue)
                Text(stringResource(R.string.menu_title), style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { heading() })
                TextButton(onSettings, Modifier.heightIn(min = 48.dp).align(Alignment.End)) { Text(stringResource(R.string.settings_action)) }
                AtlasSurface {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.hero_title), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        Image(painterResource(R.drawable.logo), stringResource(R.string.logo_description), Modifier.size(84.dp), contentScale = ContentScale.Fit)
                    }
                    Text(stringResource(R.string.hero_body), style = MaterialTheme.typography.bodyLarge, color = DeepBlue)
                }
                val best = state.progress.bestScores.values.maxOrNull() ?: 0
                val exploredCount = state.systems.count { system -> system.parts.all { it.id in state.progress.exploredParts } }
                if (best > 0 && exploredCount > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(stringResource(R.string.best_accuracy), stringResource(R.string.percent_format, best), Modifier.weight(1f))
                        StatCard(stringResource(R.string.systems_explored), stringResource(R.string.stat_value, exploredCount), Modifier.weight(1f))
                    }
                } else Text(stringResource(R.string.first_hint), style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
                if (state.loading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    StatusMessage(stringResource(R.string.opening_systems))
                }
                if (state.error) {
                    StatusMessage(stringResource(R.string.storage_error), true)
                    SecondaryButton(R.string.retry, onRetry)
                }
                Text(stringResource(R.string.choose_system), style = MaterialTheme.typography.titleMedium)
                if (state.systems.isEmpty()) EmptyState(R.string.content_error, R.string.retry, onRetry)
                state.systems.forEachIndexed { index, system ->
                    SystemCard(system.id, index + 1, state.settings.selectedSystem == system.id,
                        system.parts.all { it.id in state.progress.exploredParts }, { onSelect(system.id) })
                }
                if (state.progress.recentResults.isNotEmpty()) {
                    Text(stringResource(R.string.recent_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.attempts_format, state.progress.attempts), style = MaterialTheme.typography.bodyMedium)
                    state.progress.recentResults.take(3).forEach { result ->
                        AtlasSurface {
                            Text(stringResource(R.string.recent_result, stringResource(systemName(result.systemId)), result.accuracy), style = MaterialTheme.typography.titleMedium)
                            SecondaryButton(R.string.view_results, { onResult(result.roundId) })
                        }
                    }
                }
                if (scrollAll) MenuActions(state, onExplore, onPlay)
                Spacer(Modifier.height(12.dp))
            }
            if (!scrollAll) MenuActions(state, onExplore, onPlay)
        }
    }
}

@Composable
private fun MenuActions(state: MenuUiState, onExplore: () -> Unit, onPlay: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.selected_system, stringResource(systemName(state.settings.selectedSystem))),
            style = MaterialTheme.typography.titleMedium, color = DeepBlue)
        Text(stringResource(difficultySummary(state.settings.difficulty)), style = MaterialTheme.typography.bodyMedium)
        SecondaryButton(R.string.explore, onExplore, enabled = state.systems.isNotEmpty())
        PrimaryButton(R.string.play, onPlay, enabled = !state.loading && state.systems.isNotEmpty())
    }
}
