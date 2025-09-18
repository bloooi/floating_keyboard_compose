package com.lee.floatingkeyboard.keyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.lee.floatingkeyboard.keyboard.core.Key
import com.lee.floatingkeyboard.keyboard.input.SimpleKoreanComposer
import com.lee.floatingkeyboard.utils.KeyboardLanguage
import com.lee.floatingkeyboard.utils.LanguageManager
import kotlin.math.roundToInt

@Composable
fun FloatingKeyboard(
    modifier: Modifier = Modifier,
    languageManager: LanguageManager = remember { LanguageManager() },
    hangulComposer: SimpleKoreanComposer = remember { SimpleKoreanComposer() },
    onKeyPress: (String) -> Unit = {},
    onClose: () -> Unit = {}
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var keyboardSize by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val currentLanguage by languageManager.currentLanguage

    // Drag gesture handler function to be shared
    val handleDrag: (Offset) -> Unit = { dragAmount ->
        val newOffset = offset + dragAmount
        val maxX = with(density) { screenWidth.toPx() } - keyboardSize.x
        val maxY = with(density) { screenHeight.toPx() } - keyboardSize.y

        offset = Offset(
            x = newOffset.x.coerceIn(0f, maxX),
            y = newOffset.y.coerceIn(0f, maxY)
        )
    }

    Box(
        modifier = modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .onGloballyPositioned { coordinates ->
                keyboardSize = Offset(
                    coordinates.size.width.toFloat(),
                    coordinates.size.height.toFloat()
                )
            }
    ) {
        KeyboardContent(
            currentLanguage = currentLanguage,
            hangulComposer = hangulComposer,
            onKeyPress = { keyText ->
                when (currentLanguage) {
                    KeyboardLanguage.KOREAN -> {
                        handleHangulInput(keyText, hangulComposer, onKeyPress)
                    }
                    else -> {
                        onKeyPress(keyText)
                    }
                }
            },
            onLanguageSwitch = {
                languageManager.switchToNext()
            },
            onClose = onClose,
            onDrag = handleDrag
        )
    }
}

@Composable
private fun KeyboardContent(
    currentLanguage: KeyboardLanguage,
    hangulComposer: SimpleKoreanComposer,
    onKeyPress: (String) -> Unit,
    onLanguageSwitch: () -> Unit,
    onClose: () -> Unit,
    onDrag: (Offset) -> Unit
) {
    // Shift state for Korean keyboard
    var isKoreanShiftPressed by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .width(500.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Draggable header with controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        onDrag(dragAmount)
                    }
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Drag handle indicator
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(2.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    RoundedCornerShape(1.dp)
                                )
                        )
                    }
                }

                Text(
                    text = when (currentLanguage) {
                        KeyboardLanguage.ENGLISH -> "English"
                        KeyboardLanguage.KOREAN -> "한글"
                        KeyboardLanguage.SYMBOLS -> "123"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row {
                IconButton(
                    onClick = onLanguageSwitch,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text(
                        text = "🌐",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Keyboard layout based on current language
        when (currentLanguage) {
            KeyboardLanguage.ENGLISH -> {
                EnglishKeyboardLayout(onKeyPress = onKeyPress)
            }
            KeyboardLanguage.KOREAN -> {
                HangulKeyboardLayout(
                    onKeyPress = { key ->
                        // Reset shift state after input (except shift key itself)
                        if (key != "⇧" && isKoreanShiftPressed) {
                            isKoreanShiftPressed = false
                        }
                        onKeyPress(key)
                    },
                    isShiftPressed = isKoreanShiftPressed,
                    onShiftToggle = {
                        isKoreanShiftPressed = !isKoreanShiftPressed
                    }
                )
            }
            KeyboardLanguage.SYMBOLS -> {
                SymbolKeyboardLayout(onKeyPress = onKeyPress)
            }
        }
    }
}

@Composable
private fun EnglishKeyboardLayout(onKeyPress: (String) -> Unit) {
    val qwertyKeys = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("⇧", "z", "x", "c", "v", "b", "n", "m", "⌫"),
        listOf("123", ",", "Space", ".", "⏎")
    )

    SimpleKeyboardLayout(
        keys = qwertyKeys,
        onKeyPress = onKeyPress
    )
}

@Composable
private fun HangulKeyboardLayout(
    onKeyPress: (String) -> Unit,
    isShiftPressed: Boolean = false,
    onShiftToggle: () -> Unit = {}
) {
    // Define double consonant mappings
    val doubleConsonantMap = mapOf(
        "ㅂ" to "ㅃ",
        "ㅈ" to "ㅉ",
        "ㄷ" to "ㄸ",
        "ㄱ" to "ㄲ",
        "ㅅ" to "ㅆ"
    )

    val hangulKeys = if (isShiftPressed) {
        listOf(
            listOf(
                doubleConsonantMap["ㅂ"] ?: "ㅂ",
                doubleConsonantMap["ㅈ"] ?: "ㅈ",
                doubleConsonantMap["ㄷ"] ?: "ㄷ",
                doubleConsonantMap["ㄱ"] ?: "ㄱ",
                doubleConsonantMap["ㅅ"] ?: "ㅅ",
                "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"
            ),
            listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),
            listOf("⇧", "ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ", "⌫"),
            listOf("ABC", ",", "Space", ".", "⏎")
        )
    } else {
        listOf(
            listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"),
            listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),
            listOf("⇧", "ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ", "⌫"),
            listOf("ABC", ",", "Space", ".", "⏎")
        )
    }

    ShiftableKeyboardLayout(
        keys = hangulKeys,
        isShiftPressed = isShiftPressed,
        onKeyPress = { key ->
            if (key == "⇧") {
                onShiftToggle()
            } else {
                onKeyPress(key)
            }
        }
    )
}

