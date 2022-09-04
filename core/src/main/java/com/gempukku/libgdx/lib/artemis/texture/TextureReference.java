package com.gempukku.libgdx.lib.artemis.texture;

import com.gempukku.libgdx.lib.artemis.evaluate.EvaluableProperty;

public class TextureReference implements EvaluableProperty {
    private String path;
    private String region;

    public String getPath() {
        return path;
    }

    public String getRegion() {
        return region;
    }
}
