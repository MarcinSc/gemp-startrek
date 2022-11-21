package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class PlayerCountAmountHandler extends AmountSystem {
    public PlayerCountAmountHandler() {
        super("playerCount");
    }

    @Override
    public int resolveAmount(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        Entity gameEntity = LazyEntityUtil.findEntityWithComponent(world, GameComponent.class);
        return gameEntity.getComponent(GameComponent.class).getPlayers().size;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
