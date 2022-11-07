package com.gempukku.startrek.server.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.network.JsonDataSerializer;
import com.gempukku.libgdx.network.server.RemoteEntityManagerHandler;
import com.gempukku.libgdx.network.server.config.annotation.SerializeToClientsConfig;
import com.gempukku.startrek.server.service.CardDataService;
import com.gempukku.startrek.server.websocket.OneConnectionPerUserIntoContext;
import com.gempukku.startrek.server.websocket.WebSocketChannelConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

@Component
public class StarTrekGameWebSocketHandler extends TextWebSocketHandler implements WebSocketChannelConfig {
    @Autowired
    private CardDataService cardDataService;

    private final ObjectMap<String, StarTrekGameHolder> games = new ObjectMap<>();
    private final ObjectMap<String, OneConnectionPerUserIntoContext> gameContexts = new ObjectMap<>();

    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1,
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
                            "Games-Executor",
                            0);
                    if (t.isDaemon())
                        t.setDaemon(false);
                    if (t.getPriority() != Thread.NORM_PRIORITY)
                        t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                }
            });

    @Override
    public String getPath() {
        return "/game";
    }

    public String createGame(Array<PlayerGameInfo> players, boolean test) {
        String gameId = UUID.randomUUID().toString();

        StarTrekGameHolder gameHolder = new StarTrekGameHolder(cardDataService.getCardData(), test);

        OneConnectionPerUserIntoContext gameContext = new OneConnectionPerUserIntoContext(
                executor, gameHolder.getGameWorld().getSystem(RemoteEntityManagerHandler.class),
                new JsonDataSerializer());

        gameContext.addNetworkEntitySerializationConfig(
                new SerializeToClientsConfig());

        for (PlayerGameInfo playerGameInfo : players) {
            gameHolder.addPlayer(playerGameInfo);
        }
        gameHolder.setupGame();
        gameHolder.processGame();

        games.put(gameId, gameHolder);
        gameContexts.put(gameId, gameContext);
        return gameId;
    }

    public void removeGame(String gameId) {
        gameContexts.remove(gameId).closeAllConnections();
        games.remove(gameId).dispose();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String gameId = getGameId(session);
        OneConnectionPerUserIntoContext gameContext = gameContexts.get(gameId);
        if (gameContext == null) {
            session.close();
        } else {
            gameContext.connectionEstablished(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String gameId = getGameId(session);
        OneConnectionPerUserIntoContext gameContext = gameContexts.get(gameId);
        StarTrekGameHolder game = games.get(gameId);
        if (gameContext == null || game == null) {
            try {
                session.close();
            } catch (IOException e) {
                // Ignore
            }
        } else {
            gameContext.messageReceived(session, message);
            executor.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                game.processGame();
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }
                        }
                    });
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String gameId = getGameId(session);
        OneConnectionPerUserIntoContext gameContext = gameContexts.get(gameId);
        if (gameContext == null) {
            session.close();
        } else {
            gameContext.connectionClosed(session);
        }
    }

    private static String getGameId(WebSocketSession session) {
        URI uri = session.getUri();
        List<String> game = splitQuery(uri).get("gameId");
        return (game != null) ? game.get(0) : null;
    }

    private static Map<String, List<String>> splitQuery(URI uri) {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = uri.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            try {
                final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                if (!query_pairs.containsKey(key)) {
                    query_pairs.put(key, new LinkedList<String>());
                }
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                query_pairs.get(key).add(value);
            } catch (UnsupportedEncodingException exp) {
                throw new IllegalStateException("Unable to find UTF-8 encoding");
            }
        }
        return query_pairs;
    }
}
