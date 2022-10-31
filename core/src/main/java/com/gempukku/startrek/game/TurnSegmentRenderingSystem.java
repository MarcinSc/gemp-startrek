package com.gempukku.startrek.game;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.texture.TextureReference;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteComponent;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteDefinition;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.TextBlock;
import com.gempukku.libgdx.lib.graph.artemis.text.TextComponent;
import com.gempukku.libgdx.lib.graph.artemis.text.TextSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.turn.TurnComponent;
import com.gempukku.startrek.game.turn.TurnSegment;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;

public class TurnSegmentRenderingSystem extends BaseSystem {
    private PlayerPositionSystem playerPositionSystem;
    private SpawnSystem spawnSystem;
    private TransformSystem transformSystem;
    private SpriteSystem spriteSystem;
    private TextSystem textSystem;

    private String currentPlayer;
    private TurnSegment currentTurnSegment;
    private int displayedPointCount = -1;

    private Entity lowerPlayerTurnProcedure;
    private Entity upperPlayerTurnProcedure;

    @Override
    protected void processSystem() {
        if (lowerPlayerTurnProcedure == null) {
            float zTranslate = 3f;

            lowerPlayerTurnProcedure = spawnSystem.spawnEntity("game/playerTurnProcedure.template");
            transformSystem.setTransform(lowerPlayerTurnProcedure,
                    new Matrix4()
                            .translate(0, 0, zTranslate));

            upperPlayerTurnProcedure = spawnSystem.spawnEntity("game/playerTurnProcedure.template");
            transformSystem.setTransform(upperPlayerTurnProcedure,
                    new Matrix4()
                            .translate(0, 0, -zTranslate));
        }

        Entity turnSequenceEntity = LazyEntityUtil.findEntityWithComponent(world, TurnSequenceComponent.class);
        Entity turnEntity = LazyEntityUtil.findEntityWithComponent(world, TurnComponent.class);
        if (turnSequenceEntity != null && turnEntity != null) {
            String currentPlayer = turnSequenceEntity.getComponent(TurnSequenceComponent.class).getCurrentPlayer();
            TurnSegment turnSegment = turnEntity.getComponent(TurnComponent.class).getTurnSegment();

            Entity playerEntity = playerPositionSystem.getPlayerEntity(currentPlayer);
            int pointCount = playerEntity.getComponent(PlayerPublicStatsComponent.class).getCounterCount();
            if (!currentPlayer.equals(this.currentPlayer) || turnSegment != currentTurnSegment
                    || displayedPointCount != pointCount) {
                this.currentPlayer = currentPlayer;
                currentTurnSegment = turnSegment;
                this.displayedPointCount = pointCount;

                PlayerPosition currentPlayerPosition = playerPositionSystem.getPlayerPosition(currentPlayer);

                for (PlayerPosition playerPosition : Array.with(PlayerPosition.Lower, PlayerPosition.Upper)) {
                    boolean activePlayer = playerPosition == currentPlayerPosition;
                    Entity procedureEntity = getPlayerTurnProcedure(playerPosition);
                    Array<SpriteDefinition> sprites = procedureEntity.getComponent(SpriteComponent.class).getSprites();
                    setRegion(sprites, 1,
                            (activePlayer && turnSegment == TurnSegment.PLAY_AND_DRAW_CARDS) ?
                                    "play-and-draw-active" : "play-and-draw");
                    setRegion(sprites, 2,
                            (activePlayer && turnSegment == TurnSegment.EXECUTE_ORDERS) ?
                                    "execute-orders-active" : "execute-orders");
                    setRegion(sprites, 3,
                            (activePlayer && turnSegment == TurnSegment.DISCARD_EXCESS_CARDS) ?
                                    "discard-cards-active" : "discard-cards");
                    spriteSystem.updateSprites(procedureEntity.getId());

                    String pointsText;
                    if (activePlayer && turnSegment == TurnSegment.PLAY_AND_DRAW_CARDS) {
                        pointsText = String.valueOf(pointCount);
                    } else {
                        pointsText = "";
                    }
                    TextBlock pointsTextBlock = procedureEntity.getComponent(TextComponent.class).getTextBlocks().get(0);
                    pointsTextBlock.setText(pointsText);
                    textSystem.updateText(procedureEntity.getId(), 0);
                }
            }
        }
    }

    private void setRegion(Array<SpriteDefinition> sprites, int index, String region) {
        ((TextureReference) sprites.get(index).getProperties().get("Texture")).setRegion(region);
    }

    private Entity getPlayerTurnProcedure(PlayerPosition playerPosition) {
        if (playerPosition == PlayerPosition.Lower)
            return lowerPlayerTurnProcedure;
        else
            return upperPlayerTurnProcedure;
    }
}
