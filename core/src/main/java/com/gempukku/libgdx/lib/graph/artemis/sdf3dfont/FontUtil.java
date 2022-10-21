package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class FontUtil {
    public static float getFontAscent(BitmapFont font) {
        // Don't ask me why that is, but that's how it is...
        return font.getLineHeight() - font.getAscent();
    }

    public static float getFontDescent(BitmapFont font) {
        return font.getAscent();
    }
}
