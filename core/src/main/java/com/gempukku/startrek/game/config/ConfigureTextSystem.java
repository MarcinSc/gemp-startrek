package com.gempukku.startrek.game.config;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.libgdx.lib.artemis.font.BitmapFontSystem;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.TextSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.html.HtmlTextParser;

import java.util.function.Function;

public class ConfigureTextSystem extends BaseSystem {
    private TextSystem textSystem;
    private BitmapFontSystem bitmapFontSystem;
    private TextureSystem textureSystem;

    @Override
    protected void initialize() {
        Function<String, TextureRegion> imageTextureResolver = new Function<String, TextureRegion>() {
            @Override
            public TextureRegion apply(String s) {
                String[] params = s.split(":", 2);
                return textureSystem.getTextureRegion(params[0], params[1]);
            }
        };
        Function<String, TextureRegion> iconTextureResolver = new Function<String, TextureRegion>() {
            @Override
            public TextureRegion apply(String s) {
                return textureSystem.getTextureRegion("atlas/icons.atlas", s);
            }
        };
        HtmlTextParser htmlParser = new HtmlTextParser(
                new Function<String, BitmapFont>() {
                    @Override
                    public BitmapFont apply(String s) {
                        return bitmapFontSystem.getBitmapFont(s);
                    }
                },
                imageTextureResolver, '[', ']', '\\', '/');
        htmlParser.addTagHandler("icon", new IconTagHandler("ditherTexture", iconTextureResolver));
        htmlParser.addTagHandler("b", new BoldTagHandler());

        IconTagReplaceTextParser parser = new IconTagReplaceTextParser(htmlParser);

        textSystem.setDefaultSpriteBatchName("ditherText");
        textSystem.setTextParser(parser);
    }

    @Override
    protected void processSystem() {

    }
}
