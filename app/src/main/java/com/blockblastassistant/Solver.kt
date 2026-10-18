
package com.blockblastassistant

data class Cell(val row: Int, val col: Int)
data class Piece(val cells: List<Cell>)
data class Move(
    val pieceIndex: Int,
    val row: Int,
    val col: Int,
    val linesCleared: Int
)
data class SolverResult(
    val moves: List<Move>,
    val piecesPlaced: Int,
    val totalLinesCleared: Int
)

object Solver {
    private const val SIZE = 8

    fun solve(
        board: Array<BooleanArray>,
        pieces: List<Piece>
    ): SolverResult? {
        require(board.size == SIZE && board.all { it.size == SIZE })
        require(pieces.size == 3)

        var bestMoves = emptyList<Move>()
        var bestPlaced = -1
        var bestCleared = -1

        fun search(
            grid: Array<BooleanArray>,
            used: BooleanArray,
            moves: List<Move>,
            cleared: Int
        ) {
            if (
                moves.size > bestPlaced ||
                (moves.size == bestPlaced && cleared > bestCleared)
            ) {
                bestPlaced = moves.size
                bestCleared = cleared
                bestMoves = moves
            }

            for (i in pieces.indices) {
                if (used[i]) continue
                val piece = pieces[i]
                val maxR = piece.cells.maxOf { it.row }
                val maxC = piece.cells.maxOf { it.col }

                for (r in 0 until SIZE - maxR) {
                    for (c in 0 until SIZE - maxC) {
                        if (piece.cells.any {
                            grid[r + it.row][c + it.col]
                        }) continue

                        val next = Array(SIZE) { grid[it].clone() }
                        for (cell in piece.cells) {
                            next[r + cell.row][c + cell.col] = true
                        }

                        val rows = (0 until SIZE).filter {
                            next[it].all { cell -> cell }
                        }
                        val cols = (0 until SIZE).filter { col ->
                            (0 until SIZE).all { row -> next[row][col] }
                        }

                        for (row in rows) {
                            for (col in 0 until SIZE) next[row][col] = false
                        }
                        for (col in cols) {
                            for (row in 0 until SIZE) next[row][col] = false
                        }

                        used[i] = true
                        search(
                            next,
                            used,
                            moves + Move(i, r, c, rows.size + cols.size),
                            cleared + rows.size + cols.size
                        )
                        used[i] = false
                    }
                }
            }
        }

        search(
            Array(SIZE) { board[it].clone() },
            BooleanArray(pieces.size),
            emptyList(),
            0
        )

        if (bestPlaced < 0) return null
        return SolverResult(bestMoves, bestPlaced, bestCleared)
    }
}