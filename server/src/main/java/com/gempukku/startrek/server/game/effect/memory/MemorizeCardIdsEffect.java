package com.gempukku.startrek.server.game.effect.memory;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MemorizeCardIdsEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private IdProviderSystem idProviderSystem;
    private CardLookupSystem cardLookupSystem;

    public MemorizeCardIdsEffect() {
        super("memorizeCardIds");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String from = getOptionalFromMemory(memory, gameEffect, "from", "fromMemory");
        String filter = memory.getValue(gameEffect.getDataString("filterMemory"));
        String memoryName = gameEffect.getDataString("memory");

        memory.removeValue(memoryName);
        cardFilteringSystem.forEachCard(sourceEntity, memory, from,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        CardComponent card = entity.getComponent(CardComponent.class);
                        memory.appendValue(memoryName, card.getCardId());
                    }
                }, filter);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"memory", "filterMemory"},
                new String[]{"fromMemory", "from"});
        ValidateUtil.hasExactlyOneOf(effect, "fromMemory", "from");
    }
}
