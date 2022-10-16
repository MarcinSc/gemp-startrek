package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class TagTextParser {
    private char startTagCharacter;
    private char endTagCharacter;
    private char escapeCharacter;

    public TagTextParser(char startTagCharacter, char endTagCharacter, char escapeCharacter) {
        this.startTagCharacter = startTagCharacter;
        this.endTagCharacter = endTagCharacter;
        this.escapeCharacter = escapeCharacter;
    }

    public void parseTaggedText(String text, TagTextHandler handler) {
        StringBuilder resultBuilding = new StringBuilder();

        boolean insideTag = false;
        boolean escaping = false;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (escaping) {
                resultBuilding.append(c);
                escaping = false;
            } else {
                if (c == startTagCharacter) {
                    if (insideTag) {
                        throw new GdxRuntimeException("Start of new tag inside a tag");
                    } else {
                        if (resultBuilding.length() > 0) {
                            handler.processText(resultBuilding.toString());
                            resultBuilding.setLength(0);
                        }
                        insideTag = true;
                    }
                } else if (c == endTagCharacter) {
                    if (!insideTag) {
                        throw new GdxRuntimeException("End of tag outside of a tag");
                    } else {
                        handler.processTag(resultBuilding.toString());
                        resultBuilding.setLength(0);
                        insideTag = false;
                    }
                } else if (c == escapeCharacter) {
                    escaping = true;
                } else {
                    resultBuilding.append(c);
                }
            }
        }
        if (insideTag)
            throw new GdxRuntimeException("Tag not closed");
        if (escaping)
            throw new GdxRuntimeException("Escape sequence not completed");

        if (resultBuilding.length() > 0)
            handler.processText(resultBuilding.toString());
    }
}
