package com.example.engine

import com.example.model.BlockerType
import com.example.model.LevelConfig
import com.example.model.LevelObjective
import com.example.model.PieceType
import com.example.model.SpecialType
import kotlin.random.Random

object LevelGenerator {

    fun getLevel(levelNumber: Int): LevelConfig {
        return when (levelNumber) {
            1 -> LevelConfig(
                levelNumber = 1,
                rows = 8,
                cols = 8,
                moves = 25,
                allowedPieces = listOf(PieceType.CLASSIC, PieceType.PUDINA, PieceType.IMLI),
                objectives = listOf(
                    LevelObjective.CollectPiece(PieceType.CLASSIC, target = 15)
                ),
                starThresholds = Triple(1500, 3000, 5000),
                tutorialMessage = "Drag and drop or swipe adjacent Golgappas to match 3 of the same flavor!"
            )
            2 -> LevelConfig(
                levelNumber = 2,
                rows = 8,
                cols = 8,
                moves = 24,
                allowedPieces = listOf(PieceType.CLASSIC, PieceType.PUDINA, PieceType.IMLI, PieceType.MASALA),
                objectives = listOf(
                    LevelObjective.CollectPiece(PieceType.PUDINA, target = 18),
                    LevelObjective.CreateSpecial(SpecialType.HORIZONTAL_LINE, target = 1)
                ),
                starThresholds = Triple(2000, 4000, 6500),
                tutorialMessage = "Match 4 Golgappas in a line to create a powerful Line Blast!"
            )
            3 -> LevelConfig(
                levelNumber = 3,
                rows = 8,
                cols = 8,
                moves = 25,
                allowedPieces = listOf(PieceType.CLASSIC, PieceType.PUDINA, PieceType.IMLI, PieceType.MASALA),
                objectives = listOf(
                    LevelObjective.CollectPiece(PieceType.IMLI, target = 20),
                    LevelObjective.CreateSpecial(SpecialType.BOMB, target = 1)
                ),
                starThresholds = Triple(2500, 5000, 8000),
                tutorialMessage = "Match in a T or L shape to make a Spicy Bomb Golgappa!"
            )
            4 -> LevelConfig(
                levelNumber = 4,
                rows = 8,
                cols = 8,
                moves = 26,
                allowedPieces = listOf(PieceType.CLASSIC, PieceType.PUDINA, PieceType.IMLI, PieceType.MASALA, PieceType.ALOO),
                objectives = listOf(
                    LevelObjective.CollectPiece(PieceType.MASALA, target = 22),
                    LevelObjective.CreateSpecial(SpecialType.MEGA, target = 1)
                ),
                starThresholds = Triple(3000, 6000, 9500),
                tutorialMessage = "Match 5 Golgappas in a line to create the Rainbow Mega Golgappa!"
            )
            5 -> LevelConfig(
                levelNumber = 5,
                rows = 8,
                cols = 8,
                moves = 25,
                allowedPieces = listOf(PieceType.CLASSIC, PieceType.PUDINA, PieceType.IMLI, PieceType.MASALA, PieceType.ALOO),
                objectives = listOf(
                    LevelObjective.ClearBlocker(BlockerType.ALOO_BLOCK, target = 8),
                    LevelObjective.CollectPiece(PieceType.ALOO, target = 15)
                ),
                initialBlockers = mapOf(
                    Pair(3, 3) to Pair(BlockerType.ALOO_BLOCK, 1),
                    Pair(3, 4) to Pair(BlockerType.ALOO_BLOCK, 1),
                    Pair(4, 3) to Pair(BlockerType.ALOO_BLOCK, 1),
                    Pair(4, 4) to Pair(BlockerType.ALOO_BLOCK, 1),
                    Pair(2, 2) to Pair(BlockerType.ALOO_BLOCK, 1),
                    Pair(2, 5) to Pair(BlockerType.ALOO_BLOCK, 1),
                    Pair(5, 2) to Pair(BlockerType.ALOO_BLOCK, 1),
                    Pair(5, 5) to Pair(BlockerType.ALOO_BLOCK, 1)
                ),
                starThresholds = Triple(3500, 7000, 11000),
                tutorialMessage = "Match adjacent pieces or use specials to break Aloo Chunks!"
            )
            6 -> LevelConfig(
                levelNumber = 6,
                rows = 8,
                cols = 8,
                moves = 28,
                allowedPieces = listOf(PieceType.CLASSIC, PieceType.PUDINA, PieceType.IMLI, PieceType.MASALA, PieceType.ALOO),
                objectives = listOf(
                    LevelObjective.ClearBlocker(BlockerType.MATKA_BLOCK, target = 6),
                    LevelObjective.CollectPiece(PieceType.CLASSIC, target = 25)
                ),
                initialBlockers = mapOf(
                    Pair(1, 1) to Pair(BlockerType.MATKA_BLOCK, 2),
                    Pair(1, 6) to Pair(BlockerType.MATKA_BLOCK, 2),
                    Pair(3, 3) to Pair(BlockerType.MATKA_BLOCK, 3),
                    Pair(3, 4) to Pair(BlockerType.MATKA_BLOCK, 3),
                    Pair(6, 1) to Pair(BlockerType.MATKA_BLOCK, 2),
                    Pair(6, 6) to Pair(BlockerType.MATKA_BLOCK, 2)
                ),
                starThresholds = Triple(4000, 8000, 12000),
                tutorialMessage = "Clay Matkas take multiple hits to break open!"
            )
            7 -> LevelConfig(
                levelNumber = 7,
                rows = 8,
                cols = 8,
                moves = 24,
                allowedPieces = PieceType.values().toList(),
                objectives = listOf(
                    LevelObjective.ClearBlocker(BlockerType.SEALED_GOLGAPPA, target = 8),
                    LevelObjective.CollectPiece(PieceType.DAHI, target = 20)
                ),
                initialBlockers = mapOf(
                    Pair(2, 3) to Pair(BlockerType.SEALED_GOLGAPPA, 1),
                    Pair(2, 4) to Pair(BlockerType.SEALED_GOLGAPPA, 1),
                    Pair(3, 2) to Pair(BlockerType.SEALED_GOLGAPPA, 1),
                    Pair(3, 5) to Pair(BlockerType.SEALED_GOLGAPPA, 1),
                    Pair(4, 2) to Pair(BlockerType.SEALED_GOLGAPPA, 1),
                    Pair(4, 5) to Pair(BlockerType.SEALED_GOLGAPPA, 1),
                    Pair(5, 3) to Pair(BlockerType.SEALED_GOLGAPPA, 1),
                    Pair(5, 4) to Pair(BlockerType.SEALED_GOLGAPPA, 1)
                ),
                starThresholds = Triple(4500, 9000, 13500),
                tutorialMessage = "Crack sealed puris to release playable Golgappas!"
            )
            else -> generateProceduralLevel(levelNumber)
        }
    }

