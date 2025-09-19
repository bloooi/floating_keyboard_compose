package com.lee.floatingkeyboard.keyboard.input

/**
 * 단순 텍스트 입력을 위한 컴포저 (영어, 숫자 등)
 * 조합 기능이 필요 없는 언어용
 */
class SimpleTextComposer : TextComposer {
    
    /**
     * 문자 입력 처리 - 단순히 그대로 반환
     */
    override fun addCharacter(char: Char): TextComposer.CompositionResult {
        return TextComposer.CompositionResult(
            currentComposition = char.toString(),
            isComposing = false
        )
    }

    /**
     * 백스페이스 처리 - 조합이 없으므로 null 반환
     */
    override fun backspace(): TextComposer.CompositionResult {
        return TextComposer.CompositionResult(null, false)
    }

    /**
     * 조합 완성 - 조합이 없으므로 null 반환
     */
    override fun complete(): TextComposer.CompositionResult {
        return TextComposer.CompositionResult(null, false)
    }

    /**
     * 조합 중인지 확인 - 항상 false
     */
    override fun isComposing(): Boolean = false

    /**
     * 현재 조합 가져오기 - 항상 빈 문자열
     */
    override fun getCurrentComposition(): String = ""
}