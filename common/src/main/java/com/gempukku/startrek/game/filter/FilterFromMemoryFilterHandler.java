package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class FilterFromMemoryFilterHandler extends CardFilterSystem {
    public FilterFromMemoryFilterHandler() {
        super("filterFromMemory");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
    }
}
