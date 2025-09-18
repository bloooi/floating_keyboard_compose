package com.lee.floatingkeyboard.keyboard.input

/**
 * Improved Hangul composer based on Unicode standard algorithms
 * Inspired by hangul-jamo library and Unicode specification
 */
class ImprovedHangulComposer {
    companion object {
        // Unicode Hangul base constants
        private const val HANGUL_BASE = 0xAC00
        private const val HANGUL_END = 0xD7A3

        // Jamo counts
        private const val CHOSEONG_COUNT = 19
        private const val JUNGSEONG_COUNT = 21
        private const val JONGSEONG_COUNT = 28

        // Choseong (초성) - Leading consonants U+1100-U+1112
        private val CHOSEONG = charArrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

        // Jungseong (중성) - Medial vowels U+1161-U+1175
        private val JUNGSEONG = charArrayOf(
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
        )

        // Jongseong (종성) - Trailing consonants, index 0 = no consonant
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

        // Complex consonant combinations
        private val CONSONANT_COMBINATIONS = mapOf(
            "ㄱㅅ" to 'ㄳ', "ㄴㅈ" to 'ㄵ', "ㄴㅎ" to 'ㄶ',
            "ㄹㄱ" to 'ㄺ', "ㄹㅁ" to 'ㄻ', "ㄹㅂ" to 'ㄼ',
            "ㄹㅅ" to 'ㄽ', "ㄹㅌ" to 'ㄾ', "ㄹㅍ" to 'ㄿ',
            "ㄹㅎ" to 'ㅀ', "ㅂㅅ" to 'ㅄ'
        )
    }

    private var currentJamo = StringBuilder()
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
     * Decompose a Hangul syllable into component jamo
     */
    fun decomposeSyllable(syllable: Char): Triple<Char?, Char?, Char?> {
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
     * Compose jamo sequence into Hangul text
     */
    private fun composeJamoSequence(jamos: String): String {
        if (jamos.isEmpty()) return ""

        val result = StringBuilder()
        var i = 0

        while (i < jamos.length) {
            val char = jamos[i]

            when {
                isChoseong(char) -> {
                    // Look for following vowel
                    val vowelIndex = i + 1
                    if (vowelIndex < jamos.length && isJungseong(jamos[vowelIndex])) {
                        val vowel = jamos[vowelIndex]

                        // Look for following consonant
                        val consonantIndex = vowelIndex + 1
                        if (consonantIndex < jamos.length && isJongseong(jamos[consonantIndex])) {
                            val consonant = jamos[consonantIndex]
                            val syllable = composeSyllable(char, vowel, consonant)
                            if (syllable != null) {
                                result.append(syllable)
                                i = consonantIndex + 1
                                continue
                            }
                        }

                        // No trailing consonant
                        val syllable = composeSyllable(char, vowel)
                        if (syllable != null) {
                            result.append(syllable)
                            i = vowelIndex + 1
                            continue
                        }
                    }

                    // Standalone consonant
                    result.append(char)
                    i++
                }

                isJungseong(char) -> {
                    // Standalone vowel
                    result.append(char)
                    i++
                }

                else -> {
                    // Other characters
                    result.append(char)
                    i++
                }
            }
        }

        return result.toString()
    }

    /**
     * Add jamo character to current composition
     */
    fun addJamo(jamo: Char): String? {
        if (!isJamo(jamo)) {
            return null
        }

        // Handle complex jamo combinations
        if (currentJamo.isNotEmpty()) {
            val lastChar = currentJamo.last()
            val combined = combineJamo(lastChar, jamo)

            if (combined != null) {
                // Replace last character with combined jamo
                currentJamo.setLength(currentJamo.length - 1)
                currentJamo.append(combined)
                isComposing = true
                return composeJamoSequence(currentJamo.toString())
            }
        }

        // Add new jamo
        currentJamo.append(jamo)
        isComposing = true

        return composeJamoSequence(currentJamo.toString())
    }

    /**
     * Backspace operation
     */
    fun backspace(): String? {
        if (!isComposing || currentJamo.isEmpty()) {
            return null
        }

        // Remove last jamo
        currentJamo.setLength(currentJamo.length - 1)

        if (currentJamo.isEmpty()) {
            isComposing = false
            return ""
        }

        return composeJamoSequence(currentJamo.toString())
    }

    /**
     * Complete current composition
     */
    fun complete(): String? {
        if (!isComposing) {
            return null
        }

        val result = composeJamoSequence(currentJamo.toString())
        reset()
        return result
    }

    /**
     * Check if currently composing
     */
    fun isComposing(): Boolean = isComposing

    /**
     * Get current composition
     */
    fun getCurrentComposition(): String? {
        if (!isComposing) return null
        return composeJamoSequence(currentJamo.toString())
    }

    /**
     * Reset composer state
     */
    private fun reset() {
        currentJamo.clear()
        isComposing = false
    }
}