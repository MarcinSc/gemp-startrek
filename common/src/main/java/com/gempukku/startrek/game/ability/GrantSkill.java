package com.gempukku.startrek.game.ability;

import com.gempukku.startrek.card.PersonnelSkill;
import com.gempukku.startrek.game.filter.CardFilter;

public class GrantSkill implements CardAbility {
    private CardFilter filter;
    private PersonnelSkill skill;

    public GrantSkill(CardFilter filter, PersonnelSkill skill) {
        this.filter = filter;
        this.skill = skill;
    }

    public CardFilter getFilter() {
        return filter;
    }

    public PersonnelSkill getSkill() {
        return skill;
    }
}
