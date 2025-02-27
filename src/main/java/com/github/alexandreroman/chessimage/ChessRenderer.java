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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * A utility class for rendering chess boards from FEN notation to images.
 * This class supports customizable themes and board sizes.
 *
 * <p>The class provides functionality to:
 * <ul>
 *   <li>Render chess positions from FEN notation to PNG images</li>
 *   <li>Customize board colors using themes</li>
 *   <li>Adjust the size of the chess board</li>
 *   <li>Highlight specific squares on the board</li>
 * </ul>
 *
 * <p>FEN (Forsyth-Edwards Notation) is a standard notation for describing a chess position.
 * A FEN string includes information about piece positions, castling rights, en passant targets,
 * and move counters. This renderer mainly uses the piece positions part.
 *
 * <p>Example usage:
 * <pre>
 * // Create a renderer with default settings
 * ChessRenderer renderer = new ChessRenderer();
 *
 * // Render a position to a file
 * try (OutputStream out = new FileOutputStream("chess.png")) {
 *     renderer.render("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", out);
 * }
 * </pre>
 */
public class ChessRenderer {
    /**
     * The size of each square on the board in pixels
     */
    private final int squareSize;

    /**
     * The visual theme to use for the board
     */
    private final ChessTheme theme;

    /**
     * Cache for piece images to avoid reloading them
     */
    private final Map<Piece, BufferedImage> pieceImages = new HashMap<>(12);

    /**
     * Creates a ChessRenderer with the default green theme and standard square size.
     */
    public ChessRenderer() {
        this(ChessThemeLibrary.GREEN_THEME);
    }

    /**
     * Creates a ChessRenderer with the specified theme and standard square size.
     *
     * @param theme The chess theme to use for rendering
     */
    public ChessRenderer(ChessTheme theme) {
        this(theme, 80);
    }

    /**
     * Creates a ChessRenderer with the specified theme and square size.
     *
     * @param theme      The chess theme to use for rendering
     * @param squareSize The size of each square in pixels
     * @throws NullPointerException     If theme is null
     * @throws IllegalArgumentException If squareSize is less than 1
     */
    public ChessRenderer(ChessTheme theme, int squareSize) {
        requireNonNull(theme, "Chess theme must not be null");
        if (squareSize < 1) {
            throw new IllegalArgumentException("Square size must be greater than 0");
        }
        this.squareSize = squareSize;
        this.theme = theme;
        try {
            loadPieceImages();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize chess renderer", e);
        }
    }

    /**
     * Loads all piece images from resources into memory.
     * The naming convention for resource files is: assets/[piece letter][color].png
     * where piece letter is r(rook), n(knight), b(bishop), q(queen), k(king), p(pawn)
     * and color is l(light/white) or d(dark/black).
     *
     * @throws IOException If any piece image cannot be loaded
     */
    private void loadPieceImages() throws IOException {
        for (final Piece piece : Piece.all()) {
            // Determine the character used in the filename based on piece type
            final var fenCharacter = switch (piece.type) {
                case ROOK -> "r";
                case KNIGHT -> "n";
                case BISHOP -> "b";
                case QUEEN -> "q";
                case KING -> "k";
                case PAWN -> "p";
            };

            // Determine the color suffix used in the filename
            final var resColor = switch (piece.side) {
                case WHITE -> "l";
                case BLACK -> "d";
            };

            final var resPath = "assets/" + fenCharacter + resColor + ".png";
            try (final var resIn = ChessRenderer.class.getResourceAsStream(resPath)) {
                if (resIn == null) {
                    throw new IOException("Failed to load piece image " + piece + " from path: " + resPath);
                }
                final var resImage = ImageIO.read(resIn);
                pieceImages.put(piece, resImage);
            }
        }
    }

    /**
     * Renders a chess position from FEN notation to a PNG image.
     * This method uses the default square highlighting (no highlights).
     *
     * @param fen The chess position in FEN notation
     * @param out The output stream to write the PNG image to
     * @throws IOException          If an I/O error occurs during writing
     * @throws NullPointerException If fen or out is null
     */
    public void render(String fen, OutputStream out) throws IOException {
        render(fen, out, sq -> Optional.empty());
    }

