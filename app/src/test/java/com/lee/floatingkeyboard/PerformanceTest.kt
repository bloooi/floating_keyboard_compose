package com.lee.floatingkeyboard

import com.lee.floatingkeyboard.keyboard.input.KoreanIMEComposer
import org.junit.Before
import org.junit.Test
import kotlin.system.measureTimeMillis

/**
 * Performance tests for Korean IME functionality
 * Tests response times and memory usage for input operations
 */
class PerformanceTest {

    private lateinit var composer: KoreanIMEComposer

    @Before
    fun setUp() {
        composer = KoreanIMEComposer()
    }

    @Test
    fun `test single jamo input performance`() {
        val iterations = 1000
        val jamoList = listOf('ㄱ', 'ㅏ', 'ㄴ', 'ㅗ', 'ㅜ', 'ㅡ', 'ㅣ', 'ㅂ', 'ㅈ', 'ㄷ')

        val totalTime = measureTimeMillis {
            repeat(iterations) {
                val jamo = jamoList[it % jamoList.size]
                composer.addJamo(jamo)
                composer.complete() // Reset for next iteration
            }
        }

        val averageTime = totalTime.toDouble() / iterations
        println("Average time per jamo input: ${averageTime}ms")

        // Assert reasonable performance (should be under 1ms per operation)
        assert(averageTime < 1.0) { "Jamo input too slow: ${averageTime}ms" }
    }

    @Test
    fun `test syllable composition performance`() {
        val iterations = 500
        val syllableComponents = listOf(
            Triple('ㄱ', 'ㅏ', 'ㄴ'), // 간
            Triple('ㄴ', 'ㅏ', 'ㅁ'), // 남
            Triple('ㄷ', 'ㅏ', 'ㄹ'), // 달
            Triple('ㅁ', 'ㅏ', 'ㄴ'), // 만
            Triple('ㅂ', 'ㅏ', 'ㄱ')  // 박
        )

        val totalTime = measureTimeMillis {
            repeat(iterations) {
                val (cho, jung, jong) = syllableComponents[it % syllableComponents.size]
                composer.addJamo(cho)
                composer.addJamo(jung)
                composer.addJamo(jong)
                composer.complete()
            }
        }

        val averageTime = totalTime.toDouble() / iterations
        println("Average time per syllable composition: ${averageTime}ms")

        // Should complete syllable composition quickly
        assert(averageTime < 5.0) { "Syllable composition too slow: ${averageTime}ms" }
    }

    @Test
    fun `test complex composition performance`() {
        val iterations = 100
        val complexSequences = listOf(
            "ㅇㅏㄴㄴㅕㅇㅎㅏㅅㅔㅇㅛ", // 안녕하세요
            "ㄱㅏㅁㅅㅏㅎㅏㅁㄴㅣㄷㅏ", // 감사합니다
            "ㅎㅏㄴㄱㅜㄱㅇㅓ"          // 한국어
        )

        val totalTime = measureTimeMillis {
            repeat(iterations) {
                val sequence = complexSequences[it % complexSequences.size]
                sequence.forEach { jamo ->
                    composer.addJamo(jamo)
                }
                composer.complete()
            }
        }

        val averageTime = totalTime.toDouble() / iterations
        println("Average time per complex sequence: ${averageTime}ms")

        // Complex sequences should still be fast
        assert(averageTime < 20.0) { "Complex composition too slow: ${averageTime}ms" }
    }

    @Test
    fun `test backspace performance`() {
        val iterations = 1000

        val totalTime = measureTimeMillis {
            repeat(iterations) {
                // Build a syllable
                composer.addJamo('ㄱ')
                composer.addJamo('ㅏ')
                composer.addJamo('ㄴ')

                // Backspace three times
                composer.backspace()
                composer.backspace()
                composer.backspace()
            }
        }

        val averageTime = totalTime.toDouble() / iterations
        println("Average time per backspace operation: ${averageTime}ms")

        // Backspace should be very fast
        assert(averageTime < 2.0) { "Backspace too slow: ${averageTime}ms" }
    }

