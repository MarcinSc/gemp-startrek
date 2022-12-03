package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayRequirements;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.CardInMissionComponent;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class PlayOrDrawDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;
    private PlayerResolverSystem playerResolverSystem;
    private SpawnSystem spawnSystem;
    private ExecutionStackSystem stackSystem;
    private EventSystem eventSystem;
    private CardFilteringSystem cardFilteringSystem;
    private CardAbilitySystem cardAbilitySystem;
    private ServerEntityIdSystem serverEntityIdSystem;
    private MissionOperations missionOperations;

    @Override
    protected void initialize() {
        decisionSystem.registerDecisionTypeHandler("playOrDrawDecision", this);
    }

    @Override
    public boolean validateDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        try {
            String action = result.get("action");
            if (action == null)
                return false;
            Entity playerEntity = playerResolverSystem.findPlayerEntity(decisionPlayer);
            PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            if (action.equals("draw") && publicStats.getCounterCount() > 0 && publicStats.getDeckCount() > 0) {
                return true;
            } else if (action.equals("pass") && (publicStats.getCounterCount() == 0 || publicStats.getDeckCount() == 0)) {
                return true;
            } else if (action.equals("play")) {
                Entity playedCardEntity = serverEntityIdSystem.findfromId(result.get("cardId"));
                if (playedCardEntity == null)
                    return false;

                CardFilter playFilter = PlayRequirements.createPlayRequirements(
                        decisionPlayer, cardFilteringSystem, cardAbilitySystem);

                if (playFilter.accepts(null, null, playedCardEntity))
                    return true;
            }
        } catch (Exception exp) {
            // Ignore
        }
        return false;
    }

    @Override
    public void processDecision(String decisionPlayer, Memory memory, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        String action = result.get("action");
        if (action.equals("draw")) {
            Entity playerEntity = playerResolverSystem.findPlayerEntity(decisionPlayer);
            PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            publicStats.setCounterCount(publicStats.getCounterCount() - 1);
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

            Entity drawCardEffect = spawnSystem.spawnEntity("game/draw/drawCardEffect.template");
            ObjectMap<String, String> newEffectMemory = drawCardEffect.getComponent(EffectMemoryComponent.class).getMemory();
            newEffectMemory.put("player", decisionPlayer);
            GameEffectComponent gameEffect = drawCardEffect.getComponent(GameEffectComponent.class);
            JsonValue data = gameEffect.getClonedData();
            gameEffect.setData(data);
            stackSystem.stackEntity(drawCardEffect);
        } else if (action.equals("pass")) {
            Entity effectMemoryEntity = stackSystem.getTopMostStackEntityWithComponent(EffectMemoryComponent.class);
            EffectMemoryComponent effectMemory = effectMemoryEntity.getComponent(EffectMemoryComponent.class);
            effectMemory.getMemory().put("playerFinished", "true");
        } else if (action.equals("play")) {
            String cardId = result.get("cardId");

            Entity playerHeadquarter = cardFilteringSystem.findFirstCard(null, null, "inPlay", "missionType(Headquarters),owner(username(" + decisionPlayer + "))");
            CardInMissionComponent cardInMission = playerHeadquarter.getComponent(CardInMissionComponent.class);
            Entity missionEntity = missionOperations.findMission(cardInMission.getMissionOwner(), cardInMission.getMissionIndex());

            Entity playCardEffect = spawnSystem.spawnEntity("game/effect/play/playCardEffect.template");
            EffectMemoryComponent effectMemory = playCardEffect.getComponent(EffectMemoryComponent.class);
            Memory newEffectMemory = new Memory(effectMemory.getMemory());
            newEffectMemory.setValue("playedCardId", cardId);
            newEffectMemory.setValue("missionId", serverEntityIdSystem.getEntityId(missionEntity));
            stackSystem.stackEntity(playCardEffect);
        }
    }

    @Override
    protected void processSystem() {

    }
}
