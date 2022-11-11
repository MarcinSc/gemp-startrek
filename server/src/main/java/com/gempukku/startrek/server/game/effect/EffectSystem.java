package com.gempukku.startrek.server.game.effect;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public abstract class EffectSystem extends BaseSystem implements GameEffectHandler {
    private GameEffectSystem gameEffectSystem;
    private ExecutionStackSystem stackSystem;
    private ServerEntityIdSystem serverEntityIdSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;
    private ComponentMapper<EffectMemoryComponent> effectMemoryComponentMapper;

    private final String[] effectTypes;

    public EffectSystem(String... effectTypes) {
        this.effectTypes = effectTypes;
    }

    @Override
    protected void initialize() {
        for (String effectType : effectTypes) {
            gameEffectSystem.registerGameEffectHandler(effectType, this);
        }
    }

    protected void removeTopEffectFromStack() {
        Entity entity = stackSystem.removeTopStackEntity();
        world.deleteEntity(entity);
    }

    protected Entity createActionFromJson(JsonValue action, Entity sourceEntity) {
        Entity stackedEntity = world.createEntity();
        String actionType = action.getString("type");
        boolean createMemory = action.getBoolean("memory", false);
        GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
        if (sourceEntity != null)
            newGameEffect.setSourceEntityId(serverEntityIdSystem.getEntityId(sourceEntity));
        if (createMemory)
            effectMemoryComponentMapper.create(stackedEntity).setMemoryType("action - " + actionType);
        newGameEffect.setType(actionType);
        newGameEffect.setData(action);
        return stackedEntity;
    }

    protected void stackEffect(Entity effectEntity) {
        stackSystem.stackEntity(effectEntity);
    }

    @Override
    public boolean processEndingEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        processEffect(sourceEntity, gameEffect, memory);
        return false;
    }

    protected abstract void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory);

    @Override
    protected void processSystem() {

    }
}
