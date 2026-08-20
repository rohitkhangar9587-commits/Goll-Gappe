package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "high_scores")
data class HighScoreRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val levelNumber: Int,
    val score: Int,
    val stars: Int = 3,
    val maxCombo: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)
