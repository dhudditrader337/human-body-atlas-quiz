package com.humanbody.atlasquiz.core.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun Context.hostActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return current as? Activity
}

@Composable
fun SystemBars(darkBackground: Boolean) {
    val view = LocalView.current
    SideEffect {
        view.context.hostActivity()?.window?.let { window ->
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkBackground
            controller.isAppearanceLightNavigationBars = !darkBackground
        }
    }
}
