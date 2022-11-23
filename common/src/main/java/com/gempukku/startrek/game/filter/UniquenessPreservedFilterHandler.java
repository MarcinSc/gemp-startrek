package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class UniquenessPreservedFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;
    private CardFilteringSystem cardFilteringSystem;

    public UniquenessPreservedFilterHandler() {
        super("uniquenessPreserved");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardComponent card = cardEntity.getComponent(CardComponent.class);

                CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);
                return !cardDefinition.isUnique()
                        || cantFindCardInPlayWithSameTitle(card, cardDefinition);
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }

    private boolean cantFindCardInPlayWithSameTitle(CardComponent card, CardDefinition cardDefinition) {
        Entity cardWithSameTitle = cardFilteringSystem.findFirstCardInPlay("title(" + cardDefinition.getTitle() + ")," +
                "owner(username(" + card.getOwner() + "))");
        return cardWithSameTitle == null;
    }
}
