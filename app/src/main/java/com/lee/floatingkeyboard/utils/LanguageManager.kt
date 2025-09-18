package com.lee.floatingkeyboard.utils

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

enum class KeyboardLanguage {
    ENGLISH,
    KOREAN,
    SYMBOLS
}

class LanguageManager {
    private val _currentLanguage = mutableStateOf(KeyboardLanguage.ENGLISH)
    val currentLanguage: State<KeyboardLanguage> = _currentLanguage

    fun switchToNext() {
        _currentLanguage.value = when (_currentLanguage.value) {
            KeyboardLanguage.ENGLISH -> KeyboardLanguage.KOREAN
            KeyboardLanguage.KOREAN -> KeyboardLanguage.ENGLISH
            KeyboardLanguage.SYMBOLS -> KeyboardLanguage.ENGLISH
        }
    }

    fun switchToSymbols() {
        _currentLanguage.value = KeyboardLanguage.SYMBOLS
    }

    fun switchToLanguage(language: KeyboardLanguage) {
        _currentLanguage.value = language
    }
}