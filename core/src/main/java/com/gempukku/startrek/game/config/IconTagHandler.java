package com.gempukku.startrek.game.config;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagParsedText;

import java.util.function.Function;

public class IconTagHandler implements TagHandler {
    private String spriteSystemName;
    private Function<String, TextureRegion> textureRegionResolver;

    public IconTagHandler(String spriteSystemName, Function<String, TextureRegion> textureRegionResolver) {
        this.spriteSystemName = spriteSystemName;
        this.textureRegionResolver = textureRegionResolver;
    }

    @Override
    public void processStartTag(String tagParameters, Array<TextStyle> textStyleStack, TagParsedText tagParsedText, StringBuilder resultText) {
        TextureRegion texture = textureRegionResolver.apply(tagParameters.trim());

        TextStyle lastStyle = textStyleStack.peek();
        TextStyle duplicate = lastStyle.duplicate();
        duplicate.setAttribute(TextStyleConstants.ImageSpriteSystemName, spriteSystemName);
        duplicate.setAttribute(TextStyleConstants.ImageTextureRegion, texture);

        textStyleStack.add(duplicate);

        tagParsedText.addStyleIndex(resultText.length(), textStyleStack.peek());
        resultText.append("_");

        textStyleStack.pop();
    }

    @Override
    public void processEndTag(Array<TextStyle> textStyleStack, TagParsedText tagParsedText, StringBuilder resultText) {
        
    }
}
