package com.funnygaytest.utils.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.funnygaytest.R

enum class EndingType(
    val id: String,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int
) {
    WIN_100("win_100", R.drawable.ic_result_win_100, R.string.ending_win_100, R.string.result_text_result_win_100),
    WIN_66("win_66", R.drawable.ic_result_win_66, R.string.ending_win_66, R.string.result_text_result_win_66_99),
    WIN_33("win_33", R.drawable.ic_result_win_33, R.string.ending_win_33, R.string.result_text_result_win_33_65),
    WIN_1("win_1", R.drawable.ic_result_win_1, R.string.ending_win_1, R.string.result_text_result_win_1_32),
    LOSE_4("lose_4", R.drawable.ic_result_lose_4, R.string.ending_lose_4, R.string.result_text_result_lose_4_7),
    LOSE_8("lose_8", R.drawable.ic_result_lose_8, R.string.ending_lose_8, R.string.result_text_result_lose_8_11),
    LOSE_12("lose_12", R.drawable.ic_result_lose_12, R.string.ending_lose_12, R.string.result_text_result_lose_12_15),
    LOSE_16("lose_16", R.drawable.ic_result_lose_16, R.string.ending_lose_16, R.string.result_text_result_lose_16_19),
    LOSE_20("lose_20", R.drawable.ic_result_lose_20, R.string.ending_lose_20, R.string.result_text_result_lose_20),
    LOSE_PUSSY("lose_pussy", R.drawable.ic_result_lose_pussy, R.string.ending_lose_pussy, R.string.result_text_result_lose_pussy),
    ALL("all_endings", R.drawable.ic_result_all, R.string.ending_all, R.string.result_text_result_gaylord),
    DONATE("donate", R.drawable.ic_donate, R.string.ending_donate, R.string.feed_description_after_pay)
}