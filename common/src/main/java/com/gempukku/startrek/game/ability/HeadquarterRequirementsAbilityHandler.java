package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;

public class HeadquarterRequirementsAbilityHandler extends CardAbilityHandlerSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;

    public HeadquarterRequirementsAbilityHandler() {
        super("headquarterRequirements");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        CardFilter cardFilter = cardFilterResolverSystem.resolveCardFilter(cardAbility.getString("filter"));
        return new HeadquarterRequirements(cardFilter);
    }
}
