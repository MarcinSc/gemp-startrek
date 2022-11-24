package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.GameEntityProvider;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class PlayerCountAmountHandler extends AmountSystem {
    private GameEntityProvider gameEntityProvider;

    public PlayerCountAmountHandler() {
        super("playerCount");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        Entity gameEntity = gameEntityProvider.getGameEntity();
        return gameEntity.getComponent(GameComponent.class).getPlayers().size;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
