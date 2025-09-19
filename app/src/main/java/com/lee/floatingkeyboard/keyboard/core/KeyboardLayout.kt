package com.lee.floatingkeyboard.keyboard.core

/**
 * 키보드 레이아웃을 나타내는 데이터 클래스
 */
data class KeyboardLayout(
    val rows: List<List<KeyMetadata>>,
    val shiftToggleEnabled: Boolean = false
) {
    companion object {
        /**
         * 문자열 기반 레이아웃을 KeyboardLayout으로 변환하는 헬퍼 함수
         */
        fun fromStringRows(
            stringRows: List<List<String>>,
            shiftToggleEnabled: Boolean = false
        ): KeyboardLayout {
            val rows = stringRows.map { row ->
                row.map { keyText ->
                    when (keyText) {
                        "Space" -> KeyMetadata.space()
                        "⌫" -> KeyMetadata.backspace()
                        "⏎" -> KeyMetadata.enter()
                        "⇧" -> KeyMetadata.shift()
                        "123", "ABC" -> KeyMetadata.modeSwitch(keyText)
                        "🌐" -> KeyMetadata.languageSwitch()
                        else -> {
                            val weight = when (keyText) {
                                "Space" -> 4f
                                "⌫", "⏎", "⇧", "123", "ABC" -> 1.5f
                                else -> 1f
                            }
                            KeyMetadata.character(keyText, weight)
                        }
                    }
                }
            }
            return KeyboardLayout(rows, shiftToggleEnabled)
        }
    }
}