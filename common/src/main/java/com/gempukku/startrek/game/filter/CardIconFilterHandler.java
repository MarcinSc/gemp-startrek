package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardIcon;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class CardIconFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;

    public CardIconFilterHandler() {
        super("icon");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        CardIcon cardIcon = CardIcon.valueOf(parameters.get(0));
        return new IconCardFilter(cardIcon);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        CardIcon.valueOf(parameters.get(0));
    }

    private class IconCardFilter implements CardFilter {
        private CardIcon icon;

        public IconCardFilter(CardIcon icon) {
            this.icon = icon;
        }

        @Override
        public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity.getComponent(CardComponent.class).getCardId());
            Array<CardIcon> icons = cardDefinition.getIcons();
            if (icons == null)
                return false;
            for (CardIcon cardIcon : icons) {
                if (cardIcon == icon)
                    return true;
            }
            return false;
        }
    }
}

