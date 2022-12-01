package com.gempukku.startrek.game.decision.ui;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class CardContainerSettings {
    private float cardWidth;
    private float cardHeight;
    private float scale;
    private float containerWidth;
    private float containerHeight;
    private Matrix4 startupTransform;

    public CardContainerSettings(
            float cardWidth, float cardHeight,
            float containerWidth, float containerHeight,
            Vector2 containerPosition) {
        this(cardWidth, cardHeight, 1f, containerWidth, containerHeight, containerPosition);
    }

    public CardContainerSettings(
            float cardWidth, float cardHeight, float scale,
            float containerWidth, float containerHeight,
            Vector2 containerPosition) {
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.scale = scale;
        this.containerWidth = containerWidth;
        this.containerHeight = containerHeight;
        this.startupTransform = new Matrix4().translate(containerPosition.x, 95, containerPosition.y);
    }

    public float getScale() {
        return scale;
    }

    public float getCardWidth() {
        return cardWidth;
    }

    public float getCardHeight() {
        return cardHeight;
    }

    public float getContainerWidth() {
        return containerWidth;
    }

    public float getContainerHeight() {
        return containerHeight;
    }

    public Matrix4 getStartupTransform() {
        return startupTransform;
    }
}
