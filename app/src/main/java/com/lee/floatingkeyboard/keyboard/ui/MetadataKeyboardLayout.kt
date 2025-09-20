package com.lee.floatingkeyboard.keyboard.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lee.floatingkeyboard.keyboard.core.KeyMetadata
import com.lee.floatingkeyboard.keyboard.core.KeyboardLayout
import com.lee.floatingkeyboard.ui.theme.FloatingKeyboardTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Long press 상태를 나타내는 데이터 클래스
 */
private data class LongPressState(
    val key: KeyMetadata,
    val keyPosition: IntOffset,
    val keySize: IntOffset,
    val variants: List<KeyMetadata>,
    val selectedIndex: Int = 0
)

/**
 * KeyMetadata를 사용하는 키보드 레이아웃 컴포저블
 */
@Composable
fun MetadataKeyboardLayout(
    layout: KeyboardLayout,
    modifier: Modifier = Modifier,
    longPressDelayMs: Long = 300L,
    onKeyPress: (KeyMetadata) -> Unit = {},
    onKeyTouchDown: (KeyMetadata) -> Unit = {},
    onKeyTouchUp: (KeyMetadata) -> Unit = {}
) {
    var pressedKey by remember { mutableStateOf<KeyMetadata?>(null) }
    var longPressState by remember { mutableStateOf<LongPressState?>(null) }
    val density = LocalDensity.current

    LaunchedEffect(pressedKey) {
        if (pressedKey != null) {
            delay(150)
            pressedKey = null
        }
    }

    Box(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            layout.rows.forEach { rowKeys ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowKeys.forEachIndexed { index, keyMetadata ->
                        val isLongPressed = longPressState?.key == keyMetadata
                        KeyButton(
                            keyMetadata = keyMetadata,
                            isPressed = pressedKey == keyMetadata,
                            isLongPressed = isLongPressed,
                            longPressDelayMs = longPressDelayMs,
                            modifier = Modifier
                                .weight(keyMetadata.weight)
                                .height(50.dp)
                                .padding(
                                    start = if (index > 0) 2.dp else 0.dp,
                                    top = 1.dp,
                                    end = if (index < rowKeys.size - 1) 2.dp else 0.dp,
                                    bottom = 1.dp
                                ),
                            onTap = {
                                pressedKey = keyMetadata
                                onKeyPress(keyMetadata)
                            },
                            onLongPress = { keyPosition, keySize ->
                                val variants = keyMetadata.longPressOptions
                                if (!variants.isNullOrEmpty()) {
                                    longPressState = LongPressState(
                                        key = keyMetadata,
                                        keyPosition = keyPosition,
                                        keySize = keySize,
                                        variants = listOf(keyMetadata) + variants
                                    )
                                }
                            },
                            onDrag = { dragOffset ->
                                longPressState?.let { state ->
                                    val keyWidth = with(density) { state.keySize.x.toDp().toPx() }
                                    val selectedIndex = calculateSelectedVariantIndex(
                                        dragOffset = dragOffset.x,
                                        variantCount = state.variants.size,
                                        keyWidth = keyWidth
                                    )
                                    longPressState = state.copy(selectedIndex = selectedIndex)
                                }
                            },
                            onRelease = {
                                longPressState?.let { state ->
                                    val selectedKey = state.variants.getOrNull(state.selectedIndex) ?: keyMetadata
                                    onKeyPress(selectedKey)
                                    pressedKey = selectedKey // 눌린 키 상태 업데이트
                                }
                                longPressState = null
                            },
                            onTouchDown = {
                                onKeyTouchDown(keyMetadata)
                            },
                            onTouchUp = {
                                longPressState = null
                                onKeyTouchUp(keyMetadata)
                            }
                        )
                    }
                }
            }
        }

        // Long press popup
        longPressState?.let { state ->
            LongPressPopup(
                isVisible = true,
                keyPosition = state.keyPosition,
                keySize = state.keySize,
                variants = state.variants,
                selectedIndex = state.selectedIndex,
                onDismiss = { longPressState = null }
            )
        }
    }
}

