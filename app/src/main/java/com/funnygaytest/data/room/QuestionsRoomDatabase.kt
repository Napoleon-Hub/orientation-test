package com.funnygaytest.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.funnygaytest.data.room.dao.QuestionsDao
import com.funnygaytest.data.room.entity.QuestionsEntity
import com.funnygaytest.utils.helpers.Converters

@Database(entities = [QuestionsEntity::class], exportSchema = true, version = 2)
@TypeConverters(Converters::class)
abstract class QuestionsRoomDatabase : RoomDatabase() {
    abstract fun entitiesDao(): QuestionsDao

    companion object {
        @Volatile
        private var INSTANCE: QuestionsRoomDatabase? = null

        fun getDatabase(context: Context): QuestionsRoomDatabase {
            INSTANCE?.let { return it }
            synchronized(this) {
                return Room.databaseBuilder(
                    context, QuestionsRoomDatabase::class.java, "questions_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}