    private fun generateProceduralLevel(level: Int): LevelConfig {
        val seed = level * 31337L
        val rng = Random(seed)

        val rows = 8
        val cols = 8
        val moves = (22 + (rng.nextInt(10) - (level / 100).coerceAtMost(6))).coerceIn(18, 35)

        // Allowed pieces
        val allowedPieces = when {
            level <= 15 -> listOf(PieceType.CLASSIC, PieceType.PUDINA, PieceType.IMLI, PieceType.MASALA)
            level <= 40 -> listOf(PieceType.CLASSIC, PieceType.PUDINA, PieceType.IMLI, PieceType.MASALA, PieceType.ALOO)
            else -> PieceType.values().toList()
        }

        // Objectives generator
        val objectives = mutableListOf<LevelObjective>()
        val p1 = allowedPieces[rng.nextInt(allowedPieces.size)]
        val p1Target = (15 + (level * 0.4).toInt() + rng.nextInt(8)).coerceIn(15, 45)
        objectives.add(LevelObjective.CollectPiece(p1, p1Target))

        val blockerMap = mutableMapOf<Pair<Int, Int>, Pair<BlockerType, Int>>()

        if (level >= 8) {
            val numBlockers = (4 + (level / 15) + rng.nextInt(6)).coerceIn(4, 16)
            val blockerType = when (rng.nextInt(4)) {
                0 -> BlockerType.ALOO_BLOCK
                1 -> BlockerType.MATKA_BLOCK
                2 -> BlockerType.MASALA_BLOCK
                else -> BlockerType.SEALED_GOLGAPPA
            }
            val hp = if (blockerType == BlockerType.MATKA_BLOCK) (1 + (level / 50).coerceAtMost(2)) else 1

            // Place symmetrically or clustered
            var placed = 0
            val patternChoice = rng.nextInt(3)
            for (r in 1..6) {
                for (c in 1..6) {
                    val shouldPlace = when (patternChoice) {
                        0 -> (r in 2..5 && c in 2..5 && rng.nextBoolean()) // Center cluster
                        1 -> ((r == 1 || r == 6 || c == 1 || c == 6) && rng.nextBoolean()) // Border
                        else -> ((r + c) % 2 == 0 && rng.nextBoolean()) // Checkerboard
                    }
                    if (shouldPlace && placed < numBlockers) {
                        blockerMap[Pair(r, c)] = Pair(blockerType, hp)
                        placed++
                    }
                }
            }

            if (placed > 0 && rng.nextBoolean()) {
                objectives.add(LevelObjective.ClearBlocker(blockerType, target = placed))
            } else if (rng.nextBoolean()) {
                val p2 = allowedPieces.filter { it != p1 }.random(rng)
                objectives.add(LevelObjective.CollectPiece(p2, target = (12 + rng.nextInt(15)).coerceIn(12, 30)))
            } else {
                objectives.add(LevelObjective.CreateSpecial(SpecialType.HORIZONTAL_LINE, target = (2 + rng.nextInt(3))))
            }
        }

        val baseScore = 3000 + level * 120
        val starThresholds = Triple(baseScore, (baseScore * 1.8).toInt(), (baseScore * 2.8).toInt())

        val tutorialMessage = if (level % 25 == 0) "Spice Boss Stage! High combos are required!" else null

        return LevelConfig(
            levelNumber = level,
            rows = rows,
            cols = cols,
            moves = moves,
            allowedPieces = allowedPieces,
            objectives = objectives,
            initialBlockers = blockerMap,
            starThresholds = starThresholds,
            tutorialMessage = tutorialMessage
        )
    }
}
