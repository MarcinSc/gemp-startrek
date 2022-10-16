package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;

import java.util.function.Function;

public class ImageTagHandler implements TagHandler {
    private Function<String, TextureRegion> textureRegionResolver;

    public ImageTagHandler(Function<String, TextureRegion> textureRegionResolver) {
        this.textureRegionResolver = textureRegionResolver;
    }

    @Override
    public String startProcessingTag(String tagParameters, Array<TextStyle> textStyleStack) {
        tagParameters = tagParameters.trim();
        String[] params = tagParameters.split(" ", 2);
        String spriteSystemName = params[0];
        TextureRegion texture = textureRegionResolver.apply(params[1]);

        TextStyle lastStyle = textStyleStack.peek();
        TextStyle duplicate = lastStyle.duplicate();
        duplicate.setAttribute(TextStyleConstants.ImageSpriteSystemName, spriteSystemName);
        duplicate.setAttribute(TextStyleConstants.ImageTextureRegion, texture);

        textStyleStack.add(duplicate);

        return "_";
    }

    @Override
    public void endProcessingTag(Array<TextStyle> textStyleStack) {
        textStyleStack.pop();
    }
}
