package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Achievement
import com.example.model.BoosterType
import com.example.model.DailyReward
import com.example.repository.PlayerStats

@Composable
fun DailyRewardDialog(
    currentStreak: Int,
    lastClaimTime: Long,
    onClaimReward: (Int, DailyReward) -> Unit,
    onDismiss: () -> Unit
) {
    val rewards = listOf(
        DailyReward(1, "Day 1", "COINS", 100),
        DailyReward(2, "Day 2", "BOOSTER", 1, BoosterType.SPOON_SHUFFLE),
        DailyReward(3, "Day 3", "COINS", 200),
        DailyReward(4, "Day 4", "BOOSTER", 1, BoosterType.MIRCHI_BLAST),
        DailyReward(5, "Day 5", "LIFE", 5),
        DailyReward(6, "Day 6", "BOOSTER", 1, BoosterType.PANI_WAVE),
        DailyReward(7, "Day 7", "BOOSTER", 1, BoosterType.MEGA_GOLGAPPA)
    )

    val isClaimableToday = System.currentTimeMillis() - lastClaimTime > 24 * 60 * 60 * 1000L || lastClaimTime == 0L
    val todayIndex = if (isClaimableToday) (currentStreak % 7) + 1 else currentStreak

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1810)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF6D00))), RoundedCornerShape(28.dp))
                .shadow(24.dp)
                .testTag("daily_reward_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("DAILY REWARDS", color = Color(0xFFFFD54F), fontWeight = FontWeight.Black, fontSize = 20.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text("Claim tasty bonuses every day to crush more puris!", color = Color(0xFFFFE082), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rewards) { reward ->
                        val isToday = reward.dayNumber == todayIndex && isClaimableToday
                        val isClaimed = reward.dayNumber <= currentStreak && !isClaimableToday

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .then(
                                    if (isToday) Modifier.background(Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))))
                                    else if (isClaimed) Modifier.background(Color(0x3300E676))
                                    else Modifier.background(Color(0x22FFFFFF))
                                )
                                .border(
                                    1.dp,
                                    if (isToday) Color.Yellow else if (isClaimed) Color(0xFF00E676) else Color(0x33FFFFFF),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable(enabled = isToday) {
                                    onClaimReward(reward.dayNumber, reward)
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(reward.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    when (reward.rewardType) {
                                        "COINS" -> "🪙 ${reward.amount}"
                                        "LIFE" -> "❤️ Full"
                                        else -> "⚡ ${reward.boosterType?.displayName?.take(6) ?: "Boost"}"
                                    },
                                    color = if (isToday) Color(0xFFFFEB3B) else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (isClaimed) {
                                    Text("Claimed", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                } else if (isToday) {
                                    Text(
                                        "CLAIM!",
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.Yellow)
                                            .padding(horizontal = 4.dp)
                                    )
                                } else {
                                    Text("Locked", color = Color.Gray, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoosterShopDialog(
    coins: Int,
    boosters: Map<BoosterType, Int>,
    onBuyBooster: (BoosterType) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E272C)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF00B0FF))), RoundedCornerShape(28.dp))
                .shadow(24.dp)
                .testTag("booster_shop_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("BOOSTER SHOP", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙 $coins", color = Color(0xFFFFD54F), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(BoosterType.values().toList()) { b ->
                        val count = boosters[b] ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x33FFFFFF))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(b.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(Owned: $count)", color = Color(0xFFFFD54F), fontSize = 11.sp)
                                }
                                Text(b.description, color = Color(0xFFB0BEC5), fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onBuyBooster(b) },
                                enabled = coins >= b.coinCost,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("${b.coinCost} 🪙", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsDialog(
    achievements: List<Achievement>,
    onClaimAchievement: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF231826)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFFE040FB), Color(0xFF7C4DFF))), RoundedCornerShape(28.dp))
                .shadow(24.dp)
                .testTag("achievements_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ACHIEVEMENTS", color = Color(0xFFEA80FC), fontWeight = FontWeight.Black, fontSize = 20.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(achievements) { ach ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x33FFFFFF))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ach.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(ach.description, color = Color(0xFFE1BEE7), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { ach.progressPercent },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFF00E676),
                                    trackColor = Color(0x33FFFFFF)
                                )
                                Text("${ach.current}/${ach.target}", color = Color.LightGray, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            if (ach.isClaimed) {
                                Text("Claimed", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Button(
                                    onClick = { onClaimAchievement(ach.id) },
                                    enabled = ach.isCompleted,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("+${ach.rewardCoins} 🪙", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsDialog(
    stats: PlayerStats,
    unlockedLevel: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E281F)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFF1B5E20))), RoundedCornerShape(28.dp))
                .shadow(24.dp)
                .testTag("statistics_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PLAYER STATS", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 20.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                listOf(
                    "Current Unlocked Level" to "Level $unlockedLevel",
                    "Total Levels Cleared" to "${stats.totalLevelsCompleted}",
                    "Total Stars Earned" to "${stats.totalStars} ⭐",
                    "Golgappas Burst" to "${stats.totalGolgappasBurst}",
                    "Pani Blasts Triggered" to "${stats.totalPaniBlasts}",
                    "Specials Created" to "${stats.totalSpecialsCreated}",
                    "Highest Combo Multiplier" to "x${stats.highestCombo}"
                ).forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x22FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = Color.White, fontSize = 13.sp)
                        Text(value, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    soundEnabled: Boolean,
    musicEnabled: Boolean,
    musicVolume: Float,
    hapticsEnabled: Boolean,
    onToggleSound: (Boolean) -> Unit,
    onToggleMusic: (Boolean) -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onResetData: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C241B)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFF8D6E63))), RoundedCornerShape(28.dp))
                .shadow(24.dp)
                .testTag("settings_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SETTINGS", color = Color(0xFFFFD54F), fontWeight = FontWeight.Black, fontSize = 20.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Music Soundtrack Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = "Music",
                            tint = if (musicEnabled) Color(0xFFFFD54F) else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Music Soundtracks", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Switch(
                        checked = musicEnabled,
                        onCheckedChange = onToggleMusic,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD54F),
                            checkedTrackColor = Color(0xFF8D6E63)
                        ),
                        modifier = Modifier.testTag("music_toggle_switch")
                    )
                }

                // Music Volume Slider (shown when music is enabled)
                if (musicEnabled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Music Volume", color = Color(0xFFFFE082), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("${(musicVolume * 100).toInt()}%", color = Color(0xFFFFD54F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = musicVolume,
                            onValueChange = onMusicVolumeChange,
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD54F),
                                activeTrackColor = Color(0xFFFFB300),
                                inactiveTrackColor = Color(0x665D4037)
                            ),
                            modifier = Modifier.fillMaxWidth().height(28.dp).testTag("music_volume_slider")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sound Effects Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Sound FX",
                            tint = if (soundEnabled) Color(0xFFFFD54F) else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sound Effects (SFX)", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = onToggleSound,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD54F),
                            checkedTrackColor = Color(0xFF8D6E63)
                        ),
                        modifier = Modifier.testTag("sound_toggle_switch")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Haptics Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Vibration,
                            contentDescription = "Haptics",
                            tint = if (hapticsEnabled) Color(0xFFFFD54F) else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Haptic Vibrations", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = onToggleHaptics,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD54F),
                            checkedTrackColor = Color(0xFF8D6E63)
                        ),
                        modifier = Modifier.testTag("haptics_toggle_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33000000))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("HOW TO PLAY", color = Color(0xFFFFE082), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• Swap adjacent Golgappas to match 3 or more.\n" +
                            "• 4 in a line creates a Line Blast.\n" +
                            "• T or L shape creates a Spicy Bomb Puri.\n" +
                            "• 5 in a line creates Rainbow Mega Golgappa!\n" +
                            "• Swap two specials together for mega chain reactions!",
                            color = Color(0xFFCFD8DC),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onResetData,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reset_progress_button")
                ) {
                    Text("Reset Progress", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExitToMap: () -> Unit,
    onShowHighScores: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onResume) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1810)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.5.dp, Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))), RoundedCornerShape(28.dp))
                .shadow(24.dp)
                .testTag("pause_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("GAME PAUSED", color = Color(0xFFFFD54F), fontWeight = FontWeight.Black, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RESUME", color = Color.Black, fontWeight = FontWeight.Black)
                }

                if (onShowHighScores != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onShowHighScores,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("pause_high_scores_button")
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("HIGH SCORES (TOP 5)", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }

                if (onOpenSettings != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onOpenSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("pause_settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AUDIO / SETTINGS", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RESTART", color = Color.White, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onExitToMap,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("EXIT TO MAP", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
