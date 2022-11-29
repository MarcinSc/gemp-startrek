package com.gempukku.startrek.server.game.effect.play;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.ObjectOnStackComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class SetEffectStepEffect extends OneTimeEffectSystem {
    private EventSystem eventSystem;

    public SetEffectStepEffect() {
        super("setEffectStep");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        int step = Integer.parseInt(gameEffect.getDataString("step"));
        ObjectOnStackComponent cardOnStack = sourceEntity.getComponent(ObjectOnStackComponent.class);
        cardOnStack.setEffectStep(step);
        eventSystem.fireEvent(EntityUpdated.instance, sourceEntity);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"step"},
                new String[]{});
        Integer.parseInt(effect.getString("step"));
    }
}
