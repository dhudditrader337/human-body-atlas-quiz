package com.humanbody.atlasquiz.core.ui

import androidx.compose.runtime.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.humanbody.atlasquiz.presentation.app.UiEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun ObserveEvents(events: Flow<UiEvent>, onEvent: (UiEvent) -> Unit) {
    val currentOnEvent by rememberUpdatedState(onEvent)
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        events.collect { event ->
            if (event == UiEvent.Haptic) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            else currentOnEvent(event)
        }
    }
}
