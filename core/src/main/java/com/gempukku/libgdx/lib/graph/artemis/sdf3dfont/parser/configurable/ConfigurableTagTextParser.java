package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.*;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.ParsedText;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextParser;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;

import java.lang.StringBuilder;
import java.util.function.Function;

public class ConfigurableTagTextParser implements TextParser {
    private Function<String, BitmapFont> fontResolver;
    private TagTextParser tagTextParser;

    private ObjectMap<String, TagHandler> tagHandlers = new ObjectMap<>();

    public ConfigurableTagTextParser(Function<String, BitmapFont> fontResolver, char startTagCharacter, char endTagCharacter, char escapeCharacter) {
        this.fontResolver = fontResolver;
        tagTextParser = new TagTextParser(startTagCharacter, endTagCharacter, escapeCharacter);
    }

    public void addTagHandler(String tag, TagHandler tagHandler) {
        tagHandlers.put(tag, tagHandler);
    }

    @Override
    public ParsedText parseText(TextStyle defaultTextStyle, String text) {
        Array<TextStyle> textStyleStack = new Array<>();
        textStyleStack.add(defaultTextStyle);

        TagParsedText result = Pools.obtain(TagParsedText.class);

        StringBuilder resultText = new StringBuilder();
        TagTextHandler handler = new TagTextHandler() {
            @Override
            public void processTag(String tag) {
                int tagNameEnd = tag.indexOf(" ");
                if (tagNameEnd < 0)
                    tagNameEnd = tag.length();
                String tagName = tag.substring(0, tagNameEnd);
                TagHandler tagHandler = tagHandlers.get(tagName);
                if (tagHandler == null)
                    throw new GdxRuntimeException("Unable to find tag handler for tag: " + tagName);
                String producedText = tagHandler.startProcessingTag(tag.substring(tagNameEnd), textStyleStack);
                if (producedText != null) {
                    result.addStyleIndex(resultText.length(), textStyleStack.peek());
                    resultText.append(producedText);
                }
                tagHandler.endProcessingTag();
            }

            @Override
            public void processText(String text) {
                result.addStyleIndex(resultText.length(), textStyleStack.peek());
                resultText.append(text);
            }
        };
        tagTextParser.parseTaggedText(text, handler);
        result.setText(resultText.toString());
        return result;
    }

    public static class TagParsedText implements ParsedText, Pool.Poolable {
        private IntArray textStyleStarts = new IntArray();
        private Array<TextStyle> textStyleArray = new Array<>();
        private String text;

        public void setText(String text) {
            this.text = text;
        }

        public void addStyleIndex(int index, TextStyle style) {
            textStyleStarts.add(index);
            textStyleArray.add(style);
        }

        @Override
        public int getNextUnbreakableChunkLength(int startIndex) {
            if (startIndex >= text.length())
                return -1;
            int textLength = text.length();
            for (int i = startIndex; i < textLength; i++) {
                char c = text.charAt(i);
                if (Character.isWhitespace(c))
                    return 1 + i - startIndex;
            }
            return textLength - startIndex;
        }

        @Override
        public TextStyle getTextStyle(int glyphIndex) {
            for (int i = 1; i < textStyleStarts.size; i++) {
                if (textStyleStarts.get(i) > glyphIndex)
                    return textStyleArray.get(i - 1);
            }
            return textStyleArray.get(textStyleArray.size - 1);
        }

        @Override
        public char getCharAt(int glyphIndex) {
            return text.charAt(glyphIndex);
        }

        @Override
        public void dispose() {
            Pools.free(this);
        }

        @Override
        public void reset() {
            textStyleStarts.clear();
            textStyleArray.clear();
            text = null;
        }
    }
}
