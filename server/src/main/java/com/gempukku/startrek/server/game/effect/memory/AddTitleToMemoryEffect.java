package com.gempukku.startrek.server.game.effect.memory;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class AddTitleToMemoryEffect extends OneTimeEffectSystem {
    private CardLookupSystem cardLookupSystem;
    private CardFilteringSystem cardFilteringSystem;

    public AddTitleToMemoryEffect() {
        super("addTitleToMemory");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String memoryName = gameEffect.getDataString("memory");
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        String title = cardLookupSystem.getCardDefinition(entity).getTitle();
                        memory.appendValue(memoryName, title);
                    }
                }, gameEffect.getDataString("filter"));
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"memory", "filter"},
                new String[]{});
    }
}
