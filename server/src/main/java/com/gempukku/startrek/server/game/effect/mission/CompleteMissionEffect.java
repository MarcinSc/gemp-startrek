package com.gempukku.startrek.server.game.effect.mission;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class CompleteMissionEffect extends OneTimeEffectSystem {
    private IdProviderSystem idProviderSystem;
    private EventSystem eventSystem;
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;

    public CompleteMissionEffect() {
        super("completeMission");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        Entity missionEntity = idProviderSystem.getEntityById(memory.getValue(gameEffect.getDataString("missionMemory")));
        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
        mission.setCompleted(true);

        String owner = missionEntity.getComponent(CardComponent.class).getOwner();
        Entity playerEntity = playerResolverSystem.findPlayerEntity(owner);
        PlayerPublicStatsComponent player = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        int points = amountResolverSystem.resolveAmount(missionEntity, memory, "missionPoints");
        player.setPointCount(player.getPointCount() + points);

        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        eventSystem.fireEvent(EntityUpdated.instance, missionEntity);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"missionMemory"},
                new String[]{});
    }
}
