package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Match3GameState

@Composable
fun LevelCompleteDialog(
    state: Match3GameState,
    onNextLevel: () -> Unit,
    onBackToMap: () -> Unit
) {
    val level = state.levelConfig.levelNumber
    val score = state.currentScore
    val thresholds = state.levelConfig.starThresholds
    val starsCount = when {
        score >= thresholds.third -> 3
        score >= thresholds.second -> 2
        else -> 1
    }
    val coinsEarned = 50 + starsCount * 25

    val scaleAnim = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
    }

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1810)),
            modifier = Modifier
                .fillMaxWidth()
                .scale(scaleAnim.value)
                .border(2.5.dp, Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF6D00))), RoundedCornerShape(32.dp))
                .shadow(24.dp)
                .testTag("level_complete_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebration Title
                Text(
                    text = "GOLGAPPA VICTORY!",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
                Text(
                    text = "Level $level Completed",
                    color = Color(0xFFFFE082),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stars Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { i ->
                        val earned = i < starsCount
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = if (earned) Color(0xFFFFD700) else Color.White.copy(alpha = 0.25f),
                            modifier = Modifier
                                .size(if (i == 1) 56.dp else 44.dp)
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score Details Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x33000000))
                        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Final Score:", color = Color.White, fontSize = 14.sp)
                            Text("$score", color = Color(0xFFFFD54F), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Best Combo:", color = Color.White, fontSize = 14.sp)
                            Text("x${state.bestCombo}", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Coins Reward:", color = Color.White, fontSize = 14.sp)
                            Text("+$coinsEarned 🪙", color = Color(0xFFFFEA00), fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("next_level_button")
                ) {
                    Text(
                        text = "NEXT LEVEL",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onBackToMap,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("map_button")
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RETURN TO MAP",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LevelFailedDialog(
    state: Match3GameState,
    coins: Int,
    onAddMoves: () -> Unit,
    onRetry: () -> Unit,
    onBackToMap: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1313)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFFFF5252), RoundedCornerShape(32.dp))
                .shadow(24.dp)
                .testTag("level_failed_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OUT OF MOVES!",
                    color = Color(0xFFFF5252),
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
                Text(
                    text = "Level ${state.levelConfig.levelNumber} Failed",
                    color = Color(0xFFFFCDD2),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Remaining objectives
                Text(
                    text = "Missing Objectives:",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                state.objectives.filter { !it.isCompleted() }.forEach { obj ->
                    Text(
                        text = "• ${obj.title()}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Continue with +5 Extra Moves
                Button(
                    onClick = onAddMoves,
                    enabled = coins >= 80,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("extra_moves_button")
                ) {
                    Text(
                        text = "+5 MOVES (80 🪙)",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("retry_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retry", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onBackToMap,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("map_failed_button")
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Map", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
