package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class StackActionEffect extends EffectSystem {
    private SpawnSystem spawnSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public StackActionEffect() {
        super("stackForEachPlayer", "stackActionTemplate");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String effectType = gameEffect.getType();
        if (effectType.equals("stackForEachPlayer")) {
            stackForEachPlayerEffect(gameEffectEntity, gameEffect);
        } else if (effectType.equals("stackActionTemplate")) {
            stackActionTemplate(gameEffectEntity, gameEffect);
        }
    }

    private void stackForEachPlayerEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        GameComponent game = LazyEntityUtil.findEntityWithComponent(world, GameComponent.class).getComponent(GameComponent.class);
        Array<String> players = game.getPlayers();

        String playerIndex = gameEffect.getMemory().get("playerIndex");
        int nextPlayerIndex = 0;
        if (playerIndex != null) {
            nextPlayerIndex = Integer.parseInt(playerIndex) + 1;
        }

        if (nextPlayerIndex == players.size) {
            // Finished all players - remove from stack
            removeEffectFromStack(gameEffectEntity);
        } else {
            String player = players.get(nextPlayerIndex);
            JsonValue action = gameEffect.getClonedDataObject("action");

            action.addChild("player", new JsonValue("username(" + player + ")"));
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            newGameEffect.setType(action.getString("type"));
            newGameEffect.setData(action);
            gameEffect.getMemory().put("playerIndex", String.valueOf(nextPlayerIndex));

            stackEffect(stackedEntity);
        }
    }

    private void stackActionTemplate(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String template = gameEffect.getDataString("template");

        Entity spawnedAction = spawnSystem.spawnEntity(template);

        removeEffectFromStack(gameEffectEntity);

        stackEffect(spawnedAction);
    }

}
