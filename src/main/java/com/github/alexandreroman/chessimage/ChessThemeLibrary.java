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
 * A library of predefined chess themes.
 *
 * <p>This utility class provides a collection of commonly used chess board color themes.
 * Each theme consists of a pair of colors for light and dark squares.
 *
 * <p>The class is designed to be non-instantiable with a private constructor,
 * as it only serves as a container for static theme constants.
 *
 * <p>Example usage:
 * <pre>
 * // Use the predefined green theme
 * ChessRenderer renderer = new ChessRenderer(ChessThemeLibrary.GREEN_THEME);
 * </pre>
 */
public final class ChessThemeLibrary {
    /**
     * A classic brown chess theme.
     * Light squares use a beige color (#efdab7) and dark squares use a brown color (#b48766).
     */
    public static final ChessTheme BROWN_THEME = new ChessTheme(
            Color.decode("#efdab7"),
            Color.decode("#b48766")
    );

    /**
     * A green chess theme commonly used in tournaments.
     * Light squares use an off-white color (#ebecd0) and dark squares use a forest green color (#739552).
     */
    public static final ChessTheme GREEN_THEME = new ChessTheme(
            Color.decode("#ebecd0"),
            Color.decode("#739552")
    );

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ChessThemeLibrary() {
    }
}
