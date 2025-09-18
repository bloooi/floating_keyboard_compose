package com.lee.floatingkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lee.floatingkeyboard.keyboard.ui.FloatingKeyboard
import com.lee.floatingkeyboard.ui.theme.FloatingKeyboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var showKeyboard by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }
    var composingPosition by remember { mutableStateOf(-1) }
    var lastComposedLength by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Multilingual Floating Keyboard Demo",
                    style = MaterialTheme.typography.headlineMedium
                )

                Button(
                    onClick = { showKeyboard = !showKeyboard },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showKeyboard) "Hide Keyboard" else "Show Keyboard")
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Text Input:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = inputText.ifEmpty { "Type using the floating keyboard..." },
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Features:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Drag to move the floating keyboard\n" +
                                    "• Tap 🌐 to switch between English/Korean\n" +
                                    "• Korean Hangul composition support\n" +
                                    "• AOSP-based implementation\n" +
                                    "• Material Design 3 UI",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        if (showKeyboard) {
            FloatingKeyboard(
                modifier = Modifier.wrapContentSize(),
                onKeyPress = { keyText: String ->
                    when {
                        keyText.startsWith("COMPOSE:") -> {
                            val composedText = keyText.removePrefix("COMPOSE:")
                            if (composingPosition >= 0 && composingPosition <= inputText.length) {
                                // Replace the composing text at the specific position
                                val before = inputText.substring(0, composingPosition)
                                val after = inputText.substring(composingPosition + lastComposedLength)
                                inputText = before + composedText + after
                                lastComposedLength = composedText.length
                            } else {
                                // Start new composition at the end
                                composingPosition = inputText.length
                                inputText += composedText
                                lastComposedLength = composedText.length
                            }
                        }
                        keyText.startsWith("COMPLETE:") -> {
                            val completedText = keyText.removePrefix("COMPLETE:")
                            if (composingPosition >= 0 && composingPosition <= inputText.length) {
                                // Complete the composition
                                val before = inputText.substring(0, composingPosition)
                                val after = inputText.substring(composingPosition + lastComposedLength)
                                inputText = before + completedText + after
                            } else {
                                // No composition in progress, just append
                                inputText += completedText
                            }
                            // Reset composition state
                            composingPosition = -1
                            lastComposedLength = 0
                        }
                        keyText == "BACKSPACE" -> {
                            // Complete any ongoing composition before backspace
                            composingPosition = -1
                            lastComposedLength = 0
                            if (inputText.isNotEmpty()) {
                                inputText = inputText.dropLast(1)
                            }
                        }
                        keyText == "ENTER" -> {
                            // Complete any ongoing composition before enter
                            composingPosition = -1
                            lastComposedLength = 0
                            inputText += "\n"
                        }
                        keyText == "Space" -> {
                            // Complete any ongoing composition before space
                            composingPosition = -1
                            lastComposedLength = 0
                            inputText += " "
                        }
                        keyText in listOf("⇧", "123", "ABC", "⌫", "⏎") -> {
                            // Special keys handled by keyboard internally
                        }
                        else -> {
                            // Complete any ongoing composition before adding new text
                            composingPosition = -1
                            lastComposedLength = 0
                            inputText += keyText
                        }
                    }
                },
                onClose = { showKeyboard = false }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    FloatingKeyboardTheme {
        MainScreen()
    }
}