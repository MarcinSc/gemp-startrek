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

public class CountSkillAtLeastConditionHandler extends ConditionSystem {
    private CardFilteringSystem cardFilteringSystem;
    private AmountResolverSystem amountResolverSystem;
    private CardLookupSystem cardLookupSystem;

    public CountSkillAtLeastConditionHandler() {
        super("countSkillAtLeast");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        PersonnelSkill skill = PersonnelSkill.valueOf(parameters.get(1));
        int amount = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(2));
        int[] result = new int[1];
        cardFilteringSystem.forEachCard(sourceEntity, memory, parameters.get(0), new Consumer<Entity>() {
            @Override
            public void accept(Entity entity) {
                result[0] += amountResolverSystem.resolveAmount(entity, memory, "skillCount(" + skill.name() + ")");
            }
        }, "any");
        return result[0] >= amount;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 3);
        cardFilteringSystem.validateSource(parameters.get(0));
        PersonnelSkill.valueOf(parameters.get(1));
        amountResolverSystem.validateAmount(parameters.get(2));
    }
}
