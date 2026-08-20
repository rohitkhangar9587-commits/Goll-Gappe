package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.model.BoardCell
import com.example.model.FloatingText
import com.example.model.Match3GameState
import com.example.model.PaniBlastEffect
import com.example.model.Particle
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * High-performance, responsive Match-3 GameBoard UI component rendered in Jetpack Compose.
 * Features:
 * - AnimatedVisibility for animated piece destruction / removal.
 * - LaunchedEffect driven smooth fall-down vertical translation when pieces drop into empty cells.
 * - Special Pani Blast particle explosion and shockwave ripple animation for matches of 4+ pieces.
 * - Drag-and-drop piece swapping with live pointer displacement feedback.
 */
@Composable
fun GameBoardView(
    state: Match3GameState,
    particles: List<Particle>,
    floatingTexts: List<FloatingText>,
    onCellClicked: (Int, Int) -> Unit,
    onCellSwiped: (Int, Int, Int, Int) -> Unit,
    paniBlasts: List<PaniBlastEffect> = emptyList(),
    modifier: Modifier = Modifier
) {
    val rows = state.levelConfig.rows
    val cols = state.levelConfig.cols
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    // Live Drag-and-Drop state tracking
    var draggingRow by remember { mutableStateOf<Int?>(null) }
    var draggingCol by remember { mutableStateOf<Int?>(null) }
    var targetHoverRow by remember { mutableStateOf<Int?>(null) }
    var targetHoverCol by remember { mutableStateOf<Int?>(null) }

    val dragOffsetXPx = remember { Animatable(0f) }
    val dragOffsetYPx = remember { Animatable(0f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .aspectRatio(cols.toFloat() / rows.toFloat())
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2C3E50),
                        Color(0xFF1A252F),
                        Color(0xFF0D1317)
                    )
                )
            )
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00), Color(0xFF5D4037))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("game_board_container")
    ) {
        val boardWidth = maxWidth
        val boardHeight = maxHeight
        val cellWidth = boardWidth / cols
        val cellHeight = boardHeight / rows

        val cellWidthPx = with(density) { cellWidth.toPx() }
        val cellHeightPx = with(density) { cellHeight.toPx() }

        // 1. Grid Background Checkered Tiles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / cols
            val cellH = size.height / rows

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val isAlt = (r + c) % 2 == 0
                    val cellBg = if (isAlt) Color(0xFF1E2B37) else Color(0xFF17222C)
                    drawRect(
                        color = cellBg,
                        topLeft = Offset(c * cellW, r * cellH),
                        size = Size(cellW, cellH)
                    )
                    // Grid divider border
                    drawRect(
                        color = Color(0x11FFFFFF),
                        topLeft = Offset(c * cellW, r * cellH),
                        size = Size(cellW, cellH),
                        style = Stroke(width = 1f)
                    )
                }
            }

            // Draw target hover highlight during active drag
            val tRow = targetHoverRow
            val tCol = targetHoverCol
            if (tRow != null && tCol != null && tRow in 0 until rows && tCol in 0 until cols) {
                drawRoundRect(
                    color = Color(0xFFFFD54F).copy(alpha = 0.35f),
                    topLeft = Offset(tCol * cellW + 2f, tRow * cellH + 2f),
                    size = Size(cellW - 4f, cellH - 4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
            }
        }

        // 2. Drag & Tap Pointer Input Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(rows, cols, state.isResolving) {
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            if (state.isResolving) return@detectDragGestures
                            val c = (startOffset.x / cellWidthPx).toInt().coerceIn(0, cols - 1)
                            val r = (startOffset.y / cellHeightPx).toInt().coerceIn(0, rows - 1)
                            val cell = state.board[r][c]
                            if (cell.isPlayablePiece) {
                                draggingRow = r
                                draggingCol = c
                                coroutineScope.launch {
                                    dragOffsetXPx.snapTo(0f)
                                    dragOffsetYPx.snapTo(0f)
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val curR = draggingRow ?: return@detectDragGestures
                            val curC = draggingCol ?: return@detectDragGestures
                            if (state.isResolving) return@detectDragGestures

                            val maxDragLimit = maxOf(cellWidthPx, cellHeightPx) * 1.1f
                            val newX = (dragOffsetXPx.value + dragAmount.x).coerceIn(-maxDragLimit, maxDragLimit)
                            val newY = (dragOffsetYPx.value + dragAmount.y).coerceIn(-maxDragLimit, maxDragLimit)

                            coroutineScope.launch {
                                dragOffsetXPx.snapTo(newX)
                                dragOffsetYPx.snapTo(newY)
                            }

                            // Compute candidate target hover tile
                            val threshold = maxOf(cellWidthPx, cellHeightPx) * 0.4f
                            if (abs(newX) > threshold || abs(newY) > threshold) {
                                val dirR = if (abs(newY) > abs(newX)) (if (newY > 0) 1 else -1) else 0
                                val dirC = if (abs(newX) >= abs(newY)) (if (newX > 0) 1 else -1) else 0
                                val candR = curR + dirR
                                val candC = curC + dirC
                                if (candR in 0 until rows && candC in 0 until cols) {
                                    targetHoverRow = candR
                                    targetHoverCol = candC
                                } else {
                                    targetHoverRow = null
                                    targetHoverCol = null
                                }
                            } else {
                                targetHoverRow = null
                                targetHoverCol = null
                            }
                        },
                        onDragEnd = {
                            val curR = draggingRow
                            val curC = draggingCol
                            val tR = targetHoverRow
                            val tC = targetHoverCol

                            if (curR != null && curC != null && tR != null && tC != null && !state.isResolving) {
                                val dirR = tR - curR
                                val dirC = tC - curC
                                onCellSwiped(curR, curC, dirR, dirC)
                            }

                            // Reset drag state
                            coroutineScope.launch {
                                dragOffsetXPx.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                dragOffsetYPx.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                draggingRow = null
                                draggingCol = null
                                targetHoverRow = null
                                targetHoverCol = null
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                dragOffsetXPx.animateTo(0f)
                                dragOffsetYPx.animateTo(0f)
                                draggingRow = null
                                draggingCol = null
                                targetHoverRow = null
                                targetHoverCol = null
                            }
                        }
                    )
                }
        ) {
            // 3. Grid Cells Rendering with LaunchedEffect Fall Animation & AnimatedVisibility Piece Removal
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val cell = state.board[r][c]
                    val isSelected = state.selectedCell == Pair(r, c)
                    val isHint = state.hintCells.contains(Pair(r, c))
                    val isDraggingThisCell = (draggingRow == r && draggingCol == c)
                    val isHoveredTarget = (targetHoverRow == r && targetHoverCol == c)

                    AnimatedGridPieceCell(
                        cell = cell,
                        isSelected = isSelected,
                        isHint = isHint,
                        isDragging = isDraggingThisCell,
                        isHoveredTarget = isHoveredTarget,
                        dragOffsetXPx = dragOffsetXPx.value,
                        dragOffsetYPx = dragOffsetYPx.value,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight,
                        cellHeightPx = cellHeightPx,
                        onCellClicked = { onCellClicked(r, c) },
                        modifier = Modifier.offset(x = cellWidth * c, y = cellHeight * r)
                    )
                }
            }
        }

        // 4. Special Pani Blast Shockwave Ripples & Particle FX Layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / 480f
            val scaleY = size.height / 480f

            // A. Draw Pani Blast Shockwaves
            for (blast in paniBlasts) {
                val bx = blast.centerX * scaleX
                val by = blast.centerY * scaleY
                val progress = (blast.currentRadius / blast.maxRadius).coerceIn(0f, 1f)
                val ringRadius = blast.currentRadius * scaleX
                val strokeW = (8f * (1f - progress) + 2f) * scaleX

                // Outer expanding shockwave ripple
                drawCircle(
                    color = blast.color.copy(alpha = blast.alpha * 0.85f),
                    radius = ringRadius,
                    center = Offset(bx, by),
                    style = Stroke(width = strokeW)
                )

                // Secondary inner energy ripple
                if (ringRadius > 15f) {
                    drawCircle(
                        color = blast.secondaryColor.copy(alpha = blast.alpha * 0.6f),
                        radius = ringRadius * 0.7f,
                        center = Offset(bx, by),
                        style = Stroke(width = strokeW * 0.7f)
                    )
                }

                // Core splash flash
                if (progress < 0.4f) {
                    drawCircle(
                        color = Color.White.copy(alpha = (1f - progress / 0.4f) * 0.7f),
                        radius = 24f * scaleX,
                        center = Offset(bx, by)
                    )
                }
            }

            // B. Draw Dynamic Particles (Water drops, bubbles, puri crisps, spices)
            for (p in particles) {
                val px = p.x * scaleX
                val py = p.y * scaleY
                if (p.isSplashBubble) {
                    // Translucent bubble with white shine
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha * 0.6f),
                        radius = p.radius * scaleX,
                        center = Offset(px, py),
                        style = Stroke(width = 2f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = p.alpha * 0.8f),
                        radius = (p.radius * 0.35f) * scaleX,
                        center = Offset(px - p.radius * 0.3f * scaleX, py - p.radius * 0.3f * scaleY)
                    )
                } else if (p.isWaterDrop) {
                    drawCircle(
                        color = Color(0xFF00E5FF).copy(alpha = p.alpha),
                        radius = p.radius * scaleX,
                        center = Offset(px, py)
                    )
                } else {
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha),
                        radius = p.radius * scaleX,
                        center = Offset(px, py)
                    )
                }
            }
        }

        // 5. Floating Text & Pani Blast Banners Layer
        for (ft in floatingTexts) {
            if (ft.isPaniBlast) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = (ft.y - 280f).dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00B0FF), Color(0xFF00E5FF), Color(0xFF00E676))
                            )
                        )
                        .border(2.dp, Color.White, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = ft.text,
                        color = Color(0xFF0D47A1),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            } else {
                Text(
                    text = ft.text,
                    color = ft.color.copy(alpha = ft.alpha),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = (ft.y - 300f).dp)
                )
            }
        }

        // 6. Booster Target Mode Overlay
        if (state.activeBooster != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Tap any cell to use ${state.activeBooster.displayName}!",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(Color(0xCC000000), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Individual Grid Cell composable supporting:
 * - LaunchedEffect: Animates smooth fall-down vertical translation when a piece falls into the cell.
 * - AnimatedVisibility: Animates piece removal (scale out and fade out) when matched or destroyed.
 */
@Composable
private fun AnimatedGridPieceCell(
    cell: BoardCell,
    isSelected: Boolean,
    isHint: Boolean,
    isDragging: Boolean,
    isHoveredTarget: Boolean,
    dragOffsetXPx: Float,
    dragOffsetYPx: Float,
    cellWidth: Dp,
    cellHeight: Dp,
    cellHeightPx: Float,
    onCellClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fall-down translation animation when a piece moves/drops into an empty cell
    val fallOffsetY = remember(cell.id) { Animatable(-cellHeightPx * 1.5f) }

    LaunchedEffect(cell.id, cell.pieceType) {
        if (cell.pieceType != null || cell.isBlocker) {
            fallOffsetY.snapTo(-cellHeightPx * 1.2f)
            fallOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            fallOffsetY.snapTo(0f)
        }
    }

    Box(
        modifier = modifier
            .size(cellWidth, cellHeight)
            .then(
                if (isDragging) {
                    Modifier
                        .zIndex(10f)
                        .offset {
                            IntOffset(
                                dragOffsetXPx.roundToInt(),
                                dragOffsetYPx.roundToInt()
                            )
                        }
                        .scale(1.15f)
                        .shadow(12.dp, RoundedCornerShape(12.dp))
                } else if (isHoveredTarget) {
                    Modifier
                        .zIndex(5f)
                        .scale(1.06f)
                } else {
                    Modifier
                        .zIndex(1f)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = fallOffsetY.value.roundToInt()
                            )
                        }
                }
            )
            .pointerInput(cell.row, cell.col) {
                detectTapGestures {
                    onCellClicked()
                }
            }
            .testTag("cell_${cell.row}_${cell.col}")
    ) {
        // Animated piece removal with AnimatedVisibility
        AnimatedVisibility(
            visible = cell.pieceType != null || cell.isBlocker,
            enter = fadeIn(animationSpec = tween(180, easing = FastOutSlowInEasing)) + scaleIn(
                initialScale = 0.6f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
            exit = fadeOut(animationSpec = tween(160)) + scaleOut(
                targetScale = 0.2f,
                animationSpec = tween(160)
            )
        ) {
            GolgappaPieceView(
                cell = cell,
                isSelected = isSelected,
                isHint = isHint,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
