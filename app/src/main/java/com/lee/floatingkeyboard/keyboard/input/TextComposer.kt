package com.lee.floatingkeyboard.keyboard.input

/**
 * 텍스트 조합을 위한 범용 인터페이스
 */
interface TextComposer {
    /**
     * 조합 결과
     */
    data class CompositionResult(
        val currentComposition: String?,  // 현재 조합 중인 텍스트
        val isComposing: Boolean,        // 조합 중인지 여부
        val finishPrevious: String? = null  // 완성된 이전 텍스트
    )

    /**
     * 문자 입력 처리
     */
    fun addCharacter(char: Char): CompositionResult

    /**
     * 백스페이스 처리
     */
    fun backspace(): CompositionResult

    /**
     * 현재 조합 완성
     */
    fun complete(): CompositionResult

    /**
     * 조합 중인지 확인
     */
    fun isComposing(): Boolean

    /**
     * 현재 조합 중인 텍스트 가져오기
     */
    fun getCurrentComposition(): String
}