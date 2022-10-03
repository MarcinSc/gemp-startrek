package com.gempukku.startrek.server.game.effect.turn;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.turn.TurnComponent;
import com.gempukku.startrek.game.turn.TurnSegment;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class SetTurnSegmentEffect extends EffectSystem {
    private EventSystem eventSystem;

    public SetTurnSegmentEffect() {
        super("setTurnSegment");
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        TurnSegment turnSegment = TurnSegment.valueOf(gameEffect.getData().getString("segment"));
        Entity turnEntity = LazyEntityUtil.findEntityWithComponent(world, TurnComponent.class);
        TurnComponent turn = turnEntity.getComponent(TurnComponent.class);
        turn.setTurnSegment(turnSegment);

        eventSystem.fireEvent(EntityUpdated.instance, turnEntity);

        removeEffectFromStack(gameEffectEntity);
    }
}
