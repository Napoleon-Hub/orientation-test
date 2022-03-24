package com.funnygaytest.domain.local.questions

import com.funnygaytest.data.room.entity.QuestionsEntity

interface QuestionsRepository {

    suspend fun insertQuestions(questions: List<QuestionsEntity>)

    fun getQuestionById(id: Int): QuestionsEntity

    suspend fun getEntitiesCount(): Int

}