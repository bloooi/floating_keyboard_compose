package com.lee.floatingkeyboard.keyboard.input

/**
 * Simplified Korean composer that only handles the current syllable
 * UI manages the complete text, composer only manages current composition
 */
class SimpleKoreanComposer {
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
     * Returns: Pair<currentComposition, shouldFinishPrevious>
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
                // No vowel yet, just replace choseong or start new syllable
                if (choseong != null) {
                    // Complete previous and start new
                    val prevResult = getCurrentSyllable()
                    choseong = consonant
                    jungseong = null
                    jongseong = null
                    return CompositionResult(getCurrentSyllable(), true, finishPrevious = prevResult)
                } else {
                    choseong = consonant
                    return CompositionResult(getCurrentSyllable(), true)
                }
            }

            jongseong == null -> {
                // Add as jongseong (temporarily)
                // This will be corrected if the next input is a vowel
                jongseong = consonant
                return CompositionResult(getCurrentSyllable(), true)
            }

            else -> {
                // Complete current syllable and start new one
                val completed = getCurrentSyllable()
                choseong = consonant
                jungseong = null
                jongseong = null
                return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
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
                // If jongseong exists, decompose syllable immediately (two-set behavior)
                if (jongseong != null) {
                    return handleSyllableDecomposition(vowel)
                }

                // If no jongseong, try to combine with existing vowel
                val combined = combineJamo(jungseong!!, vowel)
                if (combined != null) {
                    jungseong = combined
                    return CompositionResult(getCurrentSyllable(), true)
                } else {
                    // Cannot combine, complete current and start new vowel
                    val completed = getCurrentSyllable()
                    choseong = null
                    jungseong = vowel
                    jongseong = null
                    return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
                }
            }

            choseong == null && jungseong != null -> {
                // Standalone vowel followed by another vowel
                // Complete current vowel and start new one
                val completed = getCurrentSyllable()
                jungseong = vowel
                return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
            }

            else -> {
                // Replace vowel (fallback case)
                jungseong = vowel
                return CompositionResult(getCurrentSyllable(), true)
            }
        }
    }

    /**
     * Handle syllable decomposition when vowel follows completed syllable
     * Example: 핫 + ㅔ -> 하세
     */
    private fun handleSyllableDecomposition(newVowel: Char): CompositionResult {
        // If we have a jongseong, try to decompose
        if (jongseong != null && choseong != null && jungseong != null) {
            val currentSyllableWithoutJongseong = composeSyllable(choseong!!, jungseong!!, null)
            val newChoseong = jongseong!!

            // Complete current syllable without jongseong
            val completedSyllable = currentSyllableWithoutJongseong?.toString() ?: ""

            // Start new syllable with moved consonant and new vowel
            choseong = newChoseong
            jungseong = newVowel
            jongseong = null

            return CompositionResult(getCurrentSyllable(), true, finishPrevious = completedSyllable)
        }

        // If no decomposition possible, complete current and start new
        val completed = getCurrentSyllable()
        choseong = null
        jungseong = newVowel
        jongseong = null

        return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
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
                    val completed = getCurrentSyllable()
                    choseong = consonant
                    jungseong = null
                    jongseong = null
                    return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
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
                // Complete current and start new
                val completed = getCurrentSyllable()
                choseong = consonant
                jungseong = null
                jongseong = null
                return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
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
                return CompositionResult(getCurrentSyllable(), choseong != null)
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

        val result = getCurrentSyllable()
        reset()
        return CompositionResult(result, false)
    }

    /**
     * Check if currently composing
     */
    fun isComposing(): Boolean = isComposing

    /**
     * Get current composition
     */
    fun getCurrentComposition(): String {
        return getCurrentSyllable()
    }

    /**
     * Reset composer state
     */
    private fun reset() {
        choseong = null
        jungseong = null
        jongseong = null
        isComposing = false
    }

    /**
     * Result of composition operation
     */
    data class CompositionResult(
        val currentComposition: String?,  // Current syllable being composed
        val isComposing: Boolean,        // Whether still composing
        val finishPrevious: String? = null  // Previous syllable to be completed
    )
}