package com.humanbody.atlasquiz.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.theme.*

val LocalAtlasMotion = staticCompositionLocalOf { true }
enum class AtlasBackdrop { Splash, Menu, Diagram, Fact, Game, Result, Settings }

@Composable
fun AtlasBackground(backdrop: AtlasBackdrop) {
    Image(painterResource(R.drawable.bg_game), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    val colors = when (backdrop) {
        AtlasBackdrop.Splash -> listOf(DeepBlue.copy(alpha = .96f), Primary.copy(alpha = .92f), DeepBlue.copy(alpha = .97f))
        AtlasBackdrop.Game -> listOf(OceanSurface.copy(alpha = .96f), OceanMist.copy(alpha = .97f), Background.copy(alpha = .98f))
        AtlasBackdrop.Result -> listOf(Background.copy(alpha = .96f), OceanSurface.copy(alpha = .96f), Secondary.copy(alpha = .94f))
        AtlasBackdrop.Fact -> listOf(OceanSurface.copy(alpha = .96f), OceanMist.copy(alpha = .97f), Background.copy(alpha = .98f))
        AtlasBackdrop.Diagram -> listOf(OceanMist.copy(alpha = .96f), Background.copy(alpha = .97f), OceanSoft.copy(alpha = .95f))
        else -> listOf(Background.copy(alpha = .97f), OceanMist.copy(alpha = .97f), OceanSurface.copy(alpha = .96f))
    }
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(colors)))
    Canvas(Modifier.fillMaxSize()) {
        val color = if (backdrop == AtlasBackdrop.Splash) Background.copy(alpha = .08f) else Primary.copy(alpha = .055f)
        drawCircle(color, size.width * .62f, Offset(size.width * .98f, size.height * .25f), style = Stroke(1.dp.toPx()))
        drawCircle(color, size.width * .73f, Offset(size.width * .98f, size.height * .25f), style = Stroke(1.dp.toPx()))
        drawCircle(color, size.width * .64f, Offset(0f, size.height * .94f))
    }
}

@Composable
fun AtlasPage(backdrop: AtlasBackdrop, content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize()) {
        AtlasBackground(backdrop)
        Column(
            Modifier.align(Alignment.TopCenter).safeDrawingPadding().widthIn(max = 720.dp)
                .fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}
