package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.card.PersonnelSkill;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class GrantSkillAbilityHandler extends CardAbilityHandlerSystem {
    private CardFilteringSystem cardFilteringSystem;

    public GrantSkillAbilityHandler() {
        super("grantSkill");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        CardFilter filter = cardFilteringSystem.resolveCardFilter(cardAbility.getString("filter"));
        PersonnelSkill skill = PersonnelSkill.valueOf(cardAbility.getString("skill"));
        return new GrantSkill(filter, skill);
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{"filter", "skill"},
                new String[]{});
        cardFilteringSystem.validateFilter(cardAbility.getString("filter"));
        PersonnelSkill.valueOf(cardAbility.getString("skill"));
    }
}
