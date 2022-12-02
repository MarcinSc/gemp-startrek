package com.gempukku.startrek.game.filter.source;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.mission.AttemptedMissionComponent;

import java.util.function.Consumer;

public class AttemptingPersonnelSource extends CardSourceSystem {
    private IdProviderSystem idProviderSystem;

    public AttemptingPersonnelSource() {
        super("attemptingPersonnel");
    }

    @Override
    public CardSource resolveSource(Array<String> parameters) {
        return new CardSource() {
            @Override
            public void forEach(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, CardFilter... filters) {
                Entity attemptedMissionEntity = LazyEntityUtil.findEntityWithComponent(world, AttemptedMissionComponent.class);
                if (attemptedMissionEntity == null)
                    return;
                AttemptedMissionComponent attemptedMission = attemptedMissionEntity.getComponent(AttemptedMissionComponent.class);
                Array<String> cardIds = attemptedMission.getAttemptingPersonnel();

                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        consumer.accept(entity);
                }
            }

            @Override
            public Entity findFirst(Entity sourceEntity, Memory memory, CardFilter... filters) {
                Entity attemptedMissionEntity = LazyEntityUtil.findEntityWithComponent(world, AttemptedMissionComponent.class);
                if (attemptedMissionEntity == null)
                    return null;
                AttemptedMissionComponent attemptedMission = attemptedMissionEntity.getComponent(AttemptedMissionComponent.class);
                Array<String> cardIds = attemptedMission.getAttemptingPersonnel();

                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        return entity;
                }
                return null;
            }

            @Override
            public boolean hasCount(Entity sourceEntity, Memory memory, int required, CardFilter... filters) {
                Entity attemptedMissionEntity = LazyEntityUtil.findEntityWithComponent(world, AttemptedMissionComponent.class);
                if (attemptedMissionEntity == null)
                    return required <= 0;
                AttemptedMissionComponent attemptedMission = attemptedMissionEntity.getComponent(AttemptedMissionComponent.class);
                Array<String> cardIds = attemptedMission.getAttemptingPersonnel();

                int count = 0;
                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters)) {
                        count++;
                        if (count >= required)
                            return true;
                    }
                }

                return false;
            }

            @Override
            public int getCount(Entity sourceEntity, Memory memory, CardFilter... filters) {
                Entity attemptedMissionEntity = LazyEntityUtil.findEntityWithComponent(world, AttemptedMissionComponent.class);
                if (attemptedMissionEntity == null)
                    return 0;
                AttemptedMissionComponent attemptedMission = attemptedMissionEntity.getComponent(AttemptedMissionComponent.class);
                Array<String> cardIds = attemptedMission.getAttemptingPersonnel();

                int result = 0;
                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        result++;
                }
                return result;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
