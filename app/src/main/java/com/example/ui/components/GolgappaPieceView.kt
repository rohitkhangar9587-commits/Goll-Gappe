package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.model.BlockerType
import com.example.model.BoardCell
import com.example.model.PieceType
import com.example.model.SpecialType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GolgappaPieceView(
    cell: BoardCell,
    isSelected: Boolean = false,
    isHint: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "piece_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelected) 1.12f else if (isHint) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mega_rotation"
    )

    Box(
        modifier = modifier.padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)
            val baseRadius = (minOf(w, h) / 2f) * 0.85f * (if (isSelected || isHint) pulseScale else 1f)

            if (cell.isBlocker) {
                drawBlocker(cell.blockerType, cell.blockerHp, w, h)
                return@Canvas
            }

            val piece = cell.pieceType ?: return@Canvas

            // Selection / Hint Halo
            if (isSelected) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFEB3B).copy(alpha = 0.8f), Color.Transparent),
                        center = center,
                        radius = baseRadius * 1.35f
                    ),
                    radius = baseRadius * 1.35f,
                    center = center
                )
            } else if (isHint) {
                drawCircle(
                    color = Color(0xFF00E676).copy(alpha = 0.6f),
                    radius = baseRadius * 1.25f,
                    center = center,
                    style = Stroke(width = 4.dp.toPx())
                )
            }

            // Draw Golgappa body based on flavor
            drawGolgappaBody(piece, center, baseRadius)

            // Draw Special overlays
            when (cell.specialType) {
                SpecialType.HORIZONTAL_LINE -> {
                    // Glowing horizontal arrows and neon stripes
                    val stripeColor = Color.White
                    drawLine(
                        color = stripeColor,
                        start = Offset(center.x - baseRadius * 0.9f, center.y),
                        end = Offset(center.x + baseRadius * 0.9f, center.y),
                        strokeWidth = 6.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFFFFD54F),
                        start = Offset(center.x - baseRadius * 0.6f, center.y - 6.dp.toPx()),
                        end = Offset(center.x + baseRadius * 0.6f, center.y - 6.dp.toPx()),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFFFFD54F),
                        start = Offset(center.x - baseRadius * 0.6f, center.y + 6.dp.toPx()),
                        end = Offset(center.x + baseRadius * 0.6f, center.y + 6.dp.toPx()),
                        strokeWidth = 3.dp.toPx()
                    )
                }
                SpecialType.VERTICAL_LINE -> {
                    // Glowing vertical arrows and neon stripes
                    val stripeColor = Color.White
                    drawLine(
                        color = stripeColor,
                        start = Offset(center.x, center.y - baseRadius * 0.9f),
                        end = Offset(center.x, center.y + baseRadius * 0.9f),
                        strokeWidth = 6.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFFFFD54F),
                        start = Offset(center.x - 6.dp.toPx(), center.y - baseRadius * 0.6f),
                        end = Offset(center.x - 6.dp.toPx(), center.y + baseRadius * 0.6f),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFFFFD54F),
                        start = Offset(center.x + 6.dp.toPx(), center.y - baseRadius * 0.6f),
                        end = Offset(center.x + 6.dp.toPx(), center.y + baseRadius * 0.6f),
                        strokeWidth = 3.dp.toPx()
                    )
                }
                SpecialType.BOMB -> {
                    // Spicy Bomb Aura with firecracker fuse
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFF1744).copy(alpha = 0.5f), Color.Transparent),
                            center = center,
                            radius = baseRadius * 1.3f
                        ),
                        radius = baseRadius * 1.3f,
                        center = center
                    )
                    // Fuse spark
                    drawCircle(
                        color = Color(0xFFFFEA00),
                        radius = 4.dp.toPx(),
                        center = Offset(center.x + baseRadius * 0.5f, center.y - baseRadius * 0.6f)
                    )
                    drawCircle(
                        color = Color(0xFFFF3D00),
                        radius = 2.5f.dp.toPx(),
                        center = Offset(center.x + baseRadius * 0.5f, center.y - baseRadius * 0.6f)
                    )
                }
                SpecialType.MEGA -> {
                    // Rotating Rainbow Prism Aura
                    rotate(rotationAngle, pivot = center) {
                        for (i in 0 until 6) {
                            val a = (i * 60.0 * Math.PI / 180.0)
                            val px = center.x + (cos(a) * baseRadius * 0.85f).toFloat()
                            val py = center.y + (sin(a) * baseRadius * 0.85f).toFloat()
                            val starColors = listOf(
                                Color(0xFFFF1744), Color(0xFFFFEA00), Color(0xFF00E676),
                                Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFFFF9100)
                            )
                            drawCircle(
                                color = starColors[i],
                                radius = 3.5.dp.toPx(),
                                center = Offset(px, py)
                            )
                        }
                    }
                    // Golden crown shimmer in center
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFD700), Color(0xFFFFA000)),
                            center = center,
                            radius = baseRadius * 0.5f
                        ),
                        radius = baseRadius * 0.45f,
                        center = center
                    )
                }
                SpecialType.NONE -> {}
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGolgappaBody(
    piece: PieceType,
    center: Offset,
    radius: Float
) {
    // 1. Crispy puffed golden puri crust
    val crustColors = when (piece) {
        PieceType.CLASSIC -> listOf(Color(0xFFFFD180), Color(0xFFFFA726), Color(0xFFE65100))
        PieceType.PUDINA -> listOf(Color(0xFFC8E6C9), Color(0xFF66BB6A), Color(0xFF1B5E20))
        PieceType.IMLI -> listOf(Color(0xFFFFCDD2), Color(0xFFE57373), Color(0xFFB71C1C))
        PieceType.MASALA -> listOf(Color(0xFFFFF9C4), Color(0xFFFFEE58), Color(0xFFF57F17))
        PieceType.ALOO -> listOf(Color(0xFFFFE0B2), Color(0xFFFF9800), Color(0xFFBF360C))
        PieceType.DAHI -> listOf(Color(0xFFF3E5F5), Color(0xFFBA68C8), Color(0xFF4A148C))
    }

    // Outer Puri Shell
    drawCircle(
        brush = Brush.radialGradient(
            colors = crustColors,
            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
            radius = radius * 1.1f
        ),
        radius = radius,
        center = center
    )

    // Shell rim border
    drawCircle(
        color = Color(0xFF5D4037).copy(alpha = 0.35f),
        radius = radius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )

    // Glossy specular highlight on crispy puri top
    drawOval(
        color = Color.White.copy(alpha = 0.45f),
        topLeft = Offset(center.x - radius * 0.55f, center.y - radius * 0.7f),
        size = Size(radius * 0.65f, radius * 0.35f)
    )

    // 2. Inner Puffed Opening with Pani/Filling
    val fillingRadius = radius * 0.55f
    val fillingOffset = Offset(center.x, center.y + radius * 0.05f)

    val paniColors = when (piece) {
        PieceType.CLASSIC -> listOf(Color(0xFF689F38), Color(0xFF33691E))
        PieceType.PUDINA -> listOf(Color(0xFF00E676), Color(0xFF1B5E20))
        PieceType.IMLI -> listOf(Color(0xFFFF1744), Color(0xFF880E4F))
        PieceType.MASALA -> listOf(Color(0xFFFFD600), Color(0xFFFF6D00))
        PieceType.ALOO -> listOf(Color(0xFFFF6F00), Color(0xFF8D6E63))
        PieceType.DAHI -> listOf(Color(0xFFFFFFFF), Color(0xFFF8BBD0))
    }

    // Opening hole
    drawCircle(
        brush = Brush.radialGradient(
            colors = paniColors,
            center = fillingOffset,
            radius = fillingRadius
        ),
        radius = fillingRadius,
        center = fillingOffset
    )

    // Crunchy boondi / masala specks inside
    val speckColor = when (piece) {
        PieceType.DAHI -> Color(0xFFD50000) // Pomegranate / Sev
        PieceType.PUDINA -> Color(0xFFC8E6C9)
        PieceType.IMLI -> Color(0xFFFFD54F)
        else -> Color(0xFFFFE082)
    }

    drawCircle(color = speckColor, radius = 2.5f.dp.toPx(), center = Offset(fillingOffset.x - 4.dp.toPx(), fillingOffset.y - 3.dp.toPx()))
    drawCircle(color = speckColor, radius = 2f.dp.toPx(), center = Offset(fillingOffset.x + 5.dp.toPx(), fillingOffset.y + 2.dp.toPx()))
    drawCircle(color = speckColor, radius = 1.8f.dp.toPx(), center = Offset(fillingOffset.x - 1.dp.toPx(), fillingOffset.y + 5.dp.toPx()))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBlocker(
    blocker: BlockerType,
    hp: Int,
    w: Float,
    h: Float
) {
    val center = Offset(w / 2f, h / 2f)
    val sizePx = minOf(w, h) * 0.85f

    when (blocker) {
        BlockerType.ALOO_BLOCK -> {
            // Spiced potato chunk block
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFB74D), Color(0xFFF57C00), Color(0xFFE65100))
                ),
                topLeft = Offset(center.x - sizePx / 2f, center.y - sizePx / 2f),
                size = Size(sizePx, sizePx),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
            )
            // Masala seasoning sprinkles
            drawCircle(color = Color(0xFF4E342E), radius = 2.dp.toPx(), center = Offset(center.x - 6.dp.toPx(), center.y - 6.dp.toPx()))
            drawCircle(color = Color(0xFFD84315), radius = 2.dp.toPx(), center = Offset(center.x + 8.dp.toPx(), center.y - 4.dp.toPx()))
            drawCircle(color = Color(0xFF4E342E), radius = 2.dp.toPx(), center = Offset(center.x + 2.dp.toPx(), center.y + 7.dp.toPx()))
        }
        BlockerType.MATKA_BLOCK -> {
            // Earthenware clay pot
            val potPath = Path().apply {
                moveTo(center.x - sizePx * 0.35f, center.y - sizePx * 0.4f)
                lineTo(center.x + sizePx * 0.35f, center.y - sizePx * 0.4f)
                cubicTo(
                    center.x + sizePx * 0.55f, center.y - sizePx * 0.1f,
                    center.x + sizePx * 0.5f, center.y + sizePx * 0.45f,
                    center.x, center.y + sizePx * 0.45f
                )
                cubicTo(
                    center.x - sizePx * 0.5f, center.y + sizePx * 0.45f,
                    center.x - sizePx * 0.55f, center.y - sizePx * 0.1f,
                    center.x - sizePx * 0.35f, center.y - sizePx * 0.4f
                )
                close()
            }
            drawPath(
                path = potPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFD84315), Color(0xFF8D6E63), Color(0xFF3E2723)),
                    center = center,
                    radius = sizePx * 0.6f
                )
            )
            // Matka rim
            drawOval(
                color = Color(0xFFFFAB91),
                topLeft = Offset(center.x - sizePx * 0.35f, center.y - sizePx * 0.45f),
                size = Size(sizePx * 0.7f, sizePx * 0.18f)
            )
            // Cracks if damaged
            if (hp < 3) {
                drawLine(
                    color = Color.Black,
                    start = Offset(center.x - sizePx * 0.2f, center.y - sizePx * 0.1f),
                    end = Offset(center.x + sizePx * 0.1f, center.y + sizePx * 0.2f),
                    strokeWidth = 2.dp.toPx()
                )
            }
            if (hp == 1) {
                drawLine(
                    color = Color.Black,
                    start = Offset(center.x + sizePx * 0.2f, center.y - sizePx * 0.2f),
                    end = Offset(center.x - sizePx * 0.1f, center.y + sizePx * 0.3f),
                    strokeWidth = 2.5f.dp.toPx()
                )
            }
        }
        BlockerType.PANI_JAR -> {
            // Glass Jar with green mint water
            drawRoundRect(
                color = Color(0xFF80DEEA).copy(alpha = 0.5f),
                topLeft = Offset(center.x - sizePx * 0.4f, center.y - sizePx * 0.45f),
                size = Size(sizePx * 0.8f, sizePx * 0.9f),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF00E676).copy(alpha = 0.75f),
                topLeft = Offset(center.x - sizePx * 0.35f, center.y - sizePx * 0.1f),
                size = Size(sizePx * 0.7f, sizePx * 0.5f),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
        BlockerType.MASALA_BLOCK -> {
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFF5252), Color(0xFFD50000), Color(0xFFB71C1C))
                ),
                topLeft = Offset(center.x - sizePx / 2f, center.y - sizePx / 2f),
                size = Size(sizePx, sizePx),
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
            )
            // Spicy chili icon mark
            drawCircle(color = Color(0xFFFFD600), radius = 4.dp.toPx(), center = center)
        }
        BlockerType.SEALED_GOLGAPPA -> {
            // Silver foil / cloth sealed golgappa
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC), Color(0xFF78909C)),
                    center = center,
                    radius = sizePx * 0.5f
                ),
                radius = sizePx * 0.45f,
                center = center
            )
            drawCircle(
                color = Color(0xFFFFD700),
                radius = sizePx * 0.45f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        BlockerType.NONE -> {}
    }
}
