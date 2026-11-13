package com.humanbody.atlasquiz.presentation.app

import android.animation.ValueAnimator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humanbody.atlasquiz.core.di.ServiceLocator
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.core.theme.Background
import com.humanbody.atlasquiz.core.ui.LocalAtlasMotion
import com.humanbody.atlasquiz.core.ui.SystemBars
import com.humanbody.atlasquiz.presentation.splash.SplashRoute
import com.humanbody.atlasquiz.presentation.menu.MenuRoute
import com.humanbody.atlasquiz.presentation.diagram.DiagramRoute
import com.humanbody.atlasquiz.presentation.fact.FactCardRoute
import com.humanbody.atlasquiz.presentation.game.GameRoute
import com.humanbody.atlasquiz.presentation.gameover.GameOverRoute
import com.humanbody.atlasquiz.presentation.settings.SettingsRoute

@Composable
fun AppRoot() {
    val vm: AppViewModel = viewModel(factory = ServiceLocator.factory())
    val screen by vm.screen.collectAsStateWithLifecycle()
    val state by vm.uiState.collectAsStateWithLifecycle()
    val motion = !state.reducedMotion && ValueAnimator.areAnimatorsEnabled()
    SystemBars(screen == Screen.Splash)
    BackHandler(enabled = screen != Screen.Menu && screen != Screen.Splash) { vm.back() }
    CompositionLocalProvider(LocalAtlasMotion provides motion) {
        Surface(Modifier.fillMaxSize(), color = Background) {
            AnimatedContent(targetState = screen,
                transitionSpec = { fadeIn(tween(if (motion) 280 else 100)) togetherWith fadeOut(tween(if (motion) 280 else 100)) },
                label = "atlasNavigation") { destination ->
                val onEvent: (UiEvent) -> Unit = { event -> if (vm.screen.value == destination) vm.handle(event) }
                when (destination) {
                    Screen.Splash -> SplashRoute(vm::finishSplash)
                    Screen.Menu -> MenuRoute(onEvent)
                    is Screen.Diagram -> DiagramRoute(destination, onEvent)
                    is Screen.FactCard -> FactCardRoute(destination, onEvent)
                    is Screen.Game -> GameRoute(destination, onEvent)
                    is Screen.GameOver -> GameOverRoute(destination, onEvent)
                    Screen.Settings -> SettingsRoute(onEvent)
                }
            }
        }
    }
}
