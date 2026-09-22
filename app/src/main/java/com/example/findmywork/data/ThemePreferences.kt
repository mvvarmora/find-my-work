package com.example.findmywork.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists and retrieves the user's Dark / Light mode preference.
 * If not explicitly configured, falls back to the Android system dark theme status.
 */
class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isDarkTheme(defaultSystem: Boolean): Boolean {
        return if (prefs.contains(KEY_IS_DARK)) {
            prefs.getBoolean(KEY_IS_DARK, defaultSystem)
        } else {
            defaultSystem
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK, isDark).apply()
    }

    companion object {
        private const val PREFS_NAME = "fmw_theme_preferences"
        private const val KEY_IS_DARK = "key_is_dark_theme"
    }
}