    /**
     * Renders a chess position from FEN notation to a PNG image with optional square highlighting.
     *
     * @param fen               The chess position in FEN notation
     * @param out               The output stream to write the PNG image to
     * @param squareHighlighter A function that returns an optional color for each square,
     *                          which will be used to highlight the square if present
     * @throws IOException          If an I/O error occurs during writing
     * @throws NullPointerException If fen or out is null
     */
    public void render(String fen, OutputStream out, Function<ChessSquare, Optional<Color>> squareHighlighter) throws IOException {
        requireNonNull(out, "Output stream must not be null");

        // Create a new image with dimensions that fit the 8x8 chess board
        final var imageWidth = squareSize * 8;
        final var image = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_ARGB);
        render(fen, image, squareHighlighter);

        // Write the image to the output stream as PNG
        ImageIO.write(image, "PNG", out);
    }

    /**
     * Renders a chess position from FEN notation to an image with optional square highlighting.
     *
     * @param fen               The chess position in FEN notation
     * @param image             The image where to render the board
     * @param squareHighlighter A function that returns an optional color for each square,
     *                          which will be used to highlight the square if present
     * @throws NullPointerException If fen or image is null
     */
    public void render(String fen, BufferedImage image, Function<ChessSquare, Optional<Color>> squareHighlighter) {
        requireNonNull(fen, "FEN must not be null");
        requireNonNull(image, "Image must not be null");

        if (squareHighlighter == null) {
            squareHighlighter = sq -> Optional.empty();
        }

        // Enable anti-aliasing for better visual quality
        final var g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        try {
            // Draw the board, coordinates, and pieces
            drawBoard(g, squareHighlighter);
            drawCoordinates(g);
            drawPiecesFromFEN(g, fen);
        } finally {
            g.dispose();
        }
    }

    /**
     * Renders a chess position from FEN notation to an image.
     *
     * @param fen   The chess position in FEN notation
     * @param image The image where to render the board
     * @throws NullPointerException If fen or image is null
     */
    public void render(String fen, BufferedImage image) {
        render(fen, image, null);
    }

    /**
     * Draws the chess board with alternating light and dark squares.
     * Square colors may be overridden by the squareHighlighter function.
     *
     * @param g                 The graphics context to draw on
     * @param squareHighlighter A function that returns an optional color for each square
     */
    private void drawBoard(Graphics2D g, Function<ChessSquare, Optional<Color>> squareHighlighter) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final var isLightSquare = (row + col) % 2 == 0;
                final var color = squareHighlighter.apply(new ChessSquare(row, col)).orElse(isLightSquare ? theme.lightColor() : theme.darkColor());
                g.setColor(color);
                g.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
            }
        }
    }

    /**
     * Draws the board coordinates (1-8 for rows, A-H for columns).
     * The coordinates are drawn in the corner of each border square
     * with a contrasting color to ensure visibility.
     *
     * @param g The graphics context to draw on
     */
    private void drawCoordinates(Graphics2D g) {
        final var font = new Font("SansSerif", Font.BOLD, squareSize / 6);
        final var fm = g.getFontMetrics(font);
        g.setFont(font);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Use contrasting color for coordinates
                final var isLightSquare = (row + col) % 2 == 0;
                if (isLightSquare) {
                    g.setColor(theme.darkColor());
                } else {
                    g.setColor(theme.lightColor());
                }

                final int x = col * squareSize;
                final int y = row * squareSize;

                // Draw row numbers on the leftmost column
                if (col == 0) {
                    String rowNumber = String.valueOf(8 - row);
                    g.drawString(rowNumber,
                            x + squareSize / 12,
                            y + fm.getAscent() + squareSize / 12);
                }

                // Draw column letters on the bottom row
                if (row == 7) {
                    String colLetter = String.valueOf((char) ('A' + col));
                    g.drawString(colLetter,
                            x + squareSize - fm.stringWidth(colLetter) - squareSize / 12,
                            y + squareSize - squareSize / 12);
                }
            }
        }
    }

    /**
     * Parses a FEN string and draws the pieces on the board.
     * FEN uses the format: each row is separated by '/', numbers represent empty squares,
     * and letters represent pieces (uppercase for white, lowercase for black).
     *
     * @param g   The graphics context to draw on
     * @param fen The chess position in Forsyth-Edwards Notation (FEN)
     */
    private void drawPiecesFromFEN(Graphics2D g, String fen) {
        // Extract just the position part (before the first space)
        final var positionPart = fen.split(" ")[0];
        final var rows = positionPart.split("/");

        for (int row = 0; row < rows.length; row++) {
            final var rowString = rows[row];
            int col = 0;

            for (int i = 0; i < rowString.length(); i++) {
                final char c = rowString.charAt(i);

                if (Character.isDigit(c)) {
                    // Digits represent consecutive empty squares
                    col += Character.getNumericValue(c);
                } else {
                    // Letters represent pieces
                    final var pieceImage = pieceImages.get(Piece.fromFenCharacter(c));
                    if (pieceImage != null) {
                        int x = col * squareSize;
                        int y = row * squareSize;

                        // Calculate size and offset to center the piece in its square
                        final int pieceSize = (int) (squareSize * 0.7);
                        final int offsetX = (squareSize - pieceSize) / 2;
                        final int offsetY = (squareSize - pieceSize) / 2;
                        g.drawImage(pieceImage, x + offsetX, y + offsetY, pieceSize, pieceSize, null);
                    }
                    col++;
                }
            }
        }
    }

    /**
     * Represents the two sides in a chess game.
     */
    private enum Side {
        WHITE, BLACK
    }

    /**
     * Represents the six different types of chess pieces.
     */
    private enum PieceType {
        ROOK, KNIGHT, BISHOP, QUEEN, KING, PAWN
    }

    /**
     * Represents a chess piece with its type and side.
     * Each piece is represented as a record containing a piece type and the side (color) it belongs to.
     */
    private record Piece(
            PieceType type,
            Side side
    ) {
        /**
         * Mapping from FEN characters to piece objects
         */
        private static final Map<Character, Piece> PIECES = new HashMap<>(12);

        // Initialize the mapping of FEN characters to Piece objects
        static {
            PIECES.put('R', new Piece(PieceType.ROOK, Side.WHITE));
            PIECES.put('r', new Piece(PieceType.ROOK, Side.BLACK));
            PIECES.put('N', new Piece(PieceType.KNIGHT, Side.WHITE));
            PIECES.put('n', new Piece(PieceType.KNIGHT, Side.BLACK));
            PIECES.put('B', new Piece(PieceType.BISHOP, Side.WHITE));
            PIECES.put('b', new Piece(PieceType.BISHOP, Side.BLACK));
            PIECES.put('Q', new Piece(PieceType.QUEEN, Side.WHITE));
            PIECES.put('q', new Piece(PieceType.QUEEN, Side.BLACK));
            PIECES.put('K', new Piece(PieceType.KING, Side.WHITE));
            PIECES.put('k', new Piece(PieceType.KING, Side.BLACK));
            PIECES.put('P', new Piece(PieceType.PAWN, Side.WHITE));
            PIECES.put('p', new Piece(PieceType.PAWN, Side.BLACK));
        }

        /**
         * Returns a collection of all possible chess pieces.
         *
         * @return A collection containing all 12 possible pieces (6 types × 2 sides)
         */
        static Collection<Piece> all() {
            return PIECES.values();
        }

        /**
         * Converts a FEN character to the corresponding Piece object.
         *
         * @param p The FEN character representing a piece
         * @return The corresponding Piece object
         * @throws IllegalArgumentException If the character doesn't represent a valid piece
         */
        static Piece fromFenCharacter(char p) {
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
    }
}
