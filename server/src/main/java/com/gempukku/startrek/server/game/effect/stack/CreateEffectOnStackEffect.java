package com.gempukku.startrek.server.game.effect.stack;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.EffectComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

public class CreateEffectOnStackEffect extends OneTimeEffectSystem {
    private AmountResolverSystem amountResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;
    private ServerEntityIdSystem serverEntityIdSystem;
    private SpawnSystem spawnSystem;

    public CreateEffectOnStackEffect() {
        super("createEffectOnStack");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String sourceMemory = gameEffect.getDataString("sourceMemory");
        String cardId = memory.getValue(sourceMemory);
        Entity cardEntity = serverEntityIdSystem.findfromId(cardId);
        CardComponent card = cardEntity.getComponent(CardComponent.class);

        int abilityIndex = amountResolverSystem.resolveAmount(sourceEntity, memory, gameEffect.getDataString("abilityIndex", "-1"));

        Entity effectEntity = spawnSystem.spawnEntity("game/effect.template");
        EffectComponent effect = effectEntity.getComponent(EffectComponent.class);
        effect.setSourceId(cardId);
        effect.setSourceCardId(card.getCardId());
        effect.setOwner(card.getOwner());

        zoneOperations.moveEffectToStack(effectEntity, abilityIndex);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"sourceMemory", "abilityIndex"},
                new String[]{});
        amountResolverSystem.validateAmount(effect.getString("abilityIndex"));
    }
}
