package com.gempukku.startrek;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.gempukku.libgdx.lib.artemis.animation.AnimationDirectorSystem;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.camera.TopDownCameraController;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluatePropertySystem;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.event.RuntimeEntityEventDispatcher;
import com.gempukku.libgdx.lib.artemis.font.BitmapFontSystem;
import com.gempukku.libgdx.lib.artemis.font.RuntimeBitmapFontHandler;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.input.InputProcessorSystem;
import com.gempukku.libgdx.lib.artemis.input.UserInputSystem;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickingSystem;
import com.gempukku.libgdx.lib.artemis.property.PropertySystem;
import com.gempukku.libgdx.lib.artemis.shape.ShapeSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.renderer.PipelineRendererSystem;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionSystem;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteBatchSystem;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.TextSystem;
import com.gempukku.libgdx.lib.graph.artemis.time.TimeKeepingSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.libgdx.network.JsonDataSerializer;
import com.gempukku.libgdx.network.client.ServerEntitySystem;
import com.gempukku.libgdx.network.client.WebsocketRemoteClientConnector;
import com.gempukku.libgdx.network.json.JsonValueNetworkMessageMarshaller;
import com.gempukku.libgdx.network.json.JsonValueServerSessionProducer;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.ConfigureTextureSystem;
import com.gempukku.startrek.common.ConnectionParamSystem;
import com.gempukku.startrek.common.FontProviderSystem;
import com.gempukku.startrek.common.IncomingUpdatesProcessor;
import com.gempukku.startrek.game.*;
import com.gempukku.startrek.game.ability.NoOpClientCardAbilityHandler;
import com.gempukku.startrek.game.config.ConfigureTextSystem;
import com.gempukku.startrek.game.decision.ClientDecisionSystem;
import com.gempukku.startrek.game.decision.ClientMandatoryTriggerActionsDecisionHandler;
import com.gempukku.startrek.game.decision.ClientOptionalTriggerActionsDecisionHandler;
import com.gempukku.startrek.game.decision.ClientPlayOrDrawDecisionHandler;
import com.gempukku.startrek.game.filter.ClientIdInFilterHandler;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.PlayerInfoRenderingSystem;
import com.gempukku.startrek.game.render.TurnSegmentRenderingSystem;
import com.gempukku.startrek.game.zone.GameStateCardsTrackingSystem;
import com.gempukku.startrek.game.zone.InitialGameStateCardsCreatorSystem;
import com.gempukku.startrek.hall.*;
import com.gempukku.startrek.login.LoginScreenRenderer;

import java.io.IOException;
import java.io.Reader;

public class WorldCreatingVisitor implements GameSceneVisitor<World> {
    private WorldConfigurationBuilder worldConfigurationBuilder;
    private CardData cardData;

    public WorldCreatingVisitor(WorldConfigurationBuilder worldConfigurationBuilder) {
        this.worldConfigurationBuilder = worldConfigurationBuilder;
        cardData = new CardData();
        cardData.initializeCards();
    }

    @Override
    public World visitLoginScene() {
        createCommonClientSystems();
        worldConfigurationBuilder.with(new LoginScreenRenderer());

        World world = new World(worldConfigurationBuilder.build());

        SpawnSystem spawnSystem = world.getSystem(SpawnSystem.class);
        spawnSystem.spawnEntity("login/rendering.template");
        return world;
    }

    @Override
    public World visitHallScene() {
        createCommonClientSystems();
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

    @Override
    public World visitPlayingGameScene(String gameId) {
        CommonGameWorldBuilder.createCommonSystems(worldConfigurationBuilder);
        createCommonClientSystems();
        worldConfigurationBuilder.with(
                new GameConnectionInitializer(),
                new GameConnectionLostHandling(),
                new WebsocketRemoteClientConnector<>(
                        new JsonDataSerializer(), new JsonValueServerSessionProducer(),
                        new JsonValueNetworkMessageMarshaller()),
                new ServerEntitySystem(),

                new BitmapFontSystem(new RuntimeBitmapFontHandler()),
                new TextSystem(),
                new ConfigureTextSystem(),

                new AnimationDirectorSystem(),
                new SpriteBatchSystem(),
                new SpriteSystem(),
                new CameraSystem(new TopDownCameraController()),
                new IncomingUpdatesProcessor(),

                new CardLookupSystem(cardData),
                new PlayerPositionSystem(),
                new InitialGameStateCardsCreatorSystem(),
                new GameStateCardsTrackingSystem(),

                new ShapeSystem(),
                new ShapePickingSystem(),
                new SelectionSystem(),

                // Card abilities
                new NoOpClientCardAbilityHandler(),

                // Decision-related
                new ClientDecisionSystem(),
                new ClientPlayOrDrawDecisionHandler(),
                new ClientMandatoryTriggerActionsDecisionHandler(),
                new ClientOptionalTriggerActionsDecisionHandler(),

                new UserInputSystem(1),

                new ClientIdInFilterHandler(),

                // Rendering
                new CardRenderingSystem(),
                new PlayerInfoRenderingSystem(),
                new TurnSegmentRenderingSystem());

        World world = new World(worldConfigurationBuilder.build());
        SpawnSystem spawnSystem = world.getSystem(SpawnSystem.class);

        // Setup
        spawnSystem.spawnEntities("game/gameSetup.entities");

        Entity gameEntity = LazyEntityUtil.findEntityWithComponent(world, StarTrekGameComponent.class);
        StarTrekGameComponent game = gameEntity.getComponent(StarTrekGameComponent.class);
        game.setGameId(gameId);

        world.process();
        // Create game entities
        spawnSystem.spawnEntity("game/playArea.template");

        return world;
    }

    private void createCommonClientSystems() {
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
                new EvaluatePropertySystem(),
                new TextureSystem(),
                new ConfigureTextureSystem(),
                new PropertySystem(properties),
                new PipelineRendererSystem(),
                new ConnectionParamSystem(),
                new StageSystem(1));
    }
}
