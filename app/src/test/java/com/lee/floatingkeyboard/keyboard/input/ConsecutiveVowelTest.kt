package com.lee.floatingkeyboard.keyboard.input

import org.junit.Test
import org.junit.Assert.*

class ConsecutiveVowelTest {

    @Test
    fun `test consecutive standalone vowels`() {
        val composer = SimpleKoreanComposer()

        // First vowel: ㅏ
        val result1 = composer.addJamo('ㅏ')
        assertEquals("ㅏ", result1.currentComposition)
        assertTrue(result1.isComposing)
        assertNull(result1.finishPrevious)

        // Second vowel: ㅓ (should complete ㅏ and start ㅓ)
        val result2 = composer.addJamo('ㅓ')
        assertEquals("ㅓ", result2.currentComposition)
        assertTrue(result2.isComposing)
        assertEquals("ㅏ", result2.finishPrevious)

        // Third vowel: ㅗ (should complete ㅓ and start ㅗ)
        val result3 = composer.addJamo('ㅗ')
        assertEquals("ㅗ", result3.currentComposition)
        assertTrue(result3.isComposing)
        assertEquals("ㅓ", result3.finishPrevious)

        // Fourth vowel: ㅜ (should complete ㅗ and start ㅜ)
        val result4 = composer.addJamo('ㅜ')
        assertEquals("ㅜ", result4.currentComposition)
        assertTrue(result4.isComposing)
        assertEquals("ㅗ", result4.finishPrevious)
    }

    @Test
    fun `test vowel combination then consecutive vowel`() {
        val composer = SimpleKoreanComposer()

        // First vowel: ㅗ
        val result1 = composer.addJamo('ㅗ')
        assertEquals("ㅗ", result1.currentComposition)
        assertTrue(result1.isComposing)
        assertNull(result1.finishPrevious)

        // Second vowel: ㅏ (should combine to ㅘ)
        val result2 = composer.addJamo('ㅏ')
        assertEquals("ㅘ", result2.currentComposition)
        assertTrue(result2.isComposing)
        assertNull(result2.finishPrevious)

        // Third vowel: ㅓ (should complete ㅘ and start ㅓ)
        val result3 = composer.addJamo('ㅓ')
        assertEquals("ㅓ", result3.currentComposition)
        assertTrue(result3.isComposing)
        assertEquals("ㅘ", result3.finishPrevious)
    }

    @Test
    fun `test vowel after consonant-vowel syllable`() {
        val composer = SimpleKoreanComposer()

        // Consonant: ㄱ
        val result1 = composer.addJamo('ㄱ')
        assertEquals("ㄱ", result1.currentComposition)
        assertTrue(result1.isComposing)

        // Vowel: ㅏ (forms 가)
        val result2 = composer.addJamo('ㅏ')
        assertEquals("가", result2.currentComposition)
        assertTrue(result2.isComposing)

        // Another vowel: ㅓ (should decompose 가 and start new vowel)
        val result3 = composer.addJamo('ㅓ')
        // Note: 가 has no jongseong, so it should complete 가 and start ㅓ
        assertEquals("ㅓ", result3.currentComposition)
        assertTrue(result3.isComposing)
        assertEquals("가", result3.finishPrevious)
    }

    @Test
    fun `test multiple vowel types consecutively`() {
        val composer = SimpleKoreanComposer()

        // Test sequence: ㅏ -> ㅐ -> ㅑ -> ㅒ -> ㅓ -> ㅔ
        val vowels = listOf('ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ')
        var expectedFinished: String? = null

        vowels.forEachIndexed { index, vowel ->
            val result = composer.addJamo(vowel)

            assertEquals(vowel.toString(), result.currentComposition)
            assertTrue(result.isComposing)

            if (index > 0) {
                assertEquals(expectedFinished, result.finishPrevious)
            } else {
                assertNull(result.finishPrevious)
            }

            expectedFinished = vowel.toString()
        }
    }

    @Test
    fun `test vowel combination limits`() {
        val composer = SimpleKoreanComposer()

        // ㅜ + ㅓ = ㅝ (valid combination)
        composer.addJamo('ㅜ')
        val result1 = composer.addJamo('ㅓ')
        assertEquals("ㅝ", result1.currentComposition)
        assertNull(result1.finishPrevious)

        // ㅝ + ㅏ (invalid combination, should complete ㅝ and start ㅏ)
        val result2 = composer.addJamo('ㅏ')
        assertEquals("ㅏ", result2.currentComposition)
        assertTrue(result2.isComposing)
        assertEquals("ㅝ", result2.finishPrevious)
    }

    @Test
    fun `test vowel after complex consonant`() {
        val composer = SimpleKoreanComposer()

        // Test with complex syllable: ㅎ + ㅏ + ㅅ = 핫
        composer.addJamo('ㅎ')
        composer.addJamo('ㅏ')
        composer.addJamo('ㅅ')

        // Current state: 핫 (완성된 음절)
        assertEquals("핫", composer.getCurrentComposition())

        // Add vowel ㅔ (should decompose: 하 + 세)
        val result = composer.addJamo('ㅔ')
        assertEquals("세", result.currentComposition)
        assertTrue(result.isComposing)
        assertEquals("하", result.finishPrevious)
    }
}