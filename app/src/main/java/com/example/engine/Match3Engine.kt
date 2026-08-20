package com.example.engine

import com.example.model.BlockerType
import com.example.model.BoardCell
import com.example.model.LevelConfig
import com.example.model.PieceType
import com.example.model.SpecialType
import kotlin.random.Random

data class MatchGroup(
    val cells: Set<Pair<Int, Int>>,
    val pieceType: PieceType,
    val specialCreated: SpecialType = SpecialType.NONE,
    val specialPosition: Pair<Int, Int>? = null
)

data class ResolveStep(
    val board: List<List<BoardCell>>,
    val clearedPieces: Map<PieceType, Int>,
    val clearedBlockers: Map<BlockerType, Int>,
    val createdSpecials: Map<SpecialType, Int>,
    val pointsAwarded: Int,
    val explodedCells: Set<Pair<Int, Int>>,
    val waterSplashes: List<Pair<Int, Int>>
)

object Match3Engine {

    fun createInitialBoard(config: LevelConfig): List<List<BoardCell>> {
        val rows = config.rows
        val cols = config.cols
        val grid = MutableList(rows) { r ->
            MutableList(cols) { c ->
                BoardCell(row = r, col = c)
            }
        }

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val blockerData = config.initialBlockers[Pair(r, c)]
                if (blockerData != null) {
                    grid[r][c] = BoardCell(
                        row = r,
                        col = c,
                        pieceType = null,
                        blockerType = blockerData.first,
                        blockerHp = blockerData.second
                    )
                } else {
                    var chosenType: PieceType
                    do {
                        chosenType = config.allowedPieces.random()
                    } while (
                        (r >= 2 && grid[r - 1][c].pieceType == chosenType && grid[r - 2][c].pieceType == chosenType) ||
                        (c >= 2 && grid[r][c - 1].pieceType == chosenType && grid[r][c - 2].pieceType == chosenType)
                    )
                    grid[r][c] = BoardCell(row = r, col = c, pieceType = chosenType)
                }
            }
        }
        return grid.map { it.toList() }
    }


    fun isValidSwap(board: List<List<BoardCell>>, from: Pair<Int, Int>, to: Pair<Int, Int>): Boolean {
        val (r1, c1) = from
        val (r2, c2) = to
        val rows = board.size
        val cols = board[0].size

        if (r1 !in 0 until rows || c1 !in 0 until cols || r2 !in 0 until rows || c2 !in 0 until cols) return false
        val cell1 = board[r1][c1]
        val cell2 = board[r2][c2]

        // Blockers cannot be swapped
        if (cell1.isBlocker || cell2.isBlocker) return false
        if (cell1.pieceType == null || cell2.pieceType == null) return false

        // Check if adjacent (Manhattan distance == 1)
        val dist = kotlin.math.abs(r1 - r2) + kotlin.math.abs(c1 - c2)
        if (dist != 1) return false

        // Special + Special swap is always valid
        if (cell1.isSpecial && cell2.isSpecial) return true
        if (cell1.specialType == SpecialType.MEGA || cell2.specialType == SpecialType.MEGA) return true

        // Swap tentatively and check if matches exist
        val swappedBoard = swapCells(board, from, to)
        val matches = findMatches(swappedBoard)
        return matches.isNotEmpty()
    }

    fun swapCells(board: List<List<BoardCell>>, p1: Pair<Int, Int>, p2: Pair<Int, Int>): List<List<BoardCell>> {
        val newBoard = board.map { it.toMutableList() }.toMutableList()
        val c1 = board[p1.first][p1.second]
        val c2 = board[p2.first][p2.second]

        newBoard[p1.first][p1.second] = c2.copy(row = p1.first, col = p1.second)
        newBoard[p2.first][p2.second] = c1.copy(row = p2.first, col = p2.second)
        return newBoard.map { it.toList() }
    }

    fun findMatches(board: List<List<BoardCell>>, swapFocus: Pair<Int, Int>? = null): List<MatchGroup> {
        val rows = board.size
        val cols = board[0].size
        val horizontalMatches = mutableListOf<List<Pair<Int, Int>>>()
        val verticalMatches = mutableListOf<List<Pair<Int, Int>>>()

        // Check Horizontal
        for (r in 0 until rows) {
            var c = 0
            while (c < cols) {
                val current = board[r][c]
                if (current.isPlayablePiece) {
                    val pType = current.pieceType!!
                    val run = mutableListOf(Pair(r, c))
                    var k = c + 1
                    while (k < cols && board[r][k].isPlayablePiece && board[r][k].pieceType == pType) {
                        run.add(Pair(r, k))
                        k++
                    }
                    if (run.size >= 3) {
                        horizontalMatches.add(run)
                    }
                    c = k
                } else {
                    c++
                }
            }
        }

        // Check Vertical
        for (c in 0 until cols) {
            var r = 0
            while (r < rows) {
                val current = board[r][c]
                if (current.isPlayablePiece) {
                    val pType = current.pieceType!!
                    val run = mutableListOf(Pair(r, c))
                    var k = r + 1
                    while (k < rows && board[k][c].isPlayablePiece && board[k][c].pieceType == pType) {
                        run.add(Pair(k, c))
                        k++
                    }
                    if (run.size >= 3) {
                        verticalMatches.add(run)
                    }
                    r = k
                } else {
                    r++
                }
            }
        }

        // Combine into MatchGroups and detect Specials (T/L Bomb, 5-Line Mega, 4-Line LineBlast)
        val groups = mutableListOf<MatchGroup>()
        val consumedCells = mutableSetOf<Pair<Int, Int>>()

        // Detect T and L shapes (intersecting horizontal and vertical)
        for (h in horizontalMatches) {
            for (v in verticalMatches) {
                val intersection = h.intersect(v.toSet())
                if (intersection.isNotEmpty()) {
                    val pType = board[h.first().first][h.first().second].pieceType!!
                    val combined = (h + v).toSet()
                    val center = intersection.first()
                    val specialPos = if (swapFocus != null && combined.contains(swapFocus)) swapFocus else center
                    groups.add(
                        MatchGroup(
                            cells = combined,
                            pieceType = pType,
                            specialCreated = SpecialType.BOMB,
                            specialPosition = specialPos
                        )
                    )
                    consumedCells.addAll(combined)
                }
            }
        }

        // Remaining horizontal matches
        for (h in horizontalMatches) {
            if (h.any { consumedCells.contains(it) }) continue
            val pType = board[h.first().first][h.first().second].pieceType!!
            val specialPos = if (swapFocus != null && h.contains(swapFocus)) swapFocus else h[h.size / 2]
            val special = when (h.size) {
                in 5..Int.MAX_VALUE -> SpecialType.MEGA
                4 -> SpecialType.HORIZONTAL_LINE
                else -> SpecialType.NONE
            }
            groups.add(
                MatchGroup(
                    cells = h.toSet(),
                    pieceType = pType,
                    specialCreated = special,
                    specialPosition = if (special != SpecialType.NONE) specialPos else null
                )
            )
            consumedCells.addAll(h)
        }

        // Remaining vertical matches
        for (v in verticalMatches) {
            if (v.any { consumedCells.contains(it) }) continue
            val pType = board[v.first().first][v.first().second].pieceType!!
            val specialPos = if (swapFocus != null && v.contains(swapFocus)) swapFocus else v[v.size / 2]
            val special = when (v.size) {
                in 5..Int.MAX_VALUE -> SpecialType.MEGA
                4 -> SpecialType.VERTICAL_LINE
                else -> SpecialType.NONE
            }
            groups.add(
                MatchGroup(
                    cells = v.toSet(),
                    pieceType = pType,
                    specialCreated = special,
                    specialPosition = if (special != SpecialType.NONE) specialPos else null
                )
            )
            consumedCells.addAll(v)
        }

        return groups
    }

    /**
     * Resolves special-special interactions (e.g. Bomb + Bomb, Mega + Line, etc.)
     */
    fun resolveSpecialCombo(
        board: List<List<BoardCell>>,
        p1: Pair<Int, Int>,
        p2: Pair<Int, Int>,
        allowedPieces: List<PieceType>
    ): Pair<List<List<BoardCell>>, Set<Pair<Int, Int>>> {
        val rows = board.size
        val cols = board[0].size
        val cell1 = board[p1.first][p1.second]
        val cell2 = board[p2.first][p2.second]

        val destroyed = mutableSetOf<Pair<Int, Int>>()
        val newBoard = board.map { it.toMutableList() }.toMutableList()

        val s1 = cell1.specialType
        val s2 = cell2.specialType

        when {
            // Mega + Mega = Ultimate board wipe
            s1 == SpecialType.MEGA && s2 == SpecialType.MEGA -> {
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        destroyed.add(Pair(r, c))
                    }
                }
            }

            // Mega + Line / Bomb = Convert all of target color to line/bomb and detonate
            (s1 == SpecialType.MEGA && (s2 == SpecialType.HORIZONTAL_LINE || s2 == SpecialType.VERTICAL_LINE || s2 == SpecialType.BOMB)) ||
            (s2 == SpecialType.MEGA && (s1 == SpecialType.HORIZONTAL_LINE || s1 == SpecialType.VERTICAL_LINE || s1 == SpecialType.BOMB)) -> {
                val nonMega = if (s1 != SpecialType.MEGA) cell1 else cell2
                val targetFlavor = nonMega.pieceType ?: allowedPieces.random()
                val targetSpecial = nonMega.specialType

                destroyed.add(p1)
                destroyed.add(p2)

                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val cell = newBoard[r][c]
                        if (cell.pieceType == targetFlavor) {
                            newBoard[r][c] = cell.copy(specialType = targetSpecial)
                            destroyed.add(Pair(r, c))
                            // Add surrounding area for bombs or rows/cols for lines
                            if (targetSpecial == SpecialType.BOMB) {
                                for (dr in -1..1) {
                                    for (dc in -1..1) {
                                        val nr = r + dr
                                        val nc = c + dc
                                        if (nr in 0 until rows && nc in 0 until cols) destroyed.add(Pair(nr, nc))
                                    }
                                }
                            } else {
                                for (k in 0 until cols) destroyed.add(Pair(r, k))
                                for (k in 0 until rows) destroyed.add(Pair(k, c))
                            }
                        }
                    }
                }
            }

            // Mega + Normal piece = Clear all of that flavor
            s1 == SpecialType.MEGA || s2 == SpecialType.MEGA -> {
                val normalCell = if (s1 != SpecialType.MEGA) cell1 else cell2
                val targetFlavor = normalCell.pieceType ?: allowedPieces.random()
                destroyed.add(p1)
                destroyed.add(p2)
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        if (newBoard[r][c].pieceType == targetFlavor) {
                            destroyed.add(Pair(r, c))
                        }
                    }
                }
            }

            // Bomb + Bomb = Mega Bomb (5x5 explosion)
            s1 == SpecialType.BOMB && s2 == SpecialType.BOMB -> {
                val cr = (p1.first + p2.first) / 2
                val cc = (p1.second + p2.second) / 2
                for (r in (cr - 2)..(cr + 2)) {
                    for (c in (cc - 2)..(cc + 2)) {
                        if (r in 0 until rows && c in 0 until cols) {
                            destroyed.add(Pair(r, c))
                        }
                    }
                }
            }

            // Line + Bomb = Super Line Blast (Clears 3 rows and 3 columns)
            (s1 == SpecialType.BOMB && (s2 == SpecialType.HORIZONTAL_LINE || s2 == SpecialType.VERTICAL_LINE)) ||
            (s2 == SpecialType.BOMB && (s1 == SpecialType.HORIZONTAL_LINE || s1 == SpecialType.VERTICAL_LINE)) -> {
                val cr = p2.first
                val cc = p2.second
                for (dr in -1..1) {
                    val r = cr + dr
                    if (r in 0 until rows) {
                        for (c in 0 until cols) destroyed.add(Pair(r, c))
                    }
                }
                for (dc in -1..1) {
                    val c = cc + dc
                    if (c in 0 until cols) {
                        for (r in 0 until rows) destroyed.add(Pair(r, c))
                    }
                }
            }

            // Line + Line = Cross Blast (1 row + 1 column)
            (s1 == SpecialType.HORIZONTAL_LINE || s1 == SpecialType.VERTICAL_LINE) &&
            (s2 == SpecialType.HORIZONTAL_LINE || s2 == SpecialType.VERTICAL_LINE) -> {
                val r = p2.first
                val c = p2.second
                for (k in 0 until cols) destroyed.add(Pair(r, k))
                for (k in 0 until rows) destroyed.add(Pair(k, c))
            }
        }

        return Pair(newBoard.map { it.toList() }, destroyed)
    }

    /**
     * Finds all cells destroyed by special explosions recursively
     */
    fun expandSpecialExplosions(
        board: List<List<BoardCell>>,
        initialCells: Set<Pair<Int, Int>>
    ): Set<Pair<Int, Int>> {
        val rows = board.size
        val cols = board[0].size
        val allDestroyed = initialCells.toMutableSet()
        val queue = ArrayDeque(initialCells)

        while (queue.isNotEmpty()) {
            val (r, c) = queue.removeFirst()
            if (r !in 0 until rows || c !in 0 until cols) continue
            val cell = board[r][c]

            when (cell.specialType) {
                SpecialType.HORIZONTAL_LINE -> {
                    for (k in 0 until cols) {
                        val p = Pair(r, k)
                        if (allDestroyed.add(p)) queue.add(p)
                    }
                }
                SpecialType.VERTICAL_LINE -> {
                    for (k in 0 until rows) {
                        val p = Pair(k, c)
                        if (allDestroyed.add(p)) queue.add(p)
                    }
                }
                SpecialType.BOMB -> {
                    for (dr in -1..1) {
                        for (dc in -1..1) {
                            val nr = r + dr
                            val nc = c + dc
                            if (nr in 0 until rows && nc in 0 until cols) {
                                val p = Pair(nr, nc)
                                if (allDestroyed.add(p)) queue.add(p)
                            }
                        }
                    }
                }
                SpecialType.MEGA -> {
                    for (dr in -2..2) {
                        for (dc in -2..2) {
                            val nr = r + dr
                            val nc = c + dc
                            if (nr in 0 until rows && nc in 0 until cols) {
                                val p = Pair(nr, nc)
                                if (allDestroyed.add(p)) queue.add(p)
                            }
                        }
                    }
                }
                SpecialType.NONE -> {}
            }
        }

        return allDestroyed
    }

    /**
     * Applies damage to adjacent blockers and removes destroyed cells
     */
    fun applyExplosionsAndGravity(
        board: List<List<BoardCell>>,
        destroyedCells: Set<Pair<Int, Int>>,
        newSpecialsToSpawn: Map<Pair<Int, Int>, Pair<SpecialType, PieceType>>,
        allowedPieces: List<PieceType>
    ): ResolveStep {
        val rows = board.size
        val cols = board[0].size
        val newBoard = board.map { it.toMutableList() }.toMutableList()

        val clearedPieces = mutableMapOf<PieceType, Int>()
        val clearedBlockers = mutableMapOf<BlockerType, Int>()
        val createdSpecials = mutableMapOf<SpecialType, Int>()
        val waterSplashes = mutableListOf<Pair<Int, Int>>()

        // 1. Check adjacent blockers to damage
        val adjacentCoords = mutableSetOf<Pair<Int, Int>>()
        for ((r, c) in destroyedCells) {
            val cell = board[r][c]
            if (cell.pieceType != null && !cell.isBlocker) {
                clearedPieces[cell.pieceType] = (clearedPieces[cell.pieceType] ?: 0) + 1
            }
            waterSplashes.add(Pair(r, c))

            // Check adjacent cells for blockers
            listOf(Pair(r - 1, c), Pair(r + 1, c), Pair(r, c - 1), Pair(r, c + 1)).forEach { adj ->
                if (adj.first in 0 until rows && adj.second in 0 until cols) {
                    adjacentCoords.add(adj)
                }
            }
        }

        // Damage adjacent blockers
        for ((r, c) in adjacentCoords) {
            val cell = newBoard[r][c]
            if (cell.isBlocker && !destroyedCells.contains(Pair(r, c))) {
                val newHp = cell.blockerHp - 1
                if (newHp <= 0) {
                    clearedBlockers[cell.blockerType] = (clearedBlockers[cell.blockerType] ?: 0) + 1
                    if (cell.blockerType == BlockerType.SEALED_GOLGAPPA) {
                        newBoard[r][c] = BoardCell(row = r, col = c, pieceType = allowedPieces.random())
                    } else {
                        newBoard[r][c] = BoardCell(row = r, col = c, pieceType = null)
                    }
                } else {
                    newBoard[r][c] = cell.copy(blockerHp = newHp)
                }
            }
        }

        // Clear destroyed cells or place new specials
        for ((r, c) in destroyedCells) {
            val cell = newBoard[r][c]
            if (cell.isBlocker) {
                clearedBlockers[cell.blockerType] = (clearedBlockers[cell.blockerType] ?: 0) + 1
            }
            val specialSpawn = newSpecialsToSpawn[Pair(r, c)]
            if (specialSpawn != null) {
                newBoard[r][c] = BoardCell(
                    row = r,
                    col = c,
                    pieceType = specialSpawn.second,
                    specialType = specialSpawn.first
                )
                createdSpecials[specialSpawn.first] = (createdSpecials[specialSpawn.first] ?: 0) + 1
            } else {
                newBoard[r][c] = BoardCell(row = r, col = c, pieceType = null)
            }
        }

        // 2. Gravity drop per column
        for (c in 0 until cols) {
            // Collect non-empty playable pieces or movable cells
            val columnPieces = mutableListOf<BoardCell>()
            for (r in rows - 1 downTo 0) {
                val cell = newBoard[r][c]
                if (cell.isBlocker) {
                    // Blockers stay anchored in place
                } else if (cell.pieceType != null) {
                    columnPieces.add(cell)
                }
            }

            var writeR = rows - 1
            for (cell in columnPieces) {
                while (writeR >= 0 && newBoard[writeR][c].isBlocker) {
                    writeR--
                }
                if (writeR >= 0) {
                    newBoard[writeR][c] = cell.copy(row = writeR, col = c)
                    writeR--
                }
            }

            // Fill remaining empty cells from top
            while (writeR >= 0) {
                if (!newBoard[writeR][c].isBlocker) {
                    newBoard[writeR][c] = BoardCell(
                        row = writeR,
                        col = c,
                        pieceType = allowedPieces.random()
                    )
                }
                writeR--
            }
        }

        val basePoints = destroyedCells.size * 60 + createdSpecials.values.sum() * 200 + clearedBlockers.values.sum() * 150

        return ResolveStep(
            board = newBoard.map { it.toList() },
            clearedPieces = clearedPieces,
            clearedBlockers = clearedBlockers,
            createdSpecials = createdSpecials,
            pointsAwarded = basePoints,
            explodedCells = destroyedCells,
            waterSplashes = waterSplashes
        )
    }

    /**
     * Checks all possible moves. Returns hint pairs if found.
     */
    fun findPossibleMoves(board: List<List<BoardCell>>): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        val rows = board.size
        val cols = board[0].size
        val possible = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val p1 = Pair(r, c)
                // Try swapping right
                if (c + 1 < cols) {
                    val p2 = Pair(r, c + 1)
                    if (isValidSwap(board, p1, p2)) possible.add(Pair(p1, p2))
                }
                // Try swapping down
                if (r + 1 < rows) {
                    val p2 = Pair(r + 1, c)
                    if (isValidSwap(board, p1, p2)) possible.add(Pair(p1, p2))
                }
            }
        }
        return possible
    }

    /**
     * Shuffles playable pieces when no moves are available
     */
    fun shuffleBoard(board: List<List<BoardCell>>): List<List<BoardCell>> {
        val rows = board.size
        val cols = board[0].size
        val playablePieces = mutableListOf<BoardCell>()

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cell = board[r][c]
                if (cell.isPlayablePiece) playablePieces.add(cell)
            }
        }

        playablePieces.shuffle()
        val newBoard = board.map { it.toMutableList() }.toMutableList()
        var idx = 0

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (newBoard[r][c].isPlayablePiece) {
                    val p = playablePieces[idx++]
                    newBoard[r][c] = p.copy(row = r, col = c)
                }
            }
        }

        return newBoard.map { it.toList() }
    }
}
