import com.lee.floatingkeyboard.keyboard.input.SimpleKoreanComposer

fun main() {
    println("=== SimpleKoreanComposer Debug Test ===")

    val composer = SimpleKoreanComposer()

    // Test 1: Simple consonant
    println("\n1. Testing single consonant 'ㄱ':")
    val result1 = composer.addJamo('ㄱ')
    println("Result: currentComposition='${result1.currentComposition}', isComposing=${result1.isComposing}, finishPrevious='${result1.finishPrevious}'")

    // Test 2: Add vowel to make syllable
    println("\n2. Adding vowel 'ㅏ' to make '가':")
    val result2 = composer.addJamo('ㅏ')
    println("Result: currentComposition='${result2.currentComposition}', isComposing=${result2.isComposing}, finishPrevious='${result2.finishPrevious}'")

    // Test 3: Complete the syllable
    println("\n3. Completing syllable:")
    val result3 = composer.complete()
    println("Result: currentComposition='${result3.currentComposition}', isComposing=${result3.isComposing}, finishPrevious='${result3.finishPrevious}'")

    // Test 4: Test consecutive consonants
    println("\n4. Testing consecutive consonants 'ㅇ', 'ㄹ':")
    val result4a = composer.addJamo('ㅇ')
    println("ㅇ Result: currentComposition='${result4a.currentComposition}', isComposing=${result4a.isComposing}, finishPrevious='${result4a.finishPrevious}'")

    val result4b = composer.addJamo('ㄹ')
    println("ㄹ Result: currentComposition='${result4b.currentComposition}', isComposing=${result4b.isComposing}, finishPrevious='${result4b.finishPrevious}'")

    // Test 5: Backspace
    println("\n5. Testing backspace:")
    val result5 = composer.backspace()
    println("Backspace Result: currentComposition='${result5.currentComposition}', isComposing=${result5.isComposing}, finishPrevious='${result5.finishPrevious}'")

    println("\n=== Test Complete ===")
}