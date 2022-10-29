package com.gempukku.startrek.card;

public enum Quadrant {
    Alpha("Α"), Beta("Β"), Gamma("Γ"), Delta("Δ");

    private String symbol;

    Quadrant(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
