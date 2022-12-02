package com.gempukku.startrek.server.game.effect.memory;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class RandomlySelectEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private AmountResolverSystem amountResolverSystem;

    public RandomlySelectEffect() {
        super("randomlySelect");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String filter = gameEffect.getDataString("filter", null);
        String filterMemory = gameEffect.getDataString("filterMemory", null);
        String amount = gameEffect.getDataString("amount", "1");
        String memoryName = gameEffect.getDataString("memory");

        if (filter == null)
            filter = memory.getValue(filterMemory);

        int count = amountResolverSystem.resolveAmount(sourceEntity, memory, amount);

        Array<Entity> matchingEntities = new Array<>();
        cardFilteringSystem.forEachCard(sourceEntity, memory, gameEffect.getDataString("from"),
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        matchingEntities.add(entity);
                    }
                }, filter
        );

        matchingEntities.shuffle();

        Array<String> selected = new Array<>();
        for (int i = 0; i < matchingEntities.size && i < count; i++) {
            selected.add(String.valueOf(matchingEntities.get(i).getId()));
        }

        String result = StringUtils.merge(selected);
        memory.setValue(memoryName, result);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"memory", "from"},
                new String[]{"amount", "filter", "filterMemory"});
        ValidateUtil.hasExactlyOneOf(effect, "filter", "filterMemory");
        cardFilteringSystem.validateSource(effect.getString("from"));
        cardFilteringSystem.validateFilter(effect.getString("filter", "any"));
        amountResolverSystem.validateAmount(effect.getString("amount", "1"));
    }
}
