package com.lee.floatingkeyboard.keyboard.language.providers

import com.lee.floatingkeyboard.keyboard.core.KeyMetadata
import com.lee.floatingkeyboard.keyboard.core.KeyboardLayout
import com.lee.floatingkeyboard.keyboard.input.SimpleTextComposer
import com.lee.floatingkeyboard.keyboard.input.TextComposer
import com.lee.floatingkeyboard.keyboard.language.LanguageProvider

/**
 * 심볼/숫자 키보드 언어 제공자
 */
class SymbolLanguageProvider : LanguageProvider {
    override val id: String = "symbols"
    override val displayName: String = "123"

    override fun createComposer(): TextComposer {
        return SimpleTextComposer()
    }

    override fun getLayout(): KeyboardLayout {
        val firstRow = listOf(
            "1" to listOf("¹", "½", "⅓", "¼", "⅛"),
            "2" to listOf("²", "⅔"),
            "3" to listOf("³", "¾"),
            "4" to listOf("⁴"),
            "5" to listOf("⁵", "⅝"),
            "6" to listOf("⁶"),
            "7" to listOf("⁷", "⅞"),
            "8" to listOf("⁸"),
            "9" to listOf("⁹"),
            "0" to listOf("°", "⁰")
        ).map { (char, variants) ->
            KeyMetadata.character(char, longPressOptions = variants)
        }

        val secondRow = listOf(
            "-" to listOf("–", "—", "_"),
            "/" to listOf("\\", "|", "¦"),
            ":" to emptyList<String>(),
            ";" to emptyList<String>(),
            "(" to listOf("[", "{", "<"),
            ")" to listOf("]", "}", ">"),
            "$" to listOf("€", "£", "¥", "₩", "₹", "¢"),
            "&" to listOf("§", "¶"),
            "@" to listOf("©", "®", "™"),
            "\"" to listOf(""", """, "«", "»")
        ).map { (char, variants) ->
            KeyMetadata.character(char, longPressOptions = if (variants.isNotEmpty()) variants else null)
        }

        val thirdRow = listOf(
            "." to listOf("…", "•", "·"),
            "," to listOf("‚", "„"),
            "?" to listOf("¿", "‽"),
            "!" to listOf("¡", "‼"),
            "'" to listOf("'", "'", "‹", "›"),
            "\"" to listOf(""", """, "«", "»"),
            "#" to listOf("№", "♯"),
            "*" to listOf("†", "‡", "★", "☆"),
            "%" to listOf("‰"),
            "=" to listOf("≈", "≠", "∞")
        ).map { (char, variants) ->
            KeyMetadata.character(char, longPressOptions = variants)
        }

        val fourthRow = listOf(
            KeyMetadata.modeSwitch("ABC"),
            KeyMetadata.character(","),
            KeyMetadata.space(),
            KeyMetadata.character("."),
            KeyMetadata.enter()
        )

        return KeyboardLayout(
            rows = listOf(firstRow, secondRow, thirdRow, fourthRow),
            shiftToggleEnabled = false
        )
    }

    override fun getNumberLayout(): KeyboardLayout {
        val firstRow = listOf("1", "2", "3")
            .map { KeyMetadata.character(it) }

        val secondRow = listOf("4", "5", "6")
            .map { KeyMetadata.character(it) }

        val thirdRow = listOf("7", "8", "9")
            .map { KeyMetadata.character(it) }

        val fourthRow = listOf(
            KeyMetadata.character("*"),
            KeyMetadata.character("0"),
            KeyMetadata.character("#")
        )

        return KeyboardLayout(
            rows = listOf(firstRow, secondRow, thirdRow, fourthRow),
            shiftToggleEnabled = false
        )
    }
}