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
            "w" to listOf("ŵ"),
            "e" to listOf("è", "é", "ê", "ë", "ē", "ě", "ę", "ė"),
            "r" to listOf("ř", "ŕ"),
            "t" to listOf("ť", "ţ", "ț", "þ"),
            "y" to listOf("ÿ", "ý", "ŷ"),
            "u" to listOf("ù", "ú", "û", "ü", "ū", "ů", "ű", "ų"),
            "i" to listOf("ì", "í", "î", "ï", "ī", "ǐ", "į", "ĳ", "İ"),
            "o" to listOf("ò", "ó", "ô", "ö", "õ", "ø", "ō", "ő", "œ"),
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
            "a" to listOf("à", "á", "â", "ä", "æ", "ã", "å", "ā", "ă", "ą", "ǎ"),
            "s" to listOf("ß", "ś", "š", "ş", "ș", "ŝ"),
            "d" to listOf("ď", "đ", "ð"),
            "f" to emptyList<String>(),
            "g" to listOf("ğ", "ġ", "ģ", "ĝ"),
            "h" to listOf("ĥ"),
            "j" to listOf("ĵ"),
            "k" to listOf("ķ"),
            "l" to listOf("ł", "ľ", "ļ")
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
                "c" to listOf("ç", "ć", "č", "ĉ"),
                "v" to emptyList<String>(),
                "b" to emptyList<String>(),
                "n" to listOf("ñ", "ń", "ň", "ņ", "ŋ"),
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