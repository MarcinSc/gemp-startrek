package com.gempukku.startrek.server.game.effect.turn;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

public class SetTurnPlayerEffect extends EffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;

    public SetTurnPlayerEffect() {
        super("setTurnPlayer");
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String username = playerResolverSystem.resolvePlayerUsername(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("player"));
        Entity turnSequenceEntity = LazyEntityUtil.findEntityWithComponent(world, TurnSequenceComponent.class);
        TurnSequenceComponent turnSequence = turnSequenceEntity.getComponent(TurnSequenceComponent.class);
        turnSequence.setCurrentPlayer(username);

        eventSystem.fireEvent(EntityUpdated.instance, turnSequenceEntity);

        removeEffectFromStack(gameEffectEntity);
    }
}
