package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class SetupMissionsEffect extends OneTimeEffectSystem {
    private SpawnSystem spawnSystem;
    private PlayerResolverSystem playerResolverSystem;

    public SetupMissionsEffect() {
        super("setupMissions");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Entity effectEntity, GameEffectComponent gameEffect, Memory memory) {
        String player = playerResolverSystem.resolvePlayerUsername(effectEntity, memory,
                gameEffect.getDataString("player"));
        for (int missionIndex = 0; missionIndex < 5; missionIndex++) {
            Entity missionEntity = spawnSystem.spawnEntity("game/mission.template");
            MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
            mission.setOwner(player);
            mission.setMissionIndex(missionIndex);
        }
    }
}
