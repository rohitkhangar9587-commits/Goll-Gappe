package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.LevelGenerator
import com.example.engine.Match3Engine
import com.example.model.BoardCell
import com.example.model.PieceType
import com.example.model.SpecialType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `app name is correct`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Pani Puri Crush", appName)
    }

    @Test
    fun `level 1 initializes with an 8x8 grid`() {
        val lvl1 = LevelGenerator.getLevel(1)
        assertEquals(8, lvl1.rows)
        assertEquals(8, lvl1.cols)
        val board = Match3Engine.createInitialBoard(lvl1)
        assertEquals(8, board.size)
        assertEquals(8, board[0].size)
    }

    @Test
    fun `procedural level generator generates 999 valid levels`() {
        val lvl1 = LevelGenerator.getLevel(1)
        assertEquals(1, lvl1.levelNumber)
        assertTrue(lvl1.moves > 0)
        assertTrue(lvl1.objectives.isNotEmpty())

        val lvl50 = LevelGenerator.getLevel(50)
        assertEquals(50, lvl50.levelNumber)
        assertTrue(lvl50.moves > 0)

        val lvl999 = LevelGenerator.getLevel(999)
        assertEquals(999, lvl999.levelNumber)
        assertTrue(lvl999.moves > 0)
    }

    @Test
    fun `board generation and match detection`() {
        val level1 = LevelGenerator.getLevel(1)
        val board = Match3Engine.createInitialBoard(level1)
        assertEquals(level1.rows, board.size)
        assertEquals(level1.cols, board[0].size)

        // Find matches in initial board should be 0 (no initial matches created)
        val matches = Match3Engine.findMatches(board)
        assertEquals(0, matches.size)
    }

    @Test
    fun `match 3 pieces horizontally identifies match`() {
        val row = listOf(
            BoardCell(0, 0, pieceType = PieceType.CLASSIC),
            BoardCell(0, 1, pieceType = PieceType.CLASSIC),
            BoardCell(0, 2, pieceType = PieceType.CLASSIC),
            BoardCell(0, 3, pieceType = PieceType.PUDINA)
        )
        val board = listOf(row)
        val matches = Match3Engine.findMatches(board)
        assertEquals(1, matches.size)
        assertEquals(3, matches[0].cells.size)
        assertEquals(PieceType.CLASSIC, matches[0].pieceType)
    }

    @Test
    fun `match 4 pieces horizontally creates horizontal line special`() {
        val row = listOf(
            BoardCell(0, 0, pieceType = PieceType.PUDINA),
            BoardCell(0, 1, pieceType = PieceType.PUDINA),
            BoardCell(0, 2, pieceType = PieceType.PUDINA),
            BoardCell(0, 3, pieceType = PieceType.PUDINA)
        )
        val board = listOf(row)
        val matches = Match3Engine.findMatches(board, swapFocus = Pair(0, 2))
        assertEquals(1, matches.size)
        assertEquals(4, matches[0].cells.size)
        assertEquals(SpecialType.HORIZONTAL_LINE, matches[0].specialCreated)
    }

    @Test
    fun `swap adjacency validation`() {
        val level1 = LevelGenerator.getLevel(1)
        val board = Match3Engine.createInitialBoard(level1)
        // Non-adjacent swap is rejected
        assertFalse(Match3Engine.isValidSwap(board, Pair(0, 0), Pair(0, 2)))
        assertFalse(Match3Engine.isValidSwap(board, Pair(0, 0), Pair(2, 2)))
    }

    @Test
    fun `music settings persistence in repository`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = com.example.repository.GameRepository(context)

        // Default should be true and volume 0.8
        assertTrue(repository.musicEnabled.value)
        assertEquals(0.8f, repository.musicVolume.value, 0.01f)

        // Change music settings
        repository.setMusicEnabled(false)
        repository.setMusicVolume(0.45f)
        assertFalse(repository.musicEnabled.value)
        assertEquals(0.45f, repository.musicVolume.value, 0.01f)

        // Verify with a new repository instance
        val repo2 = com.example.repository.GameRepository(context)
        assertFalse(repo2.musicEnabled.value)
        assertEquals(0.45f, repo2.musicVolume.value, 0.01f)
    }

    @Test
    fun `sound manager handles soundtrack switching`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = com.example.engine.SoundManager(context)

        soundManager.setMusicEnabled(true)
        soundManager.setMusicVolume(0.7f)
        soundManager.playSoundtrack(com.example.engine.SoundtrackType.GAMEPLAY)
        soundManager.playSoundtrack(com.example.engine.SoundtrackType.SCORE_SCREEN)
        soundManager.playSoundtrack(com.example.engine.SoundtrackType.MENU)
        soundManager.stopSoundtrack()
    }
}
