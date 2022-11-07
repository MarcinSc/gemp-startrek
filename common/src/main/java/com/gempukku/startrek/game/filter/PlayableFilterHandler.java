package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.card.CardFilteringSystem;

public class PlayableFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;
    private CardFilteringSystem cardFilteringSystem;

    public PlayableFilterHandler() {
        super("playable");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
                CardComponent card = cardEntity.getComponent(CardComponent.class);

                CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);
                return !cardDefinition.isUnique()
                        || cantFindCardInPlayWithSameTitle(card, cardDefinition);
            }
        };
    }

    private boolean cantFindCardInPlayWithSameTitle(CardComponent card, CardDefinition cardDefinition) {
        Entity cardWithSameTitle = cardFilteringSystem.findFirstCardInPlay("title(" + cardDefinition.getTitle() + ")," +
                "owner(username(" + card.getOwner() + "))");
        return cardWithSameTitle == null;
    }
}
