package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.PersonnelSkill;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;

public class MemorySkillFilterHandler extends CardFilterSystem {
    private AmountResolverSystem amountResolverSystem;
    private CardLookupSystem cardLookupSystem;

    public MemorySkillFilterHandler() {
        super("memorySkill");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                PersonnelSkill skill = PersonnelSkill.valueOf(memory.getValue(parameters.get(0)));
                int count = 1;
                if (parameters.size > 1)
                    count = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));

                int found = amountResolverSystem.resolveAmount(cardEntity, memory, "skillCount(" + skill.name() + ")");
                return found >= count;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.between(parameters, 1, 2);
        PersonnelSkill.valueOf(parameters.get(0));
        if (parameters.size > 1)
            amountResolverSystem.validateAmount(parameters.get(1));
    }
}
