package com.funnygaytest.utils.helpers

import android.content.Context
import com.funnygaytest.R
import com.funnygaytest.data.room.entity.Answer
import com.funnygaytest.data.room.entity.QuestionsEntity

class QuestionsGenerator(private val context: Context) {

    fun generateQuestions(): List<QuestionsEntity> {
        return listOf(
            QuestionsEntity(
                0,
                context.getString(R.string.question_1),
                listOf(
                    Answer(context.getString(R.string.question_1_answer_1), 0),
                    Answer(context.getString(R.string.question_1_answer_2), 2),
                    Answer(context.getString(R.string.question_1_answer_3), 1),
                    Answer(context.getString(R.string.question_1_answer_4), 3)
                )
            ),
            QuestionsEntity(
                1,
                context.getString(R.string.question_2),
                listOf(
                    Answer(context.getString(R.string.question_2_answer_1), 1),
                    Answer(context.getString(R.string.question_2_answer_2), 2),
                    Answer(context.getString(R.string.question_2_answer_3), 0),
                    Answer(context.getString(R.string.question_2_answer_4), 3)
                )
            ),
            QuestionsEntity(
                2,
                context.getString(R.string.question_3),
                listOf(
                    Answer(context.getString(R.string.question_3_answer_1), 1),
                    Answer(context.getString(R.string.question_3_answer_2), 2),
                    Answer(context.getString(R.string.question_3_answer_3), 3),
                    Answer(context.getString(R.string.question_3_answer_4), 0)
                )
            ),
            QuestionsEntity(
                3,
                context.getString(R.string.question_4),
                listOf(
                    Answer(context.getString(R.string.question_4_answer_1), 2),
                    Answer(context.getString(R.string.question_4_answer_2), 3),
                    Answer(context.getString(R.string.question_4_answer_3), 1),
                    Answer(context.getString(R.string.question_4_answer_4), 0)
                )
            ),
            QuestionsEntity(
                4,
                context.getString(R.string.question_5),
                listOf(
                    Answer(context.getString(R.string.question_5_answer_1), 1),
                    Answer(context.getString(R.string.question_5_answer_2), 2),
                    Answer(context.getString(R.string.question_5_answer_3), 0),
                    Answer(context.getString(R.string.question_5_answer_4), 3)
                )
            ),
            QuestionsEntity(
                5,
                context.getString(R.string.question_6),
                listOf(
                    Answer(context.getString(R.string.question_6_answer_1), 3),
                    Answer(context.getString(R.string.question_6_answer_2), 2),
                    Answer(context.getString(R.string.question_6_answer_3), 1),
                    Answer(context.getString(R.string.question_6_answer_4), 0)
                )
            ),
            QuestionsEntity(
                6,
                context.getString(R.string.question_7),
                listOf(
                    Answer(context.getString(R.string.question_7_answer_1), 1),
                    Answer(context.getString(R.string.question_7_answer_2), 2),
                    Answer(context.getString(R.string.question_7_answer_3), 0)
                )
            ),
            QuestionsEntity(
                7,
                context.getString(R.string.question_8),
                listOf(
                    Answer(context.getString(R.string.question_8_answer_1), 2),
                    Answer(context.getString(R.string.question_8_answer_2), 0),
                    Answer(context.getString(R.string.question_8_answer_3), 1),
                    Answer(context.getString(R.string.question_8_answer_4), 3)
                )
            ),
            QuestionsEntity(
                8,
                context.getString(R.string.question_9),
                listOf(
                    Answer(context.getString(R.string.question_9_answer_1), 1),
                    Answer(context.getString(R.string.question_9_answer_2), 0),
                    Answer(context.getString(R.string.question_9_answer_3), 2)
                )
            ),
            QuestionsEntity(
                9,
                context.getString(R.string.question_10),
                listOf(
                    Answer(context.getString(R.string.question_10_answer_1), 2),
                    Answer(context.getString(R.string.question_10_answer_2), 1),
                    Answer(context.getString(R.string.question_10_answer_3), 0),
                    Answer(context.getString(R.string.question_10_answer_4), 3)
                )
            ),
            QuestionsEntity(
                10,
                context.getString(R.string.question_11),
                listOf(
                    Answer(context.getString(R.string.question_11_answer_1), 3),
                    Answer(context.getString(R.string.question_11_answer_2), 1),
                    Answer(context.getString(R.string.question_11_answer_3), 2),
                    Answer(context.getString(R.string.question_11_answer_4), 0)
                )
            ),
            QuestionsEntity(
                11,
                context.getString(R.string.question_12),
                listOf(
                    Answer(context.getString(R.string.question_12_answer_1), 1),
                    Answer(context.getString(R.string.question_12_answer_2), 2),
                    Answer(context.getString(R.string.question_12_answer_3), 0),
                    Answer(context.getString(R.string.question_12_answer_4), 3)
                )
            ),
            QuestionsEntity(
                12,
                context.getString(R.string.question_13),
                listOf(
                    Answer(context.getString(R.string.question_13_answer_1), 0),
                    Answer(context.getString(R.string.question_13_answer_2), 1),
                    Answer(context.getString(R.string.question_13_answer_3), 2),
                    Answer(context.getString(R.string.question_13_answer_4), 3)
                )
            ),
            QuestionsEntity(
                13,
                context.getString(R.string.question_14),
                listOf(
                    Answer(context.getString(R.string.question_14_answer_1), 1),
                    Answer(context.getString(R.string.question_14_answer_2), 2),
                    Answer(context.getString(R.string.question_14_answer_3), 0),
                    Answer(context.getString(R.string.question_14_answer_4), 3)
                )
            ),
            QuestionsEntity(
                14,
                context.getString(R.string.question_15),
                listOf(
                    Answer(context.getString(R.string.question_15_answer_1), 0),
                    Answer(context.getString(R.string.question_15_answer_2), 2),
                    Answer(context.getString(R.string.question_15_answer_3), 1)
                )
            ),
            QuestionsEntity(
                15,
                context.getString(R.string.question_16),
                listOf(
                    Answer(context.getString(R.string.question_16_answer_1), 0),
                    Answer(context.getString(R.string.question_16_answer_2), 2),
                    Answer(context.getString(R.string.question_16_answer_3), 1)
                )
            ),
            QuestionsEntity(
                16,
                context.getString(R.string.question_17),
                listOf(
                    Answer(context.getString(R.string.question_17_answer_1), 1),
                    Answer(context.getString(R.string.question_17_answer_2), 0),
                    Answer(context.getString(R.string.question_17_answer_3), 2),
                    Answer(context.getString(R.string.question_17_answer_4), 3)
                )
            ),
            QuestionsEntity(
                17,
                context.getString(R.string.question_18),
                listOf(
                    Answer(context.getString(R.string.question_18_answer_1), 1),
                    Answer(context.getString(R.string.question_18_answer_2), 0),
                    Answer(context.getString(R.string.question_18_answer_3), 2),
                    Answer(context.getString(R.string.question_18_answer_4), 3)
                )
            ),
            QuestionsEntity(
                17,
                context.getString(R.string.question_18),
                listOf(
                    Answer(context.getString(R.string.question_18_answer_1), 1),
                    Answer(context.getString(R.string.question_18_answer_2), 0),
                    Answer(context.getString(R.string.question_18_answer_3), 2),
                    Answer(context.getString(R.string.question_18_answer_4), 3)
                )
            ),
            QuestionsEntity(
                18,
                context.getString(R.string.question_19),
                listOf(
                    Answer(context.getString(R.string.question_19_answer_1), 3),
                    Answer(context.getString(R.string.question_19_answer_2), 1),
                    Answer(context.getString(R.string.question_19_answer_3), 2),
                    Answer(context.getString(R.string.question_19_answer_4), 0)
                )
            ),
            QuestionsEntity(
                19,
                context.getString(R.string.question_20),
                listOf(
                    Answer(context.getString(R.string.question_20_answer_1), 2),
                    Answer(context.getString(R.string.question_20_answer_2), 1),
                    Answer(context.getString(R.string.question_20_answer_3), 3)
                )
            ),
            QuestionsEntity(
                20,
                context.getString(R.string.question_21),
                listOf(
                    Answer(context.getString(R.string.question_21_answer_1), 1),
                    Answer(context.getString(R.string.question_21_answer_2), 3),
                    Answer(context.getString(R.string.question_21_answer_3), 2),
                    Answer(context.getString(R.string.question_21_answer_4), 0)
                )
            ),
            QuestionsEntity(
                21,
                context.getString(R.string.question_22),
                listOf(
                    Answer(context.getString(R.string.question_22_answer_1), 2),
                    Answer(context.getString(R.string.question_22_answer_2), 3),
                    Answer(context.getString(R.string.question_22_answer_3), 0),
                    Answer(context.getString(R.string.question_22_answer_4), 1)
                )
            ),
            QuestionsEntity(
                22,
                context.getString(R.string.question_23),
                listOf(
                    Answer(context.getString(R.string.question_23_answer_1), 2),
                    Answer(context.getString(R.string.question_23_answer_2), 0),
                    Answer(context.getString(R.string.question_23_answer_3), 3),
                    Answer(context.getString(R.string.question_23_answer_4), 1),
                )
            ),
            QuestionsEntity(
                23,
                context.getString(R.string.question_24),
                listOf(
                    Answer(context.getString(R.string.question_24_answer_1), 2),
                    Answer(context.getString(R.string.question_24_answer_2), 0),
                    Answer(context.getString(R.string.question_24_answer_3), 1)
                )
            )
        )
    }
}