package com.gempukku.startrek.server.hall;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.hall.GameHallComponent;
import com.gempukku.startrek.hall.GameHallPlayerComponent;
import com.gempukku.startrek.hall.PlayedGameComponent;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.hall.event.SearchForGame;
import com.gempukku.startrek.hall.event.StopSearchingForGame;
import com.gempukku.startrek.server.game.PlayerGameInfo;
import com.gempukku.startrek.server.game.StarTrekGameWebSocketHandler;

import java.util.List;
import java.util.concurrent.Future;

public class PairingSystem extends BaseEntitySystem {
    private GameHallSystem gameHallSystem;
    private EventSystem eventSystem;
    private ComponentMapper<GameHallPlayerComponent> playerComponentMapper;
    private StarTrekServerDeckSystem deckSystem;
    private SpawnSystem spawnSystem;
    private StarTrekGameWebSocketHandler gameHandler;

    private Future<List<String>> finishedGamesFuture;

    public PairingSystem(StarTrekGameWebSocketHandler starTrekGameWebSocketHandler) {
        super(Aspect.all(GameHallPlayerComponent.class));
        gameHandler = starTrekGameWebSocketHandler;
    }

    @EventListener
    public void searchForGame(SearchForGame searchForGame, Entity entity) {
        StarTrekDeck deck = findDeck(searchForGame.getOrigin(),
                searchForGame.getStarterDeckId(), searchForGame.getPlayerDeckId());
        if (deck != null) {
            Entity playerEntity = gameHallSystem.getPlayer(searchForGame.getOrigin());
            GameHallPlayerComponent player = playerEntity.getComponent(GameHallPlayerComponent.class);

            player.setWaitingForGame(true);
            player.setChosenPlayerDeck(searchForGame.getPlayerDeckId());
            player.setChosenStarterDeck(searchForGame.getStarterDeckId());
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        }
    }

    @EventListener
    public void stopSearchingForGame(StopSearchingForGame stopSearchingForGame, Entity entity) {
        Entity playerEntity = gameHallSystem.getPlayer(stopSearchingForGame.getOrigin());
        GameHallPlayerComponent player = playerEntity.getComponent(GameHallPlayerComponent.class);

        player.setWaitingForGame(false);
        player.setChosenStarterDeck(null);
        player.setChosenStarterDeck(null);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    @Override
    protected void processSystem() {
        Entity waitingPlayer = null;
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            GameHallPlayerComponent player = playerComponentMapper.get(ids[i]);
            if (player.isWaitingForGame()) {
                if (waitingPlayer != null) {
                    Entity thisPlayer = world.getEntity(ids[i]);
                    createGame(waitingPlayer, thisPlayer);
                    waitingPlayer = null;
                } else {
                    waitingPlayer = world.getEntity(ids[i]);
                }
            }
        }
    }

    private void createGame(Entity player1Entity, Entity player2Entity) {
        GameHallPlayerComponent player1 = playerComponentMapper.get(player1Entity);
        GameHallPlayerComponent player2 = playerComponentMapper.get(player2Entity);

        Array<String> players = new Array<>(new String[]{player1.getOwner(), player2.getOwner()});
        Array<PlayerGameInfo> playersInfo = new Array<>();
        playersInfo.add(createPlayerGameInfo(player1));
        playersInfo.add(createPlayerGameInfo(player2));
        String gameId = gameHandler.createGame(playersInfo);

        Entity hallGame = spawnSystem.spawnEntity("hall/hallGame.template");
        PlayedGameComponent game = hallGame.getComponent(PlayedGameComponent.class);
        game.setGameId(gameId);
        game.setOwners(players);

        player1.setWaitingForGame(false);
        player2.setWaitingForGame(false);
        eventSystem.fireEvent(EntityUpdated.instance, player1Entity);
        eventSystem.fireEvent(EntityUpdated.instance, player2Entity);

        Entity gameHallEntity = gameHallSystem.getGameHallEntity();
        GameHallComponent gameHall = gameHallEntity.getComponent(GameHallComponent.class);
        gameHall.setGameCount(gameHall.getGameCount() + 1);
        eventSystem.fireEvent(EntityUpdated.instance, gameHallEntity);
    }

    private PlayerGameInfo createPlayerGameInfo(GameHallPlayerComponent player) {
        return new PlayerGameInfo(player.getOwner(), player.getOwner(),
                player.getAvatar(), findDeck(player.getOwner(),
                player.getChosenStarterDeck(), player.getChosenPlayerDeck()));
    }

    private StarTrekDeck findDeck(String username, String starterDeckId, String playerDeckId) {
        if (starterDeckId != null)
            return deckSystem.getStarterDeck(starterDeckId);
        return deckSystem.getPlayerDeck(username, playerDeckId);
    }
}
