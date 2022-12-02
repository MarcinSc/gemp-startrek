package com.gempukku.startrek.server.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.PersonnelSkill;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.condition.ConditionSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

import java.util.function.Consumer;

public class OnMissionSkillCountHandler extends ConditionSystem {
    private AmountResolverSystem amountResolverSystem;
    private CardFilteringSystem cardFilteringSystem;

    public OnMissionSkillCountHandler() {
        super("onMissionSkillCount");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        PersonnelSkill skill = PersonnelSkill.valueOf(parameters.get(0));
        int countRequired = 1;
        if (parameters.size > 1)
            countRequired = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
        int[] result = new int[1];
        cardFilteringSystem.forEachCard(sourceEntity, memory, "attemptingPersonnel", new Consumer<Entity>() {
            @Override
            public void accept(Entity entity) {
                result[0] += amountResolverSystem.resolveAmount(entity, memory, "skillCount(" + skill.name() + ")");
            }
        }, "attemptingPersonnel");
        return result[0] >= countRequired;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.between(parameters, 1, 2);
        PersonnelSkill.valueOf(parameters.get(0));
        if (parameters.size > 1)
            amountResolverSystem.validateAmount(parameters.get(1));
    }
}
