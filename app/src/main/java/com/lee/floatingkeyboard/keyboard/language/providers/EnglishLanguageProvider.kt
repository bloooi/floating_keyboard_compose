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
        val firstRow = listOf(
            "q" to emptyList<String>(),
            "w" to emptyList<String>(),
            "e" to listOf("è", "é", "ê", "ë", "ē", "ě"),
            "r" to listOf("ř"),
            "t" to listOf("ť", "þ"),
            "y" to listOf("ÿ", "ý"),
            "u" to listOf("ù", "ú", "û", "ü", "ū", "ů", "ű"),
            "i" to listOf("ì", "í", "î", "ï", "ī", "ǐ"),
            "o" to listOf("ò", "ó", "ô", "ö", "õ", "ø", "ō", "ő"),
            "p" to emptyList<String>()
        ).map { (char, variants) ->
            KeyMetadata.character(
                if (isShift) char.uppercase() else char,
                shiftVariant = char.uppercase(),
                longPressOptions = if (variants.isNotEmpty()) {
                    variants.map { if (isShift) it.uppercase() else it }
                } else null
            )
        }

        val secondRow = listOf(
            "a" to listOf("à", "á", "â", "ä", "æ", "ã", "å", "ā"),
            "s" to listOf("ß", "ś", "š"),
            "d" to listOf("ď", "ð"),
            "f" to emptyList<String>(),
            "g" to emptyList<String>(),
            "h" to emptyList<String>(),
            "j" to emptyList<String>(),
            "k" to emptyList<String>(),
            "l" to listOf("ł")
        ).map { (char, variants) ->
            KeyMetadata.character(
                if (isShift) char.uppercase() else char,
                shiftVariant = char.uppercase(),
                longPressOptions = if (variants.isNotEmpty()) {
                    variants.map { if (isShift) it.uppercase() else it }
                } else null
            )
        }

        val thirdRow = listOf(
            KeyMetadata.shift(),
            *listOf(
                "z" to listOf("ž", "ź", "ż"),
                "x" to emptyList<String>(),
                "c" to listOf("ç", "ć", "č"),
                "v" to emptyList<String>(),
                "b" to emptyList<String>(),
                "n" to listOf("ñ", "ń"),
                "m" to emptyList<String>()
            ).map { (char, variants) ->
                KeyMetadata.character(
                    if (isShift) char.uppercase() else char,
                    shiftVariant = char.uppercase(),
                    longPressOptions = if (variants.isNotEmpty()) {
                        variants.map { if (isShift) it.uppercase() else it }
                    } else null
                )
            }.toTypedArray(),
            KeyMetadata.backspace()
        )

        val fourthRow = listOf(
            KeyMetadata.modeSwitch("123"),
            KeyMetadata.character(",", longPressOptions = listOf("‚", "„")),
            KeyMetadata.space(),
            KeyMetadata.character(".", longPressOptions = listOf("…", "•", "·")),
            KeyMetadata.enter()
        )

        return KeyboardLayout(
            rows = listOf(firstRow, secondRow, thirdRow, fourthRow),
            shiftToggleEnabled = true
        )
    }
}