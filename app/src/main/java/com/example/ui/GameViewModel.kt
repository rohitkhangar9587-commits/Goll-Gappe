package com.example.ui

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.LevelGenerator
import com.example.engine.Match3Engine
import com.example.engine.ResolveStep
import com.example.engine.SoundManager
import com.example.engine.SoundtrackType
import com.example.model.BlockerType
import com.example.model.BoardCell
import com.example.model.BoosterType
import com.example.model.DailyReward
import com.example.model.FloatingText
import com.example.model.GameResult
import com.example.model.LevelConfig
import com.example.model.LevelObjective
import com.example.model.Match3GameState
import com.example.model.Particle
import com.example.model.PieceType
import com.example.model.SpecialType
import com.example.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class AppScreen {
    SPLASH,
    MAIN_MENU,
    LEVEL_MAP,
    GAME_PLAY
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val repository = GameRepository(application)
    val soundManager = SoundManager(application)

    private val _currentScreen = MutableStateFlow(AppScreen.MAIN_MENU)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedLevelForStart = MutableStateFlow<LevelConfig?>(null)
    val selectedLevelForStart: StateFlow<LevelConfig?> = _selectedLevelForStart.asStateFlow()

    private val _gameState = MutableStateFlow<Match3GameState?>(null)
    val gameState: StateFlow<Match3GameState?> = _gameState.asStateFlow()

    private val _particles = MutableStateFlow<List<Particle>>(emptyList())
    val particles: StateFlow<List<Particle>> = _particles.asStateFlow()

    private val _floatingTexts = MutableStateFlow<List<FloatingText>>(emptyList())
    val floatingTexts: StateFlow<List<FloatingText>> = _floatingTexts.asStateFlow()

    private val _paniBlasts = MutableStateFlow<List<com.example.model.PaniBlastEffect>>(emptyList())
    val paniBlasts: StateFlow<List<com.example.model.PaniBlastEffect>> = _paniBlasts.asStateFlow()

    // Dialog flags
    private val _showDailyRewardDialog = MutableStateFlow(false)
    val showDailyRewardDialog: StateFlow<Boolean> = _showDailyRewardDialog.asStateFlow()

    private val _showBoosterShopDialog = MutableStateFlow(false)
    val showBoosterShopDialog: StateFlow<Boolean> = _showBoosterShopDialog.asStateFlow()

    private val _showAchievementsDialog = MutableStateFlow(false)
    val showAchievementsDialog: StateFlow<Boolean> = _showAchievementsDialog.asStateFlow()

    private val _showStatsDialog = MutableStateFlow(false)
    val showStatsDialog: StateFlow<Boolean> = _showStatsDialog.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showHighScoresDialog = MutableStateFlow(false)
    val showHighScoresDialog: StateFlow<Boolean> = _showHighScoresDialog.asStateFlow()

    val top5HighScores: StateFlow<List<com.example.data.db.HighScoreRecord>> = repository.top5HighScores
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val musicEnabled: StateFlow<Boolean> = repository.musicEnabled
    val musicVolume: StateFlow<Float> = repository.musicVolume

    private var hintJob: Job? = null
    private var particleTickerJob: Job? = null

    init {
        soundManager.setSoundEnabled(repository.soundEnabled.value)
        soundManager.setMusicEnabled(repository.musicEnabled.value)
        soundManager.setMusicVolume(repository.musicVolume.value)
        soundManager.setHapticsEnabled(repository.hapticsEnabled.value)
        updateSoundtrackForState()
        startParticleTicker()
    }

    private fun updateSoundtrackForState() {
        val state = _gameState.value
        val screen = _currentScreen.value
        when {
            screen == AppScreen.GAME_PLAY -> {
                if (state?.gameResult == GameResult.WON) {
                    soundManager.playSoundtrack(SoundtrackType.SCORE_SCREEN)
                } else {
                    soundManager.playSoundtrack(SoundtrackType.GAMEPLAY)
                }
            }
            screen == AppScreen.MAIN_MENU || screen == AppScreen.LEVEL_MAP -> {
                soundManager.playSoundtrack(SoundtrackType.MENU)
            }
            else -> {
                soundManager.stopSoundtrack()
            }
        }
    }

    private fun startParticleTicker() {
        particleTickerJob?.cancel()
        particleTickerJob = viewModelScope.launch {
            while (true) {
                delay(16) // ~60fps
                if (_particles.value.isNotEmpty()) {
                    _particles.value = _particles.value.mapNotNull { p ->
                        val nextAlpha = p.alpha - 0.032f
                        if (nextAlpha <= 0f) null
                        else p.copy(
                            x = p.x + p.vx,
                            y = p.y + p.vy + 0.45f, // gravity
                            alpha = nextAlpha
                        )
                    }
                }
                if (_floatingTexts.value.isNotEmpty()) {
                    _floatingTexts.value = _floatingTexts.value.mapNotNull { t ->
                        val nextAlpha = t.alpha - 0.028f
                        if (nextAlpha <= 0f) null
                        else t.copy(
                            y = t.y - 1.5f,
                            alpha = nextAlpha
                        )
                    }
                }
                if (_paniBlasts.value.isNotEmpty()) {
                    _paniBlasts.value = _paniBlasts.value.mapNotNull { blast ->
                        val nextRadius = blast.currentRadius + 7.5f
                        val nextAlpha = blast.alpha - 0.04f
                        if (nextAlpha <= 0f || nextRadius >= blast.maxRadius) null
                        else blast.copy(
                            currentRadius = nextRadius,
                            alpha = nextAlpha
                        )
                    }
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        soundManager.playClick()
        _currentScreen.value = screen
        updateSoundtrackForState()
    }

    fun openLevelStart(levelNumber: Int) {
        soundManager.playClick()
        val config = LevelGenerator.getLevel(levelNumber)
        _selectedLevelForStart.value = config
    }

    fun closeLevelStart() {
        soundManager.playClick()
        _selectedLevelForStart.value = null
    }

    fun startLevel(config: LevelConfig, preSelectedBooster: BoosterType? = null) {
        soundManager.playClick()
        _selectedLevelForStart.value = null

        var initialBoard = Match3Engine.createInitialBoard(config)

        if (preSelectedBooster == BoosterType.MEGA_GOLGAPPA) {
            repository.useBooster(BoosterType.MEGA_GOLGAPPA)
            // Spawn mega in center
            val r = config.rows / 2
            val c = config.cols / 2
            val mutable = initialBoard.map { it.toMutableList() }.toMutableList()
            mutable[r][c] = BoardCell(row = r, col = c, pieceType = config.allowedPieces.first(), specialType = SpecialType.MEGA)
            initialBoard = mutable.map { it.toList() }
        }

        _gameState.value = Match3GameState(
            levelConfig = config,
            board = initialBoard,
            movesRemaining = config.moves + (if (preSelectedBooster == BoosterType.EXTRA_MOVES) {
                repository.useBooster(BoosterType.EXTRA_MOVES)
                5
            } else 0),
            currentScore = 0,
            objectives = config.objectives
        )

        _currentScreen.value = AppScreen.GAME_PLAY
        resetHintTimer()
        soundManager.playSoundtrack(SoundtrackType.GAMEPLAY)
    }

    fun selectCell(row: Int, col: Int) {
        val state = _gameState.value ?: return
        if (state.isResolving || state.gameResult != GameResult.PLAYING || state.isPaused) return

        // Active booster handling
        if (state.activeBooster != null) {
            applyInGameBooster(state.activeBooster, row, col)
            return
        }

        val selected = state.selectedCell
        if (selected == null) {
            val cell = state.board[row][col]
            if (cell.isPlayablePiece) {
                _gameState.value = state.copy(selectedCell = Pair(row, col))
                soundManager.playClick()
            }
        } else {
            if (selected.first == row && selected.second == col) {
                _gameState.value = state.copy(selectedCell = null)
            } else {
                val dist = kotlin.math.abs(selected.first - row) + kotlin.math.abs(selected.second - col)
                if (dist == 1) {
                    trySwap(selected, Pair(row, col))
                } else {
                    val cell = state.board[row][col]
                    if (cell.isPlayablePiece) {
                        _gameState.value = state.copy(selectedCell = Pair(row, col))
                        soundManager.playClick()
                    } else {
                        _gameState.value = state.copy(selectedCell = null)
                    }
                }
            }
        }
    }

    fun swipeCell(fromRow: Int, fromCol: Int, directionRow: Int, directionCol: Int) {
        val state = _gameState.value ?: return
        if (state.isResolving || state.gameResult != GameResult.PLAYING || state.isPaused) return
        val toRow = fromRow + directionRow
        val toCol = fromCol + directionCol
        if (toRow in 0 until state.levelConfig.rows && toCol in 0 until state.levelConfig.cols) {
            trySwap(Pair(fromRow, fromCol), Pair(toRow, toCol))
        }
    }

    private fun trySwap(p1: Pair<Int, Int>, p2: Pair<Int, Int>) {
        val state = _gameState.value ?: return
        val cell1 = state.board[p1.first][p1.second]
        val cell2 = state.board[p2.first][p2.second]

        _gameState.value = state.copy(selectedCell = null, hintCells = emptyList())
        resetHintTimer()

        // Check if special combination (e.g. Bomb+Bomb, Mega+Line, etc.)
        val isSpecialCombo = (cell1.isSpecial && cell2.isSpecial) ||
                (cell1.specialType == SpecialType.MEGA || cell2.specialType == SpecialType.MEGA)

        if (isSpecialCombo) {
            executeSpecialCombo(p1, p2)
            return
        }

        if (Match3Engine.isValidSwap(state.board, p1, p2)) {
            executeNormalSwapAndResolve(p1, p2)
        } else {
            // Invalid swap animation
            viewModelScope.launch {
                soundManager.playInvalidSwap()
                val swapped = Match3Engine.swapCells(state.board, p1, p2)
                _gameState.value = state.copy(board = swapped, isResolving = true)
                delay(180)
                _gameState.value = state.copy(board = state.board, isResolving = false)
            }
        }
    }

    private fun executeSpecialCombo(p1: Pair<Int, Int>, p2: Pair<Int, Int>) {
        viewModelScope.launch {
            val state = _gameState.value ?: return@launch
            soundManager.playMega()
            _gameState.value = state.copy(
                isResolving = true,
                movesRemaining = state.movesRemaining - 1
            )

            val swapped = Match3Engine.swapCells(state.board, p1, p2)
            _gameState.value = _gameState.value?.copy(board = swapped)
            delay(150)

            val (boardAfterCombo, destroyed) = Match3Engine.resolveSpecialCombo(
                swapped,
                p1,
                p2,
                state.levelConfig.allowedPieces
            )

            spawnExplosionParticles(destroyed)
            addFloatingText("COMBO SPLASH!", 300f, 300f, Color(0xFFFFD700))

            // Animate piece removal via AnimatedVisibility
            val clearingBoard = boardAfterCombo.map { row ->
                row.map { cell ->
                    if (destroyed.contains(Pair(cell.row, cell.col))) cell.copy(pieceType = null) else cell
                }
            }
            _gameState.value = _gameState.value?.copy(board = clearingBoard)
            delay(160)

            val resolveStep = Match3Engine.applyExplosionsAndGravity(
                boardAfterCombo,
                destroyed,
                emptyMap(),
                state.levelConfig.allowedPieces
            )

            applyStepResults(resolveStep, isInitialSwap = true)
            delay(280)

            // Continue cascading chain reactions
            runCascadeLoop()
        }
    }

    private fun executeNormalSwapAndResolve(p1: Pair<Int, Int>, p2: Pair<Int, Int>) {
        viewModelScope.launch {
            val state = _gameState.value ?: return@launch
            soundManager.playSwap()

            val swappedBoard = Match3Engine.swapCells(state.board, p1, p2)
            _gameState.value = state.copy(
                board = swappedBoard,
                isResolving = true,
                movesRemaining = state.movesRemaining - 1
            )
            delay(180)

            val matches = Match3Engine.findMatches(swappedBoard, swapFocus = p2)
            val destroyedCells = mutableSetOf<Pair<Int, Int>>()
            val newSpecials = mutableMapOf<Pair<Int, Int>, Pair<SpecialType, PieceType>>()

            for (m in matches) {
                destroyedCells.addAll(m.cells)
                if (m.specialCreated != SpecialType.NONE && m.specialPosition != null) {
                    newSpecials[m.specialPosition] = Pair(m.specialCreated, m.pieceType)
                }
            }

            // Expand line/bomb explosions if any matched cells were special
            val expandedDestroyed = Match3Engine.expandSpecialExplosions(swappedBoard, destroyedCells)
            spawnExplosionParticles(expandedDestroyed)

            // Trigger Pani Blast for 4+ matches
            val fourPlusMatches = matches.filter { it.cells.size >= 4 }
            for (bigMatch in fourPlusMatches) {
                triggerPaniBlast(
                    centerCell = bigMatch.cells.first(),
                    pieceType = bigMatch.pieceType,
                    matchSize = bigMatch.cells.size
                )
            }

            if (matches.any { it.specialCreated != SpecialType.NONE }) {
                soundManager.playLineBlast()
                if (fourPlusMatches.isEmpty()) {
                    addFloatingText("SPECIAL GOLGAPPA!", 300f, 400f, Color(0xFFFF9800))
                }
            } else {
                soundManager.playMatch(1)
            }

            // Animate piece removal via AnimatedVisibility
            val clearingBoard = swappedBoard.map { row ->
                row.map { cell ->
                    if (expandedDestroyed.contains(Pair(cell.row, cell.col))) cell.copy(pieceType = null) else cell
                }
            }
            _gameState.value = _gameState.value?.copy(board = clearingBoard)
            delay(160)

            val step = Match3Engine.applyExplosionsAndGravity(
                swappedBoard,
                expandedDestroyed,
                newSpecials,
                state.levelConfig.allowedPieces
            )

            applyStepResults(step, isInitialSwap = true)
            delay(280)

            // Cascade loop
            runCascadeLoop()
        }
    }

    private suspend fun runCascadeLoop() {
        var combo = 1
        while (true) {
            val state = _gameState.value ?: break
            val matches = Match3Engine.findMatches(state.board)
            if (matches.isEmpty()) break

            combo++
            _gameState.value = state.copy(
                comboMultiplier = combo,
                bestCombo = maxOf(state.bestCombo, combo)
            )

            val destroyedCells = mutableSetOf<Pair<Int, Int>>()
            val newSpecials = mutableMapOf<Pair<Int, Int>, Pair<SpecialType, PieceType>>()
            for (m in matches) {
                destroyedCells.addAll(m.cells)
                if (m.specialCreated != SpecialType.NONE && m.specialPosition != null) {
                    newSpecials[m.specialPosition] = Pair(m.specialCreated, m.pieceType)
                }
            }

            val expandedDestroyed = Match3Engine.expandSpecialExplosions(state.board, destroyedCells)
            spawnExplosionParticles(expandedDestroyed)

            // Trigger Pani Blast for 4+ matches in cascade
            val fourPlusMatches = matches.filter { it.cells.size >= 4 }
            for (bigMatch in fourPlusMatches) {
                triggerPaniBlast(
                    centerCell = bigMatch.cells.first(),
                    pieceType = bigMatch.pieceType,
                    matchSize = bigMatch.cells.size
                )
            }

            soundManager.playMatch(combo)

            if (combo >= 2 && fourPlusMatches.isEmpty()) {
                addFloatingText("COMBO x$combo!", 300f + Random.nextInt(-40, 40), 380f, Color(0xFF00E676))
            }

            // Animate piece removal via AnimatedVisibility
            val clearingBoard = state.board.map { row ->
                row.map { cell ->
                    if (expandedDestroyed.contains(Pair(cell.row, cell.col))) cell.copy(pieceType = null) else cell
                }
            }
            _gameState.value = _gameState.value?.copy(board = clearingBoard)
            delay(160)

            val step = Match3Engine.applyExplosionsAndGravity(
                state.board,
                expandedDestroyed,
                newSpecials,
                state.levelConfig.allowedPieces
            )

            applyStepResults(step, isInitialSwap = false)
            delay(260)
        }

        // Board is settled
        val settledState = _gameState.value ?: return
        _gameState.value = settledState.copy(isResolving = false, comboMultiplier = 1)

        // Check Deadlock
        val possibleMoves = Match3Engine.findPossibleMoves(settledState.board)
        if (possibleMoves.isEmpty()) {
            addFloatingText("SHUFFLING GOLGAPPAS!", 300f, 400f, Color(0xFFFFEB3B))
            soundManager.playSplash()
            delay(500)
            val shuffled = Match3Engine.shuffleBoard(settledState.board)
            _gameState.value = _gameState.value?.copy(board = shuffled)
        }

        checkWinLossCondition()
        resetHintTimer()
    }

    private fun applyStepResults(step: ResolveStep, isInitialSwap: Boolean) {
        val state = _gameState.value ?: return
        val mult = state.comboMultiplier

        // Update objectives
        val updatedObjectives = state.objectives.map { obj ->
            when (obj) {
                is LevelObjective.CollectPiece -> {
                    val count = step.clearedPieces[obj.pieceType] ?: 0
                    obj.copyWithProgress(obj.current + count)
                }
                is LevelObjective.ClearBlocker -> {
                    val count = step.clearedBlockers[obj.blockerType] ?: 0
                    obj.copyWithProgress(obj.current + count)
                }
                is LevelObjective.CreateSpecial -> {
                    val count = step.createdSpecials[obj.specialType] ?: 0
                    obj.copyWithProgress(obj.current + count)
                }
                is LevelObjective.ReachScore -> {
                    obj.copyWithProgress(state.currentScore + step.pointsAwarded * mult)
                }
            }
        }

        val totalPurisThisStep = step.clearedPieces.values.sum()
        val totalBlastsThisStep = if (step.explodedCells.size >= 5) 1 else 0
        val totalSpecialsThisStep = step.createdSpecials.values.sum()

        _gameState.value = state.copy(
            board = step.board,
            currentScore = state.currentScore + step.pointsAwarded * mult,
            objectives = updatedObjectives,
            totalGolgappasCleared = state.totalGolgappasCleared + totalPurisThisStep,
            totalPaniBlasts = state.totalPaniBlasts + totalBlastsThisStep,
            totalSpecialsCreated = state.totalSpecialsCreated + totalSpecialsThisStep
        )
    }

    private fun checkWinLossCondition() {
        val state = _gameState.value ?: return
        val allObjectivesDone = state.objectives.all { it.isCompleted() }

        if (allObjectivesDone) {
            soundManager.playVictory()
            soundManager.playSoundtrack(SoundtrackType.SCORE_SCREEN)
            val stars = calculateStars(state.currentScore, state.levelConfig.starThresholds)
            repository.completeLevel(
                levelNumber = state.levelConfig.levelNumber,
                score = state.currentScore,
                stars = stars,
                purisCleared = state.totalGolgappasCleared,
                paniBlasts = state.totalPaniBlasts,
                specialsCreated = state.totalSpecialsCreated,
                combo = state.bestCombo
            )
            _gameState.value = state.copy(gameResult = GameResult.WON)
        } else if (state.movesRemaining <= 0) {
            soundManager.playDefeat()
            repository.loseLife()
            _gameState.value = state.copy(gameResult = GameResult.LOST)
        }
    }

    fun retryLevel() {
        val state = _gameState.value ?: return
        startLevel(state.levelConfig)
    }

    fun restartCurrentGame() {
        val state = _gameState.value ?: return
        startLevel(state.levelConfig)
    }

    fun addExtraMovesAfterLoss() {
        val state = _gameState.value ?: return
        if (repository.spendCoins(80)) {
            soundManager.playCoin()
            _gameState.value = state.copy(
                movesRemaining = state.movesRemaining + 5,
                gameResult = GameResult.PLAYING
            )
        }
    }

    fun selectBooster(booster: BoosterType) {
        val state = _gameState.value ?: return
        if (state.isResolving || state.gameResult != GameResult.PLAYING) return

        if (booster == BoosterType.EXTRA_MOVES) {
            if (repository.useBooster(BoosterType.EXTRA_MOVES)) {
                soundManager.playCoin()
                _gameState.value = state.copy(movesRemaining = state.movesRemaining + 5)
                addFloatingText("+5 MOVES!", 300f, 300f, Color(0xFF00E676))
            }
        } else if (booster == BoosterType.SPOON_SHUFFLE) {
            if (repository.useBooster(BoosterType.SPOON_SHUFFLE)) {
                soundManager.playSplash()
                val shuffled = Match3Engine.shuffleBoard(state.board)
                _gameState.value = state.copy(board = shuffled)
                addFloatingText("STIRRED UP!", 300f, 300f, Color(0xFFFF9800))
            }
        } else {
            // Targetable booster: Mirchi Blast, Pani Wave, Mega Golgappa
            _gameState.value = state.copy(
                activeBooster = if (state.activeBooster == booster) null else booster
            )
            soundManager.playClick()
        }
    }

    private fun applyInGameBooster(booster: BoosterType, targetRow: Int, targetCol: Int) {
        val state = _gameState.value ?: return
        if (!repository.useBooster(booster)) {
            _gameState.value = state.copy(activeBooster = null)
            return
        }

        viewModelScope.launch {
            _gameState.value = state.copy(activeBooster = null, isResolving = true)

            val destroyed = mutableSetOf<Pair<Int, Int>>()
            when (booster) {
                BoosterType.MIRCHI_BLAST -> {
                    soundManager.playBomb()
                    for (dr in -1..1) {
                        for (dc in -1..1) {
                            val r = targetRow + dr
                            val c = targetCol + dc
                            if (r in 0 until state.levelConfig.rows && c in 0 until state.levelConfig.cols) {
                                destroyed.add(Pair(r, c))
                            }
                        }
                    }
                    addFloatingText("MIRCHI BLAST!", 300f, 350f, Color(0xFFFF1744))
                }
                BoosterType.PANI_WAVE -> {
                    soundManager.playSplash()
                    for (c in 0 until state.levelConfig.cols) destroyed.add(Pair(targetRow, c))
                    for (r in 0 until state.levelConfig.rows) destroyed.add(Pair(r, targetCol))
                    addFloatingText("PANI WAVE!", 300f, 350f, Color(0xFF00B0FF))
                }
                BoosterType.MEGA_GOLGAPPA -> {
                    soundManager.playMega()
                    val newB = state.board.map { it.toMutableList() }.toMutableList()
                    newB[targetRow][targetCol] = BoardCell(
                        row = targetRow,
                        col = targetCol,
                        pieceType = state.levelConfig.allowedPieces.first(),
                        specialType = SpecialType.MEGA
                    )
                    _gameState.value = state.copy(board = newB.map { it.toList() }, isResolving = false)
                    addFloatingText("MEGA GOLGAPPA READY!", 300f, 350f, Color(0xFFFFD700))
                    return@launch
                }
                else -> {}
            }

            spawnExplosionParticles(destroyed)
            val step = Match3Engine.applyExplosionsAndGravity(
                state.board,
                destroyed,
                emptyMap(),
                state.levelConfig.allowedPieces
            )
            applyStepResults(step, isInitialSwap = false)
            delay(300)
            runCascadeLoop()
        }
    }

    private fun spawnExplosionParticles(cells: Set<Pair<Int, Int>>) {
        val newParticles = mutableListOf<Particle>()
        for ((r, c) in cells) {
            val cx = (c * 60f + 30f)
            val cy = (r * 60f + 30f)
            val count = 8
            for (i in 0 until count) {
                val angle = (2.0 * Math.PI * i / count) + Random.nextDouble(-0.3, 0.3)
                val speed = Random.nextFloat() * 7f + 2f
                val colors = listOf(
                    Color(0xFFFF9800), // Golden Puri
                    Color(0xFF4CAF50), // Pudina Mint
                    Color(0xFFE91E63), // Imli Tamarind
                    Color(0xFFFFEB3B), // Yellow Masala
                    Color(0xFF00E5FF)  // Water drop
                )
                newParticles.add(
                    Particle(
                        id = System.nanoTime() + i + r * 100L + c,
                        x = cx,
                        y = cy,
                        vx = (cos(angle) * speed).toFloat(),
                        vy = (sin(angle) * speed).toFloat(),
                        color = colors.random(),
                        radius = Random.nextFloat() * 6f + 3f,
                        isWaterDrop = Random.nextBoolean()
                    )
                )
            }
        }
        _particles.value = (_particles.value + newParticles).takeLast(100)
    }

    fun triggerPaniBlast(centerCell: Pair<Int, Int>, pieceType: PieceType?, matchSize: Int) {
        val cx = (centerCell.second * 60f + 30f)
        val cy = (centerCell.first * 60f + 30f)

        soundManager.playSplash()
        soundManager.playLineBlast()

        // 1. Expanding shockwave ring effect
        val shockwave = com.example.model.PaniBlastEffect(
            id = System.nanoTime(),
            centerX = cx,
            centerY = cy,
            maxRadius = if (matchSize >= 5) 220f else 160f,
            color = when (pieceType) {
                PieceType.PUDINA -> Color(0xFF00E676)
                PieceType.IMLI -> Color(0xFFFF1744)
                PieceType.MASALA -> Color(0xFFFFD600)
                PieceType.DAHI -> Color(0xFFE0F7FA)
                else -> Color(0xFF00E5FF)
            },
            secondaryColor = Color(0xFF00B0FF)
        )
        _paniBlasts.value = (_paniBlasts.value + shockwave).takeLast(6)

        // 2. High-velocity splash & water drop particle fountain (35+ particles)
        val blastParticles = mutableListOf<Particle>()
        val particleCount = if (matchSize >= 5) 45 else 32
        for (i in 0 until particleCount) {
            val angle = (2.0 * Math.PI * i / particleCount) + Random.nextDouble(-0.35, 0.35)
            val speed = Random.nextFloat() * 11f + 4f
            val isWater = Random.nextBoolean()
            val colors = when (pieceType) {
                PieceType.PUDINA -> listOf(Color(0xFF00E676), Color(0xFF69F0AE), Color(0xFF00E5FF), Color(0xFFFFD54F))
                PieceType.IMLI -> listOf(Color(0xFFFF1744), Color(0xFFFF5252), Color(0xFF00E5FF), Color(0xFFFFB300))
                PieceType.MASALA -> listOf(Color(0xFFFFD600), Color(0xFFFFEA00), Color(0xFF00E5FF), Color(0xFFFF9800))
                PieceType.DAHI -> listOf(Color(0xFFFFFFFF), Color(0xFFE0F7FA), Color(0xFF80D8FF), Color(0xFFFFD54F))
                else -> listOf(Color(0xFF00E5FF), Color(0xFF00B0FF), Color(0xFFFFD700), Color(0xFF00E676), Color(0xFFFF3D00))
            }

            blastParticles.add(
                Particle(
                    id = System.nanoTime() + i * 7L,
                    x = cx,
                    y = cy,
                    vx = (cos(angle) * speed).toFloat(),
                    vy = (sin(angle) * speed).toFloat() - 2.5f, // upward eruption bias
                    color = colors.random(),
                    radius = Random.nextFloat() * 8f + 4f,
                    alpha = 1f,
                    isWaterDrop = isWater,
                    isSplashBubble = !isWater && Random.nextBoolean()
                )
            )
        }
        _particles.value = (_particles.value + blastParticles).takeLast(120)

        // 3. Floating Banner
        val blastTitle = if (matchSize >= 5) "💥 MEGA PANI BLAST!" else "💦 PANI BLAST!"
        addFloatingText(blastTitle, cx, cy - 20f, Color(0xFF00E5FF), isPaniBlast = true)

        // 4. Update Game State stats
        _gameState.value = _gameState.value?.let { st ->
            st.copy(totalPaniBlasts = st.totalPaniBlasts + 1)
        }
    }

    private fun addFloatingText(text: String, x: Float, y: Float, color: Color, isPaniBlast: Boolean = false) {
        val ft = FloatingText(
            id = System.nanoTime(),
            text = text,
            x = x,
            y = y,
            color = color,
            isPaniBlast = isPaniBlast
        )
        _floatingTexts.value = (_floatingTexts.value + ft).takeLast(10)
    }

    private fun calculateStars(score: Int, thresholds: Triple<Int, Int, Int>): Int {
        return when {
            score >= thresholds.third -> 3
            score >= thresholds.second -> 2
            score >= thresholds.first -> 1
            else -> 1 // Completed level grants minimum 1 star
        }
    }

    private fun resetHintTimer() {
        hintJob?.cancel()
        hintJob = viewModelScope.launch {
            delay(5000) // 5 seconds of inactivity shows hint
            val state = _gameState.value ?: return@launch
            if (!state.isResolving && state.gameResult == GameResult.PLAYING) {
                val possible = Match3Engine.findPossibleMoves(state.board)
                if (possible.isNotEmpty()) {
                    val pair = possible.random()
                    _gameState.value = state.copy(hintCells = listOf(pair.first, pair.second))
                }
            }
        }
    }

    // Modal controllers
    fun togglePause(paused: Boolean) {
        _gameState.value = _gameState.value?.copy(isPaused = paused)
    }

    fun openDailyRewardDialog() {
        soundManager.playClick()
        _showDailyRewardDialog.value = true
    }

    fun closeDailyRewardDialog() {
        soundManager.playClick()
        _showDailyRewardDialog.value = false
    }

    fun openBoosterShopDialog() {
        soundManager.playClick()
        _showBoosterShopDialog.value = true
    }

    fun closeBoosterShopDialog() {
        soundManager.playClick()
        _showBoosterShopDialog.value = false
    }

    fun openAchievementsDialog() {
        soundManager.playClick()
        _showAchievementsDialog.value = true
    }

    fun closeAchievementsDialog() {
        soundManager.playClick()
        _showAchievementsDialog.value = false
    }

    fun openStatsDialog() {
        soundManager.playClick()
        _showStatsDialog.value = true
    }

    fun closeStatsDialog() {
        soundManager.playClick()
        _showStatsDialog.value = false
    }

    fun openSettingsDialog() {
        soundManager.playClick()
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        soundManager.playClick()
        _showSettingsDialog.value = false
    }

    fun openHighScoresDialog() {
        soundManager.playClick()
        _showHighScoresDialog.value = true
    }

    fun closeHighScoresDialog() {
        soundManager.playClick()
        _showHighScoresDialog.value = false
    }

    fun claimDailyReward(day: Int, reward: DailyReward) {
        soundManager.playCoin()
        repository.claimDailyReward(day, reward)
        addFloatingText("CLAIMED REWARD!", 300f, 300f, Color(0xFFFFD700))
    }

    fun buyBooster(booster: BoosterType) {
        if (repository.spendCoins(booster.coinCost)) {
            soundManager.playCoin()
            repository.addBooster(booster, 1)
        } else {
            soundManager.playInvalidSwap()
        }
    }

    fun toggleSound(enabled: Boolean) {
        repository.setSoundEnabled(enabled)
        soundManager.setSoundEnabled(enabled)
    }

    fun toggleMusic(enabled: Boolean) {
        repository.setMusicEnabled(enabled)
        soundManager.setMusicEnabled(enabled)
        if (enabled) {
            updateSoundtrackForState()
        }
    }

    fun setMusicVolume(volume: Float) {
        repository.setMusicVolume(volume)
        soundManager.setMusicVolume(volume)
    }

    fun toggleHaptics(enabled: Boolean) {
        repository.setHapticsEnabled(enabled)
        soundManager.setHapticsEnabled(enabled)
    }
}
