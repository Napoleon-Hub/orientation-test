package com.funnygaytest.utils.enums

import androidx.annotation.DrawableRes
import com.funnygaytest.R

enum class EndingType(
    val id: String,
    @param:DrawableRes val iconRes: Int,
    val titleRes: Int
) {
    WIN_100("win_100", R.drawable.ic_result_win_100, R.string.ending_win_100),
    WIN_66("win_66", R.drawable.ic_result_win_66, R.string.ending_win_66),
    WIN_33("win_33", R.drawable.ic_result_win_33, R.string.ending_win_33),
    WIN_1("win_1", R.drawable.ic_result_win_1, R.string.ending_win_1),
    LOSE_4("lose_4", R.drawable.ic_result_lose_4, R.string.ending_lose_4),
    LOSE_8("lose_8", R.drawable.ic_result_lose_8, R.string.ending_lose_8),
    LOSE_12("lose_12", R.drawable.ic_result_lose_12, R.string.ending_lose_12),
    LOSE_16("lose_16", R.drawable.ic_result_lose_16, R.string.ending_lose_16),
    LOSE_20("lose_20", R.drawable.ic_result_lose_20, R.string.ending_lose_20),
    LOSE_PUSSY("lose_pussy", R.drawable.ic_result_lose_pussy, R.string.ending_lose_pussy),
    ALL("all_endings", R.drawable.ic_result_all, R.string.ending_all)
}