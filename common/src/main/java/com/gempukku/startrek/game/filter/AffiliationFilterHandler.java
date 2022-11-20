package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.Affiliation;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class AffiliationFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;

    public AffiliationFilterHandler() {
        super("affiliation");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        Affiliation affiliation = Affiliation.valueOf(parameters.get(0));
        return new AffiliationCardFilter(affiliation);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        Affiliation.valueOf(parameters.get(0));
    }

    private class AffiliationCardFilter implements CardFilter {
        private Affiliation affiliation;

        public AffiliationCardFilter(Affiliation affiliation) {
            this.affiliation = affiliation;
        }

        @Override
        public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity.getComponent(CardComponent.class).getCardId());
            return cardDefinition.getAffiliation() == affiliation;
        }
    }
}

