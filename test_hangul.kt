import com.lee.floatingkeyboard.keyboard.input.HangulComposer

fun main() {
    val composer = HangulComposer()

    println("Testing consecutive consonants issue:")

    // Test case 1: Two consecutive ㄱ
    println("\n1. Testing ㄱ + ㄱ:")
    val result1 = composer.addJamo('ㄱ')
    println("First ㄱ: $result1")
    println("Is composing: ${composer.isComposing()}")

    val result2 = composer.addJamo('ㄱ')
    println("Second ㄱ: $result2")
    println("Is composing: ${composer.isComposing()}")

    // Reset for next test
    composer.complete()

    // Test case 2: ㄱ + ㅏ + ㄱ (proper syllable)
    println("\n2. Testing ㄱ + ㅏ + ㄱ (가):")
    val r1 = composer.addJamo('ㄱ')
    println("ㄱ: $r1")
    val r2 = composer.addJamo('ㅏ')
    println("ㅏ: $r2")
    val r3 = composer.addJamo('ㄱ')
    println("ㄱ: $r3")

    composer.complete()

    // Test case 3: ㄱ + ㅅ (two different consonants)
    println("\n3. Testing ㄱ + ㅅ:")
    val s1 = composer.addJamo('ㄱ')
    println("ㄱ: $s1")
    val s2 = composer.addJamo('ㅅ')
    println("ㅅ: $s2")

    composer.complete()

    // Test case 4: ㅏ + ㅣ (two vowels)
    println("\n4. Testing ㅏ + ㅣ:")
    val v1 = composer.addJamo('ㅏ')
    println("ㅏ: $v1")
    val v2 = composer.addJamo('ㅣ')
    println("ㅣ: $v2")
}