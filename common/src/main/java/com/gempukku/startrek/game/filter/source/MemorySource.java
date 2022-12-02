package com.gempukku.startrek.game.filter.source;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;

import java.util.function.Consumer;

public class MemorySource extends CardSourceSystem {
    private IdProviderSystem idProviderSystem;

    public MemorySource() {
        super("memory");
    }

    @Override
    public CardSource resolveSource(Array<String> parameters) {
        return new CardSource() {
            @Override
            public void forEach(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, CardFilter... filters) {
                String[] cardIds = StringUtils.split(memory.getValue(parameters.get(0)));
                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        consumer.accept(entity);
                }
            }

            @Override
            public Entity findFirst(Entity sourceEntity, Memory memory, CardFilter... filters) {
                String[] cardIds = StringUtils.split(memory.getValue(parameters.get(0)));
                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        return entity;
                }
                return null;
            }

            @Override
            public boolean hasCount(Entity sourceEntity, Memory memory, int required, CardFilter... filters) {
                int count = 0;
                String[] cardIds = StringUtils.split(memory.getValue(parameters.get(0)));
                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters)) {
                        count++;
                        if (count >= required)
                            return true;
                    }
                }

                return false;
            }

            @Override
            public int getCount(Entity sourceEntity, Memory memory, CardFilter... filters) {
                int result = 0;
                String[] cardIds = StringUtils.split(memory.getValue(parameters.get(0)));
                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        result++;
                }
                return result;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
    }
}
