package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.Quadrant;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class QuadrantFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;

    public QuadrantFilterHandler() {
        super("quadrant");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        Quadrant quadrant = Quadrant.valueOf(parameters.get(0));
        return new QuadrantCardFilter(quadrant);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        Quadrant.valueOf(parameters.get(0));
    }

    private class QuadrantCardFilter implements CardFilter {
        private Quadrant quadrant;

        public QuadrantCardFilter(Quadrant quadrant) {
            this.quadrant = quadrant;
        }

        @Override
        public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity.getComponent(CardComponent.class).getCardId());
            return cardDefinition.getQuadrant() == quadrant;
        }
    }
}

