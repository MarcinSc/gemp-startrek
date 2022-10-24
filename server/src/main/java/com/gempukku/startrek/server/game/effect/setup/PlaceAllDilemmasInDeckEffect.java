package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.CardZone;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.server.game.deck.PlayerDilemmaPileComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

import java.util.function.Consumer;

public class PlaceAllDilemmasInDeckEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private CardLookupSystem cardLookupSystem;
    private EventSystem eventSystem;

    public PlaceAllDilemmasInDeckEffect() {
        super("placeAllDilemmasInDilemmaDeck");
    }

    @Override
    protected void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String player = playerResolverSystem.resolvePlayerUsername(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("player"));
        Entity playerEntity = playerResolverSystem.findPlayerEntity(player);
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        PlayerPublicStatsComponent playerStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        LazyEntityUtil.forEachEntityWithComponent(world, CardComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        CardComponent cardComponent = entity.getComponent(CardComponent.class);
                        if (cardComponent.getOwner().equals(player)) {
                            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardComponent.getCardId());
                            if (cardDefinition.getType() == CardType.Dilemma) {
                                cardComponent.setCardZone(CardZone.DILLEMMA_PILE);
                                dilemmaPile.getCards().add(entity.getId());
                                playerStats.setDilemmaCount(playerStats.getDilemmaCount() + 1);
                            }
                        }
                    }
                });
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }
}
