package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MoveCardToStackEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private AmountResolverSystem amountResolverSystem;
    private ZoneOperations zoneOperations;

    public MoveCardToStackEffect() {
        super("moveCardToStack");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String filter = gameEffect.getDataString("filter");
        int abilityIndex = amountResolverSystem.resolveAmount(sourceEntity, memory, gameEffect.getDataString("abilityIndex", "-1"));

        cardFilteringSystem.forEachCard(sourceEntity, memory, new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        zoneOperations.moveFromCurrentZoneToStack(cardEntity, abilityIndex);
                    }
                }, filter
        );
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"filter"},
                new String[]{"abilityIndex"});
        cardFilteringSystem.validateFilter(effect.getString("filter"));
        String abilityIndex = effect.getString("abilityIndex");
        if (abilityIndex != null)
            amountResolverSystem.validateAmount(abilityIndex);
    }
}
