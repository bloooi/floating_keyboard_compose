package com.lee.floatingkeyboard.keyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lee.floatingkeyboard.keyboard.core.KeyMetadata

/**
 * 롱프레스 팝업 상태를 관리하는 글로벌 상태
 */
object GlobalPopupState {
    var isVisible by mutableStateOf(false)
        private set
    var variants by mutableStateOf<List<KeyMetadata>>(emptyList())
        private set
    var selectedIndex by mutableStateOf(0)
        private set
    var globalPosition by mutableStateOf(Offset.Zero)
        private set
    var currentLongPressedKey by mutableStateOf<KeyMetadata?>(null)
        private set

    fun show(
        keyMetadata: KeyMetadata,
        variantList: List<KeyMetadata>,
        position: Offset
    ) {
        currentLongPressedKey = keyMetadata
        variants = variantList
        selectedIndex = 0
        globalPosition = position
        isVisible = true
    }

    fun updateSelection(index: Int) {
        selectedIndex = index.coerceIn(0, variants.size - 1)
    }

    fun hide() {
        isVisible = false
        variants = emptyList()
        selectedIndex = 0
        currentLongPressedKey = null
    }
}

/**
 * 화면 전체를 덮는 글로벌 팝업 오버레이
 * 키보드와 완전히 독립적으로 작동하며 화면 경계에서 잘리지 않도록 위치 조정
 */
@Composable
fun GlobalPopupOverlay() {
    if (GlobalPopupState.isVisible && GlobalPopupState.variants.isNotEmpty()) {
        val density = LocalDensity.current
        val configuration = LocalConfiguration.current
        val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
        val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

        // 팝업 크기 계산
        val popupWidth = with(density) {
            val itemWidth = 50.dp.toPx()
            val spacing = 2.dp.toPx()
            val padding = 16.dp.toPx()
            GlobalPopupState.variants.size * itemWidth + (GlobalPopupState.variants.size - 1) * spacing + padding
        }
        val popupHeight = with(density) {
            val itemHeight = 50.dp.toPx()
            val padding = 16.dp.toPx()
            itemHeight + padding
        }

        // 화면 경계 체크 및 위치 조정
        val adjustedPosition = calculateAdjustedPosition(
            originalPosition = GlobalPopupState.globalPosition,
            popupWidth = popupWidth,
            popupHeight = popupHeight,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            density = density
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            adjustedPosition.x.toInt(),
                            adjustedPosition.y.toInt()
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .shadow(12.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    GlobalPopupState.variants.forEachIndexed { index, variant ->
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    if (index == GlobalPopupState.selectedIndex) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    RoundedCornerShape(6.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = variant.label,
                                color = if (index == GlobalPopupState.selectedIndex) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 화면 경계를 고려하여 팝업 위치를 조정
 * 잘리지 않을 경우에는 원래 위치 유지
 */
private fun calculateAdjustedPosition(
    originalPosition: Offset,
    popupWidth: Float,
    popupHeight: Float,
    screenWidth: Float,
    screenHeight: Float,
    density: androidx.compose.ui.unit.Density
): Offset {
    val margin = with(density) { 8.dp.toPx() }

    var adjustedX = originalPosition.x
    var adjustedY = originalPosition.y

    // X축 경계 체크
    when {
        // 왼쪽 경계를 넘어가는 경우
        originalPosition.x < margin -> {
            adjustedX = margin
        }
        // 오른쪽 경계를 넘어가는 경우
        originalPosition.x + popupWidth > screenWidth - margin -> {
            adjustedX = screenWidth - popupWidth - margin
        }
        // 잘리지 않는 경우 원래 위치 유지
        else -> {
            adjustedX = originalPosition.x
        }
    }

    // Y축 경계 체크
    when {
        // 위쪽 경계를 넘어가는 경우
        originalPosition.y < margin -> {
            adjustedY = margin
        }
        // 아래쪽 경계를 넘어가는 경우
        originalPosition.y + popupHeight > screenHeight - margin -> {
            adjustedY = screenHeight - popupHeight - margin
        }
        // 잘리지 않는 경우 원래 위치 유지
        else -> {
            adjustedY = originalPosition.y
        }
    }

    return Offset(adjustedX, adjustedY)
}