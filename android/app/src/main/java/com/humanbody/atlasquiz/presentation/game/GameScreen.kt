package com.humanbody.atlasquiz.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.di.ServiceLocator
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.core.theme.*
import com.humanbody.atlasquiz.core.ui.*
import com.humanbody.atlasquiz.domain.model.CompletionReason
import com.humanbody.atlasquiz.domain.model.Difficulty
import com.humanbody.atlasquiz.presentation.app.UiEvent
import com.humanbody.atlasquiz.presentation.dialog.*
import kotlin.math.roundToInt

private data class DragLabel(val id: String, val position: Offset)

@Composable
fun GameRoute(route: Screen.Game, onEvent: (UiEvent) -> Unit) {
    val vm: GameViewModel = viewModel(key = "game:${route.roundId}", factory = ServiceLocator.factory(route))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val owner = LocalLifecycleOwner.current
    ObserveEvents(vm.events, onEvent)
    DisposableEffect(vm, owner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> vm.resume()
                Lifecycle.Event.ON_STOP -> vm.pause()
                else -> Unit
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer); vm.pause() }
    }
    GameScreen(state, vm::selectLabel, vm::target, vm::place, vm::clearSelection, vm::requestFinish,
        vm::showPause, vm::results, vm::back, vm::menu, vm::retry)
    if (state.finishDialog) FinishRoundDialog(vm::dismissFinish, vm::finishEarly)
    if (state.pauseDialog) PauseDialog(vm::dismissPause, vm::menu)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameScreen(state: GameUiState, onSelect: (String) -> Unit, onTarget: (String) -> Unit,
    onPlace: (String, String) -> Unit, onClear: () -> Unit, onFinish: () -> Unit, onPause: () -> Unit,
    onResults: () -> Unit, onBack: () -> Unit, onMenu: () -> Unit, onRetry: () -> Unit) {
    val bounds = remember { mutableMapOf<String, Rect>() }
    var drag by remember { mutableStateOf<DragLabel?>(null) }
    var rootOrigin by remember { mutableStateOf(Offset.Zero) }
    Box(Modifier.fillMaxSize().onGloballyPositioned { rootOrigin = it.positionInRoot() }) {
        AtlasPage(AtlasBackdrop.Game) {
            ScreenHeader(stringResource(R.string.game_title), onBack, state.system?.let { stringResource(systemName(it.id)) })
            val round = state.round
            val system = state.system
            if (round == null || system == null) {
                EmptyState(R.string.game_error, R.string.try_again, onRetry)
                SecondaryButton(R.string.menu, onMenu)
            } else {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.placed_count, state.correctCount, round.targets.size), style = MaterialTheme.typography.titleMedium,
                        color = DeepBlue, modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite })
                    TextButton(onPause, enabled = state.phase == GamePhase.Ready) {
                        Image(painterResource(R.drawable.icon_pause), null, Modifier.size(22.dp), contentScale = ContentScale.Fit)
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.pause_action))
                    }
                }
                LinearProgressIndicator(progress = { state.correctCount.toFloat() / round.targets.size }, Modifier.fillMaxWidth(), color = Primary, trackColor = OceanLine)
                DiagramBoard(system.id, round.targets, onTarget, submissions = round.submissions,
                    revealed = state.phase == GamePhase.Finished, enabled = state.phase == GamePhase.Ready, review = state.phase == GamePhase.Finished,
                    onTargetBounds = { id, rect -> bounds[id] = rect })
                Text(stringResource(R.string.diagram_note), style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
                if (state.phase != GamePhase.Finished && state.correctCount > 0) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        round.targets.forEachIndexed { index, part ->
                            if (round.submissions[part.id] == part.id) {
                                Text(stringResource(R.string.correct_target, index + 1, stringResource(partName(part.id))),
                                    style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
                            }
                        }
                    }
                }
                if (state.phase == GamePhase.Finished) {
                    StatusMessage(stringResource(R.string.finished_hint))
                    round.targets.forEachIndexed { index, part ->
                        Text(stringResource(R.string.target_answer, index + 1, stringResource(partName(part.id))), style = MaterialTheme.typography.bodyLarge)
                    }
                    PrimaryButton(R.string.results_action, onResults)
                    SecondaryButton(R.string.menu, onMenu)
                } else {
                    AtlasSurface {
                        Text(stringResource(R.string.label_bank), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.quiz_instructions), style = MaterialTheme.typography.bodyMedium)
                        if (round.difficulty == Difficulty.Beginner) Text(stringResource(R.string.beginner_clue), style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            round.labels.forEach { part ->
                                val placed = part.id in round.submissions.values
                                LabelButton(system.id, part, state.selectedLabel == part.id, state.phase == GamePhase.Ready && !placed, placed,
                                    clue = round.difficulty == Difficulty.Beginner,
                                    onSelect = { onSelect(part.id) },
                                    onDragStart = { drag = DragLabel(part.id, it) },
                                    onDrag = { delta -> drag = drag?.let { it.copy(position = it.position + delta) } },
                                    onDrop = {
                                        val active = drag
                                        if (active != null) bounds.entries.firstOrNull { it.value.contains(active.position) }?.let { onPlace(active.id, it.key) }
                                        drag = null
                                    }, onCancel = { drag = null })
                            }
                        }
                        val status = when {
                            state.result?.reason == CompletionReason.Mismatch -> stringResource(R.string.incorrect_feedback)
                            state.result?.reason == CompletionReason.Matched -> stringResource(R.string.complete_feedback)
                            state.selectedLabel != null -> stringResource(R.string.selected_label, stringResource(partName(state.selectedLabel)))
                            state.lastCorrect != null -> stringResource(R.string.correct_feedback, stringResource(partName(state.lastCorrect)), state.correctCount, round.targets.size)
                            else -> stringResource(R.string.select_instruction)
                        }
                        StatusMessage(status, state.result?.reason == CompletionReason.Mismatch)
                        if (state.selectedLabel != null && round.difficulty == Difficulty.Beginner) {
                            Text(stringResource(partLocation(state.selectedLabel)), style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
                        }
                        SecondaryButton(R.string.clear_selection, onClear, enabled = state.selectedLabel != null && state.phase == GamePhase.Ready)
                        SecondaryButton(R.string.finish_round, onFinish, enabled = state.phase == GamePhase.Ready)
                    }
                }
            }
        }
        drag?.let { active ->
            Surface(Modifier.zIndex(10f).offset { IntOffset((active.position.x-rootOrigin.x-60).roundToInt(), (active.position.y-rootOrigin.y-75).roundToInt()) }
                .widthIn(max = 220.dp), color = DeepBlue, shape = MaterialTheme.shapes.medium, shadowElevation = 8.dp) {
                Text(stringResource(partLabel(active.id)), color = Background, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp))
            }
        }
    }
}
