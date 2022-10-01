package com.gempukku.startrek.server.game;

import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.server.websocket.WebSocketChannelConfig;
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
    private final Map<String, StarTrekGameHolder> games = new HashMap<>();

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

    public String createGame(Array<String> players) {
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, new StarTrekGameHolder(executor));
        return gameId;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String gameId = getGameId(session);
        StarTrekGameHolder starTrekGameHolder = games.get(gameId);
        if (starTrekGameHolder == null) {
            session.close();
        } else {
            starTrekGameHolder.connectUser(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String gameId = getGameId(session);
        StarTrekGameHolder starTrekGameHolder = games.get(gameId);
        if (starTrekGameHolder == null) {
            try {
                session.close();
            } catch (IOException e) {
                // Ignore
            }
        } else {
            starTrekGameHolder.messageReceived(session, message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String gameId = getGameId(session);
        StarTrekGameHolder starTrekGameHolder = games.get(gameId);
        if (starTrekGameHolder == null) {
            session.close();
        } else {
            starTrekGameHolder.sessionClosed(session);
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
