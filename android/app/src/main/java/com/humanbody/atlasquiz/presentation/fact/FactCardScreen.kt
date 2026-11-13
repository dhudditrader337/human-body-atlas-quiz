package com.humanbody.atlasquiz.presentation.fact

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.di.ServiceLocator
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.core.theme.DeepBlue
import com.humanbody.atlasquiz.core.ui.*
import com.humanbody.atlasquiz.presentation.app.UiEvent

@Composable
fun FactCardRoute(route: Screen.FactCard, onEvent: (UiEvent) -> Unit) {
    val vm: FactCardViewModel = viewModel(key = "fact:${route.visitId}", factory = ServiceLocator.factory(route))
    val state by vm.uiState.collectAsStateWithLifecycle()
    ObserveEvents(vm.events, onEvent)
    FactCardScreen(state, { vm.move(-1) }, { vm.move(1) }, vm::play, vm::back, vm::saveVisit)
}

@Composable
fun FactCardScreen(state: FactCardUiState, onPrevious: () -> Unit, onNext: () -> Unit, onPlay: () -> Unit, onBack: () -> Unit, onRetry: () -> Unit) {
    val headingFocus = remember { FocusRequester() }
    AtlasPage(AtlasBackdrop.Fact) {
        val system = state.system
        val part = system?.parts?.getOrNull(state.index)
        ScreenHeader(stringResource(R.string.fact_ordinal, (state.index + 1).coerceAtLeast(1), system?.parts?.size ?: 5), onBack)
        if (system == null || part == null) EmptyState(R.string.fact_error, R.string.back, onBack)
        else {
            val description = stringResource(R.string.fact_diagram_description, stringResource(systemName(system.id)))
            AtlasSurface {
                AnatomyDrawing(system.id, Modifier.fillMaxWidth().height(190.dp).semantics { contentDescription = description }, listOf(part), part.id)
                Text(stringResource(systemName(system.id)), style = MaterialTheme.typography.labelSmall, color = DeepBlue)
                Text(stringResource(partName(part.id)), style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.focusRequester(headingFocus).focusable().semantics { heading() })
                Text(stringResource(R.string.where_find), style = MaterialTheme.typography.labelSmall, color = DeepBlue)
                Text(stringResource(partLocation(part.id)), style = MaterialTheme.typography.bodyLarge)
                HorizontalDivider()
                Text(stringResource(partFact(part.id)), style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 28.sp))
            }
            LaunchedEffect(part.id) { headingFocus.requestFocus() }
            StatusMessage(stringResource(when { state.saving -> R.string.save_loading; state.error -> R.string.fact_save_error; else -> R.string.fact_saved }), state.error)
            if (state.saving) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (state.error) SecondaryButton(R.string.retry, onRetry)
            SecondaryButton(R.string.previous, onPrevious, enabled = state.index > 0 && !state.saving)
            SecondaryButton(R.string.next, onNext, enabled = state.index < system.parts.lastIndex && !state.saving)
            if (state.index == system.parts.lastIndex) Text(stringResource(R.string.last_fact), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.fact_quiz_hint), style = MaterialTheme.typography.bodyLarge)
            PrimaryButton(R.string.play, onPlay)
        }
    }
}
