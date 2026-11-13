package com.humanbody.atlasquiz.presentation.diagram

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.di.ServiceLocator
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.core.theme.DeepBlue
import com.humanbody.atlasquiz.core.ui.*
import com.humanbody.atlasquiz.presentation.app.UiEvent

@Composable
fun DiagramRoute(route: Screen.Diagram, onEvent: (UiEvent) -> Unit) {
    val vm: DiagramViewModel = viewModel(key = "diagram:${route.systemId}", factory = ServiceLocator.factory(route))
    val state by vm.uiState.collectAsStateWithLifecycle()
    ObserveEvents(vm.events, onEvent)
    DiagramScreen(state, vm::openFact, vm::play, vm::back)
}

@Composable
fun DiagramScreen(state: DiagramUiState, onFact: (String) -> Unit, onPlay: () -> Unit, onBack: () -> Unit) {
    AtlasPage(AtlasBackdrop.Diagram) {
        ScreenHeader(state.system?.let { stringResource(systemName(it.id)) } ?: stringResource(R.string.diagram_title), onBack)
        val system = state.system
        if (system == null) EmptyState(R.string.system_error, R.string.back, onBack)
        else {
            StatusMessage(stringResource(R.string.explored_count, system.parts.count { it.id in state.explored }, system.parts.size))
            Text(stringResource(R.string.diagram_hint), style = MaterialTheme.typography.bodyLarge)
            DiagramBoard(system.id, system.parts, onFact, revealed = true, selectedPart = state.selectedPart, explored = state.explored)
            Text(stringResource(R.string.diagram_note), style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
            system.parts.forEachIndexed { index, part ->
                val explored = part.id in state.explored
                val description = stringResource(if (explored) R.string.explored else R.string.not_explored)
                OutlinedButton({ onFact(part.id) }, Modifier.fillMaxWidth().heightIn(min = 56.dp).semantics { stateDescription = description }) {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(R.string.system_format, index + 1, stringResource(partName(part.id))), style = MaterialTheme.typography.titleMedium)
                        Text(description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Text(stringResource(R.string.ready_quiz), style = MaterialTheme.typography.bodyLarge)
            PrimaryButton(R.string.play, onPlay)
        }
    }
}
