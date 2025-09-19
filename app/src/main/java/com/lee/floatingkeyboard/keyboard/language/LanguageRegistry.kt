package com.lee.floatingkeyboard.keyboard.language

import com.lee.floatingkeyboard.keyboard.language.providers.EnglishLanguageProvider
import com.lee.floatingkeyboard.keyboard.language.providers.KoreanLanguageProvider
import com.lee.floatingkeyboard.keyboard.language.providers.SymbolLanguageProvider

/**
 * 사용 가능한 언어들을 관리하는 레지스트리
 */
object LanguageRegistry {
    private val providers = mutableMapOf<String, LanguageProvider>()
    private var defaultProvider: LanguageProvider? = null
    
    init {
        // 기본 언어들 등록
        val koreanProvider = KoreanLanguageProvider()
        val englishProvider = EnglishLanguageProvider()
        val symbolProvider = SymbolLanguageProvider()
        
        registerLanguage(koreanProvider)
        registerLanguage(englishProvider)
        registerLanguage(symbolProvider)
        
        // 첫 번째 등록된 언어를 기본값으로 설정 (한글)
        setDefaultProvider(koreanProvider)
    }
    
    /**
     * 새로운 언어 제공자를 등록
     */
    fun registerLanguage(provider: LanguageProvider) {
        providers[provider.id] = provider
    }
    
    /**
     * 등록된 언어 제공자를 제거
     */
    fun unregisterLanguage(languageId: String) {
        val removedProvider = providers.remove(languageId)
        
        // 기본 제공자가 제거된 경우 첫 번째 등록된 제공자로 변경
        if (defaultProvider == removedProvider) {
            defaultProvider = providers.values.firstOrNull()
        }
    }
    
    /**
     * 특정 언어 제공자를 가져오기
     */
    fun getProvider(languageId: String): LanguageProvider? {
        return providers[languageId]
    }
    
    /**
     * 모든 등록된 언어 제공자 목록 반환
     */
    fun getAllProviders(): List<LanguageProvider> {
        return providers.values.toList()
    }
    
    /**
     * 등록된 언어 ID 목록 반환
     */
    fun getLanguageIds(): List<String> {
        return providers.keys.toList()
    }
    
    /**
     * 기본 언어 제공자를 설정
     */
    fun setDefaultProvider(provider: LanguageProvider) {
        if (providers.containsValue(provider)) {
            defaultProvider = provider
        } else {
            throw IllegalArgumentException("Provider must be registered first")
        }
    }
    
    /**
     * 기본 언어 제공자 반환
     */
    fun getDefaultProvider(): LanguageProvider {
        return defaultProvider ?: providers.values.first()
    }
}