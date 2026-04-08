package com.funnygaytest.utils.helpers

import com.funnygaytest.R
import com.funnygaytest.models.Answer
import com.funnygaytest.models.Question

fun generateNewGameRun(): List<Question> {
    val runQuestions = mutableListOf<Question>()

    val slot1Variants = listOf(
        Question(
            id = "q_1_a",
            questionResId = R.string.question_1_a,
            listOfAnswers = listOf(
                Answer(R.string.question_1_a_answer_1, 0),
                Answer(R.string.question_1_a_answer_2, -30),
                Answer(R.string.question_1_a_answer_3, 0),
                Answer(R.string.question_1_a_answer_4, -30),
                Answer(R.string.question_1_a_answer_5, -10)
            )
        ),
        Question(
            id = "q_1_b",
            questionResId = R.string.question_1_b,
            listOfAnswers = listOf(
                Answer(R.string.question_1_b_answer_1, -10),
                Answer(R.string.question_1_b_answer_2, -15),
                Answer(R.string.question_1_b_answer_3, 0),
                Answer(R.string.question_1_b_answer_4, -50)
            )
        )
    )
    runQuestions.add(slot1Variants.random())

    runQuestions.add(
        Question(
            id = "q_2",
            questionResId = R.string.question_2,
            listOfAnswers = listOf(
                Answer(R.string.question_2_answer_1, -10),
                Answer(R.string.question_2_answer_2, -25),
                Answer(R.string.question_2_answer_3, 0),
                Answer(R.string.question_2_answer_4, 5)
            )
        )
    )

    val slot3Variants = listOf(
        Question(
            id = "q_3_a",
            questionResId = R.string.question_3_a,
            listOfAnswers = listOf(
                Answer(R.string.question_3_a_answer_1, -5),
                Answer(R.string.question_3_a_answer_2, -30),
                Answer(R.string.question_3_a_answer_3, 0),
                Answer(R.string.question_3_a_answer_4, -50)
            )
        ),
        Question(
            id = "q_3_b",
            questionResId = R.string.question_3_b,
            listOfAnswers = listOf(
                Answer(R.string.question_3_b_answer_1, 5),
                Answer(R.string.question_3_b_answer_2, -35),
                Answer(R.string.question_3_b_answer_3, -10)
            )
        )
    )
    runQuestions.add(slot3Variants.random())

    runQuestions.add(
        Question(
            id = "q_4",
            questionResId = R.string.question_4,
            listOfAnswers = listOf(
                Answer(R.string.question_4_answer_1, -10),
                Answer(R.string.question_4_answer_2, -30),
                Answer(R.string.question_4_answer_3, 0)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_5",
            questionResId = R.string.question_5,
            listOfAnswers = listOf(
                Answer(R.string.question_5_answer_1, -15),
                Answer(R.string.question_5_answer_2, 0),
                Answer(R.string.question_5_answer_3, -10),
                Answer(R.string.question_5_answer_4, -30)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_6",
            questionResId = R.string.question_6,
            listOfAnswers = listOf(
                Answer(R.string.question_6_answer_1, -10),
                Answer(R.string.question_6_answer_2, 0),
                Answer(R.string.question_6_answer_3, -10),
                Answer(R.string.question_6_answer_4, 5)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_7",
            questionResId = R.string.question_7,
            listOfAnswers = listOf(
                Answer(R.string.question_7_answer_1, 0),
                Answer(R.string.question_7_answer_2, -20),
                Answer(R.string.question_7_answer_3, -40),
                Answer(R.string.question_7_answer_4, -10)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_8",
            questionResId = R.string.question_8,
            listOfAnswers = listOf(
                Answer(R.string.question_8_answer_1, 0),
                Answer(R.string.question_8_answer_2, -30),
                Answer(R.string.question_8_answer_3, 5)
            )
        )
    )

    val slot9Variants = listOf(
        Question(
            id = "q_9_a",
            questionResId = R.string.question_9_a,
            listOfAnswers = listOf(
                Answer(R.string.question_9_a_answer_1, -5),
                Answer(R.string.question_9_a_answer_2, -15),
                Answer(R.string.question_9_a_answer_3, -10),
                Answer(R.string.question_9_a_answer_4, 5)
            )
        ),
        Question(
            id = "q_9_b",
            questionResId = R.string.question_9_b,
            listOfAnswers = listOf(
                Answer(R.string.question_9_b_answer_1, -15),
                Answer(R.string.question_9_b_answer_2, -50),
                Answer(R.string.question_9_b_answer_3, 0)
            )
        )
    )
    runQuestions.add(slot9Variants.random())

    runQuestions.add(
        Question(
            id = "q_10",
            questionResId = R.string.question_10,
            listOfAnswers = listOf(
                Answer(R.string.question_10_answer_1, 0),
                Answer(R.string.question_10_answer_2, -10),
                Answer(R.string.question_10_answer_3, -20),
                Answer(R.string.question_10_answer_4, -30)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_11",
            questionResId = R.string.question_11,
            listOfAnswers = listOf(
                Answer(R.string.question_11_answer_1, 0),
                Answer(R.string.question_11_answer_2, -25),
                Answer(R.string.question_11_answer_3, -10),
                Answer(R.string.question_11_answer_4, -15)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_12",
            questionResId = R.string.question_12,
            listOfAnswers = listOf(
                Answer(R.string.question_12_answer_1, -50),
                Answer(R.string.question_12_answer_2, -5),
                Answer(R.string.question_12_answer_3, 0),
                Answer(R.string.question_12_answer_4, -15)
            )
        )
    )

    val slot13Variants = listOf(
        Question(
            id = "q_13_a",
            questionResId = R.string.question_13_a,
            listOfAnswers = listOf(
                Answer(R.string.question_13_a_answer_1, -5),
                Answer(R.string.question_13_a_answer_2, -15),
                Answer(R.string.question_13_a_answer_3, 5),
                Answer(R.string.question_13_a_answer_4, -30)
            )
        ),
        Question(
            id = "q_13_b",
            questionResId = R.string.question_13_b,
            listOfAnswers = listOf(
                Answer(R.string.question_13_b_answer_1, 0),
                Answer(R.string.question_13_b_answer_2, -10),
                Answer(R.string.question_13_b_answer_3, -30),
                Answer(R.string.question_13_b_answer_4, -20)
            )
        )
    )
    runQuestions.add(slot13Variants.random())

    runQuestions.add(
        Question(
            id = "q_14",
            questionResId = R.string.question_14,
            listOfAnswers = listOf(
                Answer(R.string.question_14_answer_1, 0),
                Answer(R.string.question_14_answer_2, -25),
                Answer(R.string.question_14_answer_3, -10),
                Answer(R.string.question_14_answer_4, -15)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_15",
            questionResId = R.string.question_15,
            listOfAnswers = listOf(
                Answer(R.string.question_15_answer_1, -50),
                Answer(R.string.question_15_answer_2, 0),
                Answer(R.string.question_15_answer_3, -30),
                Answer(R.string.question_15_answer_4, -10)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_16",
            questionResId = R.string.question_16,
            listOfAnswers = listOf(
                Answer(R.string.question_16_answer_1, -15),
                Answer(R.string.question_16_answer_2, 0),
                Answer(R.string.question_16_answer_3, -25)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_17",
            questionResId = R.string.question_17,
            listOfAnswers = listOf(
                Answer(R.string.question_17_answer_1, -25),
                Answer(R.string.question_17_answer_2, -15),
                Answer(R.string.question_17_answer_3, 0),
                Answer(R.string.question_17_answer_4, 5)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_18",
            questionResId = R.string.question_18,
            listOfAnswers = listOf(
                Answer(R.string.question_18_answer_1, -30),
                Answer(R.string.question_18_answer_2, -30),
                Answer(R.string.question_18_answer_3, -25),
                Answer(R.string.question_18_answer_4, 5)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_19",
            questionResId = R.string.question_19,
            listOfAnswers = listOf(
                Answer(R.string.question_19_answer_1, -10),
                Answer(R.string.question_19_answer_2, 0),
                Answer(R.string.question_19_answer_3, -40),
                Answer(R.string.question_19_answer_4, -10)
            )
        )
    )

    runQuestions.add(
        Question(
            id = "q_20",
            questionResId = R.string.question_20,
            listOfAnswers = listOf(
                Answer(R.string.question_20_answer_1, -50),
                Answer(R.string.question_20_answer_2, 5),
                Answer(R.string.question_20_answer_3, -15)
            )
        )
    )

    return runQuestions
}