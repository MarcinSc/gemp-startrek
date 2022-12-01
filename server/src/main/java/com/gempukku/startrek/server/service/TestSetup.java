package com.gempukku.startrek.server.service;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.common.UserValidation;
import com.gempukku.startrek.decision.DecisionMade;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.hall.PlayedGameComponent;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.server.game.PlayerGameInfo;
import com.gempukku.startrek.server.game.StarTrekGameWebSocketHandler;
import com.gempukku.startrek.server.game.decision.DecisionSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;
import com.gempukku.startrek.server.hall.StarTrekHallContext;
import com.gempukku.startrek.test.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

            createGame(TestUtil.createDeckWithMissions(cardData));

            setupScenario();

            pairPlayers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupScenario() {
        Entity personnel = createCard("test1", "1_207");
        Entity dilemma1 = createCard("test2", "1_4");
        Entity dilemma2 = createCard("test2", "1_8");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity planetMission = missionOperations.findMission("test1", 4);
        zoneOperations.moveFromCurrentZoneToMission(personnel, planetMission, false);
        zoneOperations.setupCardToTopOfDilemmaPile(dilemma2, false);
        zoneOperations.setupCardToTopOfDilemmaPile(dilemma1, false);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "attemptPlanetMission",
                "missionId", getCardId(planetMission));
    }

    private Entity createCard(String username, String cardId) {
        return TestUtil.createCard(world, username, cardId);
    }

    private String getCardId(Entity entity) {
        return TestUtil.getCardId(world, entity);
    }

    private void sendDecisionSuccessfully(String username, String... decisionKeysAndValues) {
        DecisionSystem decisionSystem = world.getSystem(DecisionSystem.class);
        ExecutionStackSystem executionStackSystem = world.getSystem(ExecutionStackSystem.class);

        ObjectMap<String, String> decisionResult = new ObjectMap<>();
        for (int i = 0; i < decisionKeysAndValues.length; i += 2) {
            decisionResult.put(decisionKeysAndValues[i], decisionKeysAndValues[i + 1]);
        }

        DecisionMade decisionMade = new DecisionMade(decisionResult);
        decisionMade.setOrigin(username);

        boolean result = decisionSystem.decisionMade(decisionMade, executionStackSystem.getTopMostStackEntity());
        if (result) {
            executionStackSystem.processStack();
        }
    }

    private void createGame(StarTrekDeck deck) {
        createGame(deck, deck);
    }

    private void createGame(StarTrekDeck deck1, StarTrekDeck deck2) {
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
