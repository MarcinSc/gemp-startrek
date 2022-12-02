package com.gempukku.startrek.game.filter;

import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.ValidateUtil;

public class InPlayFilterHandler extends CardFilterSystem {
    private CardFilteringSystem cardFilteringSystem;

    public InPlayFilterHandler() {
        super("inPlay");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return cardFilteringSystem.resolveCardFilter("or(zone(Core),zone(Brig),zone(Mission))");
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
