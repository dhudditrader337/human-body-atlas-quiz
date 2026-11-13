package com.humanbody.atlasquiz.presentation.gameover

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.di.ServiceLocator
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.core.theme.*
import com.humanbody.atlasquiz.core.ui.*
import com.humanbody.atlasquiz.domain.model.*
import com.humanbody.atlasquiz.presentation.app.UiEvent

@Composable
fun GameOverRoute(route: Screen.GameOver, onEvent: (UiEvent) -> Unit) {
    val vm: GameOverViewModel = viewModel(key = "result:${route.result.roundId}", factory = ServiceLocator.factory(route))
    val state by vm.uiState.collectAsStateWithLifecycle()
    ObserveEvents(vm.events, onEvent)
    GameOverScreen(state, vm::playAgain, vm::menu, vm::readFact, vm::save, vm::back)
}

@Composable
fun GameOverScreen(state: GameOverUiState, onAgain: () -> Unit, onMenu: () -> Unit,
    onFact: (String) -> Unit, onRetry: () -> Unit, onBack: () -> Unit) {
    val focus = remember { FocusRequester() }
    var entered by remember { mutableStateOf(false) }
    val motion = LocalAtlasMotion.current
    val scale by animateFloatAsState(if (entered || !motion) 1f else .96f,
        spring(dampingRatio = .85f, stiffness = 400f), label = "scoreReveal")
    LaunchedEffect(Unit) {
        entered = true
        if (state.result != null && state.system != null) focus.requestFocus()
    }
    AtlasPage(AtlasBackdrop.Result) {
        ScreenHeader(stringResource(R.string.results_title), onBack)
        val result = state.result
        if (result == null || state.system == null) EmptyState(R.string.result_missing, R.string.menu, onMenu)
        else {
            AtlasSurface(Modifier.graphicsLayer { scaleX = scale; scaleY = scale }) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.review_badge), style = MaterialTheme.typography.labelSmall, color = DeepBlue)
                    Box(Modifier.size(152.dp), contentAlignment = Alignment.Center) {
                        Canvas(Modifier.fillMaxSize()) {
                            val stroke = 7.dp.toPx()
                            drawCircle(OceanSurface, size.minDimension / 2 - stroke, style = Stroke(stroke))
                            drawArc(Primary, -90f, 360f * result.accuracy / 100f, false,
                                topLeft = Offset(stroke, stroke), size = androidx.compose.ui.geometry.Size(size.width - 2 * stroke, size.height - 2 * stroke),
                                style = Stroke(stroke, cap = StrokeCap.Round))
                        }
                        Image(painterResource(R.drawable.logo), null, Modifier.size(88.dp), contentScale = ContentScale.Fit)
                    }
                    Text(stringResource(if (result.correctCount == result.totalCount) R.string.success_title else R.string.review_title),
                        style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center,
                        modifier = Modifier.focusRequester(focus).focusable().semantics { heading() })
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }) {
                        Text(stringResource(R.string.labels_score, result.correctCount, result.totalCount), style = MaterialTheme.typography.displaySmall, color = DeepBlue)
                        Text(stringResource(R.string.accuracy_format, result.accuracy), style = MaterialTheme.typography.titleMedium)
                    }
                    Text(stringResource(when (result.reason) {
                        CompletionReason.Matched -> R.string.reason_matched
                        CompletionReason.Mismatch -> R.string.reason_mismatch
                        CompletionReason.FinishedEarly -> R.string.reason_early
                    }), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
                    if (state.newBest) Text(stringResource(R.string.new_best), style = MaterialTheme.typography.titleMedium, color = DeepBlue)
                    if (state.previousBest > 0) Text(stringResource(R.string.previous_best, state.previousBest), style = MaterialTheme.typography.bodyMedium)
                }
            }
            StatusMessage(stringResource(when { state.saving -> R.string.result_saving; state.error -> R.string.result_save_error; else -> R.string.result_saved }), state.error)
            if (state.saving) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (state.error) SecondaryButton(R.string.retry_save, onRetry)
            if (result.correctCount > 0 && state.best > 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(stringResource(R.string.correct_labels), stringResource(R.string.stat_value, result.correctCount), Modifier.weight(1f))
                    StatCard(stringResource(R.string.best_accuracy), stringResource(R.string.percent_format, state.best), Modifier.weight(1f))
                }
            }
            Text(stringResource(R.string.result_hint), style = MaterialTheme.typography.bodyLarge)
            PrimaryButton(R.string.play_again, onAgain)
            SecondaryButton(R.string.menu, onMenu)
            Text(stringResource(R.string.review_heading), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(systemName(result.systemId)), style = MaterialTheme.typography.titleMedium, color = DeepBlue)
            DiagramBoard(result.systemId, state.system.parts.take(result.totalCount), onFact, revealed = true, review = true,
                submissions = result.entries.mapNotNull { entry -> entry.submittedId?.let { entry.targetId to it } }.toMap())
            result.entries.forEachIndexed { index, entry ->
                AtlasSurface {
                    val status = stringResource(when (entry.outcome) {
                        ReviewOutcome.Correct -> R.string.correct
                        ReviewOutcome.Incorrect -> R.string.incorrect
                        ReviewOutcome.Unattempted -> R.string.unattempted
                    })
                    Column(Modifier.semantics { stateDescription = status }, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.target_answer, index + 1, stringResource(partName(entry.targetId))), style = MaterialTheme.typography.titleMedium)
                        Text(status, style = MaterialTheme.typography.labelLarge, color = DeepBlue)
                        Text(stringResource(partLocation(entry.targetId)), style = MaterialTheme.typography.bodyMedium)
                        if (entry.outcome == ReviewOutcome.Incorrect && entry.submittedId != null) {
                            Text(stringResource(R.string.submitted_label, stringResource(partName(entry.submittedId))), style = MaterialTheme.typography.bodyLarge)
                            Text(stringResource(R.string.correct_label, stringResource(partName(entry.targetId))), style = MaterialTheme.typography.bodyLarge)
                        }
                        SecondaryButton(R.string.read_fact, { onFact(entry.targetId) })
                    }
                }
            }
        }
    }
}
