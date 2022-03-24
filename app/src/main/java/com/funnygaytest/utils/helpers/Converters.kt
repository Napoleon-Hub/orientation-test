package com.funnygaytest.utils.helpers

import androidx.room.TypeConverter
import com.funnygaytest.data.room.entity.Answer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun listAnswerToPojo(data: String): List<Answer> {
        val listType: Type = object : TypeToken<ArrayList<Answer>>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun stringToListAnswer(name: List<Answer>): String {
        return Gson().toJson(name)
    }

}