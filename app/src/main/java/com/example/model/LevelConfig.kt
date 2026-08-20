package com.example.model

sealed class LevelObjective {
    abstract val target: Int
    abstract val current: Int
    abstract fun isCompleted(): Boolean
    abstract fun copyWithProgress(newCurrent: Int): LevelObjective
    abstract fun title(): String

    data class CollectPiece(
        val pieceType: PieceType,
        override val target: Int,
        override val current: Int = 0
    ) : LevelObjective() {
        override fun isCompleted(): Boolean = current >= target
        override fun copyWithProgress(newCurrent: Int): LevelObjective =
            copy(current = newCurrent.coerceAtMost(target))
        override fun title(): String = "Collect ${target - current} ${pieceType.displayName}"
    }

    data class ClearBlocker(
        val blockerType: BlockerType,
        override val target: Int,
        override val current: Int = 0
    ) : LevelObjective() {
        override fun isCompleted(): Boolean = current >= target
        override fun copyWithProgress(newCurrent: Int): LevelObjective =
            copy(current = newCurrent.coerceAtMost(target))
        override fun title(): String = "Break ${target - current} ${blockerType.displayName}"
    }

    data class CreateSpecial(
        val specialType: SpecialType,
        override val target: Int,
        override val current: Int = 0
    ) : LevelObjective() {
        override fun isCompleted(): Boolean = current >= target
        override fun copyWithProgress(newCurrent: Int): LevelObjective =
            copy(current = newCurrent.coerceAtMost(target))
        override fun title(): String = "Create ${target - current} Specials"
    }

    data class ReachScore(
        override val target: Int,
        override val current: Int = 0
    ) : LevelObjective() {
        override fun isCompleted(): Boolean = current >= target
        override fun copyWithProgress(newCurrent: Int): LevelObjective =
            copy(current = newCurrent.coerceAtMost(target))
        override fun title(): String = "Score $target pts ($current/$target)"
    }
}

enum class BoosterType(
    val displayName: String,
    val description: String,
    val coinCost: Int
) {
    MIRCHI_BLAST("Mirchi Blast", "Tap any cell to trigger a spicy 3x3 blast!", 100),
    PANI_WAVE("Pani Wave", "Clears an entire row & column in a tidal splash!", 150),
    SPOON_SHUFFLE("Spoon Stir", "Shuffles all pieces for fresh combinations!", 60),
    MEGA_GOLGAPPA("Mega Puri", "Spawns an Ultimate Rainbow Mega Golgappa!", 200),
    EXTRA_MOVES("+5 Moves", "Adds 5 bonus moves to keep playing!", 80)
}

data class LevelConfig(
    val levelNumber: Int,
    val rows: Int = 8,
    val cols: Int = 8,
    val moves: Int = 25,
    val allowedPieces: List<PieceType> = PieceType.values().toList(),
    val objectives: List<LevelObjective>,
    val initialBlockers: Map<Pair<Int, Int>, Pair<BlockerType, Int>> = emptyMap(), // (row, col) -> (Type, HP)
    val starThresholds: Triple<Int, Int, Int> = Triple(2000, 4500, 7500),
    val tutorialMessage: String? = null
)
