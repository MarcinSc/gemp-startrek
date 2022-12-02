package com.gempukku.startrek.game.ability;

import com.gempukku.startrek.card.PersonnelSkill;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.source.CardSource;

public class GrantSkill implements CardAbility {
    private CardSource source;
    private CardFilter filter;
    private PersonnelSkill skill;

    public GrantSkill(CardSource source, CardFilter filter, PersonnelSkill skill) {
        this.source = source;
        this.filter = filter;
        this.skill = skill;
    }

    public CardSource getSource() {
        return source;
    }

    public CardFilter getFilter() {
        return filter;
    }

    public PersonnelSkill getSkill() {
        return skill;
    }
}
