package com.humanbody.atlasquiz.presentation.settings

import com.humanbody.atlasquiz.domain.model.AtlasSettings

enum class SettingsStatus { Ready, Saving, Saved, Error, Cleared, ResetError, ReadError }
data class SettingsUiState(
    val settings: AtlasSettings = AtlasSettings(),
    val status: SettingsStatus = SettingsStatus.Ready,
    val resetDialog: Boolean = false
)
