package com.lee.floatingkeyboard.keyboard.input

import org.junit.Test
import org.junit.Assert.*

class DoubleConsonantTest {

    @Test
    fun `test double consonant input`() {
        val composer = SimpleKoreanComposer()

        // Test ㄲ (gg)
        val result1 = composer.addJamo('ㄲ')
        assertEquals("ㄲ", result1.currentComposition)
        assertTrue(result1.isComposing)
        assertNull(result1.finishPrevious)

        // Add vowel to make 까
        val result2 = composer.addJamo('ㅏ')
        assertEquals("까", result2.currentComposition)
        assertTrue(result2.isComposing)
        assertNull(result2.finishPrevious)
    }

    @Test
    fun `test all double consonants`() {
        val doubleConsonants = listOf('ㄲ', 'ㄸ', 'ㅃ', 'ㅆ', 'ㅉ')
        val expectedSyllables = listOf("까", "따", "빠", "싸", "짜")

        doubleConsonants.forEachIndexed { index, consonant ->
            val composer = SimpleKoreanComposer()

            // Test consonant input
            val result1 = composer.addJamo(consonant)
            assertEquals(consonant.toString(), result1.currentComposition)
            assertTrue(result1.isComposing)

            // Add ㅏ to create syllable
            val result2 = composer.addJamo('ㅏ')
            assertEquals(expectedSyllables[index], result2.currentComposition)
            assertTrue(result2.isComposing)
        }
    }

    @Test
    fun `test double consonant with final consonant`() {
        val composer = SimpleKoreanComposer()

        // Input: ㄲ + ㅏ + ㄴ = 깐
        composer.addJamo('ㄲ')
        composer.addJamo('ㅏ')
        val result = composer.addJamo('ㄴ')

        assertEquals("깐", result.currentComposition)
        assertTrue(result.isComposing)
        assertNull(result.finishPrevious)
    }

    @Test
    fun `test double consonant sequence completion`() {
        val composer = SimpleKoreanComposer()

        // Input: ㅆ + ㅓ = 써
        composer.addJamo('ㅆ')
        composer.addJamo('ㅓ')

        // Complete the syllable
        val result = composer.complete()
        assertEquals("써", result.currentComposition)
        assertFalse(result.isComposing)
    }

    @Test
    fun `test double consonant with vowel decomposition`() {
        val composer = SimpleKoreanComposer()

        // Create 딹 (ㄸ + ㅏ + ㅅ)
        composer.addJamo('ㄸ')
        composer.addJamo('ㅏ')
        composer.addJamo('ㅅ')

        // Add ㅔ to decompose: 따 + 세
        val result = composer.addJamo('ㅔ')
        assertEquals("세", result.currentComposition)
        assertTrue(result.isComposing)
        assertEquals("따", result.finishPrevious)
    }

    @Test
    fun `test double consonant consecutive input`() {
        val composer = SimpleKoreanComposer()

        // Input: ㄲ then ㅃ
        val result1 = composer.addJamo('ㄲ')
        assertEquals("ㄲ", result1.currentComposition)
        assertTrue(result1.isComposing)

        val result2 = composer.addJamo('ㅃ')
        assertEquals("ㅃ", result2.currentComposition)
        assertTrue(result2.isComposing)
        assertEquals("ㄲ", result2.finishPrevious)
    }
}