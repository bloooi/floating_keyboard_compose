import com.lee.floatingkeyboard.keyboard.input.SimpleKoreanComposer

fun main() {
    println("=== 두벌식 한글 입력 데모 ===")

    println("\n1. 기본 동작: 받침이 다음 음절의 초성이 되는 경우")
    testWord("도깨", listOf('ㄷ', 'ㅗ', 'ㄲ', 'ㅐ'))

    println("\n2. 받침이 유지되는 경우")
    testWord("한글", listOf('ㅎ', 'ㅏ', 'ㄴ', 'ㄱ', 'ㅡ', 'ㄹ'))

    println("\n3. 복잡한 단어")
    testWord("도깨비불", listOf('ㄷ', 'ㅗ', 'ㄲ', 'ㅐ', 'ㅂ', 'ㅣ', 'ㅂ', 'ㅜ', 'ㄹ'))

    println("\n4. 음절 분해 예제")
    testWord("핫도그", listOf('ㅎ', 'ㅏ', 'ㅅ', 'ㄷ', 'ㅗ', 'ㄱ', 'ㅡ'))
}

fun testWord(expectedWord: String, jamos: List<Char>) {
    val composer = SimpleKoreanComposer()
    val results = mutableListOf<String>()

    println("입력: ${jamos.joinToString(" ")}")
    println("예상: $expectedWord")

    jamos.forEach { jamo ->
        val result = composer.addJamo(jamo)
        if (result.finishPrevious != null) {
            results.add(result.finishPrevious)
        }
    }

    // 마지막 음절 완료
    val final = composer.complete()
    if (final.currentComposition != null) {
        results.add(final.currentComposition)
    }

    val actualWord = results.joinToString("")
    println("결과: $actualWord")
    println("상태: ${if (actualWord == expectedWord) "✓ 성공" else "✗ 실패"}")

    if (actualWord != expectedWord) {
        println("상세: ${results.joinToString(" + ")}")
    }
}