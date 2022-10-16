package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultTextParserTest {
    @Test
    public void testEmptyText() {
        DefaultTextParser parser = new DefaultTextParser();
        ParsedText parsedText = parser.parseText(null, "");
        assertEquals(-1, parsedText.getNextUnbreakableChunkLength(0));
    }

    @Test
    public void testOneWordText() {
        DefaultTextParser parser = new DefaultTextParser();
        ParsedText parsedText = parser.parseText(null, "foo");
        assertEquals(3, parsedText.getNextUnbreakableChunkLength(0));
        assertEquals(2, parsedText.getNextUnbreakableChunkLength(1));
        assertEquals(1, parsedText.getNextUnbreakableChunkLength(2));
        assertEquals(-1, parsedText.getNextUnbreakableChunkLength(3));
    }

    @Test
    public void testTwoWordText() {
        DefaultTextParser parser = new DefaultTextParser();
        ParsedText parsedText = parser.parseText(null, "foo bar");
        assertEquals(4, parsedText.getNextUnbreakableChunkLength(0));
        assertEquals(3, parsedText.getNextUnbreakableChunkLength(1));
        assertEquals(2, parsedText.getNextUnbreakableChunkLength(2));
        assertEquals(1, parsedText.getNextUnbreakableChunkLength(3));
        assertEquals(3, parsedText.getNextUnbreakableChunkLength(4));
        assertEquals(2, parsedText.getNextUnbreakableChunkLength(5));
        assertEquals(1, parsedText.getNextUnbreakableChunkLength(6));
        assertEquals(-1, parsedText.getNextUnbreakableChunkLength(7));
    }

    @Test
    public void testTwoLineText() {
        DefaultTextParser parser = new DefaultTextParser();
        ParsedText parsedText = parser.parseText(null, "foo\nbar");
        assertEquals(4, parsedText.getNextUnbreakableChunkLength(0));
        assertEquals(3, parsedText.getNextUnbreakableChunkLength(1));
        assertEquals(2, parsedText.getNextUnbreakableChunkLength(2));
        assertEquals(1, parsedText.getNextUnbreakableChunkLength(3));
        assertEquals(3, parsedText.getNextUnbreakableChunkLength(4));
        assertEquals(2, parsedText.getNextUnbreakableChunkLength(5));
        assertEquals(1, parsedText.getNextUnbreakableChunkLength(6));
        assertEquals(-1, parsedText.getNextUnbreakableChunkLength(7));
    }
}