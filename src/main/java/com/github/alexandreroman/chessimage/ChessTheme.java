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

/**
 * Represents a visual theme for a chess board.
 * A chess theme defines the colors used for light and dark squares on the board.
 *
 * <p>This record encapsulates the two colors that define a chess board's appearance.
 * Using Java's record feature, it provides immutable storage of the theme colors
 * along with automatically generated constructors, accessors, equals, hashCode,
 * and toString methods.
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
 * @param lightColor The color for light squares on the chess board
 * @param darkColor  The color for dark squares on the chess board
 */
public record ChessTheme(
        Color lightColor,
        Color darkColor
) {
}
