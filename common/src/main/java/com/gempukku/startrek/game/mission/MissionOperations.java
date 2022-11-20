package com.gempukku.startrek.game.mission;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GamePlayerComponent;

public class MissionOperations {
    public static Entity findMission(World world, String ownerUsername, int missionIndex) {
        return LazyEntityUtil.findEntityWithComponent(world, MissionComponent.class,
                new Predicate<Entity>() {
                    @Override
                    public boolean evaluate(Entity missionEntity) {
                        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
                        return mission.getMissionIndex() == missionIndex
                                && mission.getOwner().equals(ownerUsername);
                    }
                });
    }

    public static Entity findMission(World world, Entity playerEntity, int missionIndex) {
        GamePlayerComponent player = playerEntity.getComponent(GamePlayerComponent.class);

        return LazyEntityUtil.findEntityWithComponent(world, MissionComponent.class,
                new Predicate<Entity>() {
                    @Override
                    public boolean evaluate(Entity missionEntity) {
                        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
                        return mission.getMissionIndex() == missionIndex
                                && mission.getOwner().equals(player.getName());
                    }
                });
    }
}
