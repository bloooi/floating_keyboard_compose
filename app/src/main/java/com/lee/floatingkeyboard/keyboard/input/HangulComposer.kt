package com.lee.floatingkeyboard.keyboard.input

/**
 * 한글 입력을 위한 컴포저 (현재 음절만 관리)
 * UI가 전체 텍스트를 관리하고, 컴포저는 현재 조합만 관리
 */
class HangulComposer : TextComposer {
    companion object {
        // 유니코드 한글 베이스 상수
        private const val HANGUL_BASE = 0xAC00
        private const val HANGUL_END = 0xD7A3

        // 자모 개수
        private const val CHOSEONG_COUNT = 19
        private const val JUNGSEONG_COUNT = 21
        private const val JONGSEONG_COUNT = 28

        // 초성 - 첫소리 자음
        private val CHOSEONG = charArrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

        // 중성 - 가운데소리 모음
        private val JUNGSEONG = charArrayOf(
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
        )

        // 종성 - 끝소리 자음
        private val JONGSEONG = charArrayOf(
            '\u0000', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

        // 복합 모음 조합
        private val VOWEL_COMBINATIONS = mapOf(
            "ㅗㅏ" to 'ㅘ', "ㅗㅐ" to 'ㅙ', "ㅗㅣ" to 'ㅚ',
            "ㅜㅓ" to 'ㅝ', "ㅜㅔ" to 'ㅞ', "ㅜㅣ" to 'ㅟ',
            "ㅡㅣ" to 'ㅢ'
        )

        // 복합 자음 조합 (종성용)
        private val CONSONANT_COMBINATIONS = mapOf(
            "ㄱㅅ" to 'ㄳ', "ㄴㅈ" to 'ㄵ', "ㄴㅎ" to 'ㄶ',
            "ㄹㄱ" to 'ㄺ', "ㄹㅁ" to 'ㄻ', "ㄹㅂ" to 'ㄼ',
            "ㄹㅅ" to 'ㄽ', "ㄹㅌ" to 'ㄾ', "ㄹㅍ" to 'ㄿ',
            "ㄹㅎ" to 'ㅀ', "ㅂㅅ" to 'ㅄ'
        )
    }

    // 현재 조합 중인 음절
    private var choseong: Char? = null
    private var jungseong: Char? = null
    private var jongseong: Char? = null
    private var isComposing = false

    /**
     * 문자가 유효한 자모인지 확인
     */
    private fun isJamo(char: Char): Boolean {
        return CHOSEONG.contains(char) ||
               JUNGSEONG.contains(char) ||
               JONGSEONG.contains(char)
    }

    /**
     * 문자가 초성인지 확인
     */
    private fun isChoseong(char: Char): Boolean = CHOSEONG.contains(char)

    /**
     * 문자가 중성인지 확인
     */
    private fun isJungseong(char: Char): Boolean = JUNGSEONG.contains(char)

    /**
     * 문자가 종성인지 확인
     */
    private fun isJongseong(char: Char): Boolean = JONGSEONG.contains(char)

    /**
     * 자모를 한글 음절로 조합
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
     * 두 자모를 복합 자모로 조합 시도
     */
    private fun combineJamo(first: Char, second: Char): Char? {
        val combination = "$first$second"
        return VOWEL_COMBINATIONS[combination] ?: CONSONANT_COMBINATIONS[combination]
    }

    /**
     * 현재 조합 결과 가져오기
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
     * 문자 입력 처리 (TextComposer 인터페이스 구현)
     */
    override fun addCharacter(char: Char): TextComposer.CompositionResult {
        val result = addJamo(char)
        return TextComposer.CompositionResult(
            currentComposition = result.currentComposition,
            isComposing = result.isComposing,
            finishPrevious = result.finishPrevious
        )
    }

    /**
     * 자모 문자를 현재 조합에 추가
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
     * 초성(첫소리 자음) 입력 처리
     */
    private fun handleChoseong(consonant: Char): CompositionResult {
        when {
            !isComposing -> {
                // 새 음절 시작
                choseong = consonant
                jungseong = null
                jongseong = null
                isComposing = true
                return CompositionResult(getCurrentSyllable(), true)
            }

            jungseong == null -> {
                // 아직 모음이 없음, 초성 교체하거나 새 음절 시작
                if (choseong != null) {
                    // 이전 완성하고 새로 시작
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
                // 종성으로 추가 (임시)
                jongseong = consonant
                return CompositionResult(getCurrentSyllable(), true)
            }

            else -> {
                // 현재 음절 완성하고 새 음절 시작
                val completed = getCurrentSyllable()
                choseong = consonant
                jungseong = null
                jongseong = null
                return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
            }
        }
    }

    /**
     * 중성(가운데소리 모음) 입력 처리
     */
    private fun handleJungseong(vowel: Char): CompositionResult {
        when {
            !isComposing -> {
                // 단독 모음으로 시작
                choseong = null
                jungseong = vowel
                jongseong = null
                isComposing = true
                return CompositionResult(getCurrentSyllable(), true)
            }

            choseong != null && jungseong == null -> {
                // 자음에 모음 추가
                jungseong = vowel
                return CompositionResult(getCurrentSyllable(), true)
            }

            choseong != null && jungseong != null -> {
                // 종성이 있으면 즉시 음절 분해 (두벌식 동작)
                if (jongseong != null) {
                    return handleSyllableDecomposition(vowel)
                }

                // 종성이 없으면 기존 모음과 조합 시도
                val combined = combineJamo(jungseong!!, vowel)
                if (combined != null) {
                    jungseong = combined
                    return CompositionResult(getCurrentSyllable(), true)
                } else {
                    // 조합 불가, 현재 완성하고 새 모음 시작
                    val completed = getCurrentSyllable()
                    choseong = null
                    jungseong = vowel
                    jongseong = null
                    return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
                }
            }

            choseong == null && jungseong != null -> {
                // 단독 모음 다음에 다른 모음
                // 현재 모음 완성하고 새 모음 시작
                val completed = getCurrentSyllable()
                jungseong = vowel
                return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
            }

            else -> {
                // 모음 교체 (fallback)
                jungseong = vowel
                return CompositionResult(getCurrentSyllable(), true)
            }
        }
    }

    /**
     * 완성된 음절 다음에 모음이 올 때 음절 분해 처리
     * 예: 핫 + ㅔ -> 하세
     */
    private fun handleSyllableDecomposition(newVowel: Char): CompositionResult {
        // 종성이 있으면 분해 시도
        if (jongseong != null && choseong != null && jungseong != null) {
            val currentSyllableWithoutJongseong = composeSyllable(choseong!!, jungseong!!, null)
            val newChoseong = jongseong!!

            // 종성 없는 현재 음절 완성
            val completedSyllable = currentSyllableWithoutJongseong?.toString() ?: ""

            // 옮긴 자음과 새 모음으로 새 음절 시작
            choseong = newChoseong
            jungseong = newVowel
            jongseong = null

            return CompositionResult(getCurrentSyllable(), true, finishPrevious = completedSyllable)
        }

        // 분해 불가능하면 현재 완성하고 새로 시작
        val completed = getCurrentSyllable()
        choseong = null
        jungseong = newVowel
        jongseong = null

        return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
    }

    /**
     * 종성(끝소리 자음) 입력 처리
     */
    private fun handleJongseong(consonant: Char): CompositionResult {
        when {
            choseong != null && jungseong != null && jongseong == null -> {
                // 종성 추가
                jongseong = consonant
                return CompositionResult(getCurrentSyllable(), true)
            }

            choseong != null && jungseong != null && jongseong != null -> {
                // 기존 종성과 조합 시도
                val combined = combineJamo(jongseong!!, consonant)
                if (combined != null) {
                    jongseong = combined
                    return CompositionResult(getCurrentSyllable(), true)
                } else {
                    // 현재 음절 완성하고 새 음절 시작
                    val completed = getCurrentSyllable()
                    choseong = consonant
                    jungseong = null
                    jongseong = null
                    return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
                }
            }

            !isComposing -> {
                // 단독 자음으로 시작
                choseong = consonant
                jungseong = null
                jongseong = null
                isComposing = true
                return CompositionResult(getCurrentSyllable(), true)
            }

            else -> {
                // 현재 완성하고 새로 시작
                val completed = getCurrentSyllable()
                choseong = consonant
                jungseong = null
                jongseong = null
                return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
            }
        }
    }

    /**
     * 백스페이스 처리 (TextComposer 인터페이스 구현)
     */
    override fun backspace(): TextComposer.CompositionResult {
        val result = backspaceInternal()
        return TextComposer.CompositionResult(
            currentComposition = result.currentComposition,
            isComposing = result.isComposing,
            finishPrevious = result.finishPrevious
        )
    }

    /**
     * 백스페이스 동작
     */
    private fun backspaceInternal(): CompositionResult {
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
     * 현재 조합 완성 (TextComposer 인터페이스 구현)
     */
    override fun complete(): TextComposer.CompositionResult {
        val result = completeInternal()
        return TextComposer.CompositionResult(
            currentComposition = result.currentComposition,
            isComposing = result.isComposing,
            finishPrevious = result.finishPrevious
        )
    }

    /**
     * 현재 조합 완성
     */
    private fun completeInternal(): CompositionResult {
        if (!isComposing) {
            return CompositionResult(null, false)
        }

        val result = getCurrentSyllable()
        reset()
        return CompositionResult(result, false)
    }

    /**
     * 현재 조합 중인지 확인 (TextComposer 인터페이스 구현)
     */
    override fun isComposing(): Boolean = isComposing

    /**
     * 현재 조합 가져오기 (TextComposer 인터페이스 구현)
     */
    override fun getCurrentComposition(): String {
        return getCurrentSyllable()
    }

    /**
     * 컴포저 상태 초기화
     */
    private fun reset() {
        choseong = null
        jungseong = null
        jongseong = null
        isComposing = false
    }

    /**
     * 조합 동작 결과
     */
    data class CompositionResult(
        val currentComposition: String?,  // 현재 조합 중인 음절
        val isComposing: Boolean,        // 조합 중인지 여부
        val finishPrevious: String? = null  // 완성된 이전 음절
    )
}