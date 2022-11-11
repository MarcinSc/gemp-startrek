package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.DummyApplication;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.decision.DecisionMade;
import com.gempukku.startrek.decision.PlayerDecisionComponent;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.zone.CardInHandComponent;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.server.game.decision.DecisionSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import com.gempukku.startrek.server.game.stack.StackSystem;
import org.junit.BeforeClass;

import java.util.function.Consumer;

public abstract class AbstractGameTest {
    protected static CardData cardData;

    protected StackSystem stackSystem;
    protected EventSystem eventSystem;
    protected World world;
    protected DecisionSystem decisionSystem;
    protected SpawnSystem spawnSystem;

    @BeforeClass
    public static void initialize() {
        Gdx.app = new DummyApplication();
        Gdx.files = new HeadlessFiles();
        cardData = new CardData();
        cardData.initializeCards();
    }

    protected void putCardOnTopOfDeck(String player, String cardId) {
        Entity cardEntity = spawnSystem.spawnEntity("game/card.template");
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        card.setOwner(player);
        card.setCardId(cardId);
        world.getSystem(ZoneOperations.class).moveCardToTopOfDeck(cardEntity);
    }

    protected Array<Entity> getCardsInHand(String player) {
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

    protected StarTrekDeck createDeck(String... cardIds) {
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

        return deck;
    }

    protected void setupGame(StarTrekDeck deck) {
        setupGame(deck, deck);
    }

    protected void setupGame(StarTrekDeck deck1, StarTrekDeck deck2) {
        StarTrekGameHolder gameHolder = new StarTrekGameHolder(cardData, true);
        gameHolder.addPlayer(new PlayerGameInfo("test1", "test1", "test", deck1));
        gameHolder.addPlayer(new PlayerGameInfo("test2", "test2", "test", deck2));
        gameHolder.setupGame();
        gameHolder.processGame();

        world = gameHolder.getGameWorld();
        stackSystem = world.getSystem(StackSystem.class);
        eventSystem = world.getSystem(EventSystem.class);
        decisionSystem = world.getSystem(DecisionSystem.class);
        spawnSystem = world.getSystem(SpawnSystem.class);
    }

    private Entity getTopMostStackEntity() {
        return stackSystem.getTopMostStackEntity();
    }

    protected PlayerDecisionComponent getDecision() {
        Entity decisionEntity = getTopMostStackEntity();
        return decisionEntity.getComponent(PlayerDecisionComponent.class);
    }

    protected boolean sendDecision(String player, String... decisionKeysAndValues) {
        ObjectMap<String, String> decisionResult = new ObjectMap<>();
        for (int i = 0; i < decisionKeysAndValues.length; i += 2) {
            decisionResult.put(decisionKeysAndValues[i], decisionKeysAndValues[i + 1]);
        }

        DecisionMade decisionMade = new DecisionMade(decisionResult);
        decisionMade.setOrigin(player);

        boolean result = decisionSystem.decisionMade(decisionMade, getTopMostStackEntity());
        if (result) {
            stackSystem.processStack();
        }
        return result;
    }
}
