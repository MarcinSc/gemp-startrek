package com.gempukku.startrek.server.hall;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.event.RuntimeEntityEventDispatcher;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.network.server.RemoteEntityManagerHandler;
import com.gempukku.startrek.server.common.DummApplicationSystem;
import com.gempukku.startrek.server.common.NetworkEntityConfigurationSystem;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import org.springframework.stereotype.Component;

@Component
public class StarTrekHallContext {
    private World hallEntityWorld;

    public StarTrekHallContext() {
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        worldConfigurationBuilder.with(
                new DummApplicationSystem(),
                new ServerSpawnSystem(),
                new EventSystem(new RuntimeEntityEventDispatcher()),
                new TransformSystem(),
                new HierarchySystem(),
                new GameHallSystem(),
                new RemoteEntityManagerHandler(),
                new NetworkEntityConfigurationSystem(),
                new HallEntityProviderSystem());

        hallEntityWorld = new World(worldConfigurationBuilder.build());
    }

    public World getHallEntityWorld() {
        return hallEntityWorld;
    }
}
