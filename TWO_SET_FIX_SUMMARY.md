# 두벌식 한글 입력 수정 완료

## 문제 상황
- '도깨비불' 입력 시 '돾비불'이 됨
- ㄷ ㅗ ㄲ ㅐ 순서 입력 시 '돾'이 아닌 '도깨'가 되어야 함
- 받침이 있는 상태에서 모음이 오면 즉시 음절 분해가 되지 않음

## 원인 분석
`SimpleKoreanComposer.kt`의 `handleJungseong` 함수에서:
- 받침이 있는 상태(`jongseong != null`)에서 모음이 입력되어도
- 먼저 기존 모음과의 조합을 시도하고 있었음
- 조합 실패 시에만 음절 분해 로직으로 넘어가는 구조

## 수정 내용

### Before (문제가 있던 코드):
```kotlin
choseong != null && jungseong != null -> {
    // Try to combine with existing vowel
    val combined = combineJamo(jungseong!!, vowel)
    if (combined != null) {
        jungseong = combined
        return CompositionResult(getCurrentSyllable(), true)
    } else {
        // Check if we need to decompose last syllable
        return handleSyllableDecomposition(vowel)
    }
}
```

### After (수정된 코드):
```kotlin
choseong != null && jungseong != null -> {
    // If jongseong exists, decompose syllable immediately (two-set behavior)
    if (jongseong != null) {
        return handleSyllableDecomposition(vowel)
    }

    // If no jongseong, try to combine with existing vowel
    val combined = combineJamo(jungseong!!, vowel)
    if (combined != null) {
        jungseong = combined
        return CompositionResult(getCurrentSyllable(), true)
    } else {
        // Cannot combine, complete current and start new vowel
        val completed = getCurrentSyllable()
        choseong = null
        jungseong = vowel
        jongseong = null
        return CompositionResult(getCurrentSyllable(), true, finishPrevious = completed)
    }
}
```

## 핵심 변경점

1. **우선순위 변경**: 받침이 있으면 즉시 음절 분해
2. **두벌식 동작**: 받침 + 모음 → 무조건 음절 분해
3. **모음 조합**: 받침이 없을 때만 기존 모음과 조합 시도

## 테스트 결과

### 도깨비불 입력 테스트:
```
입력: ㄷ ㅗ ㄲ ㅐ ㅂ ㅣ ㅂ ㅜ ㄹ
결과: 도 + 깨 + 비 + 불
✓ 성공: '도깨비불'
```

### 기존 기능 유지:
- ✅ 받침 유지 (한글 → 한 + 글)
- ✅ 모음 조합 (ㅗ + ㅏ = ㅘ)
- ✅ 쌍자음 입력
- ✅ 모음 연속 입력

## 표준 두벌식 규칙 준수

1. **자음+모음+자음+모음**: 받침을 다음 음절 초성으로 이동
2. **자음+모음+자음+자음**: 받침 유지하고 새 음절 시작
3. **모음 조합**: 받침이 없을 때만 시도
4. **즉시 분해**: 받침 상태에서 모음 입력 시 바로 분해

이제 표준 한국어 키보드와 동일한 자연스러운 입력이 가능합니다.