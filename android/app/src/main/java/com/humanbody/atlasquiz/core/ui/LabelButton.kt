package com.humanbody.atlasquiz.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.theme.*
import com.humanbody.atlasquiz.domain.model.AnatomyPart

@Composable
fun LabelButton(systemId: String, part: AnatomyPart, selected: Boolean, enabled: Boolean, placed: Boolean, clue: Boolean,
    onSelect: () -> Unit, onDragStart: (Offset) -> Unit, onDrag: (Offset) -> Unit, onDrop: () -> Unit, onCancel: () -> Unit) {
    val id = part.id
    var origin by remember { mutableStateOf(Offset.Zero) }
    val selectStatus = stringResource(when { placed -> R.string.placed; selected -> R.string.selected; !enabled -> R.string.unavailable; else -> R.string.available })
    val description = if (clue) stringResource(R.string.label_clue_description, stringResource(partName(id)), stringResource(partLocation(id))) else stringResource(partName(id))
    val start by rememberUpdatedState(onDragStart)
    val move by rememberUpdatedState(onDrag)
    val drop by rememberUpdatedState(onDrop)
    val cancel by rememberUpdatedState(onCancel)
    val configuration = LocalViewConfiguration.current
    val dragConfiguration = remember(configuration) { object : ViewConfiguration by configuration {
        override val longPressTimeoutMillis: Long = 160L
    } }
    CompositionLocalProvider(LocalViewConfiguration provides dragConfiguration) {
        OutlinedButton(onSelect, Modifier.heightIn(min = 48.dp).widthIn(min = 64.dp)
            .onGloballyPositioned { origin = it.positionInRoot() }
            .pointerInput(id, enabled) {
                if (enabled) detectDragGesturesAfterLongPress(
                    onDragStart = { start(origin + it) }, onDragEnd = { drop() }, onDragCancel = { cancel() },
                    onDrag = { change, amount -> change.consume(); move(amount) }
                )
            }.semantics { stateDescription = selectStatus; this.selected = selected; contentDescription = description },
            enabled = enabled, shape = RoundedCornerShape(14.dp),
            border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) Accent else if (enabled) Primary else OceanLine),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) Secondary.copy(alpha = .18f) else Background,
                contentColor = DeepBlue, disabledContainerColor = OceanSurface, disabledContentColor = DeepBlue),
            contentPadding = PaddingValues(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (clue) AnatomyClue(systemId, part, Modifier.clip(CircleShape))
                    Text(stringResource(partLabel(id)), style = MaterialTheme.typography.labelLarge)
                }
                if (placed) Text(stringResource(R.string.placed), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
