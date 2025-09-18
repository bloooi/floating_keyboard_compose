# 플로팅 키보드 테스트 가이드

이 프로젝트에는 한글 입력 기능에 대한 포괄적인 테스트 코드가 포함되어 있습니다.

## 테스트 구조

### 1. 단위 테스트 (Unit Tests)
위치: `app/src/test/java/`

#### KoreanIMEComposerTest.kt
- **목적**: 한글 입력 엔진의 핵심 로직 테스트
- **테스트 범위**:
  - 단순 자음/모음 입력
  - 음절 조합 (ㄱ+ㅏ=가, ㄱ+ㅏ+ㄴ=간)
  - 연속 자음 입력 방지 (ㅇㄹㅊㅋㄹㅅ에서 ㄹㅅ 조합 방지)
  - 음절 분해 (안녕핫+ㅔ → 안녕하세)
  - 복합 자모 조합 (ㅗ+ㅏ=ㅘ, ㄱ+ㅅ=ㄳ)
  - 백스페이스 기능
  - Composition 상태 관리


#### PerformanceTest.kt
- **목적**: 성능 및 메모리 사용량 테스트
- **테스트 범위**:
  - 단일 자모 입력 성능 (< 1ms)
  - 음절 조합 성능 (< 5ms)
  - 복잡한 시퀀스 성능 (< 20ms)
  - 메모리 사용량 모니터링
  - 동시 처리 성능
  - 스트레스 테스트 (> 1000 chars/sec)

### 2. UI 테스트 (Instrumented Tests)
위치: `app/src/androidTest/java/`

#### FloatingKeyboardTest.kt
- **목적**: 플로팅 키보드 UI 컴포넌트 테스트
- **테스트 범위**:
  - 키보드 표시/숨김
  - 언어 전환 (한글 ↔ English ↔ 123)
  - 키 입력 이벤트
  - 드래그 핸들 기능
  - 특수키 동작 (Space, Backspace, Enter)

#### HangulInputIntegrationTest.kt
- **목적**: 전체 한글 입력 플로우 통합 테스트
- **테스트 범위**:
  - 완전한 한글 입력 시나리오
  - 언어 전환 기능
  - 키보드 드래그 기능
  - 연속 자음 입력 시나리오
  - 음절 분해 시나리오
  - 혼합 언어 입력

## 주요 테스트 시나리오

### 1. 연속 자음 입력 방지
```kotlin
// 문제 상황: ㅇㄹㅊㅋㄹㅅ → ㄹㅅ가 ㄼ로 조합되면 안됨
@Test
fun `test consecutive consonants should not combine`() {
    composer.addJamo('ㅇ')
    composer.addJamo('ㄹ')
    composer.addJamo('ㅊ')
    composer.addJamo('ㅋ')
    composer.addJamo('ㄹ')
    val result = composer.addJamo('ㅅ')

    // ㄼ 조합이 없어야 함
    assertFalse(result.text?.contains('ㄼ') == true)
}
```

### 2. 음절 분해 로직
```kotlin
// 시나리오: 안녕핫 + ㅔ → 안녕하세
@Test
fun `test syllable decomposition`() {
    // 핫: ㅎ + ㅏ + ㅅ
    composer.addJamo('ㅎ')
    composer.addJamo('ㅏ')
    composer.addJamo('ㅅ')

    // ㅔ 추가로 분해 유발
    val result = composer.addJamo('ㅔ')

    assertTrue(result.text?.contains("하세") == true)
}
```


## 테스트 실행 방법

### Android Studio에서 실행
1. 프로젝트를 Android Studio에서 열기
2. 테스트 파일을 우클릭하여 "Run Tests" 선택
3. 또는 특정 테스트 메서드를 개별 실행

### 명령줄에서 실행
```bash
# 단위 테스트 컴파일
./gradlew compileDebugUnitTestKotlin

# 모든 테스트 실행 (설정에 따라 다를 수 있음)
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "*.KoreanIMEComposerTest"

# UI 테스트 실행 (에뮬레이터 또는 실제 기기 필요)
./gradlew connectedAndroidTest
```

## 성능 기준

### 응답 시간 기준
- 단일 자모 입력: < 1ms
- 음절 조합: < 5ms
- 복잡한 시퀀스: < 20ms
- 백스페이스: < 2ms
- 음절 분해: < 10ms

### 처리량 기준
- 스트레스 테스트: > 1000 chars/sec
- 메모리 사용량: < 1MB 증가 (1000회 조합 후)

## 테스트 결과 검증

각 테스트는 다음을 검증합니다:

1. **기능 정확성**: 한글 입력 규칙이 정확히 구현되었는지
2. **성능**: 실시간 입력에 적합한 응답 속도인지
3. **메모리 효율성**: 메모리 누수가 없는지
4. **UI 일관성**: 사용자 인터페이스가 예상대로 동작하는지
5. **Edge Case 처리**: 예외 상황이 안전하게 처리되는지

## 추가 테스트 시나리오

실제 사용 환경에서 다음 시나리오도 수동으로 테스트할 것을 권장합니다:

1. **긴 텍스트 입력**: 여러 문단의 한글 텍스트 입력
2. **언어 전환**: 한글-영어 혼용 텍스트 입력
3. **드래그 동작**: 키보드를 화면 모든 위치로 드래그
4. **회전**: 화면 회전 시 키보드 동작
5. **멀티터치**: 빠른 연속 터치 입력

이 테스트 코드들은 한글 입력 기능의 안정성과 정확성을 보장하기 위해 작성되었습니다.