package com.gempukku.startrek.server.hall;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.event.RuntimeEntityEventDispatcher;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.server.RemoteEntityManagerHandler;
import com.gempukku.startrek.server.common.NetworkEntityConfigurationSystem;
import com.gempukku.startrek.server.game.StarTrekGameWebSocketHandler;
import com.gempukku.startrek.server.service.DummyGdx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class StarTrekHallContext {
    @Autowired
    private DummyGdx dummyGdx;
    @Autowired
    private StarTrekGameWebSocketHandler starTrekGameWebSocketHandler;

    private World hallEntityWorld;

    @PostConstruct
    public void buildHallEntityWorld() {
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        worldConfigurationBuilder.with(
                // Base systems
                new SpawnSystem(),
                new EventSystem(new RuntimeEntityEventDispatcher()),

                // Specific system
                new GameHallSystem(),
                new PairingSystem(starTrekGameWebSocketHandler),
                new StarTrekServerDeckSystem(),

                // Network systems
                new RemoteEntityManagerHandler(),
                new NetworkEntityConfigurationSystem());

        hallEntityWorld = new World(worldConfigurationBuilder.build());
    }

    public World getHallEntityWorld() {
        return hallEntityWorld;
    }
}
