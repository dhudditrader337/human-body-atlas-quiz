package com.humanbody.atlasquiz.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.theme.*

@Composable
fun PrimaryButton(text: Int, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val motion = LocalAtlasMotion.current
    val scale by animateFloatAsState(if (pressed && motion) .98f else 1f,
        spring(dampingRatio = .8f, stiffness = 600f), label = "buttonScale")
    val unavailable = stringResource(R.string.unavailable)
    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick, modifier.fillMaxWidth().heightIn(min = 60.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(if (enabled) 6.dp else 0.dp, shape)
            .background(Brush.horizontalGradient(if (enabled) listOf(Accent, Primary) else listOf(OceanSurface, OceanSurface)), shape)
            .background(if (enabled) Ink.copy(alpha = .22f) else Color.Transparent, shape)
            .semantics { if (!enabled) stateDescription = unavailable },
        enabled = enabled, shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Background,
            disabledContainerColor = Color.Transparent, disabledContentColor = DeepBlue),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp), interactionSource = interaction
    ) { Text(stringResource(text), style = MaterialTheme.typography.labelLarge) }
}
