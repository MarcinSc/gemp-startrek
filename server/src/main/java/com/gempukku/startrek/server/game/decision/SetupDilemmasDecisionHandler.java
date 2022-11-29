package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.deck.PlayerDilemmaPileComponent;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

public class SetupDilemmasDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;
    private PlayerResolverSystem playerResolverSystem;
    private ZoneOperations zoneOperations;

    @Override
    protected void initialize() {
        decisionSystem.registerDecisionTypeHandler("setupDilemmas", this);
    }

    @Override
    public boolean validateDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        try {
            String[] dilemmaStack = StringUtils.split(result.get("dilemmaStack"));
            String[] discardedDilemmas = StringUtils.split(result.get("discardedDilemmas"));

            String[] dilemmaCardIds = StringUtils.split(decisionData.get("dilemmaCardIds"));
            if (dilemmaStack.length + discardedDilemmas.length != dilemmaCardIds.length)
                return false;

            Array<String> sentByServer = Array.with(dilemmaCardIds);
            for (String card : dilemmaStack) {
                if (!sentByServer.removeValue(card, false))
                    return false;
            }
            for (String card : discardedDilemmas) {
                if (!sentByServer.removeValue(card, false))
                    return false;
            }
            if (sentByServer.size > 0)
                return false;

            return true;
        } catch (Exception exp) {
            return false;
        }
    }

    @Override
    public void processDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        Entity playerEntity = playerResolverSystem.findPlayerEntity(decisionPlayer);
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);

        String[] discardedDilemmas = StringUtils.split(result.get("discardedDilemmas"));
        for (String discardedDilemma : discardedDilemmas) {
            Entity firstMatchingInDeck = findFirstInDeck(dilemmaPile, discardedDilemma);
            zoneOperations.moveFromCurrentZoneToBottomOfDilemmaPile(firstMatchingInDeck, true);
        }

        String[] dilemmaStack = StringUtils.split(result.get("dilemmaStack"));
        for (String cardToStack : dilemmaStack) {
            Entity firstMatchingInDeck = findFirstInDeck(dilemmaPile, cardToStack);
            zoneOperations.moveFromCurrentZoneToTopOfDilemmaStack(firstMatchingInDeck);
        }
    }

    private Entity findFirstInDeck(PlayerDilemmaPileComponent dilemmaPile, String cardId) {
        Array<Integer> cards = dilemmaPile.getCards();
        for (int i = cards.size - 1; i >= 0; i--) {
            Entity cardEntity = world.getEntity(cards.get(i));
            if (cardEntity.getComponent(CardComponent.class).getCardId().equals(cardId))
                return cardEntity;
        }
        return null;
    }

    @Override
    protected void processSystem() {

    }
}
