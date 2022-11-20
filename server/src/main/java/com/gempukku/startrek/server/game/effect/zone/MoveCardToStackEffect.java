package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MoveCardToStackEffect extends OneTimeEffectSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private AmountResolverSystem amountResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;

    public MoveCardToStackEffect() {
        super("moveCardToStack");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String filter = gameEffect.getDataString("filter");
        int abilityIndex = amountResolverSystem.resolveAmount(sourceEntity, memory, gameEffect.getDataString("abilityIndex", "-1"));

        cardFilteringSystem.forEachCard(sourceEntity, memory, filter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        zoneOperations.removeFromCurrentZone(cardEntity);
                        zoneOperations.moveCardToStack(cardEntity, abilityIndex);
                    }
                });
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"filter"},
                new String[]{"abilityIndex"});
        cardFilterResolverSystem.validateFilter(effect.getString("filter"));
        String abilityIndex = effect.getString("abilityIndex");
        if (abilityIndex != null)
            amountResolverSystem.validateAmount(abilityIndex);
    }
}
