package com.funnygaytest.prefs.types

import android.content.Context
import com.funnygaytest.prefs.ActiveProperty
import com.funnygaytest.prefs.PrefsEntity
import androidx.core.content.edit

class ActiveBoolean(context: Context, private val defValue: Boolean) :
    ActiveProperty<Boolean>(context) {

    override fun getFromPrefs(key: String): Boolean {
        return sp.getBoolean(key, defValue)
    }

    override fun saveToPrefs(key: String, value: Boolean) {
        sp.edit { putBoolean(key, value) }
    }

}

fun PrefsEntity.activeBoolean(defValue: Boolean = false): ActiveBoolean {
    return ActiveBoolean(ctx, defValue)
}