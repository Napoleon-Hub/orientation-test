package com.funnygaytest.utils.helpers

import com.funnygaytest.R
import com.funnygaytest.domain.models.Answer
import com.funnygaytest.domain.models.Question

class QuestionsGenerator {

    fun generateQuestions(): List<Question> {
        return listOf(
            Question(
                1,
                R.string.question_1,
                listOf(
                    Answer(R.string.question_1_answer_1, 0),
                    Answer(R.string.question_1_answer_2, 5),
                    Answer(R.string.question_1_answer_3, 0),
                    Answer(R.string.question_1_answer_4, 6)
                )
            ),
            Question(
                2,
                R.string.question_2,
                listOf(
                    Answer(R.string.question_2_answer_1, 2),
                    Answer(R.string.question_2_answer_2, 2),
                    Answer(R.string.question_2_answer_3, 0),
                    Answer(R.string.question_2_answer_4, 6)
                )
            ),
            Question(
                3,
                R.string.question_3,
                listOf(
                    Answer(R.string.question_3_answer_1, 1),
                    Answer(R.string.question_3_answer_2, 3),
                    Answer(R.string.question_3_answer_3, 6),
                    Answer(R.string.question_3_answer_4, 0)
                )
            ),
            Question(
                4,
                R.string.question_4,
                listOf(
                    Answer(R.string.question_4_answer_1, 2),
                    Answer(R.string.question_4_answer_2, 4),
                    Answer(R.string.question_4_answer_3, 0),
                    Answer(R.string.question_4_answer_4, 0)
                )
            ),
            Question(
                5,
                R.string.question_5,
                listOf(
                    Answer(R.string.question_5_answer_1, 1),
                    Answer(R.string.question_5_answer_2, 4),
                    Answer(R.string.question_5_answer_3, 0),
                    Answer(R.string.question_5_answer_4, 6)
                )
            ),
            Question(
                6,
                R.string.question_6,
                listOf(
                    Answer(R.string.question_6_answer_1, 3),
                    Answer(R.string.question_6_answer_2, 1),
                    Answer(R.string.question_6_answer_3, 2),
                    Answer(R.string.question_6_answer_4, 0)
                )
            ),
            Question(
                7,
                R.string.question_7,
                listOf(
                    Answer(R.string.question_7_answer_1, 2),
                    Answer(R.string.question_7_answer_2, 3),
                    Answer(R.string.question_7_answer_3, 0)
                )
            ),
            Question(
                8,
                R.string.question_8,
                listOf(
                    Answer(R.string.question_8_answer_1, 1),
                    Answer(R.string.question_8_answer_2, 0),
                    Answer(R.string.question_8_answer_3, 1),
                    Answer(R.string.question_8_answer_4, 3)
                )
            ),
            Question(
                9,
                R.string.question_9,
                listOf(
                    Answer(R.string.question_9_answer_1, 0),
                    Answer(R.string.question_9_answer_2, 3),
                    Answer(R.string.question_9_answer_3, 0)
                )
            ),
            Question(
                10,
                R.string.question_10,
                listOf(
                    Answer(R.string.question_10_answer_1, 3),
                    Answer(R.string.question_10_answer_2, 5),
                    Answer(R.string.question_10_answer_3, 0),
                    Answer(R.string.question_10_answer_4, 4)
                )
            ),
            Question(
                11,
                R.string.question_11,
                listOf(
                    Answer(R.string.question_11_answer_1, 1),
                    Answer(R.string.question_11_answer_2, 1),
                    Answer(R.string.question_11_answer_3, 2),
                    Answer(R.string.question_11_answer_4, 0)
                )
            ),
            Question(
                12,
                R.string.question_12,
                listOf(
                    Answer(R.string.question_12_answer_1, 3),
                    Answer(R.string.question_12_answer_2, 5),
                    Answer(R.string.question_12_answer_3, 0),
                    Answer(R.string.question_12_answer_4, 4)
                )
            ),
            Question(
                13,
                R.string.question_13,
                listOf(
                    Answer(R.string.question_13_answer_1, 0),
                    Answer(R.string.question_13_answer_2, 2),
                    Answer(R.string.question_13_answer_3, 4),
                    Answer(R.string.question_13_answer_4, 6)
                )
            ),
            Question(
                14,
                R.string.question_14,
                listOf(
                    Answer(R.string.question_14_answer_1, 1),
                    Answer(R.string.question_14_answer_2, 1),
                    Answer(R.string.question_14_answer_3, 0),
                    Answer(R.string.question_14_answer_4, 2)
                )
            ),
            Question(
                15,
                R.string.question_15,
                listOf(
                    Answer(R.string.question_15_answer_1, 0),
                    Answer(R.string.question_15_answer_2, 6),
                    Answer(R.string.question_15_answer_3, 1)
                )
            ),
            Question(
                16,
                R.string.question_16,
                listOf(
                    Answer(R.string.question_16_answer_1, 0),
                    Answer(R.string.question_16_answer_2, 6),
                    Answer(R.string.question_16_answer_3, 1)
                )
            ),
            Question(
                17,
                R.string.question_17,
                listOf(
                    Answer(R.string.question_17_answer_1, 1),
                    Answer(R.string.question_17_answer_2, 0),
                    Answer(R.string.question_17_answer_3, 2),
                    Answer(R.string.question_17_answer_4, 3)
                )
            ),
            Question(
                18,
                R.string.question_18,
                listOf(
                    Answer(R.string.question_18_answer_1, 4),
                    Answer(R.string.question_18_answer_2, 0),
                    Answer(R.string.question_18_answer_3, 5),
                    Answer(R.string.question_18_answer_4, 6)
                )
            ),
            Question(
                19,
                R.string.question_19,
                listOf(
                    Answer(R.string.question_19_answer_1, 6),
                    Answer(R.string.question_19_answer_2, 2),
                    Answer(R.string.question_19_answer_3, 3),
                    Answer(R.string.question_19_answer_4, 0)
                )
            ),
            Question(
                20,
                R.string.question_20,
                listOf(
                    Answer(R.string.question_20_answer_1, 3),
                    Answer(R.string.question_20_answer_2, 0),
                    Answer(R.string.question_20_answer_3, 6)
                )
            ),
            Question(
                21,
                R.string.question_21,
                listOf(
                    Answer(R.string.question_21_answer_1, 1),
                    Answer(R.string.question_21_answer_2, 6),
                    Answer(R.string.question_21_answer_3, 5),
                    Answer(R.string.question_21_answer_4, 0)
                )
            ),
            Question(
                22,
                R.string.question_22,
                listOf(
                    Answer(R.string.question_22_answer_1, 3),
                    Answer(R.string.question_22_answer_2, 4),
                    Answer(R.string.question_22_answer_3, 2),
                    Answer(R.string.question_22_answer_4, 0)
                )
            ),
            Question(
                23,
                R.string.question_23,
                listOf(
                    Answer(R.string.question_23_answer_1, 5),
                    Answer(R.string.question_23_answer_2, 0),
                    Answer(R.string.question_23_answer_3, 6),
                    Answer(R.string.question_23_answer_4, 2),
                )
            ),
            Question(
                24,
                R.string.question_24,
                listOf(
                    Answer(R.string.question_24_answer_1, 2),
                    Answer(R.string.question_24_answer_2, 0),
                    Answer(R.string.question_24_answer_3, 1)
                )
            )
        )
    }
}