package com.gempukku.startrek.server.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class TitleInMemoryHandler extends ConditionSystem {
    private CardLookupSystem cardLookupSystem;
    private CardFilteringSystem cardFilteringSystem;

    public TitleInMemoryHandler() {
        super("titleInMemory");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        String[] titles = StringUtils.split(memory.getValue(parameters.get(0), ""));
        CardFilter filter = cardFilteringSystem.createAndFilter(parameters, 1);

        return cardFilteringSystem.hasCard(sourceEntity, memory, filter,
                new CardFilter() {
                    @Override
                    public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                        String title = cardLookupSystem.getCardDefinition(cardEntity).getTitle();
                        for (String s : titles) {
                            if (s.equals(title))
                                return true;
                        }
                        return false;
                    }
                });
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 2);
        for (int i = 1; i < parameters.size; i++) {
            cardFilteringSystem.validateFilter(parameters.get(i));
        }
    }
}
