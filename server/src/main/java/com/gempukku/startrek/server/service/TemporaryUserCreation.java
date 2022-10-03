package com.gempukku.startrek.server.service;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.hall.PlayedGameComponent;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import com.gempukku.startrek.server.game.StarTrekGameWebSocketHandler;
import com.gempukku.startrek.server.hall.StarTrekHallContext;
import com.gempukku.startrek.server.hall.StarTrekServerDeckSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TemporaryUserCreation {
    @Autowired
    private UserService userService;
    @Autowired
    private StarTrekHallContext starTrekHallContext;
    @Autowired
    private StarTrekGameWebSocketHandler gameHandler;

    @PostConstruct
    public void createTestUsers() {
        try {
            userService.registerUser("test1", "test1@gmail.com", "testtest");
            userService.registerUser("test2", "test2@gmail.com", "testtest");
            System.out.println("Users test1 and test2 registered");

            World hallWorld = starTrekHallContext.getHallEntityWorld();
            StarTrekServerDeckSystem deckSystem = hallWorld.getSystem(StarTrekServerDeckSystem.class);

            Array<String> players = new Array<>(new String[]{"test1", "test2"});
            ObjectMap<String, StarTrekDeck> playerDecks = new ObjectMap<>();
            playerDecks.put("test1", deckSystem.getPlayerDeck("test1", "temp"));
            playerDecks.put("test2", deckSystem.getPlayerDeck("test2", "temp"));
            String gameId = gameHandler.createGame(playerDecks);

            ServerSpawnSystem spawnSystem = hallWorld.getSystem(ServerSpawnSystem.class);
            Entity hallGame = spawnSystem.spawnEntity("hall/hallGame.template");
            PlayedGameComponent game = hallGame.getComponent(PlayedGameComponent.class);
            game.setGameId(gameId);
            game.setOwners(players);

            System.out.println("Paired game between test1 and test2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
