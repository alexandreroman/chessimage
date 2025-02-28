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

import java.awt.*;
import java.net.URL;
import java.util.function.Function;

/**
 * Represents a visual theme for a chess board.
 * A chess theme defines the colors used for light and dark squares on the board.
 *
 * <p>Example usage:
 * <pre>
 * // Create a custom blue theme
 * ChessTheme blueTheme = new ChessTheme(
 *     new Color(220, 240, 255),  // Light blue for light squares
 *     new Color(70, 130, 180)    // Steel blue for dark squares
 * );
 * </pre>
 *
 * You can also customize piece images by providing a function to load assets.
 *
 * @param lightColor The color for light squares on the chess board
 * @param darkColor  The color for dark squares on the chess board
 * @param assets     A function to load assets for piece images
 */
public record ChessTheme(
        Color lightColor,
        Color darkColor,
        Function<ChessPiece, URL> assets
) {
    private static final Function<ChessPiece, URL> DEFAULT_ASSETS_LOADER = new Function<ChessPiece, URL>() {
        /*
         * The naming convention for resource files is: assets/[piece letter][color].png
         * where piece letter is r(rook), n(knight), b(bishop), q(queen), k(king), p(pawn)
         * and color is l(light/white) or d(dark/black).
         */
        @Override
        public URL apply(ChessPiece piece) {
            // Determine the character used in the filename based on piece type
            final var fenCharacter = switch (piece.type()) {
                case ROOK -> "r";
                case KNIGHT -> "n";
                case BISHOP -> "b";
                case QUEEN -> "q";
                case KING -> "k";
                case PAWN -> "p";
            };

            // Determine the color suffix used in the filename
            final var resColor = switch (piece.side()) {
                case WHITE -> "l";
                case BLACK -> "d";
            };

            final var resPath = "assets/" + fenCharacter + resColor + ".png";
            return ChessTheme.class.getResource(resPath);
        }
    };

    public ChessTheme(Color lightColor, Color darkColor, Function<ChessPiece, URL> assets) {
        this.lightColor = lightColor;
        this.darkColor = darkColor;
        this.assets = assets;
    }

    public ChessTheme(Color lightColor, Color darkColor) {
        this(lightColor, darkColor, DEFAULT_ASSETS_LOADER);
    }
}
