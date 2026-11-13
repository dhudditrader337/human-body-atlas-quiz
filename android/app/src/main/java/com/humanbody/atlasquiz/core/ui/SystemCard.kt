package com.humanbody.atlasquiz.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.theme.*

@Composable
fun SystemCard(id: String, ordinal: Int, selected: Boolean, explored: Boolean, onSelect: () -> Unit) {
    val state = stringResource(if (selected) R.string.selected else if (explored) R.string.explored else R.string.available)
    OutlinedButton(onSelect, Modifier.fillMaxWidth().heightIn(min = 88.dp).semantics { stateDescription = state; this.selected = selected },
        shape = RoundedCornerShape(18.dp), border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) Accent else Primary.copy(alpha = .25f)),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) OceanMist else Background.copy(alpha = .96f), contentColor = Ink),
        contentPadding = PaddingValues(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Image(painterResource(systemSymbol(id)), null, Modifier.size(60.dp), contentScale = ContentScale.Fit)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.system_format, ordinal, stringResource(systemName(id))), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(systemDescription(id)), style = MaterialTheme.typography.bodyMedium)
                if (selected || explored) Text(state, style = MaterialTheme.typography.labelSmall, color = DeepBlue)
            }
        }
    }
}
