package com.example.model

enum class PieceType(val displayName: String, val description: String) {
    CLASSIC("Classic Golgappa", "Crispy golden puri"),
    PUDINA("Pudina Golgappa", "Refreshing green mint flavor"),
    IMLI("Imli Golgappa", "Tangy sweet dark-red tamarind"),
    MASALA("Masala Golgappa", "Spicy aromatic yellow masala"),
    ALOO("Aloo Golgappa", "Savory spiced potato mix"),
    DAHI("Dahi Golgappa", "Creamy sweet & sour curd")
}

enum class SpecialType {
    NONE,
    HORIZONTAL_LINE, // Clears full row
    VERTICAL_LINE,   // Clears full column
    BOMB,            // 3x3 explosion
    MEGA             // Rainbow Mega Golgappa (clears all of targeted flavor / massive area)
}

enum class BlockerType(val maxHp: Int, val displayName: String) {
    NONE(0, "None"),
    ALOO_BLOCK(1, "Aloo Chunk"),         // Destroyed by 1 adjacent match or blast
    MATKA_BLOCK(3, "Clay Matka"),        // 3 hits to break open
    PANI_JAR(2, "Pani Jar"),             // 2 hits to shatter
    MASALA_BLOCK(2, "Masala Block"),     // 2 hits to clear
    SEALED_GOLGAPPA(1, "Sealed Puri")    // Cracks into a playable piece
}

data class BoardCell(
    val row: Int,
    val col: Int,
    val pieceType: PieceType? = null,
    val specialType: SpecialType = SpecialType.NONE,
    val blockerType: BlockerType = BlockerType.NONE,
    val blockerHp: Int = 0,
    val id: Long = System.nanoTime() + (row * 31L + col)
) {
    val isBlocker: Boolean
        get() = blockerType != BlockerType.NONE

    val isPlayablePiece: Boolean
        get() = pieceType != null && blockerType == BlockerType.NONE

    val isSpecial: Boolean
        get() = specialType != SpecialType.NONE
}
