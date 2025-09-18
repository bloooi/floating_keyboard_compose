package com.lee.floatingkeyboard.keyboard.input

class HangulComposer {
    companion object {
        // Unicode Hangul base values
        private const val HANGUL_BASE = 0xAC00
        private const val CHOSEONG_COUNT = 19
        private const val JUNGSEONG_COUNT = 21
        private const val JONGSEONG_COUNT = 28

        // Choseong (초성) - Initial consonants
        private val CHOSEONG = charArrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

        // Jungseong (중성) - Medial vowels
        private val JUNGSEONG = charArrayOf(
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
        )

        // Jongseong (종성) - Final consonants
        private val JONGSEONG = charArrayOf(
            ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )
    }

    private var choseong: Char? = null
    private var jungseong: Char? = null
    private var jongseong: Char? = null
    private var composing = false

    fun addJamo(jamo: Char): String? {
        val choseongIndex = CHOSEONG.indexOf(jamo)
        val jungseongIndex = JUNGSEONG.indexOf(jamo)
        val jongseongIndex = JONGSEONG.indexOf(jamo)

        when {
            choseongIndex >= 0 -> {
                if (!composing) {
                    // Start new syllable
                    choseong = jamo
                    jungseong = null
                    jongseong = null
                    composing = true
                    return jamo.toString()
                } else if (jungseong == null) {
                    // Replace choseong
                    choseong = jamo
                    return jamo.toString()
                } else if (jongseong == null) {
                    // Add as jongseong
                    jongseong = jamo
                    return composeHangul()
                } else {
                    // Complete current syllable and start new one
                    val completed = composeHangul()
                    reset()
                    choseong = jamo
                    composing = true
                    return completed + jamo
                }
            }

            jungseongIndex >= 0 -> {
                if (choseong != null && jungseong == null) {
                    jungseong = jamo
                    return composeHangul()
                } else if (!composing) {
                    // Standalone vowel
                    reset()
                    return jamo.toString()
                }
            }

            jongseongIndex > 0 -> {
                if (choseong != null && jungseong != null && jongseong == null) {
                    jongseong = jamo
                    return composeHangul()
                } else if (!composing) {
                    // Standalone consonant
                    reset()
                    return jamo.toString()
                }
            }
        }

        return null
    }

    fun backspace(): String? {
        if (!composing) return null

        return when {
            jongseong != null -> {
                jongseong = null
                composeHangul()
            }
            jungseong != null -> {
                jungseong = null
                choseong?.toString()
            }
            choseong != null -> {
                reset()
                ""
            }
            else -> null
        }
    }

    fun complete(): String? {
        if (!composing) return null

        val result = composeHangul()
        reset()
        return result
    }

    fun isComposing(): Boolean = composing

    fun getCurrentComposition(): String? {
        if (!composing) return null
        return composeHangul()
    }

    private fun composeHangul(): String {
        val choIndex = choseong?.let { CHOSEONG.indexOf(it) } ?: return ""
        val jungIndex = jungseong?.let { JUNGSEONG.indexOf(it) } ?: return choseong.toString()
        val jongIndex = jongseong?.let { JONGSEONG.indexOf(it) } ?: 0

        val unicode = HANGUL_BASE + (choIndex * JUNGSEONG_COUNT + jungIndex) * JONGSEONG_COUNT + jongIndex
        return unicode.toChar().toString()
    }

    private fun reset() {
        choseong = null
        jungseong = null
        jongseong = null
        composing = false
    }
}