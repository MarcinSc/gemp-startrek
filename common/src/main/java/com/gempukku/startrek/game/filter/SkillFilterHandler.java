package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.PersonnelSkill;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.amount.AmountResolverSystem;

public class SkillFilterHandler extends CardFilterSystem {
    private AmountResolverSystem amountResolverSystem;
    private CardLookupSystem cardLookupSystem;

    public SkillFilterHandler() {
        super("skill");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        PersonnelSkill skill = PersonnelSkill.valueOf(parameters.get(0));
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
                int count = 1;
                if (parameters.size > 1)
                    count = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
                CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity.getComponent(CardComponent.class).getCardId());
                int found = 0;
                for (PersonnelSkill charSkill : cardDefinition.getSkills()) {
                    if (charSkill == skill)
                        found++;
                }

                return found >= count;
            }
        };
    }
}
