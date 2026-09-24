package com.funnygaytest.managers.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.funnygaytest.utils.enums.AppLanguage
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLocaleManager @Inject constructor() {

    val currentLanguage: AppLanguage
        get() {
            val appLocales = AppCompatDelegate.getApplicationLocales()
            val tag = if (appLocales.isEmpty) Locale.getDefault().language else appLocales[0]?.language
            return AppLanguage.fromTag(tag)
        }

    fun setLanguage(language: AppLanguage) {
        if (language == currentLanguage) return
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
    }
}
