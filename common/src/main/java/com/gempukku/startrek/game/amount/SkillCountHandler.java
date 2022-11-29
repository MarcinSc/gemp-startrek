package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.PersonnelSkill;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class SkillCountHandler extends AmountSystem {
    private CardLookupSystem cardLookupSystem;

    public SkillCountHandler() {
        super("skillCount");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        PersonnelSkill skill = PersonnelSkill.valueOf(parameters.get(0));
        int count = 0;
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(sourceEntity);
        for (PersonnelSkill cardSkill : cardDefinition.getSkills()) {
            if (cardSkill == skill)
                count++;
        }
        return count;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        PersonnelSkill.valueOf(parameters.get(0));
    }
}
