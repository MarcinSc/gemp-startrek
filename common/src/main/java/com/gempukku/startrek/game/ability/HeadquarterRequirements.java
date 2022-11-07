package com.gempukku.startrek.game.ability;

import com.gempukku.startrek.game.filter.CardFilter;

public class HeadquarterRequirements implements CardAbility {
    private CardFilter cardFilter;

    public HeadquarterRequirements(CardFilter cardFilter) {
        this.cardFilter = cardFilter;
    }

    public CardFilter getCardFilter() {
        return cardFilter;
    }
}
