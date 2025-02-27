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

import java.util.Collection;
import java.util.Map;

/**
 * Represents a chess piece with its type and side.
 * Each piece is represented as a record containing a piece type and the side (color) it belongs to.
 */
public record ChessPiece(
        Type type,
        ChessSide side
) {
    /**
     * Mapping from FEN characters to piece objects
     */
    private static final Map<Character, ChessPiece> PIECES = Map.ofEntries(
            Map.entry('R', new ChessPiece(Type.ROOK, ChessSide.WHITE)),
            Map.entry('r', new ChessPiece(Type.ROOK, ChessSide.BLACK)),
            Map.entry('N', new ChessPiece(Type.KNIGHT, ChessSide.WHITE)),
            Map.entry('n', new ChessPiece(Type.KNIGHT, ChessSide.BLACK)),
            Map.entry('B', new ChessPiece(Type.BISHOP, ChessSide.WHITE)),
            Map.entry('b', new ChessPiece(Type.BISHOP, ChessSide.BLACK)),
            Map.entry('Q', new ChessPiece(Type.QUEEN, ChessSide.WHITE)),
            Map.entry('q', new ChessPiece(Type.QUEEN, ChessSide.BLACK)),
            Map.entry('K', new ChessPiece(Type.KING, ChessSide.WHITE)),
            Map.entry('k', new ChessPiece(Type.KING, ChessSide.BLACK)),
            Map.entry('P', new ChessPiece(Type.PAWN, ChessSide.WHITE)),
            Map.entry('p', new ChessPiece(Type.PAWN, ChessSide.BLACK))
    );

    /**
     * Returns a collection of all possible chess pieces.
     *
     * @return A collection containing all 12 possible pieces (6 types × 2 sides)
     */
    static Collection<ChessPiece> all() {
        return PIECES.values();
    }

    /**
     * Converts a FEN character to the corresponding Piece object.
     *
     * @param p The FEN character representing a piece
     * @return The corresponding Piece object
     * @throws IllegalArgumentException If the character doesn't represent a valid piece
     */
    static ChessPiece fromFenCharacter(char p) {
        final var piece = PIECES.get(p);
        if (piece == null) {
            throw new IllegalArgumentException("Unknown piece: " + p);
        }
        return piece;
    }

    /**
     * Returns a string representation of this piece.
     *
     * @return A string in the format "side type" (e.g., "white queen")
     */
    @Override
    public String toString() {
        return side.name().toLowerCase() + " " + type.name().toLowerCase();
    }

    /**
     * Represents the six different types of chess pieces.
     */
    enum Type {
        ROOK, KNIGHT, BISHOP, QUEEN, KING, PAWN
    }
}
