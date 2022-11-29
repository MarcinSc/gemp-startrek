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
        int countRequired = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
        int[] result = new int[1];
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, new Consumer<Entity>() {
            @Override
            public void accept(Entity entity) {

            }
        }, "attemptingPersonnel");
        return false;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 2);
        PersonnelSkill.valueOf(parameters.get(0));
        amountResolverSystem.validateAmount(parameters.get(1));
    }
}
