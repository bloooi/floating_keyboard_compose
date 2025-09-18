import com.lee.floatingkeyboard.keyboard.input.SimpleKoreanComposer

fun main() {
    println("=== 도깨비불 입력 테스트 (수정된 구현) ===")

    val composer = SimpleKoreanComposer()

    println("입력 순서: ㄷ -> ㅗ -> ㄲ -> ㅐ")

    // ㄷ 입력
    val r1 = composer.addJamo('ㄷ')
    println("ㄷ: current='${r1.currentComposition}', finish='${r1.finishPrevious}'")

    // ㅗ 입력
    val r2 = composer.addJamo('ㅗ')
    println("ㅗ: current='${r2.currentComposition}', finish='${r2.finishPrevious}'")

    // ㄲ 입력 (임시로 종성 처리)
    val r3 = composer.addJamo('ㄲ')
    println("ㄲ: current='${r3.currentComposition}', finish='${r3.finishPrevious}'")

    // ㅐ 입력 (음절 분해 발생)
    val r4 = composer.addJamo('ㅐ')
    println("ㅐ: current='${r4.currentComposition}', finish='${r4.finishPrevious}'")

    println("\n수정된 결과:")
    println("3단계 후: ${r3.currentComposition} (임시로 받침 처리)")
    println("4단계 후: ${r4.finishPrevious ?: ""}${r4.currentComposition ?: ""}")
    println("기대 결과: 도깨")
    println("결과 분석: ${if (r4.finishPrevious == "도" && r4.currentComposition == "깨") "✓ 성공!" else "✗ 실패"}")

    println("\n=== 전체 단어 테스트: 도깨비불 ===")
    val fullComposer = SimpleKoreanComposer()
    val results = mutableListOf<String>()

    // 도깨
    fullComposer.addJamo('ㄷ')
    fullComposer.addJamo('ㅗ')
    fullComposer.addJamo('ㄲ')
    val dokae = fullComposer.addJamo('ㅐ')
    if (dokae.finishPrevious != null) results.add(dokae.finishPrevious)

    // 비
    fullComposer.addJamo('ㅂ')
    val bi = fullComposer.addJamo('ㅣ')
    if (bi.finishPrevious != null) results.add(bi.finishPrevious)

    // 불
    fullComposer.addJamo('ㅂ')
    fullComposer.addJamo('ㅜ')
    val bul = fullComposer.addJamo('ㄹ')
    if (bul.finishPrevious != null) results.add(bul.finishPrevious)

    // 마지막 음절
    val final = fullComposer.complete()
    if (final.currentComposition != null) results.add(final.currentComposition)

    val fullWord = results.joinToString("")
    println("입력된 전체 단어: $fullWord")
    println("예상 결과: 도깨비불")
    println("테스트 결과: ${if (fullWord == "도깨비불") "✓ 성공!" else "✗ 실패"}")
}