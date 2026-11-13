package com.humanbody.atlasquiz.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import com.humanbody.atlasquiz.domain.usecase.QuizRules
import com.humanbody.atlasquiz.presentation.app.AppViewModel
import com.humanbody.atlasquiz.presentation.splash.SplashViewModel
import com.humanbody.atlasquiz.presentation.menu.MenuViewModel
import com.humanbody.atlasquiz.presentation.diagram.DiagramViewModel
import com.humanbody.atlasquiz.presentation.fact.FactCardViewModel
import com.humanbody.atlasquiz.presentation.game.GameViewModel
import com.humanbody.atlasquiz.presentation.gameover.GameOverViewModel
import com.humanbody.atlasquiz.presentation.settings.SettingsViewModel

class AppViewModelFactory(
    private val repository: AtlasRepository,
    private val rules: QuizRules,
    private val route: Screen
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val instance: ViewModel = when (modelClass) {
            AppViewModel::class.java -> AppViewModel(repository)
            SplashViewModel::class.java -> SplashViewModel(repository)
            MenuViewModel::class.java -> MenuViewModel(repository)
            DiagramViewModel::class.java -> DiagramViewModel(repository, (route as Screen.Diagram).systemId)
            FactCardViewModel::class.java -> (route as Screen.FactCard).let { FactCardViewModel(repository, it.systemId, it.partId) }
            GameViewModel::class.java -> GameViewModel(repository, rules, route as Screen.Game)
            GameOverViewModel::class.java -> GameOverViewModel(repository, (route as Screen.GameOver).result)
            SettingsViewModel::class.java -> SettingsViewModel(repository)
            else -> error(modelClass.name)
        }
        return modelClass.cast(instance) ?: error(modelClass.name)
    }
}
