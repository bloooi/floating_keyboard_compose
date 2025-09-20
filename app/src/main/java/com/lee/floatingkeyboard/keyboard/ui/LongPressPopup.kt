package com.lee.floatingkeyboard.keyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.tooling.preview.Preview
import com.lee.floatingkeyboard.keyboard.core.KeyMetadata
import com.lee.floatingkeyboard.ui.theme.FloatingKeyboardTheme

/**
 * Long press 팝업 컴포저블
 */
@Composable
fun LongPressPopup(
    isVisible: Boolean,
    keyPosition: IntOffset,
    keySize: IntOffset,
    variants: List<KeyMetadata>,
    selectedIndex: Int,
    onDismiss: () -> Unit
) {
    if (!isVisible || variants.isEmpty()) return

    // 팝업의 전체 너비 계산
    val popupWidth = variants.size * 50 + (variants.size - 1) * 2 // 50dp per key + 2dp spacing
    
    // 키 위쪽에 팝업 위치, 가운데 정렬
    val popupOffset = IntOffset(
        x = keyPosition.x + (keySize.x - popupWidth) / 2,
        y = keyPosition.y - 70 // 키 위쪽으로 70dp 올림
    )

    Popup(
        offset = popupOffset,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
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
            variants.forEachIndexed { index, variant ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            if (index == selectedIndex) {
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
                        color = if (index == selectedIndex) {
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

@Preview(showBackground = true)
@Composable
fun LongPressPopupPreview() {
    FloatingKeyboardTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Sample key button to show context
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "a",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Long press popup
            LongPressPopup(
                isVisible = true,
                keyPosition = IntOffset(70, 70),
                keySize = IntOffset(50, 50),
                variants = listOf(
                    KeyMetadata.character("a"),
                    KeyMetadata.character("à"),
                    KeyMetadata.character("á"),
                    KeyMetadata.character("â"),
                    KeyMetadata.character("ä")
                ),
                selectedIndex = 2,
                onDismiss = {}
            )
        }
    }
}