@Composable
private fun SymbolKeyboardLayout(onKeyPress: (String) -> Unit) {
    val symbolKeys = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("-", "/", ":", ";", "(", ")", "$", "&", "@", "\""),
        listOf(".", ",", "?", "!", "'", "\"", "⌫"),
        listOf("ABC", "Space", "⏎")
    )

    SimpleKeyboardLayout(
        keys = symbolKeys,
        onKeyPress = onKeyPress
    )
}

private fun handleHangulInput(
    keyText: String,
    hangulComposer: SimpleKoreanComposer,
    onKeyPress: (String) -> Unit
) {
    when (keyText) {
        "⌫" -> {
            val result = hangulComposer.backspace()
            if (result.currentComposition != null) {
                if (result.isComposing) {
                    onKeyPress("COMPOSE:${result.currentComposition}")
                } else {
                    onKeyPress("COMPLETE:${result.currentComposition}")
                }
            } else {
                onKeyPress("BACKSPACE")
            }
        }
        "Space" -> {
            val completed = hangulComposer.complete()
            if (completed.currentComposition != null) {
                onKeyPress("COMPLETE:${completed.currentComposition}")
            }
            onKeyPress(" ")
        }
        "⏎" -> {
            val completed = hangulComposer.complete()
            if (completed.currentComposition != null) {
                onKeyPress("COMPLETE:${completed.currentComposition}")
            }
            onKeyPress("ENTER")
        }
        else -> {
            if (keyText.length == 1 && isKoreanJamo(keyText[0])) {
                // Korean jamo character
                val result = hangulComposer.addJamo(keyText[0])

                // Handle any previous syllable that was completed
                result.finishPrevious?.let { finished ->
                    onKeyPress("COMPLETE:$finished")
                }

                // Handle current composition
                if (result.currentComposition != null) {
                    if (result.isComposing) {
                        onKeyPress("COMPOSE:${result.currentComposition}")
                    } else {
                        onKeyPress("COMPLETE:${result.currentComposition}")
                    }
                } else {
                    onKeyPress(keyText)
                }
            } else {
                onKeyPress(keyText)
            }
        }
    }
}

private fun isKoreanJamo(char: Char): Boolean {
    return when (char) {
        // Choseong (초성) - Initial consonants
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ',
        // Jungseong (중성) - Medial vowels
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
        'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ',
        // Jongseong (종성) - Final consonants (excluding space)
        'ㄳ', 'ㄵ', 'ㄶ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅄ' -> true
        else -> false
    }
}