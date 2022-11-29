package com.gempukku.startrek.server.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.GameEntityProvider;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionSystem;
import com.gempukku.startrek.server.game.deck.HiddenDilemmaStackComponent;

public class DilemmaStackEmptyHandler extends ConditionSystem {
    private GameEntityProvider gameEntityProvider;

    public DilemmaStackEmptyHandler() {
        super("dilemmaStackEmpty");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        HiddenDilemmaStackComponent dilemmaStack = gameEntityProvider.getGameEntity().getComponent(HiddenDilemmaStackComponent.class);
        return dilemmaStack.getCards().size == 0;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
