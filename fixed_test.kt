import com.lee.floatingkeyboard.keyboard.input.SimpleKoreanComposer

fun main() {
    println("=== 수정된 두벌식 로직 테스트 ===")

    // 테스트 1: 도깨 입력
    println("\n1. 도깨 입력 테스트 (ㄷ ㅗ ㄲ ㅐ)")
    val composer1 = SimpleKoreanComposer()

    val r1 = composer1.addJamo('ㄷ')
    println("ㄷ: '${r1.currentComposition}' (${r1.isComposing})")

    val r2 = composer1.addJamo('ㅗ')
    println("ㅗ: '${r2.currentComposition}' (${r2.isComposing})")

    val r3 = composer1.addJamo('ㄲ')
    println("ㄲ: '${r3.currentComposition}' (${r3.isComposing})")

    val r4 = composer1.addJamo('ㅐ')
    println("ㅐ: current='${r4.currentComposition}', finish='${r4.finishPrevious}'")

    val result1 = "${r4.finishPrevious ?: ""}${r4.currentComposition ?: ""}"
    println("결과: '$result1' (예상: '도깨') ${if (result1 == "도깨") "✓" else "✗"}")

    // 테스트 2: 전체 도깨비불
    println("\n2. 도깨비불 전체 테스트")
    val fullComposer = SimpleKoreanComposer()
    val syllables = mutableListOf<String>()

    // 도깨
    fullComposer.addJamo('ㄷ')
    fullComposer.addJamo('ㅗ')
    fullComposer.addJamo('ㄲ')
    val dokae = fullComposer.addJamo('ㅐ')
    if (dokae.finishPrevious != null) syllables.add(dokae.finishPrevious)

    // 비
    fullComposer.addJamo('ㅂ')
    val bi = fullComposer.addJamo('ㅣ')
    if (bi.finishPrevious != null) syllables.add(bi.finishPrevious)

    // 불
    fullComposer.addJamo('ㅂ')
    fullComposer.addJamo('ㅜ')
    val bul = fullComposer.addJamo('ㄹ')
    if (bul.finishPrevious != null) syllables.add(bul.finishPrevious)

    // 마지막 음절 완료
    val final = fullComposer.complete()
    if (final.currentComposition != null) syllables.add(final.currentComposition)

    val fullWord = syllables.joinToString("")
    println("입력된 음절들: ${syllables.joinToString(" + ")}")
    println("전체 결과: '$fullWord'")
    println("예상 결과: '도깨비불'")
    println("테스트: ${if (fullWord == "도깨비불") "✓ 성공!" else "✗ 실패"}")

    // 테스트 3: 받침이 유지되는 경우
    println("\n3. 받침 유지 테스트: '한글'")
    val composer3 = SimpleKoreanComposer()

    composer3.addJamo('ㅎ')
    composer3.addJamo('ㅏ')
    val han = composer3.addJamo('ㄴ')
    println("한: '${han.currentComposition}'")

    composer3.addJamo('ㄱ')
    val gul = composer3.addJamo('ㅡ')
    println("글 시작: finish='${gul.finishPrevious}', current='${gul.currentComposition}'")

    val final3 = composer3.addJamo('ㄹ')
    val completed = composer3.complete()

    val hangeul = "${gul.finishPrevious ?: ""}${completed.currentComposition ?: ""}"
    println("한글 결과: '$hangeul' ${if (hangeul == "한글") "✓" else "✗"}")
}