package com.gempukku.startrek.server.game.deck;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

public class ShuffleDeckEffect extends EffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;

    public ShuffleDeckEffect() {
        super("shuffleDeck");
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        Entity playerEntity = playerResolverSystem.resolvePlayer(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("player"));
        String deckType = gameEffect.getData().getString("deck");
        if (deckType.equals("dilemmaDeck")) {
            PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
            dilemmaPile.getCards().shuffle();
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        } else if (deckType.equals("drawDeck")) {
            PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
            deck.getCards().shuffle();
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        }
        removeEffectFromStack(gameEffectEntity);
    }
}
