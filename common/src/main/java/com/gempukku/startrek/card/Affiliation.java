package com.gempukku.startrek.card;

public enum Affiliation {
    Bajoran(CardIcon.Car), Borg(CardIcon.Borg), Cardassian(CardIcon.Car), Dominion(CardIcon.Dom),
    Federation(CardIcon.Fed), Ferengi(CardIcon.Fer), Klingon(CardIcon.Kli), Romulan(CardIcon.Rom),
    Starfleet(CardIcon.Sta), NonAligned(CardIcon.NA);

    private CardIcon icon;

    Affiliation(CardIcon icon) {
        this.icon = icon;
    }

    public CardIcon getIcon() {
        return icon;
    }
}
