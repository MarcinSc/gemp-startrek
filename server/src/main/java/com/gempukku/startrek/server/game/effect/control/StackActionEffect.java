package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class StackActionEffect extends EffectSystem {
    private SpawnSystem spawnSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;
    private ComponentMapper<EffectMemoryComponent> effectMemoryComponentMapper;

    public StackActionEffect() {
        super("stackForEachPlayer", "stackActionTemplate");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        String effectType = gameEffect.getType();
        if (effectType.equals("stackForEachPlayer")) {
            stackForEachPlayerEffect(gameEffectEntity, gameEffect, memory);
        } else if (effectType.equals("stackActionTemplate")) {
            stackActionTemplate(gameEffectEntity, gameEffect, memory);
        }
    }

    private void stackForEachPlayerEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        GameComponent game = LazyEntityUtil.findEntityWithComponent(world, GameComponent.class).getComponent(GameComponent.class);
        Array<String> players = game.getPlayers();

        String playerIndex = memory.get("playerIndex");
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
            String actionType = action.getString("type");
            boolean createMemory = action.getBoolean("memory", false);

            action.addChild("player", new JsonValue("username(" + player + ")"));
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            if (createMemory) {
                effectMemoryComponentMapper.create(stackedEntity).setMemoryType("action - " + actionType);
            }
            newGameEffect.setType(actionType);
            newGameEffect.setData(action);
            memory.put("playerIndex", String.valueOf(nextPlayerIndex));

            stackEffect(stackedEntity);
        }
    }

    private void stackActionTemplate(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        String template = gameEffect.getDataString("template");

        Entity spawnedAction = spawnSystem.spawnEntity(template);

        removeEffectFromStack(gameEffectEntity);

        stackEffect(spawnedAction);
    }

}
