package com.lee.floatingkeyboard.keyboard.core

import android.graphics.drawable.Drawable

data class Key(
    val codes: IntArray = intArrayOf(),
    val label: CharSequence? = null,
    val icon: Drawable? = null,
    val x: Int = 0,
    val y: Int = 0,
    val width: Int = 0,
    val height: Int = 0,
    val gap: Int = 0,
    val sticky: Boolean = false,
    val modifier: Boolean = false,
    val repeatable: Boolean = false,
    val popupCharacters: CharSequence? = null,
    val popupResId: Int = 0,
    val edgeFlags: Int = 0
) {
    companion object {
        const val EDGE_LEFT = 0x01
        const val EDGE_RIGHT = 0x02
        const val EDGE_TOP = 0x04
        const val EDGE_BOTTOM = 0x08

        // Special key codes
        const val KEYCODE_SHIFT = -1
        const val KEYCODE_MODE_CHANGE = -2
        const val KEYCODE_CANCEL = -3
        const val KEYCODE_DONE = -4
        const val KEYCODE_DELETE = -5
        const val KEYCODE_ALT = -6
        const val KEYCODE_SPACE = 32
        const val KEYCODE_ENTER = 10
    }

    fun isInside(x: Int, y: Int): Boolean {
        val leftEdge = this.x
        val rightEdge = this.x + width
        val topEdge = this.y
        val bottomEdge = this.y + height
        return x >= leftEdge && x < rightEdge && y >= topEdge && y < bottomEdge
    }

    fun squaredDistanceFrom(x: Int, y: Int): Int {
        val xDist = this.x + width / 2 - x
        val yDist = this.y + height / 2 - y
        return xDist * xDist + yDist * yDist
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Key

        if (!codes.contentEquals(other.codes)) return false
        if (label != other.label) return false
        if (icon != other.icon) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = codes.contentHashCode()
        result = 31 * result + (label?.hashCode() ?: 0)
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + x
        result = 31 * result + y
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}