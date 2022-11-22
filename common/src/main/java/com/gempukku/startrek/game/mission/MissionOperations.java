package com.gempukku.startrek.game.mission;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class MissionOperations {
    public static Entity findMission(World world, String ownerUsername, int missionIndex) {
        return LazyEntityUtil.findEntityWithComponent(world, MissionComponent.class,
                new Predicate<Entity>() {
                    @Override
                    public boolean evaluate(Entity missionEntity) {
                        CardInMissionComponent cardInMission = missionEntity.getComponent(CardInMissionComponent.class);
                        return cardInMission.getMissionIndex() == missionIndex
                                && cardInMission.getMissionOwner().equals(ownerUsername);
                    }
                });
    }
}
