package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Particle(
    val id: Long,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val radius: Float,
    val alpha: Float = 1f,
    val isWaterDrop: Boolean = false,
    val isSplashBubble: Boolean = false
)

data class FloatingText(
    val id: Long,
    val text: String,
    val x: Float,
    val y: Float,
    val color: Color = Color.Yellow,
    val alpha: Float = 1f,
    val isPaniBlast: Boolean = false
)

data class ExplosionEffect(
    val id: Long,
    val centerRow: Int,
    val centerCol: Int,
    val radius: Float,
    val color: Color,
    val progress: Float
)

data class PaniBlastEffect(
    val id: Long,
    val centerX: Float,
    val centerY: Float,
    val currentRadius: Float = 0f,
    val maxRadius: Float = 140f,
    val color: Color = Color(0xFF00E5FF),
    val secondaryColor: Color = Color(0xFF00E676),
    val alpha: Float = 1f
)

enum class GameResult {
    PLAYING,
    WON,
    LOST
}

data class Match3GameState(
    val levelConfig: LevelConfig,
    val board: List<List<BoardCell>>,
    val movesRemaining: Int,
    val currentScore: Int,
    val objectives: List<LevelObjective>,
    val selectedCell: Pair<Int, Int>? = null,
    val isResolving: Boolean = false,
    val comboMultiplier: Int = 1,
    val bestCombo: Int = 1,
    val gameResult: GameResult = GameResult.PLAYING,
    val activeBooster: BoosterType? = null,
    val hintCells: List<Pair<Int, Int>> = emptyList(),
    val totalGolgappasCleared: Int = 0,
    val totalPaniBlasts: Int = 0,
    val totalSpecialsCreated: Int = 0,
    val isPaused: Boolean = false
)
