package com.gempukku.startrek.game.mission;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class MissionOperations extends EntitySystem {
    public MissionOperations() {
        super(Aspect.all(MissionComponent.class));
    }

    public Entity findMission(String ownerUsername, int missionIndex) {
        IntBag entities = getSubscription().getEntities();
        for (int i = 0, size = entities.size(); i < size; i++) {
            Entity missionEntity = world.getEntity(entities.get(i));
            CardInMissionComponent cardInMission = missionEntity.getComponent(CardInMissionComponent.class);
            if (cardInMission.getMissionOwner().equals(ownerUsername)
                    && cardInMission.getMissionIndex() == missionIndex)
                return missionEntity;
        }
        return null;
    }

    @Override
    protected void processSystem() {

    }
}
