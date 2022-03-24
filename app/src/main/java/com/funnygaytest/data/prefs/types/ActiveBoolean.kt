package com.funnygaytest.data.prefs.types

import android.content.Context
import com.funnygaytest.data.prefs.ActiveProperty
import com.funnygaytest.data.prefs.PrefsEntity

class ActiveBoolean(context: Context, private val defValue: Boolean) :
    ActiveProperty<Boolean>(context) {

    override fun getFromPrefs(key: String): Boolean {
        return sp.getBoolean(key, defValue)
    }

    override fun saveToPrefs(key: String, value: Boolean) {
        sp.edit().putBoolean(key, value).apply()
    }

}

fun PrefsEntity.activeBoolean(defValue: Boolean = false): ActiveBoolean {
    return ActiveBoolean(ctx, defValue)
}