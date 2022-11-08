package com.gempukku.startrek.server.game.effect.deck;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.deck.PlayerDeckComponent;
import com.gempukku.startrek.server.game.deck.PlayerDilemmaPileComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class ShuffleDeckEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;

    public ShuffleDeckEffect() {
        super("shuffleDeck");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        Entity playerEntity = playerResolverSystem.resolvePlayer(sourceEntity, memory,
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
