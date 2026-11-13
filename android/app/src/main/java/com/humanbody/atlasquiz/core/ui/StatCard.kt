package com.humanbody.atlasquiz.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.humanbody.atlasquiz.core.theme.DeepBlue

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    AtlasSurface(modifier) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = DeepBlue)
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
