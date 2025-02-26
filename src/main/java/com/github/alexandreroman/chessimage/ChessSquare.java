/*
 * Copyright (c) 2025 Broadcom, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.alexandreroman.chessimage;

/**
 * Represents a square on a chess board.
 * This record encapsulates the row and column coordinates of a square.
 *
 * <p>In this implementation, rows and columns are zero-indexed, where:
 * <ul>
 *   <li>row 0, column 0 corresponds to the top-left square (A8 in standard chess notation)</li>
 *   <li>row 7, column 7 corresponds to the bottom-right square (H1 in standard chess notation)</li>
 * </ul>
 *
 * @param row The row index (0-7, from top to bottom)
 * @param col The column index (0-7, from left to right)
 */
public record ChessSquare(int row, int col) {
}
