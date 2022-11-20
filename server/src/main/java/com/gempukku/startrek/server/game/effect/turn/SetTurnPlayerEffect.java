package com.gempukku.startrek.server.game.effect.turn;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class SetTurnPlayerEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;

    public SetTurnPlayerEffect() {
        super("setTurnPlayer");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String username = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory,
                gameEffect.getDataString("player"));
        Entity turnSequenceEntity = LazyEntityUtil.findEntityWithComponent(world, TurnSequenceComponent.class);
        TurnSequenceComponent turnSequence = turnSequenceEntity.getComponent(TurnSequenceComponent.class);
        turnSequence.setCurrentPlayer(username);

        eventSystem.fireEvent(EntityUpdated.instance, turnSequenceEntity);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"player"},
                new String[]{});
        playerResolverSystem.validate(effect.getString("player"));
    }
}
