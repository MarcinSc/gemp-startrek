package com.gempukku.startrek.server.game.effect;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public interface GameEffectHandler {
    boolean processEndingEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory);
}
