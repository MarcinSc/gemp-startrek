package com.gempukku.startrek.server.game.effect;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public abstract class EffectSystem extends BaseSystem implements GameEffectHandler {
    private GameEffectSystem gameEffectSystem;
    private ExecutionStackSystem stackSystem;
    private ServerEntityIdSystem serverEntityIdSystem;
    private IdProviderSystem idProviderSystem;
    private SpawnSystem spawnSystem;

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

    protected Entity spawnEffect(String path, Entity sourceEntity) {
        Entity stackedEntity = spawnSystem.spawnEntity(path);
        GameEffectComponent newGameEffect = gameEffectComponentMapper.get(stackedEntity);
        if (sourceEntity != null)
            newGameEffect.setSourceEntityId(serverEntityIdSystem.getEntityId(sourceEntity));
        return stackedEntity;
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
    public boolean processEndingEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        processEffect(sourceEntity, memory, effectEntity, gameEffect);
        return false;
    }

    protected abstract void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect);

    protected void validateOneEffect(JsonValue effect) {
        if (effect.type() != JsonValue.ValueType.object)
            throw new GdxRuntimeException("Expected one effect only");
        gameEffectSystem.validate(effect);
    }

    protected void validateEffects(JsonValue effects) {
        if (effects.type() == JsonValue.ValueType.object)
            gameEffectSystem.validate(effects);
        else {
            for (JsonValue effect : effects) {
                gameEffectSystem.validate(effect);
            }
        }
    }

    protected String getMemoryName(Entity effectEntity, String prefix) {
        return prefix + "." + idProviderSystem.getEntityId(effectEntity);
    }

    protected String getOptionalFromMemory(Memory memory, GameEffectComponent gameEffect, String normalKey, String memoryKey) {
        String result = gameEffect.getDataString(normalKey, null);
        if (result != null)
            return result;
        return memory.getValue(gameEffect.getDataString(memoryKey));
    }

    @Override
    protected void processSystem() {

    }
}
