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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChessRendererTests {
    private static String md5(String s) {
        try {
            final var md = MessageDigest.getInstance("MD5");
            return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to create MD5 from: " + s, e);
        }
    }

    private static File createFile(String name) {
        final File f;
        final var targetDir = new File("target");
        if (targetDir.exists() && targetDir.isDirectory()) {
            final var parent = new File(targetDir, "generated-test-files");
            parent.mkdirs();
            f = new File(parent, name);
        } else {
            f = new File(name);
        }
        return f;
    }

    @BeforeAll
    public static void beforeAll() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void testDefaults() {
        new ChessRenderer();
    }

    @Test
    void testTheme() {
        new ChessRenderer(ChessThemeLibrary.BROWN_THEME);
    }

    @Test
    void testInvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ChessRenderer(ChessThemeLibrary.BROWN_THEME, -1);
        });
    }

    @Test
    void testInvalidTheme() {
        assertThrows(NullPointerException.class, () -> {
            new ChessRenderer(null, 80);
        });
    }

    @Test
    void testRender() throws IOException {
        final var fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        final var out = new ByteArrayOutputStream();
        new ChessRenderer().render(fen, out);
        assertThat(out.size()).isGreaterThan(0);
        assertThat(ImageIO.read(new ByteArrayInputStream(out.toByteArray()))).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "8/8/8/8/8/8/8/8 w - - 0 1",
            "8/8/8/4p3/3P4/8/8/8 w - - 0 1"
    })
    void testRenderToImage(String fen) throws IOException {
        final Map<String, ChessTheme> themes = Map.of(
                "green", ChessThemeLibrary.GREEN_THEME,
                "brown", ChessThemeLibrary.BROWN_THEME
        );
        for (final var e : themes.entrySet()) {
            try (final var out = new FileOutputStream(createFile("%s-%s.png".formatted(md5(fen), e.getKey())))) {
                new ChessRenderer(e.getValue(), 40).render(fen, out);
            }
        }
    }

    @Test
    void testHighlightSquare() throws IOException {
        final var fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        new ChessRenderer().render(fen, new FileOutputStream(createFile("highlight.png")), sq -> {
            if (sq.col() == 7 && sq.row() == 7) {
                return Optional.of(Color.YELLOW);
            }
            if (sq.col() == 4 && sq.row() == 7) {
                return Optional.of(Color.RED);
            }
            return Optional.empty();
        });
    }

    @Test
    void testNoHighlightedSquares() throws IOException {
        final var fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        new ChessRenderer().render(fen, new FileOutputStream(createFile("no-highlight.png")), sq -> Optional.empty());
    }
}
