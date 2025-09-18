import com.lee.floatingkeyboard.keyboard.input.SimpleKoreanComposer

fun main() {
    println("=== 현재 구현 상세 분석 ===")

    val composer = SimpleKoreanComposer()

    println("단계별 입력 분석:")

    // 1. ㄷ 입력
    val r1 = composer.addJamo('ㄷ')
    println("1. ㄷ 입력:")
    println("   current: '${r1.currentComposition}', composing: ${r1.isComposing}, finish: '${r1.finishPrevious}'")

    // 2. ㅗ 입력
    val r2 = composer.addJamo('ㅗ')
    println("2. ㅗ 입력:")
    println("   current: '${r2.currentComposition}', composing: ${r2.isComposing}, finish: '${r2.finishPrevious}'")

    // 3. ㄲ 입력 (문제 발생 지점)
    val r3 = composer.addJamo('ㄲ')
    println("3. ㄲ 입력:")
    println("   current: '${r3.currentComposition}', composing: ${r3.isComposing}, finish: '${r3.finishPrevious}'")
    println("   현재 composer 상태:")
    println("   - getCurrentComposition(): '${composer.getCurrentComposition()}'")
    println("   - isComposing(): ${composer.isComposing()}")

    // 4. ㅐ 입력 (분해가 일어나야 함)
    val r4 = composer.addJamo('ㅐ')
    println("4. ㅐ 입력:")
    println("   current: '${r4.currentComposition}', composing: ${r4.isComposing}, finish: '${r4.finishPrevious}'")
    println("   현재 composer 상태:")
    println("   - getCurrentComposition(): '${composer.getCurrentComposition()}'")
    println("   - isComposing(): ${composer.isComposing()}")

    println("\n=== 문제 진단 ===")
    println("3단계 후 상태: ${r3.currentComposition}")
    println("4단계 결과:")
    println("  - 완료된 음절: ${r4.finishPrevious ?: "없음"}")
    println("  - 현재 음절: ${r4.currentComposition ?: "없음"}")

    val actualResult = "${r4.finishPrevious ?: ""}${r4.currentComposition ?: ""}"
    println("실제 결과: '$actualResult'")
    println("예상 결과: '도깨'")
    println("문제: ${if (actualResult == "도깨") "없음" else "ㅐ 입력 시 음절 분해가 제대로 안됨"}")
}