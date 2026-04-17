package com.funnygaytest.prefs.types

import android.content.Context
import androidx.core.content.edit
import com.funnygaytest.models.Question
import com.funnygaytest.prefs.ActiveProperty
import com.funnygaytest.prefs.PrefsEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber

class ActiveQuestionList(context: Context) : ActiveProperty<List<Question>>(context) {
    private val gson = Gson()
    private val type = object : TypeToken<List<Question>>() {}.type

    override fun getFromPrefs(key: String): List<Question> {
        val json = sp.getString(key, null)
        if (json == null) {
            Timber.w("Prefs: Список вопросов по ключу $key отсутствует (null)")
            return emptyList()
        }
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Timber.e(e, "Prefs: Ошибка парсинга JSON для списка вопросов")
            emptyList()
        }
    }

    override fun saveToPrefs(key: String, value: List<Question>) {
        val json = gson.toJson(value)
        sp.edit(commit = true) { putString(key, json) }
    }
}

fun PrefsEntity.activeQuestionList(): ActiveQuestionList {
    return ActiveQuestionList(ctx)
}