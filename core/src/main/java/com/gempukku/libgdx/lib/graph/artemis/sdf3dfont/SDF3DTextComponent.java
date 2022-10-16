package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.Array;

public class SDF3DTextComponent extends PooledComponent {
    private Array<SDFTextBlock> textBlocks = new Array<>();

    public Array<SDFTextBlock> getTextBlocks() {
        return textBlocks;
    }

    @Override
    protected void reset() {
        textBlocks.clear();
    }
}
