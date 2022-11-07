package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public interface AmountHandler {
    int resolveAmount(String type, Entity sourceEntity, ObjectMap<String, String> memory, Array<String> parameters);
}
