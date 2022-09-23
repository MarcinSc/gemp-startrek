package com.gempukku.startrek.common;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ObjectMap;

public class FontProviderSystem extends BaseSystem {
    private ObjectMap<String, BitmapFont> fonts = new ObjectMap<>();

    public BitmapFont getFont(String internalPath, int size) {
        String key = internalPath + ":" + size;
        BitmapFont result = fonts.get(key);
        if (result == null) {
            result = createFont(internalPath, size);
            fonts.put(key, result);
        }
        return result;
    }

    private BitmapFont createFont(String internalPath, int size) {
        FreeTypeFontGenerator freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(internalPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        return freeTypeFontGenerator.generateFont(parameter);
    }

    @Override
    protected void processSystem() {

    }

    @Override
    protected void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
    }
}
