package com.gempukku.startrek.game.config;

import com.gempukku.libgdx.lib.graph.artemis.text.parser.CharacterParsedText;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.CharacterTextParser;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;
import com.gempukku.startrek.card.CardIcon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IconTagReplaceTextParser implements CharacterTextParser {
    private Pattern tagPattern = Pattern.compile("\\[([^]]+)]");
    private CharacterTextParser delegate;

    public IconTagReplaceTextParser(CharacterTextParser delegate) {
        this.delegate = delegate;
    }

    @Override
    public CharacterParsedText parseText(TextStyle defaultTextStyle, String text) {
        Matcher matcher = tagPattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        int consumed = 0;
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                sb.append(text, consumed, matcher.start(i));
                String textInTag = matcher.group(i);
                try {
                    CardIcon icon = CardIcon.valueOf(textInTag);
                    sb.append("icon " + textInTag);
                } catch (IllegalArgumentException exp) {
                    sb.append(textInTag);
                }
                consumed = matcher.end(i);
            }
        }
        sb.append(text, consumed, text.length());
        return delegate.parseText(defaultTextStyle, sb.toString());
    }
}
