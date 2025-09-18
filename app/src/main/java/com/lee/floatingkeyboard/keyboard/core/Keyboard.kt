package com.lee.floatingkeyboard.keyboard.core

import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.Xml
import androidx.core.content.ContextCompat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class Keyboard {
    companion object {
        private const val TAG = "Keyboard"

        const val KEYCODE_SHIFT = -1
        const val KEYCODE_MODE_CHANGE = -2
        const val KEYCODE_CANCEL = -3
        const val KEYCODE_DONE = -4
        const val KEYCODE_DELETE = -5
        const val KEYCODE_ALT = -6

        const val EDGE_LEFT = 0x01
        const val EDGE_RIGHT = 0x02
        const val EDGE_TOP = 0x04
        const val EDGE_BOTTOM = 0x08
    }

    var keys: MutableList<Key> = mutableListOf()
        private set

    var rows: MutableList<Row> = mutableListOf()
        private set

    var totalHeight: Int = 0
        private set

    var totalWidth: Int = 0
        private set

    var displayWidth: Int = 0
        private set

    var displayHeight: Int = 0
        private set

    private var defaultWidth: Int = 0
    private var defaultHeight: Int = 0
    private var defaultHorizontalGap: Int = 0
    private var defaultVerticalGap: Int = 0
    private var proximityThreshold: Int = 0

    constructor()

    constructor(context: Context, xmlLayoutResId: Int) {
        displayWidth = context.resources.displayMetrics.widthPixels
        displayHeight = context.resources.displayMetrics.heightPixels

        defaultWidth = displayWidth / 10
        defaultHeight = 50
        defaultHorizontalGap = 0
        defaultVerticalGap = 0
        proximityThreshold = (defaultWidth * 1.4f).toInt()

        loadKeyboard(context, context.resources.getXml(xmlLayoutResId))
    }

    constructor(context: Context, layoutTemplateResId: Int, characters: CharSequence,
                columns: Int, horizontalPadding: Int) {
        displayWidth = context.resources.displayMetrics.widthPixels
        displayHeight = context.resources.displayMetrics.heightPixels

        defaultWidth = (displayWidth - 2 * horizontalPadding) / columns
        defaultHeight = 50
        defaultHorizontalGap = 0
        defaultVerticalGap = 0

        createKeyboardFromCharacters(characters, columns)
    }

    private fun createKeyboardFromCharacters(characters: CharSequence, columns: Int) {
        var x = 0
        var y = 0
        var column = 0

        totalHeight = 0
        val row = Row(defaultWidth, defaultHeight, defaultHorizontalGap, defaultVerticalGap)

        for (i in characters.indices) {
            val c = characters[i]
            if (column >= columns) {
                column = 0
                x = 0
                y += defaultHeight + defaultVerticalGap
                totalHeight = y + defaultHeight
                rows.add(row)
                row.keys.clear()
            }

            val key = Key(
                codes = intArrayOf(c.code),
                label = c.toString(),
                x = x,
                y = y,
                width = defaultWidth,
                height = defaultHeight
            )

            keys.add(key)
            row.keys.add(key)
            x += defaultWidth + defaultHorizontalGap
            column++
        }

        totalWidth = displayWidth
        if (row.keys.isNotEmpty()) {
            rows.add(row)
        }
    }

    private fun loadKeyboard(context: Context, parser: XmlResourceParser) {
        var inKey = false
        var inRow = false
        var leftMostKey = false
        var rightMostKey = false
        var topRow = false
        var bottomRow = false

        var x = 0
        var y = 0
        var row: Row? = null
        val res = context.resources

        try {
            var event = parser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    val tag = parser.name
                    when (tag) {
                        "Keyboard" -> {
                            parseKeyboardAttributes(parser, res)
                        }
                        "Row" -> {
                            inRow = true
                            x = 0
                            row = parseRowAttributes(parser, res)
                            rows.add(row)
                            bottomRow = rows.size == 1
                            topRow = true
                        }
                        "Key" -> {
                            inKey = true
                            val key = parseKeyAttributes(parser, res, row!!, x, y)

                            val updatedKey = when {
                                leftMostKey -> key.copy(edgeFlags = key.edgeFlags or EDGE_LEFT)
                                rightMostKey -> key.copy(edgeFlags = key.edgeFlags or EDGE_RIGHT)
                                topRow -> key.copy(edgeFlags = key.edgeFlags or EDGE_TOP)
                                bottomRow -> key.copy(edgeFlags = key.edgeFlags or EDGE_BOTTOM)
                                else -> key
                            }

                            keys.add(updatedKey)
                            row.keys.add(updatedKey)
                        }
                    }
                } else if (event == XmlPullParser.END_TAG) {
                    when (parser.name) {
                        "Key" -> {
                            inKey = false
                            x += keys.lastOrNull()?.width ?: 0
                            x += keys.lastOrNull()?.gap ?: 0
                            if (x > totalWidth) {
                                totalWidth = x
                            }
                            rightMostKey = true
                            leftMostKey = false
                        }
                        "Row" -> {
                            inRow = false
                            y += row?.defaultHeight ?: defaultHeight
                            y += row?.verticalGap ?: defaultVerticalGap
                            topRow = false
                            rightMostKey = false
                            leftMostKey = true
                        }
                    }
                }
                event = parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: $e")
            e.printStackTrace()
        }
        totalHeight = y - (rows.lastOrNull()?.verticalGap ?: defaultVerticalGap)
    }

    private fun parseKeyboardAttributes(parser: XmlResourceParser, res: Resources) {
        val a = res.obtainAttributes(Xml.asAttributeSet(parser),
            intArrayOf(
                android.R.attr.keyWidth,
                android.R.attr.keyHeight,
                android.R.attr.horizontalGap,
                android.R.attr.verticalGap
            ))

        defaultWidth = getDimensionOrFraction(a, 0, displayWidth, displayWidth / 10)
        defaultHeight = getDimensionOrFraction(a, 1, displayHeight, 50)
        defaultHorizontalGap = getDimensionOrFraction(a, 2, displayWidth, 0)
        defaultVerticalGap = getDimensionOrFraction(a, 3, displayHeight, 0)

        a.recycle()
    }

    private fun parseRowAttributes(parser: XmlResourceParser, res: Resources): Row {
        val a = res.obtainAttributes(Xml.asAttributeSet(parser),
            intArrayOf(
                android.R.attr.keyWidth,
                android.R.attr.keyHeight,
                android.R.attr.horizontalGap,
                android.R.attr.verticalGap
            ))

        val rowDefaultWidth = getDimensionOrFraction(a, 0, displayWidth, defaultWidth)
        val rowDefaultHeight = getDimensionOrFraction(a, 1, displayHeight, defaultHeight)
        val rowDefaultHorizontalGap = getDimensionOrFraction(a, 2, displayWidth, defaultHorizontalGap)
        val rowVerticalGap = getDimensionOrFraction(a, 3, displayHeight, defaultVerticalGap)

        a.recycle()

        return Row(rowDefaultWidth, rowDefaultHeight, rowDefaultHorizontalGap, rowVerticalGap)
    }

    private fun parseKeyAttributes(parser: XmlResourceParser, res: Resources,
                                   row: Row, x: Int, y: Int): Key {
        val a = res.obtainAttributes(Xml.asAttributeSet(parser),
            intArrayOf(
                android.R.attr.keyWidth,
                android.R.attr.keyHeight,
                android.R.attr.horizontalGap,
                android.R.attr.codes,
                android.R.attr.keyLabel,
                android.R.attr.keyIcon,
                android.R.attr.isModifier,
                android.R.attr.isSticky,
                android.R.attr.isRepeatable
            ))

        val width = getDimensionOrFraction(a, 0, displayWidth, row.defaultWidth)
        val height = getDimensionOrFraction(a, 1, displayHeight, row.defaultHeight)
        val gap = getDimensionOrFraction(a, 2, displayWidth, row.defaultHorizontalGap)

        val codesValue = a.getText(3)
        val codes = if (codesValue != null) {
            parseCSV(codesValue.toString()).map { it.toInt() }.toIntArray()
        } else {
            intArrayOf()
        }

        val label = a.getText(4)
        val iconResId = a.getResourceId(5, 0)
        val icon: Drawable? = null // Icon support can be added later
        val isModifier = a.getBoolean(6, false)
        val isSticky = a.getBoolean(7, false)
        val isRepeatable = a.getBoolean(8, false)

        a.recycle()

        return Key(
            codes = codes,
            label = label,
            icon = icon,
            x = x,
            y = y,
            width = width,
            height = height,
            gap = gap,
            sticky = isSticky,
            modifier = isModifier,
            repeatable = isRepeatable
        )
    }

    private fun getDimensionOrFraction(a: android.content.res.TypedArray, index: Int,
                                       base: Int, defaultValue: Int): Int {
        val value = a.peekValue(index) ?: return defaultValue

        return if (value.type == android.util.TypedValue.TYPE_DIMENSION) {
            a.getDimensionPixelOffset(index, defaultValue)
        } else {
            Math.round(a.getFraction(index, base, base, defaultValue.toFloat()))
        }
    }

    private fun parseCSV(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    fun getNearestKeys(x: Int, y: Int): IntArray {
        val nearby = mutableListOf<Int>()

        keys.forEachIndexed { index, key ->
            if (key.squaredDistanceFrom(x, y) < proximityThreshold) {
                nearby.add(index)
            }
        }

        return nearby.toIntArray()
    }

}