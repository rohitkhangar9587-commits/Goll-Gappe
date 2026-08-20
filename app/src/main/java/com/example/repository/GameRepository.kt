package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.AppDatabase
import com.example.data.db.HighScoreDao
import com.example.data.db.HighScoreRecord
import com.example.model.Achievement
import com.example.model.BoosterType
import com.example.model.DailyReward
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

data class PlayerStats(
    val totalLevelsCompleted: Int = 0,
    val totalGolgappasBurst: Int = 0,
    val totalPaniBlasts: Int = 0,
    val totalSpecialsCreated: Int = 0,
    val highestCombo: Int = 1,
    val totalStars: Int = 0
)

class GameRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("pani_puri_crush_prefs", Context.MODE_PRIVATE)
    private val database = AppDatabase.getDatabase(context)
    val highScoreDao: HighScoreDao = database.highScoreDao()

    val top5HighScores: Flow<List<HighScoreRecord>> = highScoreDao.getTop5HighScores()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        // Seed initial high score records if empty
        repositoryScope.launch {
            if (highScoreDao.getScoreCount() == 0) {
                highScoreDao.insertScore(HighScoreRecord(levelNumber = 5, score = 12850, stars = 3, maxCombo = 6, timestamp = System.currentTimeMillis() - 86400000L * 2))
                highScoreDao.insertScore(HighScoreRecord(levelNumber = 4, score = 9420, stars = 3, maxCombo = 5, timestamp = System.currentTimeMillis() - 86400000L))
                highScoreDao.insertScore(HighScoreRecord(levelNumber = 3, score = 7150, stars = 3, maxCombo = 4, timestamp = System.currentTimeMillis() - 43200000L))
                highScoreDao.insertScore(HighScoreRecord(levelNumber = 2, score = 5600, stars = 2, maxCombo = 3, timestamp = System.currentTimeMillis() - 21600000L))
                highScoreDao.insertScore(HighScoreRecord(levelNumber = 1, score = 4200, stars = 3, maxCombo = 3, timestamp = System.currentTimeMillis() - 7200000L))
            }
        }
    }

    private val _unlockedLevel = MutableStateFlow(prefs.getInt(KEY_UNLOCKED_LEVEL, 1))
    val unlockedLevel: StateFlow<Int> = _unlockedLevel.asStateFlow()

    private val _coins = MutableStateFlow(prefs.getInt(KEY_COINS, 250))
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _lives = MutableStateFlow(prefs.getInt(KEY_LIVES, 5))
    val lives: StateFlow<Int> = _lives.asStateFlow()

    private val _boosters = MutableStateFlow(loadBoosters())
    val boosters: StateFlow<Map<BoosterType, Int>> = _boosters.asStateFlow()

    private val _levelStars = MutableStateFlow(loadLevelStars())
    val levelStars: StateFlow<Map<Int, Int>> = _levelStars.asStateFlow()

    private val _highScores = MutableStateFlow(loadHighScores())
    val highScores: StateFlow<Map<Int, Int>> = _highScores.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _musicEnabled = MutableStateFlow(prefs.getBoolean(KEY_MUSIC, true))
    val musicEnabled: StateFlow<Boolean> = _musicEnabled.asStateFlow()

    private val _musicVolume = MutableStateFlow(prefs.getFloat(KEY_MUSIC_VOLUME, 0.8f))
    val musicVolume: StateFlow<Float> = _musicVolume.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTICS, true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _dailyStreak = MutableStateFlow(prefs.getInt(KEY_DAILY_STREAK, 0))
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    private val _lastDailyClaim = MutableStateFlow(prefs.getLong(KEY_LAST_DAILY_CLAIM, 0L))
    val lastDailyClaim: StateFlow<Long> = _lastDailyClaim.asStateFlow()

    private val _playerStats = MutableStateFlow(loadPlayerStats())
    val playerStats: StateFlow<PlayerStats> = _playerStats.asStateFlow()
    val stats: StateFlow<PlayerStats> = playerStats
    val lastDailyClaimTime: StateFlow<Long> = lastDailyClaim

    private val _achievements = MutableStateFlow(loadAchievements())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private fun loadBoosters(): Map<BoosterType, Int> {
        val map = mutableMapOf<BoosterType, Int>()
        for (b in BoosterType.values()) {
            map[b] = prefs.getInt("booster_${b.name}", if (b == BoosterType.MIRCHI_BLAST || b == BoosterType.SPOON_SHUFFLE) 2 else 1)
        }
        return map
    }

    private fun loadLevelStars(): Map<Int, Int> {
        val jsonStr = prefs.getString(KEY_STARS_JSON, "{}") ?: "{}"
        val map = mutableMapOf<Int, Int>()
        try {
            val json = JSONObject(jsonStr)
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                map[k.toInt()] = json.getInt(k)
            }
        } catch (_: Exception) {}
        return map
    }

    private fun loadHighScores(): Map<Int, Int> {
        val jsonStr = prefs.getString(KEY_SCORES_JSON, "{}") ?: "{}"
        val map = mutableMapOf<Int, Int>()
        try {
            val json = JSONObject(jsonStr)
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                map[k.toInt()] = json.getInt(k)
            }
        } catch (_: Exception) {}
        return map
    }

    private fun loadPlayerStats(): PlayerStats {
        return PlayerStats(
            totalLevelsCompleted = prefs.getInt("stat_levels", 0),
            totalGolgappasBurst = prefs.getInt("stat_puris", 0),
            totalPaniBlasts = prefs.getInt("stat_blasts", 0),
            totalSpecialsCreated = prefs.getInt("stat_specials", 0),
            highestCombo = prefs.getInt("stat_combo", 1),
            totalStars = _levelStars.value.values.sum()
        )
    }

    fun completeLevel(levelNumber: Int, score: Int, stars: Int, purisCleared: Int, paniBlasts: Int, specialsCreated: Int, combo: Int) {
        val currentUnlocked = _unlockedLevel.value
        if (levelNumber >= currentUnlocked) {
            val nextLevel = (levelNumber + 1).coerceAtMost(999)
            _unlockedLevel.value = nextLevel
            prefs.edit().putInt(KEY_UNLOCKED_LEVEL, nextLevel).apply()
        }

        // Save stars
        val starsMap = _levelStars.value.toMutableMap()
        val prevStars = starsMap[levelNumber] ?: 0
        if (stars > prevStars) {
            starsMap[levelNumber] = stars
            _levelStars.value = starsMap
            val json = JSONObject()
            starsMap.forEach { (k, v) -> json.put(k.toString(), v) }
            prefs.edit().putString(KEY_STARS_JSON, json.toString()).apply()
        }

        // Save high score to prefs
        val scoresMap = _highScores.value.toMutableMap()
        val prevScore = scoresMap[levelNumber] ?: 0
        if (score > prevScore) {
            scoresMap[levelNumber] = score
            _highScores.value = scoresMap
            val json = JSONObject()
            scoresMap.forEach { (k, v) -> json.put(k.toString(), v) }
            prefs.edit().putString(KEY_SCORES_JSON, json.toString()).apply()
        }

        // Save high score into Room database
        repositoryScope.launch {
            highScoreDao.insertScore(
                HighScoreRecord(
                    levelNumber = levelNumber,
                    score = score,
                    stars = stars,
                    maxCombo = combo,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Award Coins for level completion (50 coins + 25 per star)
        val coinsEarned = 50 + stars * 25
        addCoins(coinsEarned)

        // Update lifetime stats
        val currStats = _playerStats.value
        val newStats = currStats.copy(
            totalLevelsCompleted = currStats.totalLevelsCompleted + 1,
            totalGolgappasBurst = currStats.totalGolgappasBurst + purisCleared,
            totalPaniBlasts = currStats.totalPaniBlasts + paniBlasts,
            totalSpecialsCreated = currStats.totalSpecialsCreated + specialsCreated,
            highestCombo = maxOf(currStats.highestCombo, combo),
            totalStars = starsMap.values.sum()
        )
        _playerStats.value = newStats
        prefs.edit()
            .putInt("stat_levels", newStats.totalLevelsCompleted)
            .putInt("stat_puris", newStats.totalGolgappasBurst)
            .putInt("stat_blasts", newStats.totalPaniBlasts)
            .putInt("stat_specials", newStats.totalSpecialsCreated)
            .putInt("stat_combo", newStats.highestCombo)
            .apply()

        // Update Achievements
        updateAchievementProgress("ach_first_win", 1)
        updateAchievementProgress("ach_puris_100", newStats.totalGolgappasBurst)
        updateAchievementProgress("ach_puris_1000", newStats.totalGolgappasBurst)
        updateAchievementProgress("ach_puris_5000", newStats.totalGolgappasBurst)
        updateAchievementProgress("ach_combo_5", newStats.highestCombo)
        updateAchievementProgress("ach_combo_10", newStats.highestCombo)
        updateAchievementProgress("ach_stars_50", newStats.totalStars)
        updateAchievementProgress("ach_level_10", levelNumber)
        updateAchievementProgress("ach_level_50", levelNumber)
        updateAchievementProgress("ach_level_100", levelNumber)
    }

    fun loseLife() {
        val curr = _lives.value
        if (curr > 0) {
            val newLives = curr - 1
            _lives.value = newLives
            prefs.edit().putInt(KEY_LIVES, newLives).apply()
        }
    }

    fun refillLives() {
        _lives.value = 5
        prefs.edit().putInt(KEY_LIVES, 5).apply()
    }

    fun addCoins(amount: Int) {
        val newCoins = _coins.value + amount
        _coins.value = newCoins
        prefs.edit().putInt(KEY_COINS, newCoins).apply()
    }

    fun spendCoins(amount: Int): Boolean {
        if (_coins.value >= amount) {
            val newCoins = _coins.value - amount
            _coins.value = newCoins
            prefs.edit().putInt(KEY_COINS, newCoins).apply()
            return true
        }
        return false
    }

    fun useBooster(type: BoosterType): Boolean {
        val current = _boosters.value[type] ?: 0
        if (current > 0) {
            val newMap = _boosters.value.toMutableMap()
            newMap[type] = current - 1
            _boosters.value = newMap
            prefs.edit().putInt("booster_${type.name}", current - 1).apply()
            return true
        }
        return false
    }

    fun addBooster(type: BoosterType, count: Int = 1) {
        val current = _boosters.value[type] ?: 0
        val newMap = _boosters.value.toMutableMap()
        newMap[type] = current + count
        _boosters.value = newMap
        prefs.edit().putInt("booster_${type.name}", current + count).apply()
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
    }

    fun setMusicEnabled(enabled: Boolean) {
        _musicEnabled.value = enabled
        prefs.edit().putBoolean(KEY_MUSIC, enabled).apply()
    }

    fun setMusicVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _musicVolume.value = clamped
        prefs.edit().putFloat(KEY_MUSIC_VOLUME, clamped).apply()
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
    }

    fun claimDailyReward(day: Int, reward: DailyReward) {
        val now = System.currentTimeMillis()
        val nextStreak = if (day >= 7) 1 else day
        _dailyStreak.value = nextStreak
        _lastDailyClaim.value = now
        prefs.edit()
            .putInt(KEY_DAILY_STREAK, nextStreak)
            .putLong(KEY_LAST_DAILY_CLAIM, now)
            .apply()

        if (reward.rewardType == "COINS") {
            addCoins(reward.amount)
        } else if (reward.rewardType == "BOOSTER" && reward.boosterType != null) {
            addBooster(reward.boosterType, reward.amount)
        } else if (reward.rewardType == "LIFE") {
            refillLives()
        }
    }

    private fun loadAchievements(): List<Achievement> {
        val stats = _playerStats.value
        return listOf(
            Achievement("ach_first_win", "First Golgappa Bite", "Complete your first level", 1, minOf(stats.totalLevelsCompleted, 1), 50, prefs.getBoolean("claimed_ach_first_win", false)),
            Achievement("ach_puris_100", "Spice Novice", "Burst 100 Golgappas", 100, stats.totalGolgappasBurst, 100, prefs.getBoolean("claimed_ach_puris_100", false)),
            Achievement("ach_puris_1000", "Pani Puri Enthusiast", "Burst 1,000 Golgappas", 1000, stats.totalGolgappasBurst, 250, prefs.getBoolean("claimed_ach_puris_1000", false)),
            Achievement("ach_puris_5000", "Golgappa Master", "Burst 5,000 Golgappas", 5000, stats.totalGolgappasBurst, 500, prefs.getBoolean("claimed_ach_puris_5000", false)),
            Achievement("ach_combo_5", "Crispy Combo x5", "Achieve a Combo multiplier of 5x", 5, stats.highestCombo, 100, prefs.getBoolean("claimed_ach_combo_5", false)),
            Achievement("ach_combo_10", "Tsunami Splash x10", "Achieve a massive Combo multiplier of 10x", 10, stats.highestCombo, 300, prefs.getBoolean("claimed_ach_combo_10", false)),
            Achievement("ach_stars_50", "Star Chaser", "Earn 50 Stars across all levels", 50, stats.totalStars, 200, prefs.getBoolean("claimed_ach_stars_50", false)),
            Achievement("ach_level_10", "Double Digit Spice", "Reach Level 10", 10, _unlockedLevel.value, 150, prefs.getBoolean("claimed_ach_level_10", false)),
            Achievement("ach_level_50", "Half-Century Hero", "Reach Level 50", 50, _unlockedLevel.value, 400, prefs.getBoolean("claimed_ach_level_50", false)),
            Achievement("ach_level_100", "Century Legend", "Reach Level 100", 100, _unlockedLevel.value, 1000, prefs.getBoolean("claimed_ach_level_100", false))
        )
    }

    private fun updateAchievementProgress(id: String, value: Int) {
        val list = _achievements.value.map { ach ->
            if (ach.id == id) {
                ach.copy(current = maxOf(ach.current, value))
            } else ach
        }
        _achievements.value = list
    }

    fun claimAchievement(id: String): Boolean {
        val list = _achievements.value.map { ach ->
            if (ach.id == id && ach.isCompleted && !ach.isClaimed) {
                addCoins(ach.rewardCoins)
                prefs.edit().putBoolean("claimed_$id", true).apply()
                ach.copy(isClaimed = true)
            } else ach
        }
        _achievements.value = list
        return true
    }

    fun resetAllData() {
        prefs.edit().clear().apply()
        _unlockedLevel.value = 1
        _coins.value = 250
        _lives.value = 5
        _boosters.value = loadBoosters()
        _levelStars.value = emptyMap()
        _highScores.value = emptyMap()
        _dailyStreak.value = 0
        _lastDailyClaim.value = 0L
        _playerStats.value = PlayerStats()
        _achievements.value = loadAchievements()
    }

    fun resetAllProgress() = resetAllData()

    companion object {
        private const val KEY_UNLOCKED_LEVEL = "key_unlocked_level"
        private const val KEY_COINS = "key_coins"
        private const val KEY_LIVES = "key_lives"
        private const val KEY_STARS_JSON = "key_stars_json"
        private const val KEY_SCORES_JSON = "key_scores_json"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_MUSIC = "key_music"
        private const val KEY_MUSIC_VOLUME = "key_music_volume"
        private const val KEY_HAPTICS = "key_haptics"
        private const val KEY_DAILY_STREAK = "key_daily_streak"
        private const val KEY_LAST_DAILY_CLAIM = "key_last_daily_claim"
    }
}
