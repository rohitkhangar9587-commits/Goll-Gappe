package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.engine.LevelGenerator
import com.example.model.GameResult
import com.example.repository.PlayerStats
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.components.AchievementsDialog
import com.example.ui.components.BoosterShopDialog
import com.example.ui.components.DailyRewardDialog
import com.example.ui.components.GamePlayScreen
import com.example.ui.components.HighScoresDialog
import com.example.ui.components.LevelMapScreen
import com.example.ui.components.LevelStartDialog
import com.example.ui.components.MainMenuScreen
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StatisticsDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PaniPuriCrushApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PaniPuriCrushApp(viewModel: GameViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val unlockedLevel by viewModel.repository.unlockedLevel.collectAsState()
    val levelStars by viewModel.repository.levelStars.collectAsState()
    val coins by viewModel.repository.coins.collectAsState()
    val lives by viewModel.repository.lives.collectAsState()
    val boosters by viewModel.repository.boosters.collectAsState()
    val achievements by viewModel.repository.achievements.collectAsState()
    val stats by viewModel.repository.stats.collectAsState()
    val soundEnabled by viewModel.repository.soundEnabled.collectAsState()
    val musicEnabled by viewModel.repository.musicEnabled.collectAsState()
    val musicVolume by viewModel.repository.musicVolume.collectAsState()
    val hapticsEnabled by viewModel.repository.hapticsEnabled.collectAsState()

    val selectedLevelForStart by viewModel.selectedLevelForStart.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val particles by viewModel.particles.collectAsState()
    val floatingTexts by viewModel.floatingTexts.collectAsState()
    val paniBlasts by viewModel.paniBlasts.collectAsState()

    val showDailyReward by viewModel.showDailyRewardDialog.collectAsState()
    val showBoosterShop by viewModel.showBoosterShopDialog.collectAsState()
    val showAchievements by viewModel.showAchievementsDialog.collectAsState()
    val showStats by viewModel.showStatsDialog.collectAsState()
    val showSettings by viewModel.showSettingsDialog.collectAsState()
    val showHighScores by viewModel.showHighScoresDialog.collectAsState()
    val top5HighScores by viewModel.top5HighScores.collectAsState()

    // Handle back button
    BackHandler(enabled = currentScreen != AppScreen.MAIN_MENU) {
        when (currentScreen) {
            AppScreen.GAME_PLAY -> {
                if (gameState?.gameResult == GameResult.PLAYING) {
                    viewModel.togglePause(true)
                } else {
                    viewModel.navigateTo(AppScreen.LEVEL_MAP)
                }
            }
            AppScreen.LEVEL_MAP -> {
                viewModel.navigateTo(AppScreen.MAIN_MENU)
            }
            else -> {}
        }
    }

    // Screen Router
    when (currentScreen) {
        AppScreen.MAIN_MENU, AppScreen.SPLASH -> {
            MainMenuScreen(
                unlockedLevel = unlockedLevel,
                coins = coins,
                lives = lives,
                onPlayClicked = { viewModel.openLevelStart(unlockedLevel) },
                onMapClicked = { viewModel.navigateTo(AppScreen.LEVEL_MAP) },
                onDailyRewardClicked = { viewModel.openDailyRewardDialog() },
                onBoosterShopClicked = { viewModel.openBoosterShopDialog() },
                onAchievementsClicked = { viewModel.openAchievementsDialog() },
                onStatsClicked = { viewModel.openStatsDialog() },
                onSettingsClicked = { viewModel.openSettingsDialog() }
            )
        }
        AppScreen.LEVEL_MAP -> {
            LevelMapScreen(
                unlockedLevel = unlockedLevel,
                levelStars = levelStars,
                coins = coins,
                lives = lives,
                onLevelSelected = { lvl -> viewModel.openLevelStart(lvl) },
                onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                onDailyRewardClicked = { viewModel.openDailyRewardDialog() },
                onShopClicked = { viewModel.openBoosterShopDialog() },
                onEventsClicked = { viewModel.openAchievementsDialog() }
            )
        }
        AppScreen.GAME_PLAY -> {
            val state = gameState
            if (state != null) {
                GamePlayScreen(
                    state = state,
                    boosters = boosters,
                    coins = coins,
                    particles = particles,
                    floatingTexts = floatingTexts,
                    paniBlasts = paniBlasts,
                    onCellClicked = { r, c -> viewModel.selectCell(r, c) },
                    onCellSwiped = { r, c, dr, dc -> viewModel.swipeCell(r, c, dr, dc) },
                    onBoosterClicked = { b -> viewModel.selectBooster(b) },
                    onPauseClicked = { viewModel.togglePause(true) },
                    onHighScoresClicked = { viewModel.openHighScoresDialog() },
                    onOpenSettings = { viewModel.openSettingsDialog() },
                    onResumeClicked = { viewModel.togglePause(false) },
                    onRestartClicked = { viewModel.restartCurrentGame() },
                    onBackToMap = { viewModel.navigateTo(AppScreen.LEVEL_MAP) },
                    onNextLevel = {
                        val nextLvl = state.levelConfig.levelNumber + 1
                        val nextCfg = LevelGenerator.getLevel(nextLvl)
                        viewModel.startLevel(nextCfg)
                    },
                    onAddExtraMoves = { viewModel.addExtraMovesAfterLoss() }
                )
            }
        }
    }

    // Modals
    if (showHighScores) {
        HighScoresDialog(
            highScores = top5HighScores,
            onDismiss = { viewModel.closeHighScoresDialog() }
        )
    }
    selectedLevelForStart?.let { config ->
        LevelStartDialog(
            config = config,
            boosters = boosters,
            onStartLevel = { cfg, b -> viewModel.startLevel(cfg, b) },
            onDismiss = { viewModel.closeLevelStart() }
        )
    }

    if (showDailyReward) {
        val streak by viewModel.repository.dailyStreak.collectAsState()
        val lastClaim by viewModel.repository.lastDailyClaimTime.collectAsState()
        DailyRewardDialog(
            currentStreak = streak,
            lastClaimTime = lastClaim,
            onClaimReward = { day, reward -> viewModel.claimDailyReward(day, reward) },
            onDismiss = { viewModel.closeDailyRewardDialog() }
        )
    }

    if (showBoosterShop) {
        BoosterShopDialog(
            coins = coins,
            boosters = boosters,
            onBuyBooster = { b -> viewModel.buyBooster(b) },
            onDismiss = { viewModel.closeBoosterShopDialog() }
        )
    }

    if (showAchievements) {
        AchievementsDialog(
            achievements = achievements,
            onClaimAchievement = { id -> viewModel.repository.claimAchievement(id) },
            onDismiss = { viewModel.closeAchievementsDialog() }
        )
    }

    if (showStats) {
        StatisticsDialog(
            stats = stats,
            unlockedLevel = unlockedLevel,
            onDismiss = { viewModel.closeStatsDialog() }
        )
    }

    if (showSettings) {
        SettingsDialog(
            soundEnabled = soundEnabled,
            musicEnabled = musicEnabled,
            musicVolume = musicVolume,
            hapticsEnabled = hapticsEnabled,
            onToggleSound = { viewModel.toggleSound(it) },
            onToggleMusic = { viewModel.toggleMusic(it) },
            onMusicVolumeChange = { viewModel.setMusicVolume(it) },
            onToggleHaptics = { viewModel.toggleHaptics(it) },
            onResetData = {
                viewModel.repository.resetAllProgress()
                viewModel.closeSettingsDialog()
            },
            onDismiss = { viewModel.closeSettingsDialog() }
        )
    }
}
