package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont;

public enum TextHorizontalAlignment {
    center, left, right, justified, justifiedFragment;

    public float apply(float width, float availableWidth) {
        switch (this) {
            case right:
                return availableWidth - width;
            case center:
                return (availableWidth - width) / 2;
            default:
                return 0;
        }
    }
}
