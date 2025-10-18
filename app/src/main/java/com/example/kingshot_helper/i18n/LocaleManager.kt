package com.example.kingshot_helper.i18n

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleManager {
    private const val PREFS = "locale_prefs"
    private const val KEY_LANG = "lang_code"
    private val DEFAULT_LANG = "en"

    fun setLanguage(context: Context, lang: String) {
        getPrefs(context).edit().putString(KEY_LANG, lang).apply()
    }

    fun getLanguage(context: Context): String =
        getPrefs(context).getString(KEY_LANG, DEFAULT_LANG) ?: DEFAULT_LANG

    fun updateContextLocale(context: Context): Context {
        val lang = getLanguage(context)
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            return context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            return context
        }
    }

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
