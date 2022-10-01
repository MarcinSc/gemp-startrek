package com.gempukku.startrek.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.gempukku.libgdx.DummyApplication;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CardDataTest {
    @Test
    public void testLoadingCardDefinitions() {
        Gdx.files = new HeadlessFiles();
        Gdx.app = new DummyApplication();
        CardData cardData = new CardData();
        cardData.initializeCards();
        assertNotNull(cardData.getCardDefinition("1_207"));
    }
}