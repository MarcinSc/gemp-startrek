package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont;

public class SDFTextLine {
    private String text = "Test text";
    private float scale = 1f;
    private String bitmapFontPath;
    private float kerningMultiplier = -1f;
    private float letterSpacing = Float.MIN_VALUE;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getBitmapFontPath() {
        return bitmapFontPath;
    }

    public void setBitmapFontPath(String bitmapFontPath) {
        this.bitmapFontPath = bitmapFontPath;
    }

    public float getKerningMultiplier() {
        return kerningMultiplier;
    }

    public void setKerningMultiplier(float kerningMultiplier) {
        this.kerningMultiplier = kerningMultiplier;
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
    }
}
