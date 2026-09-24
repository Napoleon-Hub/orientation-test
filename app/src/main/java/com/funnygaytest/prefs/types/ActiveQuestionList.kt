package com.funnygaytest.prefs.types

import android.content.Context
import androidx.core.content.edit
import com.funnygaytest.models.Question
import com.funnygaytest.prefs.ActiveProperty
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.utils.helpers.findQuestionById
import com.google.gson.Gson
import com.google.gson.JsonParser
import timber.log.Timber

class ActiveQuestionList(context: Context) : ActiveProperty<List<Question>>(context) {
    private val gson = Gson()

    override fun getFromPrefs(key: String): List<Question> {
        val json = sp.getString(key, null)
        if (json == null) {
            Timber.w("Prefs: Список вопросов по ключу $key отсутствует (null)")
            return emptyList()
        }
        return try {
            val ids = JsonParser.parseString(json).asJsonArray.map { element ->
                if (element.isJsonObject) element.asJsonObject.get("id").asString else element.asString
            }
            val questions = ids.mapNotNull { findQuestionById(it) }
            if (questions.size != ids.size) {
                Timber.w("Prefs: В сохранённом забеге есть неизвестные вопросы, забег сброшен")
                emptyList()
            } else {
                questions
            }
        } catch (e: Exception) {
            Timber.e(e, "Prefs: Ошибка парсинга JSON для списка вопросов")
            emptyList()
        }
    }

    override fun saveToPrefs(key: String, value: List<Question>) {
        val json = gson.toJson(value.map { it.id })
        sp.edit(commit = true) { putString(key, json) }
    }
}

fun PrefsEntity.activeQuestionList(): ActiveQuestionList {
    return ActiveQuestionList(ctx)
}
