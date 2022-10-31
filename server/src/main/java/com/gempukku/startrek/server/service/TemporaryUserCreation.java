package com.gempukku.startrek.server.service;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.hall.PlayedGameComponent;
import com.gempukku.startrek.server.game.PlayerGameInfo;
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
            Array<PlayerGameInfo> playersInfo = new Array<>();
            playersInfo.add(new PlayerGameInfo("test1", "test1", "red-shirt-male",
                    deckSystem.getPlayerDeck("test1", "temp")));
            playersInfo.add(new PlayerGameInfo("test2", "test2", "red-shirt-male",
                    deckSystem.getPlayerDeck("test2", "temp")));
            String gameId = gameHandler.createGame(playersInfo, true);

            SpawnSystem spawnSystem = hallWorld.getSystem(SpawnSystem.class);
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
