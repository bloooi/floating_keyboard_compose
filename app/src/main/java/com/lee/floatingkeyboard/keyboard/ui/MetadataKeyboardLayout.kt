package com.lee.floatingkeyboard.keyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lee.floatingkeyboard.keyboard.core.KeyMetadata
import com.lee.floatingkeyboard.keyboard.core.KeyboardLayout
import kotlinx.coroutines.delay

/**
 * KeyMetadata를 사용하는 키보드 레이아웃 컴포저블
 */
@Composable
fun MetadataKeyboardLayout(
    layout: KeyboardLayout,
    modifier: Modifier = Modifier,
    onKeyPress: (KeyMetadata) -> Unit = {}
) {
    var pressedKey by remember { mutableStateOf<KeyMetadata?>(null) }

    LaunchedEffect(pressedKey) {
        if (pressedKey != null) {
            delay(150)
            pressedKey = null
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        layout.rows.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                rowKeys.forEachIndexed { index, keyMetadata ->
                    Box(
                        modifier = Modifier
                            .weight(keyMetadata.weight)
                            .height(50.dp)
                            .clickable {
                                pressedKey = keyMetadata
                                onKeyPress(keyMetadata)
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
                                    elevation = if (pressedKey == keyMetadata) 6.dp else 2.dp,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = keyMetadata.label,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = if (keyMetadata.label.length > 3) 12.sp else 16.sp,
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