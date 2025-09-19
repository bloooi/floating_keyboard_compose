package com.lee.floatingkeyboard.keyboard.language

import com.lee.floatingkeyboard.keyboard.core.KeyboardLayout
import com.lee.floatingkeyboard.keyboard.input.TextComposer

/**
 * 언어별 키보드 기능을 제공하는 인터페이스
 */
interface LanguageProvider {
    /**
     * 언어 고유 식별자
     */
    val id: String
    
    /**
     * 사용자에게 표시할 언어 이름
     */
    val displayName: String
    
    /**
     * 해당 언어의 텍스트 조합기를 생성
     */
    fun createComposer(): TextComposer
    
    /**
     * 기본 키보드 레이아웃을 반환
     */
    fun getLayout(): KeyboardLayout
    
    /**
     * Shift 상태의 키보드 레이아웃을 반환
     */
    fun getShiftLayout(): KeyboardLayout = getLayout()
    
    /**
     * 심볼 키보드 레이아웃을 반환 (옵션)
     */
    fun getSymbolLayout(): KeyboardLayout? = null
    
    /**
     * 숫자 키보드 레이아웃을 반환 (옵션)
     */
    fun getNumberLayout(): KeyboardLayout? = null
}