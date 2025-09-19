package com.lee.floatingkeyboard.utils

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.lee.floatingkeyboard.keyboard.language.LanguageProvider
import com.lee.floatingkeyboard.keyboard.language.LanguageRegistry

enum class KeyboardLanguage {
    ENGLISH,
    KOREAN,
    SYMBOLS
}

class LanguageManager {
    private val availableLanguages = LanguageRegistry.getAllProviders()
    private var currentIndex = 0
    
    private val _currentLanguage = mutableStateOf(availableLanguages.getOrNull(currentIndex) ?: LanguageRegistry.getDefaultProvider())
    val currentLanguage: State<LanguageProvider> = _currentLanguage

    fun switchToNext() {
        currentIndex = (currentIndex + 1) % availableLanguages.size
        _currentLanguage.value = availableLanguages[currentIndex]
    }

    fun switchToPrevious() {
        currentIndex = if (currentIndex == 0) availableLanguages.size - 1 else currentIndex - 1
        _currentLanguage.value = availableLanguages[currentIndex]
    }

    fun switchToLanguage(languageId: String) {
        val provider = LanguageRegistry.getProvider(languageId)
        if (provider != null) {
            val index = availableLanguages.indexOf(provider)
            if (index != -1) {
                currentIndex = index
                _currentLanguage.value = provider
            }
        }
    }
    
    fun switchToProvider(provider: LanguageProvider) {
        val index = availableLanguages.indexOf(provider)
        if (index != -1) {
            currentIndex = index
            _currentLanguage.value = provider
        }
    }
}