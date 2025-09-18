package com.lee.floatingkeyboard.keyboard.core

data class Row(
    val defaultWidth: Int,
    val defaultHeight: Int,
    val defaultHorizontalGap: Int,
    val verticalGap: Int,
    val keys: MutableList<Key> = mutableListOf(),
    val mode: Int = 0
) {
    companion object {
        const val EDGE_LEFT = 0x01
        const val EDGE_RIGHT = 0x02
        const val EDGE_TOP = 0x04
        const val EDGE_BOTTOM = 0x08
    }
}