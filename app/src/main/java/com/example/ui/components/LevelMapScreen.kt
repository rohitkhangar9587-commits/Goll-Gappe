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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Level Map Screen matching Reference Image 2:
 * - Winding flavor river with green pudina, red imli, yellow masala pani banks
 * - Top carved wooden HUD: Mail [3], Heart [5 Full +], Level [17], Coins [2,500 +], Settings
 * - Floating side badges: Daily Reward [!], Special Offers [!]
 * - Crispy golden Puri level nodes with 3D white digits, golden stars, and milestone crowns
 * - Bottom 3-tab stone navigation bar: [MAP], [EVENTS], [SHOP]
 */
@Composable
fun LevelMapScreen(
    unlockedLevel: Int,
    levelStars: Map<Int, Int>,
    coins: Int,
    lives: Int,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onDailyRewardClicked: () -> Unit = {},
    onShopClicked: () -> Unit = {},
    onEventsClicked: () -> Unit = {}
) {
    val totalLevels = 999
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(unlockedLevel) {
        val targetIndex = (unlockedLevel - 1).coerceIn(0, totalLevels - 1)
        listState.scrollToItem(maxOf(0, targetIndex - 2))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_map")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_current"
    )

    Scaffold(
        topBar = {
            // TOP CARVED WOODEN HUD BAR (Matching Reference Image 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF3E2723), Color(0xFF2C1810))
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))),
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button & Mail
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .testTag("map_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Mail Capsule with notification badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF8D6E63), Color(0xFF5D4037))
                                )
                            )
                            .border(1.5.dp, Color(0xFFFFD54F), CircleShape)
                            .clickable { onDailyRewardClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Mail,
                            contentDescription = "Mail",
                            tint = Color(0xFFFFECB3),
                            modifier = Modifier.size(18.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD50000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("3", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Heart Lives Capsule
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(19.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF5D4037), Color(0xFF3E2723))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(19.dp))
                        .padding(horizontal = 8.dp),
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
                            modifier = Modifier.size(16.dp)
                        )
                        Text("$lives", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Text("Full", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    }
                }

                // Current Level Star Indicator
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFFD54F), Color(0xFFF57C00))
                            )
                        )
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$unlockedLevel", color = Color(0xFF3E2723), fontWeight = FontWeight.Black, fontSize = 15.sp)
                }

                // Coins Capsule
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(19.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF5D4037), Color(0xFF3E2723))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(19.dp))
                        .padding(horizontal = 6.dp)
                        .clickable { onShopClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🪙", fontSize = 12.sp)
                        Text("$coins", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                        }
                    }
                }

                // Settings
                IconButton(
                    onClick = { /* settings */ },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        },
        bottomBar = {
            // BOTTOM 3-TAB NAVIGATION BAR (MAP, EVENTS, SHOP - Matching Reference Image 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF3E2723), Color(0xFF1B110F))
                        )
                    )
                    .border(
                        1.5.dp,
                        Color(0xFFFFD54F),
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // MAP TAB (Active)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                                )
                            )
                            .border(2.dp, Color(0xFFFFD54F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Map, contentDescription = "Map", tint = Color(0xFFFFEB3B), modifier = Modifier.size(22.dp))
                    }
                    Text("MAP", color = Color(0xFFFFEB3B), fontWeight = FontWeight.Black, fontSize = 10.sp)
                }

                // EVENTS TAB
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onEventsClicked() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFE65100), Color(0xFFBF360C))
                                )
                            )
                            .border(2.dp, Color(0xFFFFD54F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Events", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Text("EVENTS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }

                // SHOP TAB
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onShopClicked() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF6A1B9A), Color(0xFF4A148C))
                                )
                            )
                            .border(2.dp, Color(0xFFFFD54F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Shop", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Text("SHOP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val target = (unlockedLevel - 1).coerceIn(0, totalLevels - 1)
                        listState.animateScrollToItem(maxOf(0, target - 2))
                    }
                },
                containerColor = Color(0xFFFF9800),
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.testTag("jump_to_current_level_button")
            ) {
                Icon(Icons.Default.Navigation, contentDescription = "Current Level")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Winding Flavor River Background
            Image(
                painter = painterResource(id = R.drawable.bg_map_river),
                contentDescription = "Map River Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 2. Left Floating Side Badges (Daily Reward & Special Offers)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // DAILY REWARD BADGE
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                            )
                        )
                        .border(2.dp, Color(0xFFFFD54F), CircleShape)
                        .clickable { onDailyRewardClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Daily Reward", tint = Color(0xFFFFD54F), modifier = Modifier.size(22.dp))
                        Text("DAILY", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD50000))
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }

                // SPECIAL OFFERS BADGE
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFE65100), Color(0xFFBF360C))
                            )
                        )
                        .border(2.dp, Color(0xFFFFD54F), CircleShape)
                        .clickable { onShopClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌶️", fontSize = 14.sp)
                        Text("OFFERS", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD50000))
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            // 3. Winding Level Road Nodes
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(totalLevels) { index ->
                    val lvl = index + 1
                    val isUnlocked = lvl <= unlockedLevel
                    val isCurrent = lvl == unlockedLevel
                    val stars = levelStars[lvl] ?: 0
                    val isMilestone = (lvl % 5 == 0)

                    val horizontalBias = (sin(lvl * 0.8) * 85f).dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(
                                    start = if (horizontalBias > 0.dp) horizontalBias else 0.dp,
                                    end = if (horizontalBias < 0.dp) -horizontalBias else 0.dp
                                )
                                .clickable(enabled = isUnlocked) {
                                    onLevelSelected(lvl)
                                }
                                .testTag("map_level_${lvl}")
                        ) {
                            // Milestone Crown or Avatar Pin
                            if (isMilestone && isUnlocked) {
                                Text("👑", fontSize = 18.sp)
                            } else if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF00E676))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("YOU'RE HERE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            // Crispy Golden Puri Level Node (3D styled)
                            Box(
                                modifier = Modifier
                                    .size(if (isCurrent) 66.dp else 54.dp)
                                    .scale(if (isCurrent) pulseScale else 1f)
                                    .shadow(if (isCurrent) 14.dp else 6.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> Brush.radialGradient(
                                                listOf(Color(0xFFFFEA00), Color(0xFFFFB300), Color(0xFFE65100))
                                            )
                                            isUnlocked -> Brush.radialGradient(
                                                listOf(Color(0xFFFFE082), Color(0xFFFFB74D), Color(0xFFD87D2B))
                                            )
                                            else -> Brush.radialGradient(
                                                listOf(Color(0xFF757575), Color(0xFF424242), Color(0xFF212121))
                                            )
                                        }
                                    )
                                    .border(
                                        width = if (isCurrent) 3.dp else 2.dp,
                                        color = if (isCurrent) Color.White else if (isUnlocked) Color(0xFF8D6E63) else Color(0xFF616161),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUnlocked) {
                                    Text(
                                        text = "$lvl",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = if (isCurrent) 22.sp else 18.sp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0xFFBDBDBD),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // 3 Golden Stars underneath completed levels
                            if (isUnlocked) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(3) { starIdx ->
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = "Star",
                                            tint = if (starIdx < stars || (stars == 0 && isUnlocked && !isCurrent)) Color(0xFFFFD700) else Color(0x44FFFFFF),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
