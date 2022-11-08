package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.Affiliation;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;

public class AffiliationFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;

    public AffiliationFilterHandler() {
        super("affiliation");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        Affiliation affiliation = Affiliation.valueOf(parameters.get(0));
        return new AffiliationCardFilter(affiliation);
    }

    private class AffiliationCardFilter implements CardFilter {
        private Affiliation affiliation;

        public AffiliationCardFilter(Affiliation affiliation) {
            this.affiliation = affiliation;
        }

        @Override
        public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity.getComponent(CardComponent.class).getCardId());
            return cardDefinition.getAffiliation() == affiliation;
        }
    }
}

