package com.gempukku.startrek.server.game.effect.mission;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.mission.AttemptedMissionComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

import java.util.function.Consumer;

public class AssignAttemptingPersonnelEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private PlayerResolverSystem playerResolverSystem;
    private IdProviderSystem idProviderSystem;
    private ExecutionStackSystem executionStackSystem;
    private EventSystem eventSystem;

    public AssignAttemptingPersonnelEffect() {
        super("assignAttemptingPersonnel");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String player = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, gameEffect.getDataString("player"));
        String memoryName = gameEffect.getDataString("memory");
        String filter = gameEffect.getDataString("filter");
        Array<String> personnelIds = new Array<>();
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, new Consumer<Entity>() {
            @Override
            public void accept(Entity entity) {
                personnelIds.add(idProviderSystem.getEntityId(entity));
            }
        }, filter);
        memory.setValue(memoryName, StringUtils.merge(personnelIds));

        Entity attemptedMissionEntity = executionStackSystem.getTopMostStackEntityWithComponent(AttemptedMissionComponent.class);
        AttemptedMissionComponent attemptedMission = attemptedMissionEntity.getComponent(AttemptedMissionComponent.class);
        attemptedMission.setOwner(player);
        attemptedMission.getAttemptingPersonnel().addAll(personnelIds);
        eventSystem.fireEvent(EntityUpdated.instance, attemptedMissionEntity);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"filter", "memory", "player"},
                new String[]{});
        cardFilteringSystem.validateFilter(effect.getString("filter"));
        playerResolverSystem.validatePlayer(effect.getString("player"));
    }
}
