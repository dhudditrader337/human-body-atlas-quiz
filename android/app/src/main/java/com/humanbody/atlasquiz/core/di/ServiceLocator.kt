package com.humanbody.atlasquiz.core.di

import android.content.Context
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.data.local.AtlasPreferences
import com.humanbody.atlasquiz.data.repository.AtlasRepositoryImpl
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import com.humanbody.atlasquiz.domain.usecase.QuizRules

object ServiceLocator {
    private var storage: AtlasRepository? = null
    private val rules = QuizRules()
    fun initialize(context: Context) {
        if (storage == null) storage = AtlasRepositoryImpl(AtlasPreferences(context.applicationContext))
    }
    fun factory(route: Screen = Screen.Menu): AppViewModelFactory = AppViewModelFactory(checkNotNull(storage), rules, route)
}
