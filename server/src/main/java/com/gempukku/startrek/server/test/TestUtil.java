package com.gempukku.startrek.server.test;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdComponent;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.decision.DecisionMade;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.zone.CardInHandComponent;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.server.game.decision.DecisionSystem;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

import java.util.function.Consumer;

public class TestUtil {
    public static StarTrekDeck createDeckWithMissions(CardData cardData, String... cardIds) {
        StarTrekDeck deck = new StarTrekDeck();
        for (String cardId : cardIds) {
            CardDefinition cardDefinition = cardData.getCardDefinition(cardId);
            CardType type = cardDefinition.getType();
            if (type == CardType.Dilemma)
                deck.getDillemas().add(cardId);
            else if (type == CardType.Mission)
                deck.getMissions().add(cardId);
            else
                deck.getDrawDeck().add(cardId);
        }

        // Missions
        deck.getMissions().add("1_170");
        deck.getMissions().add("1_187");
        deck.getMissions().add("1_188");
        deck.getMissions().add("1_198");
        deck.getMissions().add("1_199");

        return deck;
    }

    public static boolean playCard(World world, Entity playedCard) {
        String cardId = playedCard.getComponent(ServerEntityIdComponent.class).getId();
        return sendDecision(world, "test1",
                "action", "play",
                "cardId", cardId);
    }

    public static boolean useTrigger(World world, Entity usedCard, int triggerIndex) {
        String cardId = usedCard.getComponent(ServerEntityIdComponent.class).getId();
        return sendDecision(world, "test1",
                "action", "use",
                "cardId", cardId,
                "triggerIndex", String.valueOf(triggerIndex));
    }


    public static boolean sendDecision(World world, String player, String... decisionKeysAndValues) {
        ObjectMap<String, String> decisionResult = new ObjectMap<>();
        for (int i = 0; i < decisionKeysAndValues.length; i += 2) {
            decisionResult.put(decisionKeysAndValues[i], decisionKeysAndValues[i + 1]);
        }

        DecisionMade decisionMade = new DecisionMade(decisionResult);
        decisionMade.setOrigin(player);

        DecisionSystem decisionSystem = world.getSystem(DecisionSystem.class);
        ExecutionStackSystem stackSystem = world.getSystem(ExecutionStackSystem.class);
        boolean result = decisionSystem.decisionMade(decisionMade, stackSystem.getTopMostStackEntity());
        if (result) {
            stackSystem.processStack();
        }
        return result;
    }

    public static Entity createCard(World world, String owner, String cardId) {
        CardLookupSystem cardLookupSystem = world.getSystem(CardLookupSystem.class);
        SpawnSystem spawnSystem = world.getSystem(SpawnSystem.class);

        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        if (cardDefinition == null)
            throw new RuntimeException("Unable to locate card with id: " + cardId);

        Entity cardEntity = spawnSystem.spawnEntity("game/card.template");
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        card.setOwner(owner);
        card.setCardId(cardId);
        return cardEntity;
    }

    public static String getCardId(World world, Entity cardEntity) {
        return world.getSystem(ServerEntityIdSystem.class).getEntityId(cardEntity);
    }

    public static Array<Entity> getCardsInHand(World world, String player) {
        Array<Entity> result = new Array<>();
        LazyEntityUtil.forEachEntityWithComponent(world, CardInHandComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        CardComponent card = entity.getComponent(CardComponent.class);
                        if (card.getOwner().equals(player))
                            result.add(entity);
                    }
                });
        return result;
    }
}
