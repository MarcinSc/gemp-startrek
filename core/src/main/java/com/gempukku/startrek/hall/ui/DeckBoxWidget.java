package com.gempukku.startrek.hall.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.gempukku.startrek.hall.DeckBoxRenderingSystem;

public class DeckBoxWidget extends Table {
    private Image image;
    private Label label;

    private String deckId;
    private String deckName;
    private String cardImage;

    private DeckBoxRenderingSystem deckBoxRenderingSystem;
    private String defaultNameLabel = "";

    public DeckBoxWidget(DeckBoxRenderingSystem deckBoxRenderingSystem, Skin skin) {
        this.deckBoxRenderingSystem = deckBoxRenderingSystem;
        image = new Image(deckBoxRenderingSystem.getDeckboxTexture(null));
        image.setScaling(Scaling.fit);
        label = new Label(defaultNameLabel, skin);
        label.setEllipsis(true);
        label.setAlignment(Align.center);

        add(image).fillX().row();
        add(label).fillX().row();
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDefaultNameLabel(String defaultNameLabel) {
        this.defaultNameLabel = defaultNameLabel;
        if (deckName == null)
            label.setText(defaultNameLabel);
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
        if (deckName != null)
            this.label.setText(deckName);
        else
            this.label.setText(defaultNameLabel);
    }

    public void setCardImage(String cardId) {
        this.cardImage = cardId;
        this.image.setDrawable(new TextureRegionDrawable(deckBoxRenderingSystem.getDeckboxTexture(cardId)));
    }
}
