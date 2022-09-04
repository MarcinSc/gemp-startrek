package com.gempukku.startrek.server.hall;

import com.artemis.BaseSystem;
import org.springframework.web.socket.WebSocketSession;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class GamesContainerSystem extends BaseSystem {
//    private JSONEntitySerializer jsonEntitySerializer;
//    private int lastGameId = -1;
//
//    private Map<String, OverpowerGameContainer> gameContainers = new ConcurrentHashMap<>();
//
//    private Map<WebSocketSession, WebsocketClientConnection> connections = new ConcurrentHashMap<>();
//
//    private ExecutorService executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MINUTES,
//            new LinkedBlockingQueue<>(1000),
//            new ThreadFactory() {
//                @Override
//                public Thread newThread(Runnable r) {
//                    Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
//                            "Game-Executor",
//                            0);
//                    if (t.isDaemon())
//                        t.setDaemon(false);
//                    if (t.getPriority() != Thread.NORM_PRIORITY)
//                        t.setPriority(Thread.NORM_PRIORITY);
//                    return t;
//                }
//            });
//
//    public GamesContainerSystem(JSONEntitySerializer jsonEntitySerializer) {
//        this.jsonEntitySerializer = jsonEntitySerializer;
//    }
//
//    private OverpowerGameContainer getContainer(String gameId) {
//        return gameContainers.get(gameId);
//    }
//
//    public String createGame(List<PlayerGameSettings> playerGameSettings) {
//        String gameId = String.valueOf(++lastGameId);
//        OverpowerGameContainer gameContainer = new OverpowerGameContainer();
//        for (PlayerGameSettings playerGameSetting : playerGameSettings) {
//            gameContainer.addPlayer(playerGameSetting.getUsername(), playerGameSetting.getDeckList(), playerGameSetting.getPortrait());
//        }
//
//        gameContainer.startContext();
//        gameContainer.startGame();
//
//        gameContainers.put(gameId, gameContainer);
//
//        return gameId;
//    }
//
//    public void attachPlayer(final String gameId, final String playerName, WebSocketSession session) {
//        final WebsocketClientConnection connection = new WebsocketClientConnection(playerName, session, jsonEntitySerializer);
//        connections.put(session, connection);
//
//        executor.submit(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            final OverpowerGameContainer container = getContainer(gameId);
//                            container.getRemoteHandler().addClientConnection(playerName, connection);
//                        } catch (Exception exp) {
//                            exp.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    public void processEvent(String gameId, String playerName, WebSocketSession session,
//                             final String entityId, final EventFromClient event) {
//        final WebsocketClientConnection connection = connections.get(session);
//        executor.submit(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            connection.eventReceived(entityId, event);
//                        } catch (Exception exp) {
//                            exp.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    public void detachPlayer(final String gameId, final String playerName, WebSocketSession session) {
//        final WebsocketClientConnection connection = connections.get(session);
//        connections.remove(session);
//        executor.submit(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            final OverpowerGameContainer container = getContainer(gameId);
//                            if (container != null)
//                                container.getRemoteHandler().removeClientConnection(playerName, connection);
//                        } catch (Exception exp) {
//                            exp.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    public Future<List<String>> removeFinishedGames() {
//        return executor.submit(
//                new Callable<List<String>>() {
//                    @Override
//                    public List<String> call() throws Exception {
//                        List<String> removedGames = new LinkedList<>();
//                        Iterator<Map.Entry<String, OverpowerGameContainer>> containersIterator = gameContainers.entrySet().iterator();
//                        while (containersIterator.hasNext()) {
//                            Map.Entry<String, OverpowerGameContainer> gameContainerEntry = containersIterator.next();
//                            String gameId = gameContainerEntry.getKey();
//                            OverpowerGameContainer container = gameContainerEntry.getValue();
//                            if (container.isGameFinished()) {
//                                removedGames.add(gameId);
//                                containersIterator.remove();
//
//                                container.getRemoteHandler().disconnectAllClients();
//                            }
//                        }
//                        return removedGames;
//                    }
//                });
//    }
//
    @Override
    protected void processSystem() {

    }
}
