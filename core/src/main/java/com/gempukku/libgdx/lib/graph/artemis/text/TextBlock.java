package com.gempukku.libgdx.lib.graph.artemis.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class TextBlock {
    private String spriteBatchName = null;
    private Matrix4 transform = new Matrix4();
    private Vector3 rightVector = new Vector3(1, 0, 0);
    private Vector3 upVector = new Vector3(0, 1, 0);
    private Color color = new Color(1, 1, 1, 1);
    private String text = "";
    private boolean wrap = true;
    private float targetWidth = 0f;
    private boolean kerning = true;
    private float letterSpacing = 0f;
    private boolean scaleDownToFit = false;
    private float scaleDownMultiplier = 1.1f;
    private float width = 0.5f;
    private float edge = 0.01f;
    private TextHorizontalAlignment horizontalAlignment = TextHorizontalAlignment.left;
    private TextVerticalAlignment verticalAlignment = TextVerticalAlignment.top;
    private String bitmapFontPath;

    public String getSpriteBatchName() {
        return spriteBatchName;
    }

    public void setSpriteBatchName(String spriteBatchName) {
        this.spriteBatchName = spriteBatchName;
    }

    public Matrix4 getTransform() {
        return transform;
    }

    public Vector3 getRightVector() {
        return rightVector;
    }

    public Vector3 getUpVector() {
        return upVector;
    }

    public Color getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextHorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(TextHorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public TextVerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(TextVerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public String getBitmapFontPath() {
        return bitmapFontPath;
    }

    public void setBitmapFontPath(String bitmapFontPath) {
        this.bitmapFontPath = bitmapFontPath;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getEdge() {
        return edge;
    }

    public void setEdge(float edge) {
        this.edge = edge;
    }

    public float getTargetWidth() {
        return targetWidth;
    }

    public void setTargetWidth(float targetWidth) {
        this.targetWidth = targetWidth;
    }

    public boolean getKerning() {
        return kerning;
    }

    public void setKerning(boolean kerning) {
        this.kerning = kerning;
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public boolean isScaleDownToFit() {
        return scaleDownToFit;
    }

    public void setScaleDownToFit(boolean scaleDownToFit) {
        this.scaleDownToFit = scaleDownToFit;
    }

    public float getScaleDownMultiplier() {
        return scaleDownMultiplier;
    }

    public void setScaleDownMultiplier(float scaleDownMultiplier) {
        this.scaleDownMultiplier = scaleDownMultiplier;
    }
}
