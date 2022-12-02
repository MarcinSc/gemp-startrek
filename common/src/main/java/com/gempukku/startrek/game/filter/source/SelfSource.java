package com.gempukku.startrek.game.filter.source;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;

import java.util.function.Consumer;

public class SelfSource extends CardSourceSystem {
    private IdProviderSystem idProviderSystem;

    public SelfSource() {
        super("self");
    }

    @Override
    public CardSource resolveSource(Array<String> parameters) {
        return new CardSource() {
            @Override
            public void forEach(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, CardFilter... filters) {
                if (isAccepted(sourceEntity, memory, sourceEntity, filters))
                    consumer.accept(sourceEntity);
            }

            @Override
            public Entity findFirst(Entity sourceEntity, Memory memory, CardFilter... filters) {
                if (isAccepted(sourceEntity, memory, sourceEntity, filters))
                    return sourceEntity;
                return null;
            }

            @Override
            public boolean hasCount(Entity sourceEntity, Memory memory, int required, CardFilter... filters) {
                if (required > 1)
                    return false;
                if (isAccepted(sourceEntity, memory, sourceEntity, filters))
                    return true;

                return false;
            }

            @Override
            public int getCount(Entity sourceEntity, Memory memory, CardFilter... filters) {
                if (isAccepted(sourceEntity, memory, sourceEntity, filters))
                    return 1;
                return 0;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
