package com.gempukku.startrek.game.filter.source;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.mission.AttemptedMissionComponent;

public class AttemptingPersonnelSource extends CardSourceSystem {
    private IdProviderSystem idProviderSystem;

    public AttemptingPersonnelSource() {
        super("attemptingPersonnel");
    }

    @Override
    public CardSource resolveSource(Array<String> parameters) {
        return new ShortcutCardSource() {
            @Override
            protected void forEachWithShortcut(Entity sourceEntity, Memory memory, ShortcutConsumer<Entity> consumer, CardFilter... filters) {
                Entity attemptedMissionEntity = LazyEntityUtil.findEntityWithComponent(world, AttemptedMissionComponent.class);
                if (attemptedMissionEntity == null)
                    return;
                AttemptedMissionComponent attemptedMission = attemptedMissionEntity.getComponent(AttemptedMissionComponent.class);
                Array<String> cardIds = attemptedMission.getAttemptingPersonnel();

                for (String cardId : cardIds) {
                    Entity entity = idProviderSystem.getEntityById(cardId);
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        if (consumer.accept(entity))
                            return;
                }
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
