package com.gempukku.startrek.server.game.amount;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public class AmountResolverSystem extends BaseSystem {
    public int resolveAmount(Entity sourceEntity, ObjectMap<String, String> memory, String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exp) {

        }
        throw new RuntimeException("Unable to resolve amount: " + value);
    }

    @Override
    protected void processSystem() {

    }
}
