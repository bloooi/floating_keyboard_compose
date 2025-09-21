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
 * 키보드와 완전히 독립적으로 작동
 */
@Composable
fun GlobalPopupOverlay() {
    if (GlobalPopupState.isVisible && GlobalPopupState.variants.isNotEmpty()) {
        val density = LocalDensity.current

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            GlobalPopupState.globalPosition.x.toInt(),
                            GlobalPopupState.globalPosition.y.toInt()
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