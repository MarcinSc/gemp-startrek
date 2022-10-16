package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable;

import org.junit.Before;
import org.junit.Test;

public class TagTextParserTest {
    private SystemOutTagTextHandler handler = new SystemOutTagTextHandler();
    private TagTextParser parser;

    @Before
    public void init() {
        parser = new TagTextParser('<', '>', '\\');
    }

    @Test
    public void justText() {
        parser.parseTaggedText("Text", handler);
    }

    @Test
    public void justTag() {
        parser.parseTaggedText("<tag>", handler);
    }

    @Test
    public void textAndTag() {
        parser.parseTaggedText("foo<bar>foo", handler);
    }

    @Test
    public void escapeTag() {
        parser.parseTaggedText("foo\\<bar", handler);
    }

    @Test
    public void escapeInsideOfTag() {
        parser.parseTaggedText("<foo\\<bar>", handler);
    }
}