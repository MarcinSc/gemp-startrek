package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.PlaysInCoreAbility;
import com.gempukku.startrek.game.ability.TriggerAbility;

public class HasAbilityFilterHandler extends CardFilterSystem {
    private CardAbilitySystem cardAbilitySystem;

    public HasAbilityFilterHandler() {
        super("hasAbility");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        Class<? extends CardAbility> cardAbilityType = getCardAbility(parameters.get(0));
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                return cardAbilitySystem.getCardAbilities(cardEntity, cardAbilityType).size > 0;
            }
        };
    }

    private Class<? extends CardAbility> getCardAbility(String type) {
        if (type.equals("PlaysInCore"))
            return PlaysInCoreAbility.class;
        if (type.equals("Trigger"))
            return TriggerAbility.class;
        throw new GdxRuntimeException("Unable to find ability type: " + type);
    }
}
