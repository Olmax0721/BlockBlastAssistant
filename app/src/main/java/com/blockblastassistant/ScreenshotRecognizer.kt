package com.blockblastassistant

import android.graphics.Bitmap
import kotlin.math.max
import kotlin.math.min

object ScreenshotRecognizer {

    fun recognizeBoard(bitmap: Bitmap): Array<BooleanArray> {
        val size = 8
        val result = Array(size) { BooleanArray(size) }

        // İlk deneme için dikey ekran yerleşimi varsayılıyor.
        val boardWidth = (bitmap.width * 0.90f).toInt()
        val boardSize = min(boardWidth, (bitmap.height * 0.50f).toInt())
        val left = (bitmap.width - boardSize) / 2
        val top = (bitmap.height * 0.22f).toInt()

        if (left < 0 || top < 0 ||
            left + boardSize > bitmap.width ||
            top + boardSize > bitmap.height
        ) {
            return result
        }

        val cellSize = boardSize.toFloat() / size

        for (row in 0 until size) {
            for (col in 0 until size) {
                val x = (left + (col + 0.5f) * cellSize).toInt()
                val y = (top + (row + 0.5f) * cellSize).toInt()

                val pixel = bitmap.getPixel(
                    x.coerceIn(0, bitmap.width - 1),
                    y.coerceIn(0, bitmap.height - 1)
                )

                val red = android.graphics.Color.red(pixel)
                val green = android.graphics.Color.green(pixel)
                val blue = android.graphics.Color.blue(pixel)

                val brightest = max(red, max(green, blue))
                val darkest = min(red, min(green, blue))
                val saturationDifference = brightest - darkest

                // Parlak veya renkli merkezleri dolu kare kabul et.
                result[row][col] =
                    (saturationDifference > 35 && brightest > 80) ||
                    (red > 155 && green > 155 && blue > 155)
            }
        }

        return result
    }
}