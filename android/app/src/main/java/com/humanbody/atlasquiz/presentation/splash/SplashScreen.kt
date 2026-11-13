package com.humanbody.atlasquiz.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanbody.atlasquiz.R
import com.humanbody.atlasquiz.core.di.ServiceLocator
import com.humanbody.atlasquiz.core.theme.*
import com.humanbody.atlasquiz.core.ui.*

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val vm: SplashViewModel = viewModel(factory = ServiceLocator.factory())
    val state by vm.uiState.collectAsStateWithLifecycle()
    val finished by vm.finished.collectAsStateWithLifecycle()
    SplashScreen(state.copy(finished = finished), onFinished)
}

@Composable
fun SplashScreen(state: SplashUiState, onFinished: () -> Unit) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    LaunchedEffect(state.finished) { if (state.finished) currentOnFinished() }
    val infinite = rememberInfiniteTransition(label = "atlasFloat")
    val float by infinite.animateFloat(-5f, 5f, infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "emblemFloat")
    val ring by infinite.animateFloat(1f, 1.06f, infiniteRepeatable(tween(1800), RepeatMode.Reverse), label = "ringBreath")
    val motion = LocalAtlasMotion.current && !state.reducedMotion
    val opening = stringResource(R.string.splash_status)
    Box(Modifier.fillMaxSize()) {
        AtlasBackground(AtlasBackdrop.Splash)
        Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(stringResource(R.string.atlas_edition), style = MaterialTheme.typography.labelSmall, color = Background.copy(alpha = .9f))
            Spacer(Modifier.height(32.dp))
            Box(Modifier.size(232.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.size(232.dp).graphicsLayer { scaleX = if (motion) ring else 1f; scaleY = scaleX }
                    .border(1.dp, Background.copy(alpha = .22f), CircleShape))
                Box(Modifier.size(204.dp).border(1.dp, Background.copy(alpha = .10f), CircleShape))
                Image(painterResource(R.drawable.logo), stringResource(R.string.logo_description),
                    Modifier.size(188.dp).offset(y = if (motion) float.dp else 0.dp), contentScale = ContentScale.Fit)
            }
            Spacer(Modifier.height(28.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp, lineHeight = 38.sp),
                color = Background, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold,
                modifier = Modifier.widthIn(max = 330.dp).semantics { heading() })
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.splash_subtitle), style = MaterialTheme.typography.bodyLarge, color = Background,
                textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 290.dp))
            Spacer(Modifier.height(40.dp))
            LinearProgressIndicator(Modifier.width(176.dp).height(4.dp).semantics { contentDescription = opening },
                color = Accent, trackColor = Background.copy(alpha = .24f))
            Spacer(Modifier.height(16.dp))
            Text(opening, style = MaterialTheme.typography.bodyMedium, color = Background.copy(alpha = .9f),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        }
    }
}
