package com.gempukku.startrek.server.service;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.common.UserValidation;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.hall.PlayedGameComponent;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.server.game.PlayerGameInfo;
import com.gempukku.startrek.server.game.StarTrekGameWebSocketHandler;
import com.gempukku.startrek.server.hall.StarTrekHallContext;
import com.gempukku.startrek.server.test.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertNotNull;

@Profile("test")
@Service
public class TestSetup {
    @Autowired
    private UserService userService;
    @Autowired
    private StarTrekHallContext starTrekHallContext;
    @Autowired
    private StarTrekGameWebSocketHandler gameHandler;
    @Autowired
    private CardDataService cardDataService;

    private CardData cardData;
    private World hallWorld;
    private String gameId;
    private World world;

    @PostConstruct
    public void setup() {
        cardData = cardDataService.getCardData();
        hallWorld = starTrekHallContext.getHallEntityWorld();

        try {
            createTestUsers();

            setupScenario();

            pairPlayers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupScenario() {
        setupGame(createDeckWithMissions("1_217"));

        Entity card = getCardsInHand("test1").get(0);
        playCardSuccessfully(card);

        assertNotNull(card.getComponent(FaceDownCardInMissionComponent.class));

        // Pass on mandatory
        sendDecisionSuccessfully("test1", "action", "pass");
        sendDecisionSuccessfully("test2", "action", "pass");

    }

    private StarTrekDeck createDeckWithMissions(String... cards) {
        return TestUtil.createDeckWithMissions(cardData, cards);
    }

    private Array<Entity> getCardsInHand(String player) {
        return TestUtil.getCardsInHand(world, player);
    }

    private Entity createCard(String username, String cardId) {
        return TestUtil.createCard(world, username, cardId);
    }

    private String getCardId(Entity entity) {
        return TestUtil.getCardId(world, entity);
    }

    private void playCardSuccessfully(Entity card) {
        TestUtil.playCard(world, card);
    }

    private void sendDecisionSuccessfully(String username, String... decisionKeysAndValues) {
        TestUtil.sendDecision(world, username, decisionKeysAndValues);
    }

    private void setupGame(StarTrekDeck deck) {
        setupGame(deck, deck);
    }

    private void setupGame(StarTrekDeck deck1, StarTrekDeck deck2) {
        Array<PlayerGameInfo> playersInfo = new Array<>();
        playersInfo.add(new PlayerGameInfo("test1", "test1", "red-shirt-male",
                deck1));
        playersInfo.add(new PlayerGameInfo("test2", "test2", "red-shirt-male",
                deck2));
        gameId = gameHandler.createGame(playersInfo, true);
        world = gameHandler.getGameHolder(gameId).getGameWorld();
    }

    private void pairPlayers() {
        Array<String> players = new Array<>(new String[]{"test1", "test2"});
        SpawnSystem spawnSystem = hallWorld.getSystem(SpawnSystem.class);
        Entity hallGame = spawnSystem.spawnEntity("hall/hallGame.template");
        PlayedGameComponent game = hallGame.getComponent(PlayedGameComponent.class);
        game.setGameId(gameId);
        game.setOwners(players);
        System.out.println("Paired game between test1 and test2");
    }

    private void createTestUsers() throws UserValidation.UserValidationException, UserConflictException {
        userService.registerUser("test1", "test1@gmail.com", "testtest");
        userService.registerUser("test2", "test2@gmail.com", "testtest");
        System.out.println("Users test1 and test2 registered");
    }
}
