
package com.blockblastassistant

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.view.ViewGroup

class MainActivity : Activity() {

    private val board = Array(8) { BooleanArray(8) }
    private val boardButtons = Array(8) { arrayOfNulls<Button>(8) }

    private val shapes = listOf(
        Piece(listOf(Cell(0,0))),
        Piece(listOf(Cell(0,0), Cell(0,1))),
        Piece(listOf(Cell(0,0), Cell(1,0))),
        Piece(listOf(Cell(0,0), Cell(0,1), Cell(0,2))),
        Piece(listOf(Cell(0,0), Cell(1,0), Cell(2,0))),
        Piece(listOf(Cell(0,0), Cell(0,1), Cell(1,0), Cell(1,1))),
        Piece(listOf(Cell(0,0), Cell(1,0), Cell(2,0), Cell(2,1))),
        Piece(listOf(Cell(0,0), Cell(0,1), Cell(0,2), Cell(1,1))),
        Piece(listOf(Cell(0,0), Cell(0,1), Cell(0,2), Cell(0,3))),
        Piece(listOf(Cell(0,0), Cell(1,0), Cell(2,0), Cell(3,0))),
        Piece(listOf(Cell(0,0), Cell(0,1), Cell(0,2), Cell(1,0), Cell(1,1), Cell(1,2)))
    )

    private val shapeNames = listOf(
        "Tek kare", "Yatay 2", "Dikey 2",
        "Yatay 3", "Dikey 3", "Kare 2x2",
        "L şekli", "T şekli", "Yatay 4",
        "Dikey 4", "Dikdörtgen 2x3"
    )

    private lateinit var selectors: List<Spinner>
    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 12, 12, 12)
        }

        root.addView(TextView(this).apply {
            text = "Block Blast Assistant — 8×8"
            textSize = 22f
            gravity = Gravity.CENTER
        })

        root.addView(TextView(this).apply {
            text = "Dolu kareleri işaretle. Sonra elindeki 3 parçayı seç."
            textSize = 15f
            setPadding(0, 12, 0, 12)
        })

        val grid = GridLayout(this).apply {
            rowCount = 8
            columnCount = 8
            alignmentMode = GridLayout.ALIGN_BOUNDS
        }

        for (r in 0 until 8) {
            for (c in 0 until 8) {
                val button = Button(this).apply {
                    text = "·"
                    textSize = 15f
                    setPadding(0, 0, 0, 0)
                    minWidth = 0
                    minimumWidth = 0
                    minHeight = 0
                    minimumHeight = 0

                    setOnClickListener {
                        board[r][c] = !board[r][c]
                        updateCell(r, c)
                    }
                }

                val size = (resources.displayMetrics.widthPixels / 8.5).toInt()
                grid.addView(button, GridLayout.LayoutParams().apply {
                    width = size
                    height = size
                    setMargins(1, 1, 1, 1)
                })
                boardButtons[r][c] = button
                updateCell(r, c)
            }
        }

        root.addView(grid)

        root.addView(TextView(this).apply {
            text = "Elindeki parçalar"
            textSize = 18f
            setPadding(0, 12, 0, 4)
        })

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            shapeNames
        ).also {
            it.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }

        selectors = (1..3).map { number ->
            val label = TextView(this).apply {
                text = "Parça $number"
            }
            root.addView(label)

            Spinner(this).apply {
                this.adapter = adapter
                root.addView(this)
            }
        }

        root.addView(Button(this).apply {
            text = "En iyi hamleleri hesapla"
            setOnClickListener { calculate() }
        })

        root.addView(Button(this).apply {
            text = "Tahtayı temizle"
            setOnClickListener {
                for (r in 0 until 8) {
                    for (c in 0 until 8) {
                        board[r][c] = false
                        updateCell(r, c)
                    }
                }
                result.text = ""
            }
        })

        result = TextView(this).apply {
            textSize = 16f
            setPadding(0, 12, 0, 12)
        }
        root.addView(result)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
    }

    private fun updateCell(r: Int, c: Int) {
        boardButtons[r][c]?.apply {
            text = if (board[r][c]) "■" else "·"
            setBackgroundColor(
                if (board[r][c]) Color.rgb(70, 110, 180)
                else Color.LTGRAY
            )
        }
    }

    private fun calculate() {
        try {
            val pieces = selectors.map { shapes[it.selectedItemPosition] }
            val answer = Solver.solve(board, pieces)

            if (answer == null || answer.moves.isEmpty()) {
                result.text = "Yerleştirilebilir hamle bulunamadı."
                return
            }

            result.text = buildString {
                appendLine("Yerleştirilebilen parça: ${answer.piecesPlaced}/3")
                appendLine("Temizlenen satır/sütun sayısı: ${answer.totalLinesCleared}")
                appendLine()

                answer.moves.forEach { move ->
                    appendLine(
                        "Parça ${move.pieceIndex + 1}: " +
                        "satır ${move.row + 1}, sütun ${move.col + 1}"
                    )
                }

                if (answer.piecesPlaced < 3) {
                    appendLine()
                    append("Seçilen parçaların hepsi yerleşemiyor.")
                }
            }
        } catch (e: Exception) {
            result.text = "Hesaplama hatası: ${e.message}"
        }
    }
}