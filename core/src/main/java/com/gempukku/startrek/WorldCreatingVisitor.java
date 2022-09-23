package com.gempukku.startrek;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.event.RuntimeEntityEventDispatcher;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.input.InputProcessorSystem;
import com.gempukku.libgdx.lib.artemis.property.PropertySystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.texture.RuntimeTextureHandler;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.renderer.PipelineRendererSystem;
import com.gempukku.libgdx.lib.graph.artemis.time.TimeKeepingSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.libgdx.network.JsonDataSerializer;
import com.gempukku.libgdx.network.client.WebsocketRemoteClientConnector;
import com.gempukku.libgdx.network.json.JsonValueNetworkMessageMarshaller;
import com.gempukku.libgdx.network.json.JsonValueServerSessionProducer;
import com.gempukku.startrek.common.ConnectionParamSystem;
import com.gempukku.startrek.common.FontProviderSystem;
import com.gempukku.startrek.common.IncomingUpdatesProcessor;
import com.gempukku.startrek.hall.*;
import com.gempukku.startrek.login.LoginScreenRenderer;

import java.io.IOException;
import java.io.Reader;

public class WorldCreatingVisitor implements GameSceneVisitor<World> {
    private WorldConfigurationBuilder worldConfigurationBuilder;

    public WorldCreatingVisitor(WorldConfigurationBuilder worldConfigurationBuilder) {
        this.worldConfigurationBuilder = worldConfigurationBuilder;
    }

    @Override
    public World visitLoginScene() {
        createCommonSystems();
        worldConfigurationBuilder.with(new LoginScreenRenderer());

        World world = new World(worldConfigurationBuilder.build());

        SpawnSystem spawnSystem = world.getSystem(SpawnSystem.class);
        spawnSystem.spawnEntity("login/rendering.template");
        return world;
    }

    @Override
    public World visitHallScene() {
        createCommonSystems();
        worldConfigurationBuilder.with(
                new DeckBoxRenderingSystem(),
                new GameHallConnectionInitializer(),
                new GameHallPlayerProviderSystem(),
                new GameHallUIRenderer(),
                new HallConnectionLostHandling(),
                new WebsocketRemoteClientConnector<>(
                        new JsonDataSerializer(), new JsonValueServerSessionProducer(),
                        new JsonValueNetworkMessageMarshaller()),
                new FontProviderSystem(),
                new IncomingUpdatesProcessor(),
                new TransitionToGame());

        World world = new World(worldConfigurationBuilder.build());
        SpawnSystem spawnSystem = world.getSystem(SpawnSystem.class);
        spawnSystem.spawnEntity("hall/rendering.template");
        return world;
    }

    private void createCommonSystems() {
        ObjectMap<String, String> properties = new ObjectMap<>();
        try (Reader reader = Gdx.files.internal("application.properties").reader()) {
            PropertiesUtils.load(properties, reader);
        } catch (IOException exp) {
            throw new GdxRuntimeException(exp);
        }
        worldConfigurationBuilder.with(
                new EventSystem(new RuntimeEntityEventDispatcher()),
                new TimeKeepingSystem(),
                new SpawnSystem(),
                new HierarchySystem(),
                new TransformSystem(),
                new InputProcessorSystem(),
                new TextureSystem(new RuntimeTextureHandler()),
                new PropertySystem(properties),
                new PipelineRendererSystem(),
                new ConnectionParamSystem(),
                new StageSystem(1));
    }
}
