package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class StopEffect extends EffectSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private SpawnSystem spawnSystem;

    public StopEffect() {
        super("stop");
    }

    @Override
    protected void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        boolean stopStacked = Boolean.parseBoolean(memory.getValue("internal.stopStacked", "false"));
        if (!stopStacked) {
            String filter = gameEffect.getDataString("filter", null);
            String select = gameEffect.getDataString("select", null);
            Entity effectEntity;
            if (filter != null) {
                memory.setValue("internal.stopFilter", filter);
                memory.setValue("internal.stopMemory", "cardsToStop");
                effectEntity = spawnSystem.spawnEntity("game/effect/stop/stopWithFilterEffect.template");
            } else {
                memory.setValue("internal.prompt", "Choose cards to stop");
                memory.setValue("internal.stopFilter", select);
                memory.setValue("internal.stopMin", "1");
                memory.setValue("internal.stopMax", "1");
                effectEntity = spawnSystem.spawnEntity("game/effect/stop/stopWithSelectEffect.template");
            }
            memory.setValue("internal.stopStacked", "true");
            stackEffect(effectEntity);
        } else {
            memory.removeValue("internal.stopStacked");
            removeTopEffectFromStack();
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{},
                new String[]{"filter", "select"});
        ValidateUtil.hasExactlyOneOf(effect, "filter", "select");
        cardFilterResolverSystem.validateFilter(effect.getString("filter", "any"));
        cardFilterResolverSystem.validateFilter(effect.getString("select", "any"));
    }
}
