package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.BoosterType
import com.example.model.LevelConfig
import com.example.model.LevelObjective

@Composable
fun LevelStartDialog(
    config: LevelConfig,
    boosters: Map<BoosterType, Int>,
    onStartLevel: (LevelConfig, BoosterType?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedBooster by remember { mutableStateOf<BoosterType?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1E1B)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF6D00))), RoundedCornerShape(28.dp))
                .shadow(20.dp)
                .testTag("level_start_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LEVEL ${config.levelNumber}",
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Moves Banner
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFFF9800), Color(0xFFE65100))))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${config.moves} MOVES",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Objectives
                Text(
                    text = "TARGET OBJECTIVES",
                    color = Color(0xFFFFE082),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                config.objectives.forEach { obj ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "• ${obj.title()}",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                if (config.tutorialMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "💡 ${config.tutorialMessage}",
                        color = Color(0xFFB9F6CA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Pre-game Booster Selection
                Text(
                    text = "Equip a Booster (Optional):",
                    color = Color(0xFFFFD54F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(BoosterType.MEGA_GOLGAPPA, BoosterType.EXTRA_MOVES).forEach { bType ->
                        val count = boosters[bType] ?: 0
                        val isSelected = selectedBooster == bType
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0x66FFD54F) else Color(0x22FFFFFF))
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Yellow else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (count > 0) {
                                        selectedBooster = if (isSelected) null else bType
                                    }
                                }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = if (bType == BoosterType.MEGA_GOLGAPPA) "✨ Mega Puri" else "➕ +5 Moves",
                                color = if (count > 0) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Qty: $count",
                                color = if (count > 0) Color(0xFFFFD54F) else Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Play Button
                Button(
                    onClick = { onStartLevel(config, selectedBooster) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("play_level_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLAY",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
