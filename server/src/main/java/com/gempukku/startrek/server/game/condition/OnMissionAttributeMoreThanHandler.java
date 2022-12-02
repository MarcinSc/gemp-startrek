package com.gempukku.startrek.server.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardAttribute;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.condition.ConditionSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

import java.util.function.Consumer;

public class OnMissionAttributeMoreThanHandler extends ConditionSystem {
    private AmountResolverSystem amountResolverSystem;
    private CardFilteringSystem cardFilteringSystem;

    public OnMissionAttributeMoreThanHandler() {
        super("onMissionAttributeMoreThan");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        CardAttribute attribute = CardAttribute.valueOf(parameters.get(0));
        int countRequired = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
        int[] result = new int[1];
        cardFilteringSystem.forEachCard(sourceEntity, memory, "attemptingPersonnel", new Consumer<Entity>() {
            @Override
            public void accept(Entity entity) {
                result[0] += amountResolverSystem.resolveAmount(entity, memory, "attribute(" + attribute.name() + ")");
            }
        }, "attemptingPersonnel");
        return result[0] > countRequired;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 2);
        CardAttribute.valueOf(parameters.get(0));
        amountResolverSystem.validateAmount(parameters.get(1));
    }
}
