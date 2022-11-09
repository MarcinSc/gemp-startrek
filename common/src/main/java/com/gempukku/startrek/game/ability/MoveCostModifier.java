package com.gempukku.startrek.game.ability;

public class MoveCostModifier implements CardAbility {
    private String shipFilter;
    private String fromFilter;
    private String toFilter;
    private String amount;

    public MoveCostModifier(String shipFilter, String fromFilter, String toFilter, String amount) {
        this.shipFilter = shipFilter;
        this.fromFilter = fromFilter;
        this.toFilter = toFilter;
        this.amount = amount;
    }

    public String getShipFilter() {
        return shipFilter;
    }

    public String getFromFilter() {
        return fromFilter;
    }

    public String getToFilter() {
        return toFilter;
    }

    public String getAmount() {
        return amount;
    }
}
