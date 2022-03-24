package com.funnygaytest.domain.local.questions

import com.funnygaytest.data.room.dao.QuestionsDao
import com.funnygaytest.data.room.entity.QuestionsEntity
import javax.inject.Inject

class QuestionsRepositoryImpl @Inject constructor(private val dao: QuestionsDao) :
    QuestionsRepository {

    override suspend fun insertQuestions(questions: List<QuestionsEntity>) = dao.insertQuestions(questions)

    override fun getQuestionById(id: Int): QuestionsEntity = dao.getQuestionById(id)

    override suspend fun getEntitiesCount(): Int = dao.getEntitiesCount()

}