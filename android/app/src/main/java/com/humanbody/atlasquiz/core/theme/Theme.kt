package com.humanbody.atlasquiz.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AtlasColors = lightColorScheme(
    primary = Primary, onPrimary = Background, primaryContainer = OceanSurface, onPrimaryContainer = DeepBlue,
    secondary = Secondary, onSecondary = Ink, secondaryContainer = OceanMist, onSecondaryContainer = DeepBlue,
    tertiary = Accent, onTertiary = Background, background = Background, onBackground = Ink,
    surface = Background, onSurface = Ink, surfaceVariant = OceanSurface, onSurfaceVariant = DeepBlue,
    outline = Primary, outlineVariant = OceanLine, error = Accent, onError = Background
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AtlasColors, typography = AtlasTypography, content = content)
}
