package com.lee.floatingkeyboard.keyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.lee.floatingkeyboard.keyboard.core.Key
import com.lee.floatingkeyboard.keyboard.core.Keyboard
import kotlinx.coroutines.delay

@Composable
fun KeyboardView(
    keyboard: Keyboard,
    modifier: Modifier = Modifier,
    onKeyPress: (Key) -> Unit = {},
    onKeyLongPress: (Key) -> Unit = {}
) {
    var pressedKey by remember { mutableStateOf<Key?>(null) }
    val density = LocalDensity.current

    LaunchedEffect(pressedKey) {
        if (pressedKey != null) {
            delay(150) // Visual feedback duration
            pressedKey = null
        }
    }

    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        Layout(
            content = {
                keyboard.keys.forEach { key ->
                    KeyButton(
                        key = key,
                        isPressed = pressedKey == key,
                        onKeyPress = { _ ->
                            pressedKey = key
                            onKeyPress(key)
                        },
                        onKeyLongPress = onKeyLongPress
                    )
                }
            },
            modifier = Modifier.pointerInput(keyboard) {
                detectTapGestures(
                    onTap = { offset ->
                        val x = with(density) { offset.x.toDp().value.toInt() }
                        val y = with(density) { offset.y.toDp().value.toInt() }

                        val tappedKey = keyboard.keys.find { key ->
                            key.isInside(x, y)
                        }

                        tappedKey?.let { key ->
                            pressedKey = key
                            onKeyPress(key)
                        }
                    }
                )
            }
        ) { measurables, constraints ->
            val placeables = measurables.mapIndexed { index, measurable ->
                val key = keyboard.keys[index]
                val keyConstraints = Constraints.fixed(
                    width = with(density) { key.width.dp.roundToPx() },
                    height = with(density) { key.height.dp.roundToPx() }
                )
                measurable.measure(keyConstraints)
            }

            val totalWidth = keyboard.totalWidth
            val totalHeight = keyboard.totalHeight

            layout(totalWidth, totalHeight) {
                placeables.forEachIndexed { index, placeable ->
                    val key = keyboard.keys[index]
                    placeable.place(
                        IntOffset(key.x, key.y)
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleKeyboardLayout(
    keys: List<List<String>>,
    modifier: Modifier = Modifier,
    onKeyPress: (String) -> Unit = {}
) {
    var pressedKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pressedKey) {
        if (pressedKey != null) {
            delay(150)
            pressedKey = null
        }
    }

    Column(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                rowKeys.forEachIndexed { index, keyText ->
                    val weight = when (keyText) {
                        "Space" -> 4f
                        "⇧", "⌫", "⏎" -> 1.5f
                        "123", "ABC" -> 1.5f
                        else -> 1f
                    }

                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .height(50.dp)
                            .clickable {
                                pressedKey = keyText
                                onKeyPress(keyText)
                            }
                    ) {
                        // Visual button with proper spacing
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = if (index > 0) 2.dp else 0.dp,
                                    top = 1.dp,
                                    end = if (index < rowKeys.size - 1) 2.dp else 0.dp,
                                    bottom = 1.dp
                                )
                                .shadow(
                                    elevation = if (pressedKey == keyText) 6.dp else 2.dp,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = keyText,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = if (keyText.length > 3) 12.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShiftableKeyboardLayout(
    keys: List<List<String>>,
    isShiftPressed: Boolean = false,
    modifier: Modifier = Modifier,
    onKeyPress: (String) -> Unit = {}
) {
    var pressedKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pressedKey) {
        if (pressedKey != null) {
            delay(150)
            pressedKey = null
        }
    }

    Column(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                rowKeys.forEachIndexed { index, keyText ->
                    val weight = when (keyText) {
                        "Space" -> 4f
                        "⇧", "⌫", "⏎" -> 1.5f
                        "123", "ABC" -> 1.5f
                        else -> 1f
                    }

                    // Determine if this is a shift key and apply special styling
                    val isShiftKey = keyText == "⇧"
                    val shiftBackgroundColor = if (isShiftKey && isShiftPressed) {
                        MaterialTheme.colorScheme.primary
                    } else if (isShiftKey) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }

                    val shiftTextColor = if (isShiftKey && isShiftPressed) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }

                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .height(50.dp)
                            .clickable {
                                pressedKey = keyText
                                onKeyPress(keyText)
                            }
                    ) {
                        // Visual button with proper spacing
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = if (index > 0) 2.dp else 0.dp,
                                    top = 1.dp,
                                    end = if (index < rowKeys.size - 1) 2.dp else 0.dp,
                                    bottom = 1.dp
                                )
                                .shadow(
                                    elevation = if (pressedKey == keyText) 6.dp else 2.dp,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clip(RoundedCornerShape(4.dp))
                                .background(shiftBackgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = keyText,
                                color = shiftTextColor,
                                fontSize = if (keyText.length > 3) 12.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}