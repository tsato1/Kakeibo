package com.kakeibo.feature_settings.presentation.settings_list

sealed class SettingsListEvent {
    object DateFormatChanged : SettingsListEvent()
    object FractionDigitsChanged : SettingsListEvent()
    object NumColumnsChanged : SettingsListEvent()
    object DeleteAllItems : SettingsListEvent()
    object ShowAds : SettingsListEvent()
}