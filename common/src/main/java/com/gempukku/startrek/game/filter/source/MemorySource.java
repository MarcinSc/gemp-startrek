package com.gempukku.startrek.game.filter.source;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;

public class MemorySource extends CardSourceSystem {
    private IdProviderSystem idProviderSystem;

    public MemorySource() {
        super("memory");
    }

    @Override
    public CardSource resolveSource(Array<String> parameters) {
        return new ShortcutCardSource() {
            @Override
            protected void forEachWithShortcut(Entity sourceEntity, Memory memory, ShortcutConsumer<Entity> consumer, CardFilter... filters) {
                String[] cardIds = StringUtils.split(memory.getValue(parameters.get(0)));
                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        if (consumer.accept(entity))
                            return;
                }
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
    }
}
