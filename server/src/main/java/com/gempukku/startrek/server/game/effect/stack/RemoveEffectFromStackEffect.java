package com.gempukku.startrek.server.game.effect.stack;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

public class RemoveEffectFromStackEffect extends OneTimeEffectSystem {
    private ZoneOperations zoneOperations;
    private ConditionResolverSystem conditionResolverSystem;

    public RemoveEffectFromStackEffect() {
        super("removeEffectFromStack");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        boolean destroy = conditionResolverSystem.resolveBoolean(sourceEntity, memory, gameEffect.getDataString("destroy", "true"));
        zoneOperations.removeEffectFromStack(destroy);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{},
                new String[]{"destroy"});
    }
}
