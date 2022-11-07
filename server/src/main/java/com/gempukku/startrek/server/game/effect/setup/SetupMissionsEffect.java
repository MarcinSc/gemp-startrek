package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

public class SetupMissionsEffect extends OneTimeEffectSystem {
    private SpawnSystem spawnSystem;
    private PlayerResolverSystem playerResolverSystem;

    public SetupMissionsEffect() {
        super("setupMissions");
    }

    @Override
    protected void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String player = playerResolverSystem.resolvePlayerUsername(gameEffectEntity, gameEffect.getMemory(),
                gameEffect.getDataString("player"));
        for (int missionIndex = 0; missionIndex < 5; missionIndex++) {
            Entity missionEntity = spawnSystem.spawnEntity("game/mission.template");
            MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
            mission.setOwner(player);
            mission.setMissionIndex(missionIndex);
        }
    }
}