@Composable
private fun KeyButton(
    keyMetadata: KeyMetadata,
    isPressed: Boolean,
    isLongPressed: Boolean,
    longPressDelayMs: Long,
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: (keyPosition: IntOffset, keySize: IntOffset) -> Unit = { _, _ -> },
    onDrag: (dragOffset: Offset) -> Unit = {},
    onRelease: () -> Unit = {},
    onTouchDown: () -> Unit = {},
    onTouchUp: () -> Unit = {}
) {
    var keyPosition by remember { mutableStateOf(IntOffset.Zero) }
    var keySize by remember { mutableStateOf(IntOffset.Zero) }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                // 전체 화면 기준 절대 위치 계산
                val windowPosition = coordinates.localToWindow(Offset.Zero)
                
                keyPosition = IntOffset(
                    windowPosition.x.toInt(),
                    windowPosition.y.toInt()
                )
                keySize = IntOffset(
                    coordinates.size.width,
                    coordinates.size.height
                )
            }
            .pointerInput(keyMetadata, longPressDelayMs) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var longPressJob: kotlinx.coroutines.Job? = null
                    var isDragging = false
                    var longPressTriggered = false
                    val startPosition = down.position
                    
                    onTouchDown()
                    
                    // Start long press timer
                    longPressJob = GlobalScope.launch {
                        delay(longPressDelayMs)
                        if (!isDragging) {
                            longPressTriggered = true
                            onLongPress(keyPosition, keySize)
                        }
                    }
                    
                    // Handle drag and release
                    do {
                        val event = awaitPointerEvent()
                        val currentPointer = event.changes.firstOrNull() ?: break
                        
                        if (currentPointer.pressed) {
                            val dragDistance = sqrt(
                                (currentPointer.position.x - startPosition.x) * (currentPointer.position.x - startPosition.x) +
                                (currentPointer.position.y - startPosition.y) * (currentPointer.position.y - startPosition.y)
                            )
                            
                            if (dragDistance > 10f && !isDragging) {
                                isDragging = true
                            }
                            
                            // longPressTriggered만 사용
                            if (longPressTriggered && isDragging) {
                                onDrag(Offset(
                                    currentPointer.position.x - startPosition.x,
                                    currentPointer.position.y - startPosition.y
                                ))
                            }
                        }
                        
                        currentPointer.consume()
                        
                    } while (event.changes.any { it.pressed })
                    
                    // Handle gesture end
                    longPressJob.cancel()
                    onTouchUp()
                    
                    // longPressTriggered만 사용
                    if (longPressTriggered) {
                        onRelease()
                    } else if (!isDragging) {
                        onTap()
                    }
                }
            }
    ) {
        // Visual button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = when {
                        isLongPressed -> 8.dp
                        isPressed -> 6.dp
                        else -> 2.dp
                    },
                    shape = RoundedCornerShape(4.dp)
                )
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (isLongPressed) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = keyMetadata.label,
                color = if (isLongPressed) {
                    MaterialTheme.colorScheme.onTertiary
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                },
                fontSize = if (keyMetadata.label.length > 3) 12.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        // Long press hint indicator
        if (!keyMetadata.longPressOptions.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(4.dp)
                    .background(
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

/**
 * 드래그 위치에 따라 선택된 변형의 인덱스를 계산
 */
private fun calculateSelectedVariantIndex(
    dragOffset: Float,
    variantCount: Int,
    keyWidth: Float
): Int {
    if (variantCount <= 1) return 0

    // 한 변형당 필요한 드래그 거리 (키 너비의 30%)
    val dragDistancePerVariant = keyWidth * 0.3f
    
    // 드래그 거리를 기준으로 인덱스 계산 (0부터 시작)
    val index = when {
        dragOffset >= 0 -> (dragOffset / dragDistancePerVariant).toInt()
        else -> -((-dragOffset) / dragDistancePerVariant).toInt()
    }

    Log.d("LongPress", "dragOffset: $dragOffset, dragDistancePerVariant: $dragDistancePerVariant, calculated index: $index, final index: ${index.coerceIn(0, variantCount - 1)}")
    return index.coerceIn(0, variantCount - 1)
}

@Preview(showBackground = true)
@Composable
fun MetadataKeyboardLayoutPreview() {
    FloatingKeyboardTheme {
        val sampleLayout = KeyboardLayout(
            rows = listOf(
                listOf(
                    KeyMetadata.character("a", longPressOptions = listOf("à", "á", "â", "ä", "æ")),
                    KeyMetadata.character("s", longPressOptions = listOf("ß", "ś", "š")),
                    KeyMetadata.character("d"),
                    KeyMetadata.character("f"),
                    KeyMetadata.character("g")
                ),
                listOf(
                    KeyMetadata.character("q"),
                    KeyMetadata.character("w"),
                    KeyMetadata.character("e", longPressOptions = listOf("è", "é", "ê", "ë")),
                    KeyMetadata.character("r"),
                    KeyMetadata.character("t")
                ),
                listOf(
                    KeyMetadata.shift(),
                    KeyMetadata.character("z"),
                    KeyMetadata.character("x"),
                    KeyMetadata.character("c", longPressOptions = listOf("ç", "ć", "č")),
                    KeyMetadata.backspace()
                )
            ),
            shiftToggleEnabled = true
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            MetadataKeyboardLayout(
                layout = sampleLayout,
                onKeyPress = { keyMetadata ->
                    println("Key pressed: ${keyMetadata.label}")
                }
            )
        }
    }
}