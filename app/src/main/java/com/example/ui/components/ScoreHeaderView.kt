package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Match3GameState

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.EmojiEvents

/**
 * Score Tracking Header UI Component positioned right at the top of the GameBoard:
 * - Dynamic animated score count-up state whenever the player matches 3+ pieces.
 * - Star Milestone progress gauge showing progression to 1-star, 2-star, and 3-star thresholds.
 * - Combo Multiplier badge with fire effects during cascades.
 * - Direct Top 5 High Scores Room database leaderboard modal access button.
 */
@Composable
fun ScoreHeaderView(
    state: Match3GameState,
    modifier: Modifier = Modifier,
    onHighScoresClicked: () -> Unit = {}
) {
    val currentScore = state.currentScore
    val thresholds = state.levelConfig.starThresholds
    val maxScoreThreshold = thresholds.third.toFloat().coerceAtLeast(1000f)

    // Animated score count-up
    val animatedScore by animateIntAsState(
        targetValue = currentScore,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "animated_score"
    )

    // Progress towards 3 stars (0f to 1f)
    val targetProgress = (currentScore.toFloat() / maxScoreThreshold).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "score_progress"
    )

    val starsEarned = when {
        currentScore >= thresholds.third -> 3
        currentScore >= thresholds.second -> 2
        currentScore >= thresholds.first -> 1
        else -> 0
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4E342E),
                        Color(0xFF3E2723),
                        Color(0xFF271510)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300), Color(0xFFFF8F00))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("score_header_view")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1: Score Badge & Value + Combo Multiplier
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Score Plaque with clickable High Scores Trophy button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onHighScoresClicked() }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("high_scores_trigger_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFFEE58), Color(0xFFF57F17), Color(0xFFE65100))
                                )
                            )
                            .border(1.5.dp, Color(0xFFFFF9C4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "High Scores Trophy",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SCORE",
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = " 🏆",
                                fontSize = 9.sp
                            )
                        }
                        Text(
                            text = "$animatedScore",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            modifier = Modifier.testTag("score_display_value")
                        )
                    }
                }

                // Dynamic Combo Multiplier Badge
                AnimatedVisibility(
                    visible = state.comboMultiplier > 1,
                    enter = fadeIn() + scaleIn(initialScale = 0.7f),
                    exit = fadeOut() + scaleOut(targetScale = 0.5f)
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF3D00), Color(0xFFFF6D00), Color(0xFFFFAB00))
                                )
                            )
                            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🔥 x${state.comboMultiplier} COMBO!",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }

                // Target Score Milestone Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Next Star: ",
                        color = Color(0xAAFFFFFF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    val nextThreshold = when {
                        currentScore < thresholds.first -> thresholds.first
                        currentScore < thresholds.second -> thresholds.second
                        else -> thresholds.third
                    }
                    Text(
                        text = "$nextThreshold",
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            // Row 2: Star Milestone Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color(0xFF1B1310))
                    .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(7.dp))
            ) {
                // Animated Progress Fill
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = animatedProgress)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00E676),
                                    Color(0xFFFFEA00),
                                    Color(0xFFFF9100)
                                )
                            )
                        )
                )

                // 3 Star Node Indicators along the progress track
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star 1 (at ~33% or threshold 1)
                    val s1Ratio = (thresholds.first.toFloat() / maxScoreThreshold).coerceIn(0.1f, 0.9f)
                    val s2Ratio = (thresholds.second.toFloat() / maxScoreThreshold).coerceIn(0.2f, 0.95f)

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star 1",
                        tint = if (starsEarned >= 1) Color(0xFFFFD700) else Color(0x66FFFFFF),
                        modifier = Modifier
                            .size(10.dp)
                            .scale(if (starsEarned >= 1) 1.2f else 1.0f)
                    )

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star 2",
                        tint = if (starsEarned >= 2) Color(0xFFFFD700) else Color(0x66FFFFFF),
                        modifier = Modifier
                            .size(11.dp)
                            .scale(if (starsEarned >= 2) 1.25f else 1.0f)
                    )

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star 3",
                        tint = if (starsEarned >= 3) Color(0xFFFFD700) else Color(0x66FFFFFF),
                        modifier = Modifier
                            .size(12.dp)
                            .scale(if (starsEarned >= 3) 1.3f else 1.0f)
                    )
                }
            }
        }
    }
}
