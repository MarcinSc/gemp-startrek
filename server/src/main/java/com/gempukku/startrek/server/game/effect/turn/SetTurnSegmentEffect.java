package com.gempukku.startrek.server.game.effect.turn;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.turn.TurnComponent;
import com.gempukku.startrek.game.turn.TurnSegment;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class SetTurnSegmentEffect extends OneTimeEffectSystem {
    private EventSystem eventSystem;

    public SetTurnSegmentEffect() {
        super("setTurnSegment");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        TurnSegment turnSegment = TurnSegment.valueOf(gameEffect.getDataString("segment"));
        Entity turnEntity = LazyEntityUtil.findEntityWithComponent(world, TurnComponent.class);
        TurnComponent turn = turnEntity.getComponent(TurnComponent.class);
        turn.setTurnSegment(turnSegment);

        eventSystem.fireEvent(EntityUpdated.instance, turnEntity);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"segment"},
                new String[]{});
        TurnSegment.valueOf(effect.getString("segment"));
    }
}
