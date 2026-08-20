package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LevelObjective
import com.example.model.Match3GameState

/**
 * Authentic street-food / wooden carved HUD view matching the reference design:
 * - Left: Wooden Menu Button & Heart Life Capsule
 * - Center: Large Carved Wooden "MOVES" Board
 * - Right: Gold Coin Capsule with "+"
 * - Sub-bar: Leaf-shaped "GOAL" display with target piece, Center Pani Bowl Objective, and 3-Star Wicker Basket Tray
 */
@Composable
fun HUDView(
    state: Match3GameState,
    onPauseClicked: () -> Unit,
    coins: Int = 2500,
    lives: Int = 5,
    modifier: Modifier = Modifier
) {
    val moves = state.movesRemaining
    val score = state.currentScore
    val thresholds = state.levelConfig.starThresholds
    val starsCount = when {
        score >= thresholds.third -> 3
        score >= thresholds.second -> 2
        score >= thresholds.first -> 1
        else -> 0
    }

    val primaryObjective = state.objectives.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("hud_view")
    ) {
        // TOP ROW: [Menu] [Heart Capsule]  --- [MOVES Signboard] ---  [Coin Capsule]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Left Wooden Menu Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF8D6E63), Color(0xFF5D4037), Color(0xFF3E2723))
                        )
                    )
                    .border(2.5.dp, Color(0xFFFFD54F), CircleShape)
                    .clickable { onPauseClicked() }
                    .testTag("pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFFFFECB3),
                    modifier = Modifier.size(24.dp)
                )
            }

            // 2. Heart Lives Capsule
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF5D4037), Color(0xFF3E2723))
                        )
                    )
                    .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(22.dp))
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD32F2F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Lives",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "$lives",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "FULL",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // 3. Center Large Carved Wooden "MOVES" Signboard
            Box(
                modifier = Modifier
                    .width(108.dp)
                    .height(68.dp)
                    .shadow(8.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFFFE082), Color(0xFFFFCA28), Color(0xFFFFA000))
                        )
                    )
                    .border(3.dp, Color(0xFF5D4037), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2E7D32))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MOVES",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "$moves",
                        color = Color(0xFF3E2723),
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp
                    )
                }
            }

            // 4. Right Coins Capsule
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF5D4037), Color(0xFF3E2723))
                        )
                    )
                    .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(22.dp))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(Color(0xFFFFEE58), Color(0xFFF57F17))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "₹",
                            color = Color(0xFF3E2723),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "$coins",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Coins",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // SUB-ROW: [Leaf Goal Plaque]  -- [Center Goal Water Bowl] --  [3-Star Wicker Basket Tray]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Leaf Shaped Goal Plaque (Left)
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .shadow(6.dp, RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp, topEnd = 8.dp, bottomStart = 8.dp))
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp, topEnd = 8.dp, bottomStart = 8.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C))
                        )
                    )
                    .border(
                        2.dp,
                        Color(0xFFFFD54F),
                        RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp, topEnd = 8.dp, bottomStart = 8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Crispy Golgappa Mini Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFFE082), Color(0xFFFFB300), Color(0xFF8D6E63))
                                )
                            )
                            .border(1.5.dp, Color(0xFF4CAF50), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Green filling dot
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E7D32))
                        )
                    }

                    Column {
                        Text(
                            text = "GOAL",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                        if (primaryObjective != null) {
                            Text(
                                text = when (primaryObjective) {
                                    is LevelObjective.CollectPiece -> "${primaryObjective.current} / ${primaryObjective.target}"
                                    is LevelObjective.ClearBlocker -> "${primaryObjective.current} / ${primaryObjective.target}"
                                    is LevelObjective.CreateSpecial -> "${primaryObjective.current} / ${primaryObjective.target}"
                                    is LevelObjective.ReachScore -> "${score} / ${primaryObjective.target}"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = "Match 3",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // 2. Center Clay Bowl of Pani with Target Counter
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF388E3C), Color(0xFF1B5E20), Color(0xFF5D4037))
                        )
                    )
                    .border(2.5.dp, Color(0xFF8D6E63), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Liquid swirl & remaining goal pieces
                val targetRemaining = if (primaryObjective != null) {
                    maxOf(0, primaryObjective.target - primaryObjective.current)
                } else 0

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🌶️",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$targetRemaining",
                        color = Color(0xFFFFEB3B),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            // 3. 3-Star Wicker Basket Tray (Right)
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(44.dp)
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF5D4037), Color(0xFF3E2723))
                        )
                    )
                    .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(22.dp))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val isStarEarned = index < starsCount
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star ${index + 1}",
                            tint = if (isStarEarned) Color(0xFFFFD700) else Color(0x55FFFFFF),
                            modifier = Modifier.size(if (index == 1) 24.dp else 19.dp)
                        )
                    }
                }
            }
        }
    }
}
