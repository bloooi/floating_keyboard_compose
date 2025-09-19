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
import com.lee.floatingkeyboard.keyboard.core.KeyAction
import com.lee.floatingkeyboard.keyboard.input.TextComposer
import com.lee.floatingkeyboard.keyboard.language.LanguageProvider
import com.lee.floatingkeyboard.utils.LanguageManager
import kotlin.math.roundToInt

@Composable
fun FloatingKeyboard(
    textComposer: TextComposer,
    modifier: Modifier = Modifier,
    languageManager: LanguageManager = remember { LanguageManager() },
    onKeyPress: (String) -> Unit = {},
    onClose: () -> Unit = {}
) {
    val currentLanguageProvider by languageManager.currentLanguage
    
    var offset by remember { mutableStateOf(Offset.Zero) }
    var keyboardSize by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

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
            languageProvider = currentLanguageProvider,
            textComposer = textComposer,
            onKeyPress = { keyAction ->
                // 모든 언어에 대해 통일된 처리
                handleTextInput(keyAction, textComposer, onKeyPress)
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
    languageProvider: LanguageProvider,
    textComposer: TextComposer,
    onKeyPress: (KeyAction) -> Unit,
    onLanguageSwitch: () -> Unit,
    onClose: () -> Unit,
    onDrag: (Offset) -> Unit
) {
    // Shift state
    var isShiftPressed by remember { mutableStateOf(false) }
    
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
                    text = languageProvider.displayName,
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
        val layout = if (isShiftPressed) {
            languageProvider.getShiftLayout()
        } else {
            languageProvider.getLayout()
        }

        MetadataKeyboardLayout(
            layout = layout,
            onKeyPress = { keyMetadata ->
                when (keyMetadata.action) {
                    KeyAction.Shift -> {
                        if (layout.shiftToggleEnabled) {
                            isShiftPressed = !isShiftPressed
                        }
                    }
                    KeyAction.LanguageSwitch -> {
                        onLanguageSwitch()
                    }
                    else -> {
                        // Reset shift state after input (except shift key itself)
                        if (isShiftPressed && keyMetadata.action != KeyAction.Shift) {
                            isShiftPressed = false
                        }
                        onKeyPress(keyMetadata.action)
                    }
                }
            }
        )
    }
}



private fun handleTextInput(
    keyAction: KeyAction,
    textComposer: TextComposer,
    onKeyPress: (String) -> Unit
) {
    when (keyAction) {
        KeyAction.Backspace -> {
            val result = textComposer.backspace()
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
        KeyAction.Space -> {
            val completed = textComposer.complete()
            if (completed.currentComposition != null) {
                onKeyPress("COMPLETE:${completed.currentComposition}")
            }
            onKeyPress(" ")
        }
        KeyAction.Enter -> {
            val completed = textComposer.complete()
            if (completed.currentComposition != null) {
                onKeyPress("COMPLETE:${completed.currentComposition}")
            }
            onKeyPress("ENTER")
        }
        is KeyAction.Character -> {
            if (keyAction.char.length == 1) {
                // 단일 문자 입력
                val result = textComposer.addCharacter(keyAction.char[0])

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
                    onKeyPress(keyAction.char)
                }
            } else {
                onKeyPress(keyAction.char)
            }
        }
        is KeyAction.Symbol -> {
            onKeyPress(keyAction.symbol)
        }
        KeyAction.Shift -> {
            // Shift는 UI 레벨에서 처리되므로 여기서는 무시
        }
        KeyAction.LanguageSwitch -> {
            // 언어 전환은 UI 레벨에서 처리되므로 여기서는 무시
        }
        KeyAction.NumberMode,
        KeyAction.LetterMode -> {
            // 모드 전환은 UI 레벨에서 처리되므로 여기서는 무시
        }
    }
}

// isValidInputComponent 함수 제거됨 - 이제 각 Composer가 자체적으로 유효성 검사