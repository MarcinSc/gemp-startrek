package com.gempukku.startrek.hall.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.gempukku.startrek.common.FontProviderSystem;

public class ImageWithOverlayDrawable extends BaseDrawable {
    private TextureRegionDrawable textureRegionDrawable;
    private Label label;
    private Drawable pixelRegionDrawable;
    private float textHeight;

    public ImageWithOverlayDrawable(FontProviderSystem fontProvider, Texture pixelTexture, TextureRegion textureRegion, String text) {
        this.textureRegionDrawable = new TextureRegionDrawable(textureRegion);
        this.pixelRegionDrawable = new TextureRegionDrawable(new TextureRegion(pixelTexture)).tint(new Color(0, 0, 0, 0.5f));
        setMinWidth(textureRegion.getRegionWidth());
        setMinHeight(textureRegion.getRegionHeight());
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = fontProvider.getFont("assets/font/LABTSECB.ttf", 30);
        label = new Label(text, style);
        label.setAlignment(Align.bottomRight);
        label.setWidth(textureRegion.getRegionWidth());
        textHeight = label.getPrefHeight();
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        textureRegionDrawable.draw(batch, x, y, width, height);
        pixelRegionDrawable.draw(batch, x, y, width, textHeight + 10);
        label.setBounds(x + 5, y + 5, width - 10, height - 10);
        label.draw(batch, 1f);
    }
}
