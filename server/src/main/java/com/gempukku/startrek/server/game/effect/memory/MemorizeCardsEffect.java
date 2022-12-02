package com.gempukku.startrek.server.game.effect.memory;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MemorizeCardsEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private IdProviderSystem idProviderSystem;

    public MemorizeCardsEffect() {
        super("memorizeCards");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String from = memory.getValue(gameEffect.getDataString("fromMemory"));
        String filter = memory.getValue(gameEffect.getDataString("filterMemory"));
        String memoryName = gameEffect.getDataString("memory");

        memory.removeValue(memoryName);
        cardFilteringSystem.forEachCard(sourceEntity, memory, from,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        memory.appendValue(memoryName, idProviderSystem.getEntityId(entity));
                    }
                }, filter);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"fromMemory", "memory", "filterMemory"},
                new String[]{});
    }
}
