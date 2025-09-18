import com.lee.floatingkeyboard.keyboard.input.SimpleKoreanComposer

fun main() {
    println("=== 쌍자음 입력 데모 ===")

    val composer = SimpleKoreanComposer()

    // 데모 1: ㄲ + ㅏ = 까
    println("\n1. ㄲ + ㅏ = 까")
    val result1 = composer.addJamo('ㄲ')
    println("ㄲ 입력: '${result1.currentComposition}', composing=${result1.isComposing}")

    val result2 = composer.addJamo('ㅏ')
    println("ㅏ 추가: '${result2.currentComposition}', composing=${result2.isComposing}")

    // 새로운 composer로 데모 2: 쌍자음 연속 입력
    println("\n2. 쌍자음 연속 입력: ㄸ -> ㅃ")
    val composer2 = SimpleKoreanComposer()

    val r1 = composer2.addJamo('ㄸ')
    println("ㄸ 입력: '${r1.currentComposition}', finish='${r1.finishPrevious}'")

    val r2 = composer2.addJamo('ㅃ')
    println("ㅃ 입력: '${r2.currentComposition}', finish='${r2.finishPrevious}'")

    // 데모 3: 모든 쌍자음 테스트
    println("\n3. 모든 쌍자음으로 음절 생성:")
    val doubleConsonants = listOf('ㄲ', 'ㄸ', 'ㅃ', 'ㅆ', 'ㅉ')

    doubleConsonants.forEach { consonant ->
        val testComposer = SimpleKoreanComposer()
        testComposer.addJamo(consonant)
        val syllableResult = testComposer.addJamo('ㅏ')
        println("$consonant + ㅏ = ${syllableResult.currentComposition}")
    }

    // 데모 4: 쌍자음으로 복잡한 음절
    println("\n4. 쌍자음으로 복잡한 음절: ㅆ + ㅓ + ㄴ = 썬")
    val composer3 = SimpleKoreanComposer()
    composer3.addJamo('ㅆ')
    composer3.addJamo('ㅓ')
    val complexResult = composer3.addJamo('ㄴ')
    println("ㅆ + ㅓ + ㄴ = ${complexResult.currentComposition}")

    println("\n=== 데모 완료 ===")
}