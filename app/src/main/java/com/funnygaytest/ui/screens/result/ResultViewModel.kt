package com.funnygaytest.ui.screens.result

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnygaytest.R
import com.funnygaytest.data.game.GameSessionRepository
import com.funnygaytest.managers.firebase.firestore.FirestoreManager
import com.funnygaytest.managers.music.MusicController
import com.funnygaytest.models.firebase.LabStats
import com.funnygaytest.utils.enums.EndingType
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultUiState(
    val isRateEnabled: Boolean = true,
    val healthLeft: Int,
    val lastQuestionNumber: Int,
    @param:StringRes val titleRes: Int = titleRes(healthLeft, lastQuestionNumber),
    @param:StringRes val resultTextRes: Int = resultTextRes(healthLeft, lastQuestionNumber),
    val currentEnding: EndingType? = null,
    val isNewEnding: Boolean = false,
    val isAllEndingsUnlocked: Boolean = false
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    musicController: MusicController,
    private val savedStateHandle: SavedStateHandle,
    private val gameSessionRepository: GameSessionRepository,
    private val firestoreManager: FirestoreManager,
    @param:ApplicationContext private val appContext: Context
) : ViewModel(), MusicController by musicController {

    private val _uiState = MutableStateFlow<ResultUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private var reviewManager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null

    init {
        viewModelScope.launch {
            val (health, questionNumber) = restoreRunOutcome() ?: takeFinishedRun()
            _uiState.value = ResultUiState(healthLeft = health, lastQuestionNumber = questionNumber)
            requestReviewInfo()
            if (!restoreEnding()) recordEnding(health, questionNumber)
        }
    }

    fun rateUs(activity: Activity) {
        reviewInfo?.let { reviewInfo ->
            val flow = reviewManager?.launchReviewFlow(activity, reviewInfo)
            flow?.addOnCompleteListener {
                _uiState.update { it?.copy(isRateEnabled = false) }
            }
        }
    }

    private fun restoreRunOutcome(): Pair<Int, Int>? {
        val health = savedStateHandle.get<Int>(KEY_HEALTH) ?: return null
        val questionNumber = savedStateHandle.get<Int>(KEY_QUESTION_NUMBER) ?: return null
        return health to questionNumber
    }

    private suspend fun takeFinishedRun(): Pair<Int, Int> {
        val session = gameSessionRepository.session.first()
        savedStateHandle[KEY_HEALTH] = session.health
        savedStateHandle[KEY_QUESTION_NUMBER] = session.questionNumber
        gameSessionRepository.clear()
        return session.health to session.questionNumber
    }

    private fun restoreEnding(): Boolean {
        val endingId = savedStateHandle.get<String>(KEY_ENDING) ?: return false
        val ending = EndingType.entries.find { it.id == endingId } ?: return false
        showEnding(
            ending = ending,
            isNew = savedStateHandle[KEY_IS_NEW_ENDING] ?: false,
            isAllUnlocked = savedStateHandle[KEY_ALL_ENDINGS_UNLOCKED] ?: false
        )
        return true
    }

    private suspend fun recordEnding(health: Int, questionNumber: Int) {
        val ending = endingFor(health, questionNumber)
        val achievements = (firestoreManager.getStats().firstOrNull() ?: LabStats()).achievements

        val isNew = ending.id !in achievements
        val uniqueCoreEndings = (achievements + ending.id)
            .filter { it != EndingType.ALL.id && it != EndingType.DONATE.id }
            .distinct()
            .size
        val wasAllUnlocked = EndingType.ALL.id in achievements
        val unlocksAll = uniqueCoreEndings >= CORE_ENDINGS_COUNT && !wasAllUnlocked

        savedStateHandle[KEY_ENDING] = ending.id
        savedStateHandle[KEY_IS_NEW_ENDING] = isNew
        savedStateHandle[KEY_ALL_ENDINGS_UNLOCKED] = wasAllUnlocked || unlocksAll

        firestoreManager.recordTestResult(isWin = health > 0, achievementId = ending.id)
        if (unlocksAll) firestoreManager.recordTestResult(achievementId = EndingType.ALL.id)

        showEnding(ending, isNew, isAllUnlocked = wasAllUnlocked || unlocksAll)
    }

    private fun showEnding(ending: EndingType, isNew: Boolean, isAllUnlocked: Boolean) {
        _uiState.update {
            it?.copy(
                currentEnding = ending,
                isNewEnding = isNew,
                isAllEndingsUnlocked = isAllUnlocked
            )
        }
    }

    private fun requestReviewInfo() {
        reviewManager = ReviewManagerFactory.create(appContext)
        val request = reviewManager?.requestReviewFlow()
        request?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _uiState.update { it?.copy(isRateEnabled = true) }
                reviewInfo = task.result
            } else {
                _uiState.update { it?.copy(isRateEnabled = false) }
            }
        }
    }

    private companion object {
        const val CORE_ENDINGS_COUNT = 10
        const val KEY_HEALTH = "health"
        const val KEY_QUESTION_NUMBER = "questionNumber"
        const val KEY_ENDING = "ending"
        const val KEY_IS_NEW_ENDING = "isNewEnding"
        const val KEY_ALL_ENDINGS_UNLOCKED = "allEndingsUnlocked"
    }

}

private fun endingFor(health: Int, questionNumber: Int): EndingType = when {
    health > 0 -> when (health) {
        100 -> EndingType.WIN_100
        in 66..99 -> EndingType.WIN_66
        in 33..65 -> EndingType.WIN_33
        else -> EndingType.WIN_1
    }
    else -> when (questionNumber) {
        in 2..7 -> EndingType.LOSE_4
        in 8..11 -> EndingType.LOSE_8
        in 12..15 -> EndingType.LOSE_12
        in 16..19 -> EndingType.LOSE_16
        20 -> EndingType.LOSE_20
        else -> EndingType.LOSE_PUSSY
    }
}

@StringRes
private fun titleRes(healthLeft: Int, lastQuestionNumber: Int): Int = when {
    healthLeft > 0 -> R.string.result_title_win
    lastQuestionNumber == 1 -> R.string.result_title_lose_pussy
    else -> R.string.result_title_lose
}

@StringRes
private fun resultTextRes(healthLeft: Int, lastQuestionNumber: Int): Int =
    if (healthLeft > 0) {
        when (healthLeft) {
            100 -> R.string.result_text_result_win_100
            in 66..99 -> R.string.result_text_result_win_66_99
            in 33..65 -> R.string.result_text_result_win_33_65
            else -> R.string.result_text_result_win_1_32
        }
    } else {
        when (lastQuestionNumber) {
            in 2..7 -> R.string.result_text_result_lose_4_7
            in 8..11 -> R.string.result_text_result_lose_8_11
            in 12..15 -> R.string.result_text_result_lose_12_15
            in 16..19 -> R.string.result_text_result_lose_16_19
            20 -> R.string.result_text_result_lose_20
            else -> R.string.result_text_result_lose_pussy
        }
    }
