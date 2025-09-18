package com.lee.floatingkeyboard.keyboard.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lee.floatingkeyboard.ui.theme.FloatingKeyboardTheme
import com.lee.floatingkeyboard.utils.LanguageManager
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for FloatingKeyboard
 * Tests keyboard interactions, language switching, and drag functionality
 */
@RunWith(AndroidJUnit4::class)
class FloatingKeyboardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun floatingKeyboard_displaysCorrectly() {
        var keyPressedText = ""
        var keyboardClosed = false

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard(
                    onKeyPress = { keyPressedText = it },
                    onClose = { keyboardClosed = true }
                )
            }
        }

        // Verify keyboard is displayed
        composeTestRule.onNodeWithText("한글").assertIsDisplayed()
        composeTestRule.onNodeWithText("🌐").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Close").assertIsDisplayed()
    }

    @Test
    fun floatingKeyboard_showsCorrectLayoutForKorean() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard()
            }
        }

        // Check for Korean jamo characters
        composeTestRule.onNodeWithText("ㅂ").assertIsDisplayed()
        composeTestRule.onNodeWithText("ㅈ").assertIsDisplayed()
        composeTestRule.onNodeWithText("ㄷ").assertIsDisplayed()
        composeTestRule.onNodeWithText("ㄱ").assertIsDisplayed()
        composeTestRule.onNodeWithText("ㅅ").assertIsDisplayed()
    }

    @Test
    fun floatingKeyboard_switchesToEnglish() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard()
            }
        }

        // Tap language switch button
        composeTestRule.onNodeWithText("🌐").performClick()

        // Should show English layout
        composeTestRule.onNodeWithText("English").assertIsDisplayed()
        composeTestRule.onNodeWithText("q").assertIsDisplayed()
        composeTestRule.onNodeWithText("w").assertIsDisplayed()
        composeTestRule.onNodeWithText("e").assertIsDisplayed()
    }

    @Test
    fun floatingKeyboard_switchesToSymbols() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard()
            }
        }

        // Switch to English first
        composeTestRule.onNodeWithText("🌐").performClick()

        // Switch to symbols
        composeTestRule.onNodeWithText("🌐").performClick()

        // Should show symbols layout
        composeTestRule.onNodeWithText("123").assertIsDisplayed()
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun floatingKeyboard_handleKeyPress() {
        var keyPressedText = ""

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard(
                    onKeyPress = { keyPressedText = it }
                )
            }
        }

        // Press a Korean consonant
        composeTestRule.onNodeWithText("ㄱ").performClick()

        // Should trigger key press callback
        assert(keyPressedText.contains("COMPOSE:") || keyPressedText == "ㄱ")
    }

    @Test
    fun floatingKeyboard_handlesSpaceKey() {
        var keyPressedText = ""

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard(
                    onKeyPress = { keyPressedText = it }
                )
            }
        }

        // Press space key
        composeTestRule.onNodeWithText("Space").performClick()

        // Should trigger space key press
        assert(keyPressedText.contains(" ") || keyPressedText.contains("Space"))
    }

    @Test
    fun floatingKeyboard_handlesBackspace() {
        var keyPressedText = ""

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard(
                    onKeyPress = { keyPressedText = it }
                )
            }
        }

        // Press backspace key
        composeTestRule.onNodeWithText("⌫").performClick()

        // Should trigger backspace
        assert(keyPressedText.contains("BACKSPACE") || keyPressedText == "⌫")
    }

    @Test
    fun floatingKeyboard_handlesEnterKey() {
        var keyPressedText = ""

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard(
                    onKeyPress = { keyPressedText = it }
                )
            }
        }

        // Press enter key
        composeTestRule.onNodeWithText("⏎").performClick()

        // Should trigger enter key press
        assert(keyPressedText.contains("ENTER") || keyPressedText == "⏎")
    }

    @Test
    fun floatingKeyboard_closesCorrectly() {
        var keyboardClosed = false

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard(
                    onClose = { keyboardClosed = true }
                )
            }
        }

        // Press close button
        composeTestRule.onNodeWithContentDescription("Close").performClick()

        // Should trigger close callback
        assert(keyboardClosed)
    }

    @Test
    fun floatingKeyboard_hasDragHandle() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard()
            }
        }

        // Verify drag handle is present (look for the draggable header area)
        composeTestRule.onNode(hasClickAction()).assertExists()
    }

    @Test
    fun floatingKeyboard_showsCorrectLanguageLabel() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard()
            }
        }

        // Should start with Korean
        composeTestRule.onNodeWithText("한글").assertIsDisplayed()

        // Switch to English
        composeTestRule.onNodeWithText("🌐").performClick()
        composeTestRule.onNodeWithText("English").assertIsDisplayed()

        // Switch to symbols
        composeTestRule.onNodeWithText("🌐").performClick()
        composeTestRule.onNodeWithText("123").assertIsDisplayed()
    }

    @Test
    fun floatingKeyboard_koreanSequentialInput() {
        var keyPressedTexts = mutableListOf<String>()

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard(
                    onKeyPress = { keyPressedTexts.add(it) }
                )
            }
        }

        // Input sequence: ㄱ + ㅏ (should make 가)
        composeTestRule.onNodeWithText("ㄱ").performClick()
        composeTestRule.onNodeWithText("ㅏ").performClick()

        // Should have composition events
        assert(keyPressedTexts.any { it.contains("COMPOSE:") })
    }

    @Test
    fun floatingKeyboard_englishInput() {
        var keyPressedText = ""

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard(
                    onKeyPress = { keyPressedText = it }
                )
            }
        }

        // Switch to English
        composeTestRule.onNodeWithText("🌐").performClick()

        // Press 'q' key
        composeTestRule.onNodeWithText("q").performClick()

        // Should get direct key press
        assert(keyPressedText == "q")
    }

    @Test
    fun floatingKeyboard_symbolInput() {
        var keyPressedText = ""

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                FloatingKeyboard(
                    onKeyPress = { keyPressedText = it }
                )
            }
        }

        // Switch to English first
        composeTestRule.onNodeWithText("🌐").performClick()

        // Switch to symbols
        composeTestRule.onNodeWithText("🌐").performClick()

        // Press '1' key
        composeTestRule.onNodeWithText("1").performClick()

        // Should get direct key press
        assert(keyPressedText == "1")
    }
}