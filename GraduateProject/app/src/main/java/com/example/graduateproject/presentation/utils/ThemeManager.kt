package com.example.graduateproject.presentation.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_NIGHT_MODE = "night_mode"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun applyTheme(context: Context) {
        val mode =
            getPrefs(context).getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun setDarkMode(context: Context) {
        getPrefs(context).edit { putInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_YES) }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    fun setLightMode(context: Context) {
        getPrefs(context).edit { putInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_NO) }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
