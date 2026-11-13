package com.humanbody.atlasquiz.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.theme.*

@Composable
fun AtlasSurface(modifier: Modifier = Modifier, selected: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = Background.copy(alpha = .96f),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) Accent else Primary.copy(alpha = .18f)),
        shadowElevation = 3.dp) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun ScreenHeader(title: String, onBack: () -> Unit, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onBack, Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.back)) }
        Text(title, style = MaterialTheme.typography.headlineMedium, color = Ink, modifier = Modifier.semantics { heading() })
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = DeepBlue)
    }
}

@Composable
fun StatusMessage(text: String, error: Boolean = false) {
    Text(text, color = if (error) Ink else DeepBlue, style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite; stateDescription = text })
}

@Composable
fun EmptyState(message: Int, action: Int, onAction: () -> Unit) {
    AtlasSurface {
        Text(stringResource(message), style = MaterialTheme.typography.titleMedium)
        SecondaryButton(action, onAction)
    }
}
