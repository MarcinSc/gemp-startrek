package com.gempukku.startrek.server.game.effect.memory;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MemorizeCardByIdEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private IdProviderSystem idProviderSystem;

    public MemorizeCardByIdEffect() {
        super("memorizeCardById");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String memoryName = gameEffect.getDataString("memory");
        memory.removeValue(memoryName);
        Array<String> cardsToFind = StringUtils.splitToArray(memory.getValue(gameEffect.getDataString("idsMemory")));
        cardFilteringSystem.forEachCard(sourceEntity, memory, gameEffect.getDataString("from"),
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        CardComponent card = entity.getComponent(CardComponent.class);
                        if (cardsToFind.removeValue(card.getCardId(), false))
                            memory.appendValue(memoryName, idProviderSystem.getEntityId(entity));
                    }
                }, memory.getValue(gameEffect.getDataString("filterMemory")));
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"from", "filterMemory", "idsMemory", "memory"},
                new String[]{});
        cardFilteringSystem.validateSource(effect.getString("from"));
    }
}
