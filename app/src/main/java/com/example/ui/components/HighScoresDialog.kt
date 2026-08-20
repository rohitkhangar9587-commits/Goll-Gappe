package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.HighScoreRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Top 5 High Scores Dialog powered by Room Database:
 * Displays the player's top 5 highest scores in a modal
 * with gold/silver/bronze medals, stars, max combo achievements, and timestamps.
 */
@Composable
fun HighScoresDialog(
    highScores: List<HighScoreRecord>,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3E2723), // Dark Terracotta Brown
                            Color(0xFF271510),
                            Color(0xFF1B0C08)
                        )
                    )
                )
                .border(
                    width = 2.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFFFD54F), Color(0xFFFF8F00), Color(0xFF8D6E63))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(18.dp)
                .testTag("high_scores_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar with Trophy Icon and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(6.dp, CircleShape)
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
                                contentDescription = "Trophy",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "HALL OF FAME",
                                color = Color(0xFFFFD54F),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "TOP 5 HIGH SCORES",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0x33000000), CircleShape)
                            .testTag("close_high_scores_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // High Scores List or Empty State
                if (highScores.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No high scores recorded yet!\nMatch 3+ Golgappas to set a record!",
                            color = Color(0xCCFFFFFF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((highScores.size * 68).coerceAtMost(340).dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(highScores.take(5)) { index, item ->
                            val rank = index + 1
                            val rankColors = when (rank) {
                                1 -> listOf(Color(0xFFFFD700), Color(0xFFFFA000)) // Gold
                                2 -> listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E)) // Silver
                                3 -> listOf(Color(0xFFFFAB91), Color(0xFFD84315)) // Bronze
                                else -> listOf(Color(0xFF6D4C41), Color(0xFF4E342E))
                            }
                            val rankEmoji = when (rank) {
                                1 -> "🥇"
                                2 -> "🥈"
                                3 -> "🥉"
                                else -> "#$rank"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(14.dp))
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                if (rank == 1) Color(0xFF4E342E) else Color(0xFF33211D),
                                                Color(0xFF211310)
                                            )
                                        )
                                    )
                                    .border(
                                        width = if (rank == 1) 1.8.dp else 1.dp,
                                        brush = Brush.horizontalGradient(rankColors),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Rank Medal / Number
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Brush.radialGradient(rankColors))
                                        .border(1.dp, Color(0x66FFFFFF), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = rankEmoji,
                                        fontSize = if (rank <= 3) 16.sp else 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (rank > 3) Color.White else Color.Unspecified
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Level & Date
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Level ${item.levelNumber}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        // Stars
                                        Row {
                                            repeat(item.stars) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Star",
                                                    tint = Color(0xFFFFD700),
                                                    modifier = Modifier.size(11.dp)
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = dateFormat.format(Date(item.timestamp)),
                                            color = Color(0x88FFFFFF),
                                            fontSize = 10.sp
                                        )
                                        if (item.maxCombo > 1) {
                                            Text(
                                                text = "• x${item.maxCombo} Combo",
                                                color = Color(0xFFFFB300),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                // Score display
                                Text(
                                    text = String.format("%,d", item.score),
                                    color = if (rank == 1) Color(0xFFFFD54F) else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF8F00)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "CLOSE",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
