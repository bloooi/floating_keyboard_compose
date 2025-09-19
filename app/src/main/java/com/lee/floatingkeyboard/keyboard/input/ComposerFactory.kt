package com.lee.floatingkeyboard.keyboard.input

import com.lee.floatingkeyboard.utils.KeyboardLanguage

/**
 * 언어별로 적절한 TextComposer를 생성하는 팩토리
 */
object ComposerFactory {
    
    /**
     * 주어진 언어에 맞는 TextComposer 인스턴스를 생성
     */
    fun createComposer(language: KeyboardLanguage): TextComposer {
        return when (language) {
            KeyboardLanguage.KOREAN -> HangulComposer()
            KeyboardLanguage.ENGLISH -> SimpleTextComposer()
            KeyboardLanguage.SYMBOLS -> SimpleTextComposer()
        }
    }
}