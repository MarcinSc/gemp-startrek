package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.game.CardComponent;

public class YourFilterHandler extends CardFilterSystem {
    public YourFilterHandler() {
        super("your");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
                CardComponent card = sourceEntity.getComponent(CardComponent.class);
                CardComponent tested = cardEntity.getComponent(CardComponent.class);
                return tested.getOwner().equals(card.getOwner());
            }
        };
    }
}
