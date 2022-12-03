package com.gempukku.startrek.server.game.effect.card;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class DownloadEffect extends EffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private AmountResolverSystem amountResolverSystem;
    private SpawnSystem spawnSystem;

    public DownloadEffect() {
        super("download");
    }

    @Override
    protected void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        boolean downloadStacked = Boolean.parseBoolean(memory.getValue("internal.downloadStacked", "false"));
        if (!downloadStacked) {
            String player = gameEffect.getDataString("player");
            String playerUsername = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, player);
            String filter = gameEffect.getDataString("filter");
            int amount = amountResolverSystem.resolveAmount(sourceEntity, memory, gameEffect.getDataString("amount", "1"));

            memory.setValue("internal.prompt", "Choose cards to download");
            memory.setValue("internal.allCardsFilter", "owner(username(" + playerUsername + "))");
            memory.setValue("internal.matchingCardsFilter", "owner(username(" + playerUsername + "))," + filter);
            memory.setValue("internal.player", player);
            memory.setValue("internal.selectMin", String.valueOf(amount));
            memory.setValue("internal.selectMax", String.valueOf(amount));
            Entity downloadEffectEntity = spawnEffect("game/effect/download/downloadEffect.template", sourceEntity);

            memory.setValue("internal.downloadStacked", "true");
            stackEffect(downloadEffectEntity);
        } else {
            memory.removeValue("internal.downloadStacked", "internal.prompt", "internal.allCardsFilter",
                    "internal.matchingCardsFilter", "internal.player", "internal.selectMin", "internal.selectMax");
            removeTopEffectFromStack();
        }

    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"player", "filter"},
                new String[]{"amount"});
        playerResolverSystem.validatePlayer(effect.getString("player"));
        cardFilteringSystem.validateFilter(effect.getString("filter"));
        amountResolverSystem.validateAmount(effect.getString("amount", "1"));
    }
}
