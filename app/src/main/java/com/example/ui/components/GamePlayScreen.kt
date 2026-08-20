package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BoosterType
import com.example.model.FloatingText
import com.example.model.GameResult
import com.example.model.Match3GameState
import com.example.model.Particle

/**
 * GamePlay Screen with Banana leaf & Spice table mat background,
 * wooden carved HUD, 8x8 match-3 grid, and terracotta booster bar.
 */
@Composable
fun GamePlayScreen(
    state: Match3GameState,
    boosters: Map<BoosterType, Int>,
    coins: Int,
    lives: Int = 5,
    particles: List<Particle>,
    floatingTexts: List<FloatingText>,
    paniBlasts: List<com.example.model.PaniBlastEffect> = emptyList(),
    onCellClicked: (Int, Int) -> Unit,
    onCellSwiped: (Int, Int, Int, Int) -> Unit,
    onBoosterClicked: (BoosterType) -> Unit,
    onPauseClicked: () -> Unit,
    onHighScoresClicked: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onResumeClicked: () -> Unit,
    onRestartClicked: () -> Unit,
    onBackToMap: () -> Unit,
    onNextLevel: () -> Unit,
    onAddExtraMoves: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("game_play_screen")
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Banana Leaf & Rustic Spice Table Background Art
            Image(
                painter = painterResource(id = R.drawable.bg_gameplay_table),
                contentDescription = "Gameplay Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark vignette overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color(0x66000000), Color(0xAA000000))
                        )
                    )
            )

            // 2. Gameplay Layout Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top HUD with Menu, Heart, Moves, and Coins
                HUDView(
                    state = state,
                    coins = coins,
                    lives = lives,
                    onPauseClicked = onPauseClicked
                )

                // Dedicated Score Tracking Header & Star Progress Bar
                ScoreHeaderView(
                    state = state,
                    onHighScoresClicked = onHighScoresClicked,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Center Match-3 Game Board with Drag-and-Drop & Particle Layers
                GameBoardView(
                    state = state,
                    particles = particles,
                    floatingTexts = floatingTexts,
                    paniBlasts = paniBlasts,
                    onCellClicked = onCellClicked,
                    onCellSwiped = onCellSwiped,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Booster Bar with 5 Terracotta Clay Pot Boosters
                BoosterBar(
                    boosters = boosters,
                    activeBooster = state.activeBooster,
                    onBoosterClicked = onBoosterClicked
                )
            }

            // Pause Dialog
            if (state.isPaused) {
                PauseDialog(
                    onResume = onResumeClicked,
                    onRestart = onRestartClicked,
                    onExitToMap = onBackToMap,
                    onShowHighScores = onHighScoresClicked,
                    onOpenSettings = onOpenSettings
                )
            }

            // Level Complete Dialog
            if (state.gameResult == GameResult.WON) {
                LevelCompleteDialog(
                    state = state,
                    onNextLevel = onNextLevel,
                    onBackToMap = onBackToMap
                )
            }

            // Level Failed Dialog
            if (state.gameResult == GameResult.LOST) {
                LevelFailedDialog(
                    state = state,
                    coins = coins,
                    onAddMoves = onAddExtraMoves,
                    onRetry = onRestartClicked,
                    onBackToMap = onBackToMap
                )
            }
        }
    }
}
