package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.GameEntityProvider;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public class StackActionEffect extends EffectSystem {
    private GameEffectSystem gameEffectSystem;
    private SpawnSystem spawnSystem;
    private GameEntityProvider gameEntityProvider;

    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;
    private ComponentMapper<EffectMemoryComponent> effectMemoryComponentMapper;

    public StackActionEffect() {
        super("stackForEachPlayer", "stackActionTemplate");
    }

    @Override
    public void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String effectType = gameEffect.getType();
        if (effectType.equals("stackForEachPlayer")) {
            stackForEachPlayerEffect(sourceEntity, gameEffect, memory);
        } else if (effectType.equals("stackActionTemplate")) {
            stackActionTemplate(sourceEntity, gameEffect, memory);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        String type = effect.getString("type");
        if (type.equals("stackForEachPlayer")) {
            ValidateUtil.effectExpectedFields(effect,
                    new String[]{"playerMemory", "action"},
                    new String[]{});
            validate(effect.get("action"));
        } else if (type.equals("stackActionTemplate")) {
            ValidateUtil.effectExpectedFields(effect,
                    new String[]{"template"},
                    new String[]{});
        }
    }

    private void stackForEachPlayerEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        GameComponent game = gameEntityProvider.getGameEntity().getComponent(GameComponent.class);
        Array<String> players = game.getPlayers();

        String playerIndex = memory.getValue("playerIndex");
        int nextPlayerIndex = 0;
        if (playerIndex != null) {
            nextPlayerIndex = Integer.parseInt(playerIndex) + 1;
        }

        String playerMemoryName = gameEffect.getDataString("playerMemory");
        if (nextPlayerIndex == players.size) {
            memory.removeValue(playerMemoryName);
            memory.removeValue("playerIndex");
            // Finished all players - remove from stack
            removeTopEffectFromStack();
        } else {
            String player = players.get(nextPlayerIndex);
            memory.setValue(playerMemoryName, player);
            JsonValue action = gameEffect.getClonedDataObject("action");
            Entity actionToStack = createActionFromJson(action, sourceEntity);
            memory.setValue("playerIndex", String.valueOf(nextPlayerIndex));

            stackEffect(actionToStack);
        }
    }

    private void stackActionTemplate(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String template = gameEffect.getDataString("template");

        Entity spawnedAction = spawnSystem.spawnEntity(template);

        removeTopEffectFromStack();

        stackEffect(spawnedAction);
    }

}
