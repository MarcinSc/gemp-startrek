package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class HeadquarterRequirementsAbilityHandler extends CardAbilityHandlerSystem {
    private CardFilteringSystem cardFilteringSystem;

    public HeadquarterRequirementsAbilityHandler() {
        super("headquarterRequirements");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        CardFilter cardFilter = cardFilteringSystem.resolveCardFilter(cardAbility.getString("filter"));
        return new HeadquarterRequirements(cardFilter);
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{"filter"},
                new String[]{});
        cardFilteringSystem.validateFilter(cardAbility.getString("filter"));
    }
}
