package com.lee.floatingkeyboard.keyboard.core

/**
 * 키에 대한 메타데이터를 담는 데이터 클래스
 */
data class KeyMetadata(
    val action: KeyAction,
    val label: String = KeyAction.toString(action),
    val weight: Float = 1f,
    val shiftVariant: KeyMetadata? = null,
    val longPressOptions: List<KeyMetadata>? = null,
    val isRepeatable: Boolean = false,
    val isSticky: Boolean = false
) {
    companion object {
        /**
         * 일반 문자 키 생성
         */
        fun character(
            char: String, 
            weight: Float = 1f,
            shiftVariant: String? = null,
            longPressOptions: List<String>? = null
        ): KeyMetadata {
            return KeyMetadata(
                action = KeyAction.Character(char),
                weight = weight,
                shiftVariant = shiftVariant?.let { character(it) },
                longPressOptions = longPressOptions?.map { character(it) }
            )
        }
        
        /**
         * 스페이스 키 생성
         */
        fun space(weight: Float = 4f): KeyMetadata {
            return KeyMetadata(
                action = KeyAction.Space,
                label = "Space",
                weight = weight
            )
        }
        
        /**
         * 백스페이스 키 생성
         */
        fun backspace(weight: Float = 1.5f): KeyMetadata {
            return KeyMetadata(
                action = KeyAction.Backspace,
                weight = weight,
                isRepeatable = true
            )
        }
        
        /**
         * 엔터 키 생성
         */
        fun enter(weight: Float = 1.5f): KeyMetadata {
            return KeyMetadata(
                action = KeyAction.Enter,
                weight = weight
            )
        }
        
        /**
         * 시프트 키 생성
         */
        fun shift(weight: Float = 1.5f): KeyMetadata {
            return KeyMetadata(
                action = KeyAction.Shift,
                weight = weight,
                isSticky = true
            )
        }
        
        /**
         * 모드 전환 키 생성
         */
        fun modeSwitch(label: String, weight: Float = 1.5f): KeyMetadata {
            val action = when (label) {
                "123" -> KeyAction.NumberMode
                "ABC" -> KeyAction.LetterMode
                else -> KeyAction.Symbol(label)
            }
            return KeyMetadata(
                action = action,
                label = label,
                weight = weight
            )
        }
        
        /**
         * 언어 전환 키 생성
         */
        fun languageSwitch(weight: Float = 1f): KeyMetadata {
            return KeyMetadata(
                action = KeyAction.LanguageSwitch,
                label = "🌐",
                weight = weight
            )
        }
    }
}