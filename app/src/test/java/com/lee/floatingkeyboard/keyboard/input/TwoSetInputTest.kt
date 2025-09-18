package com.lee.floatingkeyboard.keyboard.input

import org.junit.Test
import org.junit.Assert.*

class TwoSetInputTest {

    @Test
    fun `test dokkaebibul input - two-set method`() {
        val composer = SimpleKoreanComposer()

        // Input sequence: ㄷ -> ㅗ -> ㄲ -> ㅐ
        // Expected: 도깨 (not 돾)

        // ㄷ input
        val r1 = composer.addJamo('ㄷ')
        assertEquals("ㄷ", r1.currentComposition)
        assertTrue(r1.isComposing)
        assertNull(r1.finishPrevious)

        // ㅗ input - forms 도
        val r2 = composer.addJamo('ㅗ')
        assertEquals("도", r2.currentComposition)
        assertTrue(r2.isComposing)
        assertNull(r2.finishPrevious)

        // ㄲ input - should add as jongseong temporarily
        val r3 = composer.addJamo('ㄲ')
        assertEquals("돾", r3.currentComposition) // Temporarily has jongseong
        assertTrue(r3.isComposing)
        assertNull(r3.finishPrevious)

        // ㅐ input - should decompose immediately: 도 + 깨
        val r4 = composer.addJamo('ㅐ')
        assertEquals("깨", r4.currentComposition)
        assertTrue(r4.isComposing)
        assertEquals("도", r4.finishPrevious) // Previous syllable should be completed
    }

    @Test
    fun `test proper jongseong handling`() {
        val composer = SimpleKoreanComposer()

        // Input: 한 (ㅎ + ㅏ + ㄴ) - should keep jongseong
        composer.addJamo('ㅎ')
        composer.addJamo('ㅏ')
        val result = composer.addJamo('ㄴ')

        assertEquals("한", result.currentComposition)
        assertTrue(result.isComposing)
        assertNull(result.finishPrevious)

        // Complete the syllable
        val completed = composer.complete()
        assertEquals("한", completed.currentComposition)
        assertFalse(completed.isComposing)
    }

    @Test
    fun `test syllable decomposition with vowel`() {
        val composer = SimpleKoreanComposer()

        // Input: 핫 (ㅎ + ㅏ + ㅅ) + ㅔ -> 하세
        composer.addJamo('ㅎ')
        composer.addJamo('ㅏ')
        composer.addJamo('ㅅ')

        // Should be 핫 at this point
        assertEquals("핫", composer.getCurrentComposition())

        // Add ㅔ - should decompose to 하세
        val result = composer.addJamo('ㅔ')
        assertEquals("세", result.currentComposition)
        assertTrue(result.isComposing)
        assertEquals("하", result.finishPrevious)
    }

    @Test
    fun `test two-set vs three-set behavior`() {
        val composer = SimpleKoreanComposer()

        // Two-set behavior test: consonant after CV should decompose when vowel follows
        // Input: ㄱ + ㅏ + ㄴ -> 간 (temporarily)
        composer.addJamo('ㄱ')
        composer.addJamo('ㅏ')
        val temp = composer.addJamo('ㄴ')
        assertEquals("간", temp.currentComposition)

        // Then add vowel: + ㅏ -> should become 가나
        val decomposed = composer.addJamo('ㅏ')
        assertEquals("나", decomposed.currentComposition)
        assertEquals("가", decomposed.finishPrevious)
    }

    @Test
    fun `test complex word like dokkaebibul`() {
        val composer = SimpleKoreanComposer()
        val results = mutableListOf<String>()

        // Input: 도깨비불
        // ㄷㅗ -> 도
        composer.addJamo('ㄷ')
        composer.addJamo('ㅗ')

        // ㄲㅐ -> should become 도 + 깨
        composer.addJamo('ㄲ')
        val r1 = composer.addJamo('ㅐ')
        if (r1.finishPrevious != null) results.add(r1.finishPrevious)

        // ㅂㅣ -> should become 깨 + 비
        composer.addJamo('ㅂ')
        val r2 = composer.addJamo('ㅣ')
        if (r2.finishPrevious != null) results.add(r2.finishPrevious)

        // ㅂㅜㄹ -> should become 비 + 불
        composer.addJamo('ㅂ')
        composer.addJamo('ㅜ')
        val r3 = composer.addJamo('ㄹ')
        if (r3.finishPrevious != null) results.add(r3.finishPrevious)

        // Final syllable
        val final = composer.complete()
        if (final.currentComposition != null) results.add(final.currentComposition)

        val fullWord = results.joinToString("")
        assertEquals("도깨비불", fullWord)
    }
}