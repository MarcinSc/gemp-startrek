package com.gempukku.startrek.game.turn;

public enum TurnSegment {
    PLAY_AND_DRAW_CARDS("game/segment/playAndDrawCardsSegment.template"),
    EXECUTE_ORDERS("game/segment/executeOrdersSegment.template"),
    DISCARD_EXCESS_CARDS("game/segment/discardExcessCardsSegment.template");

    private String entityTemplate;

    private TurnSegment(String entityTemplate) {
        this.entityTemplate = entityTemplate;
    }

    public String getEntityTemplate() {
        return entityTemplate;
    }
}
