package com.example.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val current: Int,
    val rewardCoins: Int,
    val isClaimed: Boolean = false
) {
    val isCompleted: Boolean
        get() = current >= target
    val progressPercent: Float
        get() = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
}

data class DailyReward(
    val dayNumber: Int,
    val title: String,
    val rewardType: String, // "COINS" or "BOOSTER" or "LIFE"
    val amount: Int,
    val boosterType: BoosterType? = null
)
