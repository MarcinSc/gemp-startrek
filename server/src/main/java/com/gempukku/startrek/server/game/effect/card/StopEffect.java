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

public class StopEffect extends EffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private SpawnSystem spawnSystem;

    public StopEffect() {
        super("stop");
    }

    @Override
    protected void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        boolean stopStacked = Boolean.parseBoolean(memory.getValue("internal.stopStacked", "false"));
        if (!stopStacked) {
            String filter = gameEffect.getDataString("filter", null);
            String select = gameEffect.getDataString("select", null);
            String opponentSelect = gameEffect.getDataString("opponentSelect", null);
            Entity stopEffectEntity;
            memory.setValue("internal.from", gameEffect.getDataString("from"));
            if (filter != null) {
                memory.setValue("internal.stopFilter", filter);
                memory.setValue("internal.stopMemory", "cardsToStop");
                stopEffectEntity = spawnSystem.spawnEntity("game/effect/stop/stopWithFilterEffect.template");
            } else if (select != null) {
                memory.setValue("internal.prompt", "Choose cards to stop");
                memory.setValue("internal.stopFilter", select);
                memory.setValue("internal.stopMin", "1");
                memory.setValue("internal.stopMax", "1");
                stopEffectEntity = spawnSystem.spawnEntity("game/effect/stop/stopWithSelectEffect.template");
            } else if (opponentSelect != null) {
                String opponentUsername = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, gameEffect.getDataString("player"));

                memory.setValue("internal.prompt", "Choose cards to stop");
                memory.setValue("internal.player", "username(" + opponentUsername + ")");
                memory.setValue("internal.stopFilter", opponentSelect);
                memory.setValue("internal.stopMin", "1");
                memory.setValue("internal.stopMax", "1");
                stopEffectEntity = spawnSystem.spawnEntity("game/effect/stop/stopWithOpponentSelectEffect.template");
            } else {
                throw new GdxRuntimeException("Unable to resolve a stop effect");
            }
            memory.setValue("internal.stopStacked", "true");
            stackEffect(stopEffectEntity);
        } else {
            memory.removeValue("internal.stopStacked");
            removeTopEffectFromStack();
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"from"},
                new String[]{"filter", "select", "opponentSelect", "player"});
        ValidateUtil.hasExactlyOneOf(effect, "filter", "select", "opponentSelect");
        ValidateUtil.ifPresentCheckFor(effect, "opponentSelect", "player");

        cardFilteringSystem.validateSource(effect.getString("from"));
        cardFilteringSystem.validateFilter(effect.getString("filter", "any"));
        cardFilteringSystem.validateFilter(effect.getString("select", "any"));
        cardFilteringSystem.validateFilter(effect.getString("opponentSelect", "any"));
        playerResolverSystem.validatePlayer(effect.getString("player", "currentPlayer"));
    }
}
