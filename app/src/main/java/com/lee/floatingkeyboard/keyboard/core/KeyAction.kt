package com.lee.floatingkeyboard.keyboard.core

/**
 * 키보드 키의 동작을 나타내는 sealed class
 */
sealed class KeyAction {
    /**
     * 일반 문자 입력
     */
    data class Character(val char: String) : KeyAction()
    
    /**
     * 백스페이스 동작
     */
    object Backspace : KeyAction()
    
    /**
     * 스페이스 동작
     */
    object Space : KeyAction()
    
    /**
     * 엔터 동작
     */
    object Enter : KeyAction()
    
    /**
     * 시프트 토글
     */
    object Shift : KeyAction()
    
    /**
     * 언어 전환
     */
    object LanguageSwitch : KeyAction()
    
    /**
     * 심볼 키
     */
    data class Symbol(val symbol: String) : KeyAction()
    
    /**
     * 숫자 모드 전환
     */
    object NumberMode : KeyAction()
    
    /**
     * 문자 모드 전환 
     */
    object LetterMode : KeyAction()
    
    companion object {
        /**
         * 문자열로부터 KeyAction을 생성하는 헬퍼 함수
         */
        fun fromString(keyText: String): KeyAction {
            return when (keyText) {
                "⌫" -> Backspace
                "Space" -> Space
                "⏎" -> Enter
                "⇧" -> Shift
                "123" -> NumberMode
                "ABC" -> LetterMode
                "🌐" -> LanguageSwitch
                else -> {
                    if (keyText.length == 1) {
                        Character(keyText)
                    } else {
                        Symbol(keyText)
                    }
                }
            }
        }
        
        /**
         * KeyAction을 문자열로 변환하는 헬퍼 함수
         */
        fun toString(action: KeyAction): String {
            return when (action) {
                is Character -> action.char
                Backspace -> "⌫"
                Space -> "Space"
                Enter -> "⏎"
                Shift -> "⇧"
                NumberMode -> "123"
                LetterMode -> "ABC"
                LanguageSwitch -> "🌐"
                is Symbol -> action.symbol
            }
        }
    }
}