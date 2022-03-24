package com.funnygaytest.data.room.entity

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "questions_table")
class QuestionsEntity(
    @PrimaryKey val id: Int,
    val question: String,
    val answerList: List<Answer>
)

@Keep
data class Answer(
    val answerString: String,
    val answerPoints: Int,
    var pressed: Boolean = false
)