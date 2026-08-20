package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Main Menu Title Screen matching Reference Image 1:
 * - Sky blue clouds background with sunburst rays and floating 3D golgappas
 * - 3D glossy embossed game title logo
 * - Wooden carved HUD bar with Mail, Heart capsule, Level star badge, Coin bar, and Settings
 * - Chunky 3D embossed PLAY button
 * - Quick action portals for Level Map, Daily Rewards, Booster Shop, and Achievements
 */
@Composable
fun MainMenuScreen(
    unlockedLevel: Int,
    coins: Int,
    lives: Int,
    onPlayClicked: () -> Unit,
    onMapClicked: () -> Unit,
    onDailyRewardClicked: () -> Unit,
    onBoosterShopClicked: () -> Unit,
    onAchievementsClicked: () -> Unit,
    onStatsClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "title_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_menu_screen")
    ) {
        // 1. Full-screen Clouds & Golgappas Background Art
        Image(
            painter = painterResource(id = R.drawable.bg_splash_clouds),
            contentDescription = "Splash Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Subtle gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x33000000),
                            Color(0x99000000)
                        )
                    )
                )
        )

        // 2. Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP STATUS BAR (Wooden carved style matching Reference Image 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mail Button with notification badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF8D6E63), Color(0xFF5D4037), Color(0xFF3E2723))
                            )
                        )
                        .border(2.dp, Color(0xFFFFD54F), CircleShape)
                        .clickable { onAchievementsClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Mail,
                        contentDescription = "Mail",
                        tint = Color(0xFFFFECB3),
                        modifier = Modifier.size(20.dp)
                    )
                    // Red Notification Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD50000))
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Heart Lives Capsule
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF5D4037), Color(0xFF3E2723))
                            )
                        )
                        .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Lives",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "$lives",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Text(
                            "Full",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                // Current Level Star Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFFD54F), Color(0xFFFF8F00), Color(0xFF5D4037))
                            )
                        )
                        .border(2.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$unlockedLevel",
                            color = Color(0xFF3E2723),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }

                // Coins Capsule
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF5D4037), Color(0xFF3E2723))
                            )
                        )
                        .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp)
                        .clickable { onBoosterShopClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🪙", fontSize = 13.sp)
                        Text(
                            "$coins",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Settings Gear Button
                IconButton(
                    onClick = onSettingsClicked,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .border(1.5.dp, Color(0x66FFFFFF), CircleShape)
                        .testTag("settings_button")
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. 3D Embossed Title Logo Banner
            Box(
                modifier = Modifier
                    .scale(pulseScale)
                    .fillMaxWidth()
                    .height(210.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_game_logo),
                    contentDescription = "Goll Gappe Crunch Game Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Primary Chunky 3D PLAY Button (Matching Reference Design)
            Button(
                onClick = onPlayClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(68.dp)
                    .shadow(16.dp, RoundedCornerShape(26.dp))
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFF57C00), Color(0xFFE65100))
                        )
                    )
                    .border(3.dp, Color(0xFFFFF9C4), RoundedCornerShape(26.dp))
                    .testTag("main_play_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF3E2723),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY LEVEL $unlockedLevel",
                        color = Color(0xFF3E2723),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Level Map Winding Road Portal Button
            Button(
                onClick = onMapClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(54.dp)
                    .shadow(10.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF2E7D32), Color(0xFF388E3C), Color(0xFF4CAF50))
                        )
                    )
                    .border(2.5.dp, Color(0xFFFFD54F), RoundedCornerShape(22.dp))
                    .testTag("main_map_button")
            ) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "EXPLORE FLAVOR MAP",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Secondary Quick Actions Grid matching map icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MenuFeatureCard(
                    title = "Daily Reward",
                    icon = Icons.Default.CardGiftcard,
                    color = Color(0xFFFF4081),
                    badgeText = "!",
                    modifier = Modifier.weight(1f),
                    onClick = onDailyRewardClicked,
                    testTag = "main_daily_reward_button"
                )
                MenuFeatureCard(
                    title = "Booster Shop",
                    icon = Icons.Default.ShoppingCart,
                    color = Color(0xFF00E5FF),
                    badgeText = null,
                    modifier = Modifier.weight(1f),
                    onClick = onBoosterShopClicked,
                    testTag = "main_booster_shop_button"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MenuFeatureCard(
                    title = "Achievements",
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFFE040FB),
                    badgeText = null,
                    modifier = Modifier.weight(1f),
                    onClick = onAchievementsClicked,
                    testTag = "main_achievements_button"
                )
                MenuFeatureCard(
                    title = "Player Stats",
                    icon = Icons.Default.Leaderboard,
                    color = Color(0xFF00E676),
                    badgeText = null,
                    modifier = Modifier.weight(1f),
                    onClick = onStatsClicked,
                    testTag = "main_stats_button"
                )
            }
        }
    }
}

@Composable
private fun MenuFeatureCard(
    title: String,
    icon: ImageVector,
    color: Color,
    badgeText: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xCC2C1E1B)),
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .border(2.dp, color.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD50000))
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badgeText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
