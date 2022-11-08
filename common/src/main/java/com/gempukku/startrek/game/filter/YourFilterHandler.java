package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;

public class YourFilterHandler extends CardFilterSystem {
    public YourFilterHandler() {
        super("your");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardComponent card = sourceEntity.getComponent(CardComponent.class);
                CardComponent tested = cardEntity.getComponent(CardComponent.class);
                return tested.getOwner().equals(card.getOwner());
            }
        };
    }
}
