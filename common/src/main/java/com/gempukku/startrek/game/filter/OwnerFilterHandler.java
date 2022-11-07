package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class OwnerFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;
    private PlayerResolverSystem playerResolverSystem;

    public OwnerFilterHandler() {
        super("owner");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        return new OwnerCardFilter(parameters.get(0));
    }

    private class OwnerCardFilter implements CardFilter {
        private String owner;

        public OwnerCardFilter(String owner) {
            this.owner = owner;
        }

        @Override
        public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
            String username = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, owner);
            return username.equals(cardEntity.getComponent(CardComponent.class).getOwner());
        }
    }
}