    @Test
    fun `test syllable decomposition performance`() {
        val iterations = 200

        val totalTime = measureTimeMillis {
            repeat(iterations) {
                // Build 핫 (hot)
                composer.addJamo('ㅎ')
                composer.addJamo('ㅏ')
                composer.addJamo('ㅅ')

                // Add ㅔ to trigger decomposition to 하세
                composer.addJamo('ㅔ')
                composer.complete()
            }
        }

        val averageTime = totalTime.toDouble() / iterations
        println("Average time per syllable decomposition: ${averageTime}ms")

        // Decomposition should be reasonably fast
        assert(averageTime < 10.0) { "Syllable decomposition too slow: ${averageTime}ms" }
    }

    @Test
    fun `test memory usage during composition`() {
        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()

        // Perform many composition operations
        repeat(1000) {
            composer.addJamo('ㄱ')
            composer.addJamo('ㅏ')
            composer.addJamo('ㄴ')
            composer.complete()
        }

        runtime.gc() // Suggest garbage collection
        Thread.sleep(100) // Give GC time to run

        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryIncrease = finalMemory - initialMemory

        println("Memory increase after 1000 compositions: ${memoryIncrease / 1024}KB")

        // Memory usage should not increase significantly
        assert(memoryIncrease < 1024 * 1024) { "Memory usage too high: ${memoryIncrease / 1024}KB" }
    }

    @Test
    fun `test concurrent composition performance`() {
        val composers = (1..5).map { KoreanIMEComposer() }
        val iterations = 200

        val totalTime = measureTimeMillis {
            repeat(iterations) { iteration ->
                composers.forEach { composer ->
                    val jamo = when (iteration % 3) {
                        0 -> 'ㄱ'
                        1 -> 'ㅏ'
                        else -> 'ㄴ'
                    }
                    composer.addJamo(jamo)
                }

                // Complete all compositions
                composers.forEach { it.complete() }
            }
        }

        val averageTime = totalTime.toDouble() / iterations
        println("Average time per concurrent composition batch: ${averageTime}ms")

        // Concurrent operations should scale well
        assert(averageTime < 15.0) { "Concurrent composition too slow: ${averageTime}ms" }
    }

    @Test
    fun `test stress test with rapid input`() {
        val rapidInputSequence = "ㄱㅏㄴㅏㄷㅏㄹㅏㅁㅏㅂㅏㅅㅏㅇㅏㅈㅏㅊㅏㅋㅏㅌㅏㅍㅏㅎㅏ"
        val iterations = 50

        val totalTime = measureTimeMillis {
            repeat(iterations) {
                rapidInputSequence.forEach { jamo ->
                    composer.addJamo(jamo)
                }
                composer.complete()
            }
        }

        val averageTime = totalTime.toDouble() / iterations
        val totalChars = rapidInputSequence.length * iterations
        val charsPerSecond = (totalChars * 1000.0) / totalTime

        println("Characters per second in stress test: $charsPerSecond")
        println("Average sequence completion time: ${averageTime}ms")

        // Should handle rapid input well
        assert(charsPerSecond > 1000) { "Stress test performance too low: $charsPerSecond chars/sec" }
    }

    @Test
    fun `test edge case performance`() {
        val edgeCases = listOf(
            '\u0000', // Null character
            'a',       // English character
            '1',       // Number
            '!',       // Symbol
            'ㄱ',      // Valid jamo
        )

        val iterations = 1000

        val totalTime = measureTimeMillis {
            repeat(iterations) {
                edgeCases.forEach { char ->
                    composer.addJamo(char)
                }
                composer.complete()
            }
        }

        val averageTime = totalTime.toDouble() / iterations
        println("Average time per edge case batch: ${averageTime}ms")

        // Edge cases should be handled efficiently
        assert(averageTime < 5.0) { "Edge case handling too slow: ${averageTime}ms" }
    }
}