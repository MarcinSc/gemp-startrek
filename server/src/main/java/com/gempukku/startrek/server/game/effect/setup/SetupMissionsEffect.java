package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.Entity;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

public class SetupMissionsEffect extends EffectSystem {
    private ServerSpawnSystem spawnSystem;
    private PlayerResolverSystem playerResolverSystem;

    public SetupMissionsEffect() {
        super("setupMissions");
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String player = playerResolverSystem.resolvePlayerUsername(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("player"));
        for (int missionIndex = 0; missionIndex < 5; missionIndex++) {
            Entity missionEntity = spawnSystem.spawnEntity("game/mission.template");
            MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
            mission.setOwner(player);
            mission.setMissionIndex(missionIndex);
        }
        removeEffectFromStack(gameEffectEntity);
    }
}
