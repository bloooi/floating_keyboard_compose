package com.lee.floatingkeyboard.keyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lee.floatingkeyboard.keyboard.core.Key

@Composable
fun KeyButton(
    key: Key,
    modifier: Modifier = Modifier,
    isPressed: Boolean = false,
    onKeyPress: (Key) -> Unit = {},
    onKeyLongPress: (Key) -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    val backgroundColor = when {
        isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        key.modifier -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        key.modifier -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .size(
                width = (key.width / 10).dp,
                height = 50.dp
            )
            .padding(1.dp)
            .shadow(
                elevation = if (isPressed) 6.dp else 2.dp,
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onKeyPress(key)
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            key.label != null -> {
                Text(
                    text = key.label.toString(),
                    color = textColor,
                    fontSize = if (key.label.length > 3) 12.sp else 16.sp,
                    fontWeight = if (key.modifier) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            key.icon != null -> {
                // Icon support can be added here
                Text(
                    text = "⌨",
                    color = textColor,
                    fontSize = 16.sp
                )
            }
            else -> {
                Text(
                    text = if (key.codes.isNotEmpty()) key.codes[0].toChar().toString() else "",
                    color = textColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun SpecialKeyButton(
    text: String,
    modifier: Modifier = Modifier,
    width: Dp = 60.dp,
    height: Dp = 50.dp,
    isPressed: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    onClick: () -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .size(width, height)
            // Expand touch area by adding padding to clickable area but not visual area
            .clickable {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
                .shadow(
                    elevation = if (isPressed) 6.dp else 2.dp,
                    shape = RoundedCornerShape(4.dp)
                )
                .clip(RoundedCornerShape(4.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = if (text.length > 3) 12.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

