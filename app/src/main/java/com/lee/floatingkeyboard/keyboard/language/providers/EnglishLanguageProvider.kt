package com.lee.floatingkeyboard.keyboard.language.providers

import com.lee.floatingkeyboard.keyboard.core.KeyMetadata
import com.lee.floatingkeyboard.keyboard.core.KeyboardLayout
import com.lee.floatingkeyboard.keyboard.input.SimpleTextComposer
import com.lee.floatingkeyboard.keyboard.input.TextComposer
import com.lee.floatingkeyboard.keyboard.language.LanguageProvider

/**
 * 영어 키보드 언어 제공자
 */
class EnglishLanguageProvider : LanguageProvider {
    override val id: String = "english"
    override val displayName: String = "English"

    override fun createComposer(): TextComposer {
        return SimpleTextComposer()
    }

    override fun getLayout(): KeyboardLayout {
        return createEnglishLayout(false)
    }

    override fun getShiftLayout(): KeyboardLayout {
        return createEnglishLayout(true)
    }

    private fun createEnglishLayout(isShift: Boolean): KeyboardLayout {
        val firstRow = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
            .map { char ->
                KeyMetadata.character(
                    if (isShift) char.uppercase() else char,
                    shiftVariant = char.uppercase()
                )
            }

        val secondRow = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
            .map { char ->
                KeyMetadata.character(
                    if (isShift) char.uppercase() else char,
                    shiftVariant = char.uppercase()
                )
            }

        val thirdRow = listOf(
            KeyMetadata.shift(),
            *listOf("z", "x", "c", "v", "b", "n", "m").map { char ->
                KeyMetadata.character(
                    if (isShift) char.uppercase() else char,
                    shiftVariant = char.uppercase()
                )
            }.toTypedArray(),
            KeyMetadata.backspace()
        )

        val fourthRow = listOf(
            KeyMetadata.modeSwitch("123"),
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