package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.DefaultTextParser;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.ParsedText;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultGlyphOffseterTest {
    private static DefaultGlyphOffseter glyphOffseter;
    private static DefaultTextParser parser;
    private static TextStyle defaultStyle;
    private static BitmapFont bitmapFont;

    @BeforeClass
    public static void init() {
        Gdx.files = new HeadlessFiles();
        Gdx.graphics = new MockGraphics();
        glyphOffseter = new DefaultGlyphOffseter();
        parser = new DefaultTextParser();

        bitmapFont = new BitmapFont();

        defaultStyle = new TextStyle();
        defaultStyle.getAttributes().put(TextStyleConstants.Font, bitmapFont);
    }

    @AfterClass
    public static void dispose() {
        bitmapFont.dispose();
    }

    @Test
    public void testLayoutOfEmptyText() {
        ParsedText parsedText = parser.parseText(defaultStyle, "");
        GlyphOffsetText offsetText = glyphOffseter.offsetText(parsedText, 100, false);
        assertEquals(0, offsetText.getLineCount());
        assertEquals(0f, offsetText.getTextWidth(), 0.0001f);
        assertEquals(0f, offsetText.getTextHeight(), 0.0001f);
    }
}