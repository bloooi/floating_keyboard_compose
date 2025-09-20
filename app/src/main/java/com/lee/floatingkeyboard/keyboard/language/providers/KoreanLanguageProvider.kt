package com.lee.floatingkeyboard.keyboard.language.providers

import com.lee.floatingkeyboard.keyboard.core.KeyMetadata
import com.lee.floatingkeyboard.keyboard.core.KeyboardLayout
import com.lee.floatingkeyboard.keyboard.input.HangulComposer
import com.lee.floatingkeyboard.keyboard.input.TextComposer
import com.lee.floatingkeyboard.keyboard.language.LanguageProvider

/**
 * 한글 키보드 언어 제공자
 */
class KoreanLanguageProvider : LanguageProvider {
    override val id: String = "korean"
    override val displayName: String = "한글"

    override fun createComposer(): TextComposer {
        return HangulComposer()
    }

    override fun getLayout(): KeyboardLayout {
        return createHangulLayout(false)
    }

    override fun getShiftLayout(): KeyboardLayout {
        return createHangulLayout(true)
    }

    private fun createHangulLayout(isShift: Boolean): KeyboardLayout {
        // 쌍자음 매핑 정의
        val doubleConsonants = mapOf(
            "ㅂ" to "ㅃ",
            "ㅈ" to "ㅉ",
            "ㄷ" to "ㄸ",
            "ㄱ" to "ㄲ",
            "ㅅ" to "ㅆ"
        )

        val firstRow = if (isShift) {
            listOf(
                KeyMetadata.character(doubleConsonants["ㅂ"] ?: "ㅂ"),
                KeyMetadata.character(doubleConsonants["ㅈ"] ?: "ㅈ"),
                KeyMetadata.character(doubleConsonants["ㄷ"] ?: "ㄷ"),
                KeyMetadata.character(doubleConsonants["ㄱ"] ?: "ㄱ"),
                KeyMetadata.character(doubleConsonants["ㅅ"] ?: "ㅅ"),
                KeyMetadata.character("ㅛ"),
                KeyMetadata.character("ㅕ"),
                KeyMetadata.character("ㅑ"),
                KeyMetadata.character("ㅐ"),
                KeyMetadata.character("ㅔ")
            )
        } else {
            listOf(
                KeyMetadata.character("ㅂ", longPressOptions = listOf("ㅃ")),
                KeyMetadata.character("ㅈ", longPressOptions = listOf("ㅉ")),
                KeyMetadata.character("ㄷ", longPressOptions = listOf("ㄸ")),
                KeyMetadata.character("ㄱ", longPressOptions = listOf("ㄲ")),
                KeyMetadata.character("ㅅ", longPressOptions = listOf("ㅆ")),
                KeyMetadata.character("ㅛ"),
                KeyMetadata.character("ㅕ"),
                KeyMetadata.character("ㅑ"),
                KeyMetadata.character("ㅐ"),
                KeyMetadata.character("ㅔ")
            )
        }

        val secondRow = listOf(
            KeyMetadata.character("ㅁ"),
            KeyMetadata.character("ㄴ"),
            KeyMetadata.character("ㅇ"),
            KeyMetadata.character("ㄹ"),
            KeyMetadata.character("ㅎ"),
            KeyMetadata.character("ㅗ", longPressOptions = listOf("ㅚ", "ㅙ")),
            KeyMetadata.character("ㅓ", longPressOptions = listOf("ㅔ")),
            KeyMetadata.character("ㅏ", longPressOptions = listOf("ㅐ")),
            KeyMetadata.character("ㅣ", longPressOptions = listOf("ㅢ"))
        )

        val thirdRow = listOf(
            KeyMetadata.shift(),
            KeyMetadata.character("ㅋ"),
            KeyMetadata.character("ㅌ"),
            KeyMetadata.character("ㅊ"),
            KeyMetadata.character("ㅍ"),
            KeyMetadata.character("ㅠ"),
            KeyMetadata.character("ㅜ", longPressOptions = listOf("ㅟ", "ㅝ")),
            KeyMetadata.character("ㅡ", longPressOptions = listOf("ㅢ")),
            KeyMetadata.backspace()
        )

        val fourthRow = listOf(
            KeyMetadata.modeSwitch("ABC"),
            KeyMetadata.character(","),
            KeyMetadata.space(),
            KeyMetadata.character("."),
            KeyMetadata.enter()
        )

        return KeyboardLayout(
            rows = listOf(firstRow, secondRow, thirdRow, fourthRow),
            shiftToggleEnabled = true
        )
    }
}