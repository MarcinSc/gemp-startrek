package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.DummyApplication;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.decision.PlayerDecisionComponent;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.server.game.decision.DecisionSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;
import com.gempukku.startrek.server.test.TestUtil;
import org.junit.BeforeClass;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractGameTest {
    protected static CardData cardData;

    protected ExecutionStackSystem stackSystem;
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

    protected Entity putCardOnTopOfDeck(String player, String cardId) {
        Entity cardEntity = spawnSystem.spawnEntity("game/card.template");
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        card.setOwner(player);
        card.setCardId(cardId);
        world.getSystem(ZoneOperations.class).setupCardToTopOfDeck(cardEntity);
        return cardEntity;
    }

    protected Array<Entity> getCardsInHand(String player) {
        return TestUtil.getCardsInHand(world, player);
    }

    protected Entity createCard(String username, String cardId) {
        return TestUtil.createCard(world, username, cardId);
    }

    protected String getCardId(Entity cardEntity) {
        return TestUtil.getCardId(world, cardEntity);
    }

    protected StarTrekDeck createDeckWithMissions(String... cardIds) {
        return TestUtil.createDeckWithMissions(cardData, cardIds);
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
        stackSystem = world.getSystem(ExecutionStackSystem.class);
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
        return TestUtil.sendDecision(world, player, decisionKeysAndValues);
    }

    protected void sendDecisionSuccessfully(String player, String... decisionKeysAndValues) {
        assertTrue(sendDecision(player, decisionKeysAndValues));
    }

    protected void sendDecisionFailure(String player, String... decisionKeysAndValues) {
        assertFalse(sendDecision(player, decisionKeysAndValues));
    }

    protected void playCardSuccessfully(Entity playedCard) {
        assertTrue(
                playCard(playedCard));
    }

    protected boolean playCard(Entity playedCard) {
        return TestUtil.playCard(world, playedCard);
    }

    protected boolean useTrigger(Entity usedCard, int triggerIndex) {
        return TestUtil.useTrigger(world, usedCard, triggerIndex);
    }

    protected void useTriggerSuccessfully(Entity usedCard, int triggerIndex) {
        assertTrue(useTrigger(usedCard, triggerIndex));
    }

    protected Entity findEntity(String source, String filter) {
        return world.getSystem(CardFilteringSystem.class).findFirstCard(null, null, source, filter);
    }

    protected Entity getPlayer(String username) {
        return world.getSystem(PlayerResolverSystem.class).findPlayerEntity(username);
    }
}
