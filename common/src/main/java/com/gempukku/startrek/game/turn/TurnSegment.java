package com.gempukku.startrek.game.turn;

public enum TurnSegment {
    PLAY_AND_DRAW_CARDS("playAndDrawCardsSegment.template"),
    EXECUTE_ORDERS("executeOrdersSegment.template"),
    DISCARD_EXCESS_CARDS("discardExcessCardsSegment.template");

    private String entityTemplate;

    private TurnSegment(String entityTemplate) {
        this.entityTemplate = entityTemplate;
    }

    public String getEntityTemplate() {
        return entityTemplate;
    }
}
