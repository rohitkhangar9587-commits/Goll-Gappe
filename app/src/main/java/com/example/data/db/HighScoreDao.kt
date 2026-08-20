package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {

    @Query("SELECT * FROM high_scores ORDER BY score DESC LIMIT 5")
    fun getTop5HighScores(): Flow<List<HighScoreRecord>>

    @Query("SELECT * FROM high_scores WHERE levelNumber = :levelNumber ORDER BY score DESC LIMIT 5")
    fun getTop5ForLevel(levelNumber: Int): Flow<List<HighScoreRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(scoreRecord: HighScoreRecord)

    @Query("SELECT COUNT(*) FROM high_scores")
    suspend fun getScoreCount(): Int

    @Query("DELETE FROM high_scores")
    suspend fun clearAllScores()
}
