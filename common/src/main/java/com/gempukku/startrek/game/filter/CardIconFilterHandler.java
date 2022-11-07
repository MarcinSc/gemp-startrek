package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardIcon;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;

public class CardIconFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;

    public CardIconFilterHandler() {
        super("icon");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        CardIcon cardIcon = CardIcon.valueOf(parameters.get(0));
        return new IconCardFilter(cardIcon);
    }

    private class IconCardFilter implements CardFilter {
        private CardIcon icon;

        public IconCardFilter(CardIcon icon) {
            this.icon = icon;
        }

        @Override
        public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity.getComponent(CardComponent.class).getCardId());
            for (CardIcon cardIcon : cardDefinition.getIcons()) {
                if (cardIcon == icon)
                    return true;
            }
            return false;
        }
    }
}

