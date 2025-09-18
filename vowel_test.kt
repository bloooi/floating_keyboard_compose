import com.lee.floatingkeyboard.keyboard.input.SimpleKoreanComposer

fun main() {
    println("=== 모음 연속 입력 테스트 (수정 후) ===")

    val composer = SimpleKoreanComposer()

    // 테스트 1: 모음만 연속 입력 (ㅏㅓㅗㅜ)
    println("\n1. 모음 연속 입력 테스트: ㅏ -> ㅓ -> ㅗ -> ㅜ")

    val result1 = composer.addJamo('ㅏ')
    println("ㅏ: current='${result1.currentComposition}', composing=${result1.isComposing}, finish='${result1.finishPrevious}'")

    val result2 = composer.addJamo('ㅓ')
    println("ㅓ: current='${result2.currentComposition}', composing=${result2.isComposing}, finish='${result2.finishPrevious}'")

    val result3 = composer.addJamo('ㅗ')
    println("ㅗ: current='${result3.currentComposition}', composing=${result3.isComposing}, finish='${result3.finishPrevious}'")

    val result4 = composer.addJamo('ㅜ')
    println("ㅜ: current='${result4.currentComposition}', composing=${result4.isComposing}, finish='${result4.finishPrevious}'")

    // 테스트 2: 복합 모음 생성 후 연속 입력
    println("\n2. 복합 모음 후 연속 입력 테스트: ㅗ -> ㅏ (=ㅘ) -> ㅓ")

    val composer2 = SimpleKoreanComposer()
    val r1 = composer2.addJamo('ㅗ')
    println("ㅗ: current='${r1.currentComposition}', composing=${r1.isComposing}, finish='${r1.finishPrevious}'")

    val r2 = composer2.addJamo('ㅏ')  // ㅘ 생성
    println("ㅏ (ㅘ): current='${r2.currentComposition}', composing=${r2.isComposing}, finish='${r2.finishPrevious}'")

    val r3 = composer2.addJamo('ㅓ')  // 새로운 모음
    println("ㅓ: current='${r3.currentComposition}', composing=${r3.isComposing}, finish='${r3.finishPrevious}'")

    println("\n=== 테스트 완료 ===")
}