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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoosterType

/**
 * Bottom Booster Bar matching the Reference UI design:
 * Long carved wooden platter with 5 distinct terracotta clay bowl boosters:
 * 1. PUDINA WAVE (Splashing green mint wave in clay bowl)
 * 2. IMLI BLAST (Tangy tamarind swirl)
 * 3. MIRCHI BOMB (Fiery spicy chili)
 * 4. MIX SHUFFLE (Crispy puri bowl / boondi)
 * 5. MEGA GOLGAPPA (Golden royal golgappa)
 */
@Composable
fun BoosterBar(
    boosters: Map<BoosterType, Int>,
    activeBooster: BoosterType?,
    onBoosterClicked: (BoosterType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .shadow(12.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4E342E), Color(0xFF3E2723), Color(0xFF271510))
                )
            )
            .border(
                width = 2.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFFD54F), Color(0xFFFF8F00), Color(0xFFFFD54F))
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClayBoosterButton(
                titleTop = "PUDINA",
                titleBottom = "WAVE",
                iconEmoji = "🌊",
                liquidColor = Color(0xFF00E676),
                count = boosters[BoosterType.PANI_WAVE] ?: 3,
                isSelected = activeBooster == BoosterType.PANI_WAVE,
                onClick = { onBoosterClicked(BoosterType.PANI_WAVE) },
                testTag = "booster_pudina_wave"
            )

            ClayBoosterButton(
                titleTop = "IMLI",
                titleBottom = "BLAST",
                iconEmoji = "🍫",
                liquidColor = Color(0xFF5D4037),
                count = boosters[BoosterType.MIRCHI_BLAST] ?: 3,
                isSelected = activeBooster == BoosterType.MIRCHI_BLAST,
                onClick = { onBoosterClicked(BoosterType.MIRCHI_BLAST) },
                testTag = "booster_imli_blast"
            )

            ClayBoosterButton(
                titleTop = "MIRCHI",
                titleBottom = "BOMB",
                iconEmoji = "🌶️",
                liquidColor = Color(0xFFD50000),
                count = 2,
                isSelected = activeBooster == BoosterType.MIRCHI_BLAST,
                onClick = { onBoosterClicked(BoosterType.MIRCHI_BLAST) },
                testTag = "booster_mirchi_bomb"
            )

            ClayBoosterButton(
                titleTop = "MIX",
                titleBottom = "SHUFFLE",
                iconEmoji = "🥣",
                liquidColor = Color(0xFFFF9800),
                count = boosters[BoosterType.SPOON_SHUFFLE] ?: 3,
                isSelected = false,
                onClick = { onBoosterClicked(BoosterType.SPOON_SHUFFLE) },
                testTag = "booster_mix_shuffle"
            )

            ClayBoosterButton(
                titleTop = "MEGA",
                titleBottom = "GOLGAPPA",
                iconEmoji = "✨",
                liquidColor = Color(0xFFFFD700),
                count = boosters[BoosterType.MEGA_GOLGAPPA] ?: 1,
                isSelected = activeBooster == BoosterType.MEGA_GOLGAPPA,
                onClick = { onBoosterClicked(BoosterType.MEGA_GOLGAPPA) },
                testTag = "booster_mega_golgappa"
            )
        }
    }
}

@Composable
private fun ClayBoosterButton(
    titleTop: String,
    titleBottom: String,
    iconEmoji: String,
    liquidColor: Color,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp)
            .testTag(testTag)
    ) {
        // Terracotta Clay Bowl with liquid filling & emoji/icon
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(if (isSelected) 1.1f else 1f)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF8D6E63), Color(0xFF5D4037), Color(0xFF3E2723))
                    )
                )
                .border(
                    width = if (isSelected) 2.5.dp else 1.5.dp,
                    color = if (isSelected) Color(0xFFFFEB3B) else Color(0xFFFFD54F),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Liquid Inner Bowl
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(liquidColor.copy(alpha = 0.9f), liquidColor.copy(alpha = 0.4f), Color.Black)
                        )
                    )
                    .border(1.dp, Color(0x66FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = 18.sp
                )
            }

            // Green Count Badge in the corner (matching reference screenshot)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF00E676), Color(0xFF2E7D32))
                        )
                    )
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Two-line Gold / White Label
        Text(
            text = titleTop,
            color = if (isSelected) Color(0xFFFFEB3B) else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = titleBottom,
            color = if (isSelected) Color(0xFFFFEB3B) else Color(0xFFFFD54F),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 9.sp,
            textAlign = TextAlign.Center
        )
    }
}
