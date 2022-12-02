package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class KillEffect extends EffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private SpawnSystem spawnSystem;

    public KillEffect() {
        super("kill");
    }

    @Override
    protected void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        boolean killStacked = Boolean.parseBoolean(memory.getValue("internal.killStacked", "false"));
        if (!killStacked) {
            String filter = gameEffect.getDataString("filter", null);
            String randomSelect = gameEffect.getDataString("randomSelect", null);
            memory.setValue("internal.from", gameEffect.getDataString("from"));
            Entity killEffectEntity;
            if (filter != null) {
                memory.setValue("internal.killFilter", filter);
                memory.setValue("internal.killMemory", "cardsToKill");
                killEffectEntity = spawnSystem.spawnEntity("game/effect/kill/killWithFilterEffect.template");
            } else if (randomSelect != null) {
                memory.setValue("internal.killFilter", randomSelect);
                memory.setValue("internal.killMemory", "cardsToKill");
                killEffectEntity = spawnSystem.spawnEntity("game/effect/kill/killWithRandomSelectEffect.template");
            } else {
                throw new GdxRuntimeException("Unable to resolve a kill effect");
            }
            memory.setValue("internal.killStacked", "true");
            stackEffect(killEffectEntity);
        } else {
            memory.removeValue("internal.killStacked");
            removeTopEffectFromStack();
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"from"},
                new String[]{"filter", "randomSelect"});
        ValidateUtil.hasExactlyOneOf(effect, "filter", "randomSelect");

        cardFilteringSystem.validateSource(effect.getString("from"));
        cardFilteringSystem.validateFilter(effect.getString("filter", "any"));
        cardFilteringSystem.validateFilter(effect.getString("randomSelect", "any"));
    }
}
