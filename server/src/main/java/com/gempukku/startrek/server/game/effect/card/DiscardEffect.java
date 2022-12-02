package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class DiscardEffect extends EffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private PlayerResolverSystem playerResolverSystem;
    private SpawnSystem spawnSystem;

    public DiscardEffect() {
        super("discard");
    }

    @Override
    protected void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        boolean discardStacked = Boolean.parseBoolean(memory.getValue("internal.discardStacked", "false"));
        if (!discardStacked) {
            String filter = gameEffect.getDataString("select");
            String player = gameEffect.getDataString("player");
            String playerUsername = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, player);

            memory.setValue("internal.prompt", "Choose what to discard");
            memory.setValue("internal.discardFilter", "zone(Hand),owner(username(" + playerUsername + "))," + filter);
            memory.setValue("internal.player", "username(" + playerUsername + ")");
            memory.setValue("internal.discardMin", "1");
            memory.setValue("internal.discardMax", "1");
            Entity discardEffectEntity = spawnSystem.spawnEntity("game/effect/discard/discardWithSelectEffect.template");

            memory.setValue("internal.discardStacked", "true");
            stackEffect(discardEffectEntity);
        } else {
            memory.removeValue("internal.discardStacked");
            removeTopEffectFromStack();
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"select", "player"},
                new String[]{});
        cardFilteringSystem.validateFilter(effect.getString("select"));
        playerResolverSystem.validatePlayer(effect.getString("player"));
    }
}
