package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class TitleFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;
    private PlayerResolverSystem playerResolverSystem;

    public TitleFilterHandler() {
        super("title");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new TitleCardFilter(parameters.get(0));
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
    }

    private class TitleCardFilter implements CardFilter {
        private String title;

        public TitleCardFilter(String title) {
            this.title = title;
        }

        @Override
        public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);
            return cardDefinition.getTitle().equals(title);
        }
    }
}
