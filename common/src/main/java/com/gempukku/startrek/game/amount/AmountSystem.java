package com.gempukku.startrek.game.amount;

import com.artemis.BaseSystem;

public abstract class AmountSystem extends BaseSystem implements AmountHandler {
    private AmountResolverSystem amountResolverSystem;

    private String[] amountTypes;

    public AmountSystem(String... amountTypes) {
        this.amountTypes = amountTypes;
    }

    @Override
    protected void initialize() {
        for (String amountType : amountTypes) {
            amountResolverSystem.registerAmountHandler(amountType, this);
        }
    }

    @Override
    protected void processSystem() {

    }
}
