package com.lee.floatingkeyboard.keyboard.input

/**
 * Korean IME Composer with proper composition behavior
 * Handles syllable formation, decomposition, and composition state
 */
class KoreanIMEComposer {
    companion object {
        // Unicode Hangul base constants
        private const val HANGUL_BASE = 0xAC00
        private const val HANGUL_END = 0xD7A3

        // Jamo counts
        private const val CHOSEONG_COUNT = 19
        private const val JUNGSEONG_COUNT = 21
        private const val JONGSEONG_COUNT = 28

        // Choseong (초성) - Leading consonants
        private val CHOSEONG = charArrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

        // Jungseong (중성) - Medial vowels
        private val JUNGSEONG = charArrayOf(
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
        )

        // Jongseong (종성) - Trailing consonants
        private val JONGSEONG = charArrayOf(
            '\u0000', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

        // Complex vowel combinations
        private val VOWEL_COMBINATIONS = mapOf(
            "ㅗㅏ" to 'ㅘ', "ㅗㅐ" to 'ㅙ', "ㅗㅣ" to 'ㅚ',
            "ㅜㅓ" to 'ㅝ', "ㅜㅔ" to 'ㅞ', "ㅜㅣ" to 'ㅟ',
            "ㅡㅣ" to 'ㅢ'
        )

        // Complex consonant combinations for jongseong
        private val CONSONANT_COMBINATIONS = mapOf(
            "ㄱㅅ" to 'ㄳ', "ㄴㅈ" to 'ㄵ', "ㄴㅎ" to 'ㄶ',
            "ㄹㄱ" to 'ㄺ', "ㄹㅁ" to 'ㄻ', "ㄹㅂ" to 'ㄼ',
            "ㄹㅅ" to 'ㄽ', "ㄹㅌ" to 'ㄾ', "ㄹㅍ" to 'ㄿ',
            "ㄹㅎ" to 'ㅀ', "ㅂㅅ" to 'ㅄ'
        )
    }

    // Current syllable being composed
    private var choseong: Char? = null
    private var jungseong: Char? = null
    private var jongseong: Char? = null
    private var isComposing = false

    // Previous committed text for syllable decomposition
    private var committedText = StringBuilder()

    /**
     * Check if character is a valid jamo
     */
    private fun isJamo(char: Char): Boolean {
        return CHOSEONG.contains(char) ||
               JUNGSEONG.contains(char) ||
               JONGSEONG.contains(char)
    }

    /**
     * Check if character is a choseong (initial consonant)
     */
    private fun isChoseong(char: Char): Boolean = CHOSEONG.contains(char)

    /**
     * Check if character is a jungseong (vowel)
     */
    private fun isJungseong(char: Char): Boolean = JUNGSEONG.contains(char)

    /**
     * Check if character is a jongseong (final consonant)
     */
    private fun isJongseong(char: Char): Boolean = JONGSEONG.contains(char)

    /**
     * Decompose a Hangul syllable into component jamo
     */
    private fun decomposeSyllable(syllable: Char): Triple<Char?, Char?, Char?> {
        val code = syllable.code
        if (code < HANGUL_BASE || code > HANGUL_END) {
            return Triple(null, null, null)
        }

        val syllableIndex = code - HANGUL_BASE
        val jongseongIndex = syllableIndex % JONGSEONG_COUNT
        val jungseongIndex = (syllableIndex / JONGSEONG_COUNT) % JUNGSEONG_COUNT
        val choseongIndex = syllableIndex / (JUNGSEONG_COUNT * JONGSEONG_COUNT)

        return Triple(
            CHOSEONG[choseongIndex],
            JUNGSEONG[jungseongIndex],
            if (jongseongIndex == 0) null else JONGSEONG[jongseongIndex]
        )
    }

    /**
     * Compose jamo into a Hangul syllable
     */
    private fun composeSyllable(choseong: Char, jungseong: Char, jongseong: Char? = null): Char? {
        val choseongIndex = CHOSEONG.indexOf(choseong)
        val jungseongIndex = JUNGSEONG.indexOf(jungseong)
        val jongseongIndex = if (jongseong == null) 0 else JONGSEONG.indexOf(jongseong)

        if (choseongIndex == -1 || jungseongIndex == -1 || jongseongIndex == -1) {
            return null
        }

        val code = HANGUL_BASE +
                  (choseongIndex * JUNGSEONG_COUNT + jungseongIndex) * JONGSEONG_COUNT +
                  jongseongIndex

        return code.toChar()
    }

    /**
     * Try to combine two jamo into a complex jamo
     */
    private fun combineJamo(first: Char, second: Char): Char? {
        val combination = "$first$second"
        return VOWEL_COMBINATIONS[combination] ?: CONSONANT_COMBINATIONS[combination]
    }

    /**
     * Get current composition result
     */
    private fun getCurrentSyllable(): String {
        if (!isComposing) return ""

        return when {
            choseong != null && jungseong != null -> {
                val syllable = composeSyllable(choseong!!, jungseong!!, jongseong)
                syllable?.toString() ?: ""
            }
            choseong != null -> choseong.toString()
            jungseong != null -> jungseong.toString()
            else -> ""
        }
    }

    /**
     * Add jamo character to current composition
     */
    fun addJamo(jamo: Char): CompositionResult {
        if (!isJamo(jamo)) {
            return CompositionResult(null, false)
        }

        return when {
            isChoseong(jamo) -> handleChoseong(jamo)
            isJungseong(jamo) -> handleJungseong(jamo)
            isJongseong(jamo) -> handleJongseong(jamo)
            else -> CompositionResult(null, false)
        }
    }

    /**
     * Handle choseong (initial consonant) input
     */
    private fun handleChoseong(consonant: Char): CompositionResult {
        when {
            !isComposing -> {
                // Start new syllable
                choseong = consonant
                jungseong = null
                jongseong = null
                isComposing = true
                return CompositionResult(getCurrentSyllable(), true)
            }

            jungseong == null -> {
                // Replace choseong if no vowel yet
                choseong = consonant
                return CompositionResult(getCurrentSyllable(), true)
            }

            jongseong == null -> {
                // Add as jongseong
                jongseong = consonant
                return CompositionResult(getCurrentSyllable(), true)
            }

            else -> {
                // Complete current syllable and start new one
                val completedSyllable = getCurrentSyllable()
                committedText.append(completedSyllable)

                // Start new syllable
                choseong = consonant
                jungseong = null
                jongseong = null
                isComposing = true

                return CompositionResult(
                    committedText.toString() + getCurrentSyllable(),
                    true,
                    committed = completedSyllable
                )
            }
        }
    }

    /**
     * Handle jungseong (vowel) input
     */
    private fun handleJungseong(vowel: Char): CompositionResult {
        when {
            !isComposing -> {
                // Start with standalone vowel
                choseong = null
                jungseong = vowel
                jongseong = null
                isComposing = true
                return CompositionResult(getCurrentSyllable(), true)
            }

            choseong != null && jungseong == null -> {
                // Add vowel to consonant
                jungseong = vowel
                return CompositionResult(getCurrentSyllable(), true)
            }

            choseong != null && jungseong != null -> {
                // Try to combine with existing vowel
                val combined = combineJamo(jungseong!!, vowel)
                if (combined != null) {
                    jungseong = combined
                    return CompositionResult(getCurrentSyllable(), true)
                } else {
                    // Check if we need to decompose last syllable
                    return handleSyllableDecomposition(vowel)
                }
            }

            else -> {
                // Replace vowel
                jungseong = vowel
                return CompositionResult(getCurrentSyllable(), true)
            }
        }
    }

    /**
     * Handle syllable decomposition when vowel follows completed syllable
     * Example: 안녕핫 + ㅔ -> 안녕하세
     */
    private fun handleSyllableDecomposition(newVowel: Char): CompositionResult {
        // If we have a jongseong, try to decompose
        if (jongseong != null && choseong != null && jungseong != null) {
            val currentSyllableWithoutJongseong = composeSyllable(choseong!!, jungseong!!, null)

            // Move jongseong to choseong of new syllable
            val newChoseong = jongseong!!

            // Complete current syllable without jongseong
            val completedSyllable = currentSyllableWithoutJongseong?.toString() ?: ""
            committedText.append(completedSyllable)

            // Start new syllable with moved consonant and new vowel
            choseong = newChoseong
            jungseong = newVowel
            jongseong = null

            return CompositionResult(
                committedText.toString() + getCurrentSyllable(),
                true,
                committed = completedSyllable
            )
        }

        // If no decomposition possible, complete current and start new
        val completedSyllable = getCurrentSyllable()
        committedText.append(completedSyllable)

        choseong = null
        jungseong = newVowel
        jongseong = null

        return CompositionResult(
            committedText.toString() + getCurrentSyllable(),
            true,
            committed = completedSyllable
        )
    }

    /**
     * Handle jongseong (final consonant) input
     */
    private fun handleJongseong(consonant: Char): CompositionResult {
        when {
            choseong != null && jungseong != null && jongseong == null -> {
                // Add as jongseong
                jongseong = consonant
                return CompositionResult(getCurrentSyllable(), true)
            }

            choseong != null && jungseong != null && jongseong != null -> {
                // Try to combine with existing jongseong
                val combined = combineJamo(jongseong!!, consonant)
                if (combined != null) {
                    jongseong = combined
                    return CompositionResult(getCurrentSyllable(), true)
                } else {
                    // Complete current syllable and start new one
                    val completedSyllable = getCurrentSyllable()
                    committedText.append(completedSyllable)

                    choseong = consonant
                    jungseong = null
                    jongseong = null
                    isComposing = true

                    return CompositionResult(
                        committedText.toString() + getCurrentSyllable(),
                        true,
                        committed = completedSyllable
                    )
                }
            }

            !isComposing -> {
                // Start with standalone consonant
                choseong = consonant
                jungseong = null
                jongseong = null
                isComposing = true
                return CompositionResult(getCurrentSyllable(), true)
            }

            else -> {
                // Add as standalone consonant
                return CompositionResult(consonant.toString(), false)
            }
        }
    }

    /**
     * Backspace operation
     */
    fun backspace(): CompositionResult {
        if (!isComposing) {
            return CompositionResult(null, false)
        }

        when {
            jongseong != null -> {
                jongseong = null
                return CompositionResult(getCurrentSyllable(), true)
            }
            jungseong != null -> {
                jungseong = null
                return CompositionResult(getCurrentSyllable(), true)
            }
            choseong != null -> {
                reset()
                return CompositionResult("", false)
            }
            else -> {
                reset()
                return CompositionResult(null, false)
            }
        }
    }

    /**
     * Complete current composition
     */
    fun complete(): CompositionResult {
        if (!isComposing) {
            return CompositionResult(null, false)
        }

        val finalText = committedText.toString() + getCurrentSyllable()
        reset()
        return CompositionResult(finalText, false, committed = finalText)
    }

    /**
     * Check if currently composing
     */
    fun isComposing(): Boolean = isComposing

    /**
     * Get current composition including committed text
     */
    fun getCurrentComposition(): String {
        return if (isComposing) {
            committedText.toString() + getCurrentSyllable()
        } else {
            ""
        }
    }

    /**
     * Reset composer state
     */
    private fun reset() {
        choseong = null
        jungseong = null
        jongseong = null
        isComposing = false
        committedText.clear()
    }

    /**
     * Result of composition operation
     */
    data class CompositionResult(
        val text: String?,           // Current composition text (null if no change)
        val isComposing: Boolean,    // Whether text is still being composed
        val committed: String? = null // Any text that was committed
    )
}