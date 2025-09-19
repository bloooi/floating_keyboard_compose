package com.lee.floatingkeyboard.keyboard.input

import com.lee.floatingkeyboard.utils.KeyboardLanguage
import com.lee.floatingkeyboard.keyboard.language.LanguageRegistry

/**
 * 언어별로 적절한 TextComposer를 생성하는 팩토리
 * @deprecated LanguageProvider 시스템을 사용하세요
 */
@Deprecated("Use LanguageProvider system instead", ReplaceWith("LanguageRegistry.getProvider(languageId)?.createComposer()"))
object ComposerFactory {
    
    /**
     * 주어진 언어에 맞는 TextComposer 인스턴스를 생성
     * @deprecated LanguageProvider 시스템을 사용하세요
     */
    @Deprecated("Use LanguageProvider system instead")
    fun createComposer(language: KeyboardLanguage): TextComposer {
        val languageId = when (language) {
            KeyboardLanguage.KOREAN -> "korean"
            KeyboardLanguage.ENGLISH -> "english"
            KeyboardLanguage.SYMBOLS -> "symbols"
        }
        return LanguageRegistry.getProvider(languageId)?.createComposer()
            ?: LanguageRegistry.getDefaultProvider().createComposer()
    }
}