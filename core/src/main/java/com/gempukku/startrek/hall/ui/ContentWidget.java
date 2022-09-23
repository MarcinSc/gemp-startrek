package com.gempukku.startrek.hall.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Scaling;

public class ContentWidget extends WidgetGroup {
    private float textSidePadding = 10;
    private float textBottomPadding = 20;
    private Image image;
    private Image textBgImage;
    private Label[] labels;
    private Texture pixelTexture;

    public ContentWidget(Texture pixelTexture) {
        this.pixelTexture = pixelTexture;
        setFillParent(true);
    }

    public void setDrawable(Image image, Label[] labels) {
        this.image = image;
        this.image.setScaling(Scaling.fit);
        for (Label label : labels) {
            label.setWrap(true);
        }
        textBgImage = new Image(pixelTexture);
        textBgImage.setColor(0, 0, 0, 0.3f);
        this.labels = labels;
        addActor(image);
        addActor(textBgImage);
        for (Label label : labels) {
            addActor(label);
        }
        invalidate();
    }

    @Override
    protected void positionChanged() {
        invalidate();
    }

    @Override
    public void layout() {
        if (image != null) {
            float width = getWidth();
            float height = getHeight();

            image.setHeight(height);
            image.setBounds(0, 0, width, height);

            float widthWithPadding = width - textSidePadding * 2;

            float textHeight = 0;
            for (Label label : labels) {
                label.setWidth(widthWithPadding);
                textHeight += label.getPrefHeight();
            }

            float y = textHeight;
            for (Label label : labels) {
                float prefHeight = label.getPrefHeight();
                label.setBounds(textSidePadding, textBottomPadding + y - prefHeight, widthWithPadding, prefHeight);
                y -= prefHeight;
            }

            textBgImage.setBounds(0, 0, width, textBottomPadding + textHeight);
        }
    }
}
