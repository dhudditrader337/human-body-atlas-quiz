package com.humanbody.atlasquiz.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.theme.*

@Composable
fun SecondaryButton(text: Int, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val unavailable = stringResource(R.string.unavailable)
    val shape = RoundedCornerShape(16.dp)
    OutlinedButton(onClick, modifier.fillMaxWidth().heightIn(min = 48.dp).shadow(if (enabled) 1.dp else 0.dp, shape)
        .semantics { if (!enabled) stateDescription = unavailable },
        enabled = enabled, shape = shape, border = BorderStroke(1.dp, if (enabled) Primary else OceanLine),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Background.copy(alpha = .9f), contentColor = DeepBlue,
            disabledContainerColor = OceanSurface, disabledContentColor = DeepBlue),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (text == R.string.menu) {
            Image(painterResource(R.drawable.icon_home), null, Modifier.size(24.dp), contentScale = ContentScale.Fit)
            Spacer(Modifier.width(8.dp))
        }
        Text(stringResource(text), style = MaterialTheme.typography.labelLarge)
    }
}
