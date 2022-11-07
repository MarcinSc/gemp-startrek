package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class TitleFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;
    private PlayerResolverSystem playerResolverSystem;

    public TitleFilterHandler() {
        super("title");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        return new TitleCardFilter(parameters.get(0));
    }

    private class TitleCardFilter implements CardFilter {
        private String title;

        public TitleCardFilter(String title) {
            this.title = title;
        }

        @Override
        public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);
            return cardDefinition.getTitle().equals(title);
        }
    }
}
