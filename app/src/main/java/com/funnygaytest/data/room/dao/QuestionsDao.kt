package com.funnygaytest.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.funnygaytest.data.room.entity.QuestionsEntity

@Dao
interface QuestionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionsEntity>)

    @Query("SELECT * FROM questions_table WHERE id = :id")
    fun getQuestionById(id : Int): QuestionsEntity

    @Query("SELECT COUNT(*) FROM questions_table")
    suspend fun getEntitiesCount(): Int

}