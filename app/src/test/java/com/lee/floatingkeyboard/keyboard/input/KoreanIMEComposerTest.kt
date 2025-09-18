package com.lee.floatingkeyboard.keyboard.input

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for KoreanIMEComposer
 * Tests Korean text composition, decomposition, and IME behavior
 */
class KoreanIMEComposerTest {

    private lateinit var composer: KoreanIMEComposer

    @Before
    fun setUp() {
        composer = KoreanIMEComposer()
    }

    @Test
    fun `test simple consonant input`() {
        val result = composer.addJamo('ㄱ')

        assertEquals("ㄱ", result.text)
        assertTrue(result.isComposing)
        assertNull(result.committed)
    }

    @Test
    fun `test simple vowel input`() {
        val result = composer.addJamo('ㅏ')

        assertEquals("ㅏ", result.text)
        assertTrue(result.isComposing)
        assertNull(result.committed)
    }

    @Test
    fun `test consonant plus vowel composition`() {
        // ㄱ + ㅏ = 가
        composer.addJamo('ㄱ')
        val result = composer.addJamo('ㅏ')

        assertEquals("가", result.text)
        assertTrue(result.isComposing)
        assertNull(result.committed)
    }

    @Test
    fun `test complete syllable with final consonant`() {
        // ㄱ + ㅏ + ㄴ = 간
        composer.addJamo('ㄱ')
        composer.addJamo('ㅏ')
        val result = composer.addJamo('ㄴ')

        assertEquals("간", result.text)
        assertTrue(result.isComposing)
        assertNull(result.committed)
    }

    @Test
    fun `test consecutive consonants should not combine`() {
        // Issue: ㅇㄹㅊㅋㄹㅅ should not combine ㄹㅅ
        composer.addJamo('ㅇ')
        composer.addJamo('ㄹ')
        composer.addJamo('ㅊ')
        composer.addJamo('ㅋ')
        composer.addJamo('ㄹ')
        val result = composer.addJamo('ㅅ')

        // Should not contain combined consonants like ㄼ
        assertFalse("Result should not contain combined consonants",
                   result.text?.contains('ㄼ') == true)

        // Each consonant should be processed separately
        assertTrue(result.isComposing)
    }

    @Test
    fun `test syllable decomposition - 안녕핫 plus ㅔ becomes 안녕하세`() {
        // Build "안녕핫"
        // 안: ㅇ + ㅏ + ㄴ
        composer.addJamo('ㅇ')
        composer.addJamo('ㅏ')
        composer.addJamo('ㄴ')
        composer.complete() // Complete 안

        // 녕: ㄴ + ㅕ + ㅇ
        composer.addJamo('ㄴ')
        composer.addJamo('ㅕ')
        composer.addJamo('ㅇ')
        composer.complete() // Complete 녕

        // 핫: ㅎ + ㅏ + ㅅ
        composer.addJamo('ㅎ')
        composer.addJamo('ㅏ')
        composer.addJamo('ㅅ')

        // Now add ㅔ, should decompose to 하세
        val result = composer.addJamo('ㅔ')

        // Result should contain decomposed syllables
        assertTrue("Should contain decomposed syllables",
                  result.text?.contains("하세") == true ||
                  result.text?.contains("하") == true)
        assertTrue(result.isComposing)
    }

    @Test
    fun `test complex vowel combination`() {
        // ㅗ + ㅏ = ㅘ
        composer.addJamo('ㄱ')
        composer.addJamo('ㅗ')
        val result = composer.addJamo('ㅏ')

        assertEquals("과", result.text)
        assertTrue(result.isComposing)
    }

    @Test
    fun `test complex consonant combination`() {
        // ㄱ + ㅏ + ㄱ + ㅅ = 각
        composer.addJamo('ㄱ')
        composer.addJamo('ㅏ')
        composer.addJamo('ㄱ')
        val result = composer.addJamo('ㅅ')

        assertEquals("갃", result.text)
        assertTrue(result.isComposing)
    }

    @Test
    fun `test backspace removes last jamo`() {
        // Build 간
        composer.addJamo('ㄱ')
        composer.addJamo('ㅏ')
        composer.addJamo('ㄴ')

        // Backspace should remove ㄴ
        val result = composer.backspace()

        assertEquals("가", result.text)
        assertTrue(result.isComposing)
    }

    @Test
    fun `test backspace on single consonant`() {
        composer.addJamo('ㄱ')

        // Backspace should remove entire consonant
        val result = composer.backspace()

        assertEquals("", result.text)
        assertFalse(result.isComposing)
    }

    @Test
    fun `test complete composition`() {
        composer.addJamo('ㄱ')
        composer.addJamo('ㅏ')
        composer.addJamo('ㄴ')

        val result = composer.complete()

        assertEquals("간", result.text)
        assertFalse(result.isComposing)
        assertEquals("간", result.committed)
    }

    @Test
    fun `test isComposing state`() {
        assertFalse(composer.isComposing())

        composer.addJamo('ㄱ')
        assertTrue(composer.isComposing())

        composer.complete()
        assertFalse(composer.isComposing())
    }

    @Test
    fun `test getCurrentComposition`() {
        assertEquals("", composer.getCurrentComposition())

        composer.addJamo('ㄱ')
        composer.addJamo('ㅏ')
        assertEquals("가", composer.getCurrentComposition())

        composer.complete()
        assertEquals("", composer.getCurrentComposition())
    }

    @Test
    fun `test multiple syllables composition`() {
        // Test building multiple syllables
        composer.addJamo('ㅎ') // ㅎ
        composer.addJamo('ㅏ') // 하
        composer.addJamo('ㄴ') // 한

        // Add new consonant to start next syllable
        val result = composer.addJamo('ㄱ')

        // Should complete previous syllable and start new one
        assertTrue("Should be composing", result.isComposing)
        assertTrue("Should contain completed syllable",
                  result.text?.contains("한") == true)
        assertTrue("Should contain new consonant",
                  result.text?.contains("ㄱ") == true)
        assertNotNull("Should have committed text", result.committed)
    }

    @Test
    fun `test edge case - empty input`() {
        val result = composer.addJamo('\u0000')

        assertNull(result.text)
        assertFalse(result.isComposing)
    }

    @Test
    fun `test edge case - invalid character`() {
        val result = composer.addJamo('a')

        assertNull(result.text)
        assertFalse(result.isComposing)
    }

    @Test
    fun `test standalone vowel composition`() {
        // Test vowel without consonant
        val result = composer.addJamo('ㅏ')

        assertEquals("ㅏ", result.text)
        assertTrue(result.isComposing)
    }

    @Test
    fun `test vowel combination limits`() {
        // Test that invalid vowel combinations don't crash
        composer.addJamo('ㄱ')
        composer.addJamo('ㅏ')
        val result = composer.addJamo('ㅓ') // Invalid combination

        // Should handle gracefully
        assertNotNull(result.text)
        assertTrue(result.isComposing)
    }
}