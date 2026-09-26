package com.funnygaytest.platform.locale

import androidx.annotation.DrawableRes
import com.funnygaytest.R

enum class AppLanguage(
    val tag: String,
    val displayName: String,
    @param:DrawableRes val flagRes: Int
) {
    RU("ru", "Русский", R.drawable.ic_flag_ru),
    EN("en", "English", R.drawable.ic_flag_en),
    DE("de", "Deutsch", R.drawable.ic_flag_de);

    companion object {
        val Default = EN

        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { tag?.startsWith(it.tag) == true } ?: Default
    }
}
