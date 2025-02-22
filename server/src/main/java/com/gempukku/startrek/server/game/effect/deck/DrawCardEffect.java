package com.gempukku.startrek.server.game.effect.deck;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.CardInHandComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

public class DrawCardEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private EventSystem eventSystem;
    private ZoneOperations zoneOperations;

    public DrawCardEffect() {
        super("drawCard");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String player = gameEffect.getDataString("player");
        Entity playerEntity = playerResolverSystem.resolvePlayer(sourceEntity, memory, player);

        zoneOperations.moveFromTopOfDeckToHand(playerEntity);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"player"},
                new String[]{});
        playerResolverSystem.validatePlayer(effect.getString("player"));
    }
}
