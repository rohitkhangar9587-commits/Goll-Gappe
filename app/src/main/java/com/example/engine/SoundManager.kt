package com.example.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class SoundtrackType {
    NONE,
    GAMEPLAY,
    SCORE_SCREEN,
    MENU
}

class SoundManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var isSoundEnabled = true
    private var isMusicEnabled = true
    private var musicVolume = 0.8f
    private var isHapticsEnabled = true

    private var currentSoundtrack: SoundtrackType = SoundtrackType.NONE
    private var musicJob: Job? = null

    private val sampleRate = 44100

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (!enabled) {
            stopMusicPlayback()
        } else if (currentSoundtrack != SoundtrackType.NONE) {
            startMusicPlayback(currentSoundtrack)
        }
    }

    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        isHapticsEnabled = enabled
    }

    fun playSoundtrack(type: SoundtrackType) {
        if (currentSoundtrack == type && musicJob?.isActive == true) return
        currentSoundtrack = type
        if (isMusicEnabled && type != SoundtrackType.NONE) {
            startMusicPlayback(type)
        } else if (type == SoundtrackType.NONE) {
            stopMusicPlayback()
        }
    }

    fun stopSoundtrack() {
        currentSoundtrack = SoundtrackType.NONE
        stopMusicPlayback()
    }

    private fun stopMusicPlayback() {
        musicJob?.cancel()
        musicJob = null
    }

    private fun startMusicPlayback(type: SoundtrackType) {
        musicJob?.cancel()
        musicJob = scope.launch {
            when (type) {
                SoundtrackType.GAMEPLAY -> runGameplaySoundtrackLoop()
                SoundtrackType.SCORE_SCREEN -> runScoreSoundtrackLoop()
                SoundtrackType.MENU -> runMenuSoundtrackLoop()
                SoundtrackType.NONE -> {}
            }
        }
    }

    /**
     * Upbeat, fast-paced Indian street food match-3 gameplay soundtrack loop (~135 BPM)
     * Rich multi-tone flute/sitar melodies with rhythmic bass beats.
     */
    private suspend fun runGameplaySoundtrackLoop() {
        // Pentatonic C major folk scale frequencies (Hz)
        val c4 = 261.63f
        val d4 = 293.66f
        val e4 = 329.63f
        val g4 = 392.00f
        val a4 = 440.00f
        val c5 = 523.25f
        val d5 = 587.33f
        val e5 = 659.25f
        val g5 = 783.99f

        // 16-step upbeat melodic motifs
        val melody1 = listOf(c5, e5, g4, c5, d5, e5, d5, g4, c5, d5, e5, g5, e5, d5, c5, g4)
        val melody2 = listOf(a4, c5, e5, a4, g4, c5, e5, g4, fBass(c4), g4, a4, c5, d5, e5, d5, c4)

        val beatDurationMs = 210 // ~140 BPM eighth notes

        while (scope.isActive && currentSoundtrack == SoundtrackType.GAMEPLAY) {
            for (freq in melody1) {
                if (!scope.isActive || !isMusicEnabled || currentSoundtrack != SoundtrackType.GAMEPLAY) break
                playSynthMusicTone(freq, durationMs = beatDurationMs - 25, isHarmonic = true, bassPulse = true)
                delay(beatDurationMs.toLong())
            }
            for (freq in melody2) {
                if (!scope.isActive || !isMusicEnabled || currentSoundtrack != SoundtrackType.GAMEPLAY) break
                playSynthMusicTone(freq, durationMs = beatDurationMs - 25, isHarmonic = true, bassPulse = true)
                delay(beatDurationMs.toLong())
            }
        }
    }

    private fun fBass(base: Float): Float = base * 0.5f

    /**
     * Celebratory, triumphant, joyous Score Screen / Victory soundtrack loop (~115 BPM)
     * High shimmering celebratory chords, victory arpeggios, and fanfare cadences.
     */
    private suspend fun runScoreSoundtrackLoop() {
        val c4 = 261.63f
        val e4 = 329.63f
        val g4 = 392.00f
        val a4 = 440.00f
        val b4 = 493.88f
        val c5 = 523.25f
        val d5 = 587.33f
        val e5 = 659.25f
        val g5 = 783.99f
        val c6 = 1046.50f

        val victoryMotif = listOf(
            c5, e5, g5, c6,
            g5, e5, c5, g4,
            a4, c5, e5, a4,
            g4, b4, d5, g5,
            c5, e5, g5, c6
        )

        val beatDurationMs = 280

        while (scope.isActive && currentSoundtrack == SoundtrackType.SCORE_SCREEN) {
            for (freq in victoryMotif) {
                if (!scope.isActive || !isMusicEnabled || currentSoundtrack != SoundtrackType.SCORE_SCREEN) break
                playSynthMusicTone(freq, durationMs = beatDurationMs - 30, isHarmonic = true, isCelebratory = true)
                delay(beatDurationMs.toLong())
            }
            delay(400) // Brief musical pause before looping
        }
    }

    /**
     * Mellow, catchy festive street food raga lounge soundtrack loop (~110 BPM)
     */
    private suspend fun runMenuSoundtrackLoop() {
        val c4 = 261.63f
        val d4 = 293.66f
        val e4 = 329.63f
        val g4 = 392.00f
        val a4 = 440.00f
        val c5 = 523.25f

        val menuMotif = listOf(
            c4, e4, g4, a4, c5, a4, g4, e4,
            d4, e4, g4, a4, g4, e4, d4, c4
        )

        val beatDurationMs = 300

        while (scope.isActive && currentSoundtrack == SoundtrackType.MENU) {
            for (freq in menuMotif) {
                if (!scope.isActive || !isMusicEnabled || currentSoundtrack != SoundtrackType.MENU) break
                playSynthMusicTone(freq, durationMs = beatDurationMs - 35, isHarmonic = true)
                delay(beatDurationMs.toLong())
            }
            delay(200)
        }
    }

    private fun playSynthMusicTone(
        freq: Float,
        durationMs: Int,
        isHarmonic: Boolean = false,
        bassPulse: Boolean = false,
        isCelebratory: Boolean = false
    ) {
        if (!isMusicEnabled || musicVolume <= 0.01f) return

        scope.launch {
            try {
                val effectiveVol = musicVolume * 0.35f
                val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
                if (numSamples <= 0) return@launch
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toFloat() / sampleRate
                    val progress = i.toFloat() / numSamples
                    val envelope = (1f - progress) * (1f - exp(-progress * 25f))

                    // Fundamental sine tone
                    var sampleVal = sin(2.0 * PI * freq * t).toFloat()

                    // Harmonic warm overtone (sitar/flute shimmer)
                    if (isHarmonic) {
                        sampleVal += 0.4f * sin(2.0 * PI * (freq * 2f) * t).toFloat()
                        sampleVal += 0.15f * sin(2.0 * PI * (freq * 3f) * t).toFloat()
                    }

                    // Celebratory chime overtone
                    if (isCelebratory) {
                        sampleVal += 0.25f * sin(2.0 * PI * (freq * 4f) * t).toFloat()
                    }

                    // Percussive bass rhythm pulse
                    if (bassPulse && progress < 0.35f) {
                        val bassEnv = exp(-progress * 15f)
                        val bassVal = sin(2.0 * PI * (freq * 0.5f) * t).toFloat()
                        sampleVal += bassVal * bassEnv * 0.4f
                    }

                    buffer[i] = (sampleVal * envelope * Short.MAX_VALUE * effectiveVol).toInt().coerceIn(
                        Short.MIN_VALUE.toInt(),
                        Short.MAX_VALUE.toInt()
                    ).toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                delay(durationMs.toLong() + 10)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
            }
        }
    }

    private fun playTone(
        freqStart: Float,
        freqEnd: Float,
        durationMs: Int,
        decay: Float = 5f,
        waveType: Int = 0 // 0=Sine, 1=Square/Crunch, 2=Noise/Pani splash
    ) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
                if (numSamples <= 0) return@launch
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toFloat() / sampleRate
                    val progress = i.toFloat() / numSamples
                    val currentFreq = freqStart + (freqEnd - freqStart) * progress
                    val envelope = exp(-progress * decay)

                    val sampleVal: Float = when (waveType) {
                        1 -> { // Crunch / Square blend
                            val s = sin(2.0 * PI * currentFreq * t).toFloat()
                            if (s > 0f) 0.7f else -0.7f
                        }
                        2 -> { // Water splash noise blend
                            val noise = (Math.random() * 2.0 - 1.0).toFloat()
                            val s = sin(2.0 * PI * currentFreq * t).toFloat()
                            (s * 0.4f + noise * 0.6f)
                        }
                        else -> { // Pure Sine
                            sin(2.0 * PI * currentFreq * t).toFloat()
                        }
                    }

                    buffer[i] = (sampleVal * envelope * Short.MAX_VALUE * 0.6f).toInt().coerceIn(
                        Short.MIN_VALUE.toInt(),
                        Short.MAX_VALUE.toInt()
                    ).toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                delay(durationMs.toLong() + 20)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
            }
        }
    }

    fun playSwap() {
        playTone(320f, 480f, 60, decay = 8f)
        vibrate(15)
    }

    fun playInvalidSwap() {
        playTone(220f, 160f, 100, decay = 6f)
        vibrate(30)
    }

    fun playMatch(combo: Int = 1) {
        val pentatonic = listOf(523.25f, 587.33f, 659.25f, 783.99f, 880f, 1046.50f, 1174.66f, 1318.51f)
        val baseFreq = pentatonic[(combo - 1).coerceIn(0, pentatonic.size - 1)]
        playTone(baseFreq, baseFreq * 1.25f, 120, decay = 4f, waveType = 0)
        vibrate(25)
    }

    fun playSplash() {
        playTone(600f, 200f, 160, decay = 5f, waveType = 2)
        vibrate(35)
    }

    fun playLineBlast() {
        playTone(400f, 1200f, 220, decay = 3f, waveType = 1)
        vibrate(50)
    }

    fun playBomb() {
        playTone(180f, 60f, 280, decay = 2.5f, waveType = 1)
        vibrate(70)
    }

    fun playMega() {
        scope.launch {
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f, 1318.51f)
            for (f in notes) {
                playTone(f, f * 1.1f, 80, decay = 3f)
                delay(40)
            }
        }
        vibrate(90)
    }

    fun playCoin() {
        playTone(987.77f, 1318.51f, 150, decay = 4f)
        vibrate(20)
    }

    fun playVictory() {
        scope.launch {
            val fanfare = listOf(440f, 554.37f, 659.25f, 880f)
            for (f in fanfare) {
                playTone(f, f, 180, decay = 2f)
                delay(120)
            }
        }
        vibrate(100)
    }

    fun playDefeat() {
        scope.launch {
            val lossNotes = listOf(440f, 415.30f, 392f, 349.23f)
            for (f in lossNotes) {
                playTone(f, f * 0.95f, 160, decay = 3f)
                delay(100)
            }
        }
        vibrate(60)
    }

    fun playClick() {
        playTone(800f, 600f, 30, decay = 12f)
        vibrate(10)
    }

    private fun vibrate(durationMs: Long) {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }
}
