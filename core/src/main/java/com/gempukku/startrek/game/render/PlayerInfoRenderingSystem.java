package com.gempukku.startrek.game.render;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.texture.TextureReference;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteComponent;
import com.gempukku.libgdx.lib.graph.artemis.text.TextBlock;
import com.gempukku.libgdx.lib.graph.artemis.text.TextComponent;
import com.gempukku.libgdx.lib.graph.artemis.text.TextSystem;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;

public class PlayerInfoRenderingSystem extends BaseEntitySystem {
    private PlayerPositionSystem playerPositionSystem;
    //    private CameraSystem cameraSystem;
    private SpawnSystem spawnSystem;
    private TransformSystem transformSystem;
    private TextSystem textSystem;

    private int addedPlayers = 0;
    private final ObjectMap<String, Integer> displayedPoints = new ObjectMap<>();
    private final ObjectMap<String, Entity> nameplates = new ObjectMap<>();

    public PlayerInfoRenderingSystem() {
        super(Aspect.all(GamePlayerComponent.class));
    }

    @Override
    protected void processSystem() {
        if (addedPlayers < 2) {
            createPlayerNameplates();
        } else {
            IntBag playerIds = getSubscription().getEntities();
            for (int i = 0; i < playerIds.size(); i++) {
                Entity playerEntity = world.getEntity(playerIds.get(i));
                GamePlayerComponent player = playerEntity.getComponent(GamePlayerComponent.class);
                PlayerPublicStatsComponent playerStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
                String username = player.getName();
                int playerPoints = playerStats.getPointCount();
                if (playerPoints != displayedPoints.get(username)) {
                    Entity nameplateEntity = nameplates.get(username);
                    TextComponent textBlocks = nameplateEntity.getComponent(TextComponent.class);
                    TextBlock pointsText = textBlocks.getTextBlocks().get(1);
                    pointsText.setText(String.valueOf(playerPoints));
                    displayedPoints.put(username, playerPoints);
                    textSystem.updateText(nameplateEntity.getId(), 1);
                }
            }
        }
    }

    private void createPlayerNameplates() {
        //Camera camera = cameraSystem.getCamera("main");

        IntBag playerIds = getSubscription().getEntities();
        for (int i = 0; i < playerIds.size(); i++) {
            Entity playerEntity = world.getEntity(playerIds.get(i));
            GamePlayerComponent player = playerEntity.getComponent(GamePlayerComponent.class);

            Entity nameplateEntity = spawnSystem.spawnEntity("game/playerNameplate.template");
            nameplates.put(player.getName(), nameplateEntity);

            SpriteComponent sprites = nameplateEntity.getComponent(SpriteComponent.class);
            TextureReference avatarTexture = (TextureReference) sprites.getSprites().get(0).getProperties().get("Texture");
            avatarTexture.setRegion(player.getAvatar());

            TextComponent textBlocks = nameplateEntity.getComponent(TextComponent.class);
            TextBlock displayNameText = textBlocks.getTextBlocks().get(0);
            displayNameText.setText(player.getDisplayName());

            TextBlock pointsText = textBlocks.getTextBlocks().get(1);
            pointsText.setText("0");
            displayedPoints.put(player.getName(), 0);

            PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(player.getName());

            float zDistance = 2f;
            float zTranslate = playerPosition == PlayerPosition.Lower ? zDistance : -zDistance;

            transformSystem.setTransform(nameplateEntity,
                    new Matrix4()
                            .translate(-6.2f, 0.5f, zTranslate)
                            .scl(1.5f));

            addedPlayers++;
        }
    }
}
