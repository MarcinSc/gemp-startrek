package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.PersonnelSkill;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

import java.util.function.Consumer;

public class CountMemorySkillAtLeastConditionHandler extends ConditionSystem {
    private CardFilteringSystem cardFilteringSystem;
    private AmountResolverSystem amountResolverSystem;
    private CardLookupSystem cardLookupSystem;

    public CountMemorySkillAtLeastConditionHandler() {
        super("countMemorySkillAtLeast");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        PersonnelSkill skill = PersonnelSkill.valueOf(memory.getValue(parameters.get(1)));
        int amount = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(2));
        int[] result = new int[1];
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, new Consumer<Entity>() {
            @Override
            public void accept(Entity entity) {
                result[0] += amountResolverSystem.resolveAmount(entity, memory, "skillCount(" + skill.name() + ")");
            }
        }, parameters.get(0));
        return result[0] >= amount;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 3);
        cardFilteringSystem.validateFilter(parameters.get(0));
        amountResolverSystem.validateAmount(parameters.get(2));
    }
}
