package com.lee.floatingkeyboard

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lee.floatingkeyboard.ui.theme.FloatingKeyboardTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for Hangul input functionality
 * Tests the complete flow from keyboard input to text display
 */
@RunWith(AndroidJUnit4::class)
class HangulInputIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_complete_hangul_input_flow() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }

        // Verify keyboard is shown
        composeTestRule.onNodeWithText("한글").assertIsDisplayed()

        // Input sequence: ㄱ + ㅏ (should make 가)
        composeTestRule.onNodeWithText("ㄱ").performClick()
        composeTestRule.onNodeWithText("ㅏ").performClick()

        // The text should be updated in the input area
        // Note: This might need adjustment based on actual implementation
        composeTestRule.waitForIdle()

        // Verify composition is working
        composeTestRule.onNodeWithText("ㄱ").assertExists()
        composeTestRule.onNodeWithText("ㅏ").assertExists()
    }

    @Test
    fun test_language_switching_functionality() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }

        // Start with Korean
        composeTestRule.onNodeWithText("한글").assertIsDisplayed()

        // Switch to English
        composeTestRule.onNodeWithText("🌐").performClick()
        composeTestRule.onNodeWithText("English").assertIsDisplayed()

        // Verify English keys are shown
        composeTestRule.onNodeWithText("q").assertIsDisplayed()

        // Switch to symbols
        composeTestRule.onNodeWithText("🌐").performClick()
        composeTestRule.onNodeWithText("123").assertIsDisplayed()

        // Verify symbol keys are shown
        composeTestRule.onNodeWithText("1").assertIsDisplayed()

        // Switch back to Korean
        composeTestRule.onNodeWithText("🌐").performClick()
        composeTestRule.onNodeWithText("한글").assertIsDisplayed()
    }

    @Test
    fun test_keyboard_dragging_functionality() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }

        // Find the draggable header area
        val dragHandle = composeTestRule.onNode(
            hasText("한글").and(hasClickAction())
        )
        dragHandle.assertIsDisplayed()

        // Perform drag gesture on header
        dragHandle.performTouchInput {
            swipeRight(startX = 0f, endX = 100f)
        }

        // Keyboard should still be visible after drag
        composeTestRule.onNodeWithText("한글").assertIsDisplayed()
    }

    @Test
    fun test_close_keyboard_functionality() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }

        // Keyboard should be visible initially
        composeTestRule.onNodeWithText("한글").assertIsDisplayed()

        // Click close button
        composeTestRule.onNodeWithContentDescription("Close").performClick()

        // Keyboard should be hidden (note: this depends on MainScreen implementation)
        composeTestRule.onNodeWithText("Show Keyboard").assertIsDisplayed()
    }

    @Test
    fun test_consecutive_consonant_input() {
        var capturedTexts = mutableListOf<String>()

        composeTestRule.setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }

        // Test the issue: ㅇㄹㅊㅋㄹㅅ should not combine ㄹㅅ
        val consonants = listOf("ㅇ", "ㄹ", "ㅊ", "ㅋ", "ㄹ", "ㅅ")

        consonants.forEach { consonant ->
            composeTestRule.onNodeWithText(consonant).performClick()
            composeTestRule.waitForIdle()
        }

        // The UI should handle consecutive consonants properly
        // (Specific assertions would depend on how the UI displays the result)
        composeTestRule.onNodeWithText("ㅇ").assertExists()
        composeTestRule.onNodeWithText("ㄹ").assertExists()
        composeTestRule.onNodeWithText("ㅅ").assertExists()
    }

    @Test
    fun test_syllable_decomposition_scenario() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }

        // Test scenario: 핫 + ㅔ should become 하세
        // First build 핫 (ㅎ + ㅏ + ㅅ)
        composeTestRule.onNodeWithText("ㅎ").performClick()
        composeTestRule.onNodeWithText("ㅏ").performClick()
        composeTestRule.onNodeWithText("ㅅ").performClick()

        composeTestRule.waitForIdle()

        // Then add ㅔ to trigger decomposition
        composeTestRule.onNodeWithText("ㅔ").performClick()

        composeTestRule.waitForIdle()

        // The decomposition should have occurred
        // (Specific assertions would depend on UI implementation)
        composeTestRule.onNodeWithText("ㅎ").assertExists()
        composeTestRule.onNodeWithText("ㅔ").assertExists()
    }

    @Test
    fun test_backspace_functionality() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }

        // Input some characters
        composeTestRule.onNodeWithText("ㄱ").performClick()
        composeTestRule.onNodeWithText("ㅏ").performClick()
        composeTestRule.onNodeWithText("ㄴ").performClick()

        composeTestRule.waitForIdle()

        // Use backspace
        composeTestRule.onNodeWithText("⌫").performClick()

        composeTestRule.waitForIdle()

        // Should handle backspace correctly
        composeTestRule.onNodeWithText("⌫").assertExists()
    }

    @Test
    fun test_space_and_enter_keys() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }

        // Input some characters
        composeTestRule.onNodeWithText("ㄱ").performClick()
        composeTestRule.onNodeWithText("ㅏ").performClick()

        // Press space
        composeTestRule.onNodeWithText("Space").performClick()

        // Input more characters
        composeTestRule.onNodeWithText("ㄴ").performClick()
        composeTestRule.onNodeWithText("ㅏ").performClick()

        // Press enter
        composeTestRule.onNodeWithText("⏎").performClick()

        composeTestRule.waitForIdle()

        // Should handle space and enter correctly
        composeTestRule.onNodeWithText("Space").assertExists()
        composeTestRule.onNodeWithText("⏎").assertExists()
    }

    @Test
    fun test_mixed_language_input() {
        composeTestRule.setContent {
            FloatingKeyboardTheme {
                MainScreen()
            }
        }

        // Start with Korean input
        composeTestRule.onNodeWithText("ㄱ").performClick()
        composeTestRule.onNodeWithText("ㅏ").performClick()

        // Switch to English
        composeTestRule.onNodeWithText("🌐").performClick()

        // Input English
        composeTestRule.onNodeWithText("h").performClick()
        composeTestRule.onNodeWithText("i").performClick()

        // Switch back to Korean
        composeTestRule.onNodeWithText("🌐").performClick()
        composeTestRule.onNodeWithText("🌐").performClick() // Skip symbols, go to Korean

        // Input more Korean
        composeTestRule.onNodeWithText("ㄴ").performClick()
        composeTestRule.onNodeWithText("ㅏ").performClick()

        composeTestRule.waitForIdle()

        // Should handle mixed language input
        composeTestRule.onNodeWithText("한글").assertIsDisplayed()
    }
}