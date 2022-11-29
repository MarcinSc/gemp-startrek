package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class MoveTopDilemmaStackCardToStackEffect extends OneTimeEffectSystem {
    private ZoneOperations zoneOperations;
    private IdProviderSystem idProviderSystem;

    public MoveTopDilemmaStackCardToStackEffect() {
        super("moveTopDilemmaStackCardToStack");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        Entity movedCard = zoneOperations.moveTopDilemmaStackCardToStack(0);
        String memorize = gameEffect.getDataString("memory");
        if (memorize != null) {
            memory.setValue(memorize, idProviderSystem.getEntityId(movedCard));
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{},
                new String[]{"memory"});
    }
}
