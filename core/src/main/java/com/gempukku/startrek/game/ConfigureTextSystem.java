package com.gempukku.startrek.game;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gempukku.libgdx.lib.artemis.font.BitmapFontSystem;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.SDF3DTextSystem;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html.HtmlTextParser;

import java.util.function.Function;

public class ConfigureTextSystem extends BaseSystem {
    private SDF3DTextSystem textSystem;
    private BitmapFontSystem bitmapFontSystem;

    @Override
    protected void initialize() {
        textSystem.setTextParser(new HtmlTextParser(
                new Function<String, BitmapFont>() {
                    @Override
                    public BitmapFont apply(String s) {
                        return bitmapFontSystem.getBitmapFont(s);
                    }
                }, '[', ']', '\\'));
    }

    @Override
    protected void processSystem() {

    }
}
