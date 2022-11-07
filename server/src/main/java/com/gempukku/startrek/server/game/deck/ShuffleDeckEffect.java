package com.gempukku.startrek.server.game.deck;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

public class ShuffleDeckEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;

    public ShuffleDeckEffect() {
        super("shuffleDeck");
    }

    @Override
    protected void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        Entity playerEntity = playerResolverSystem.resolvePlayer(gameEffectEntity, memory,
                gameEffect.getDataString("player"));
        String deckType = gameEffect.getDataString("deck");
        if (deckType.equals("dilemmaDeck")) {
            PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
            dilemmaPile.getCards().shuffle();
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        } else if (deckType.equals("drawDeck")) {
            PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
            deck.getCards().shuffle();
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        }
    }
}
