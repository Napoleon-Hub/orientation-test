package com.funnygaytest.prefs.types

import android.content.Context
import com.funnygaytest.prefs.ActiveProperty
import com.funnygaytest.prefs.PrefsEntity
import androidx.core.content.edit

class ActiveInt(ctx: Context, private val defValue: Int = 0) : ActiveProperty<Int>(ctx) {
    override fun getFromPrefs(key: String): Int {
        return sp.getInt(key, defValue)
    }

    override fun saveToPrefs(key: String, value: Int) {
        sp.edit { putInt(key, value) }
    }
}

fun PrefsEntity.activeInt(defValue: Int = 0) = ActiveInt(ctx, defValue)