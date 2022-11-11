package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayRequirements;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.stack.StackSystem;

public class PlayOrDrawDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;
    private PlayerResolverSystem playerResolverSystem;
    private SpawnSystem spawnSystem;
    private StackSystem stackSystem;
    private EventSystem eventSystem;
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private CardAbilitySystem cardAbilitySystem;

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
                int cardId = Integer.parseInt(result.get("cardId"));
                Entity playedCardEntity = world.getEntity(cardId);
                if (playedCardEntity == null)
                    return false;

                CardFilter playFilter = PlayRequirements.createPlayRequirements(
                        decisionPlayer, cardFilteringSystem, cardFilterResolverSystem, cardAbilitySystem);

                if (playFilter.accepts(null, null, playedCardEntity))
                    return true;
            }
        } catch (Exception exp) {
            // Ignore
        }
        return false;
    }

    @Override
    public void processDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        String action = result.get("action");
        if (action.equals("draw")) {
            Entity playerEntity = playerResolverSystem.findPlayerEntity(decisionPlayer);
            PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            publicStats.setCounterCount(publicStats.getCounterCount() - 1);
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

            Entity drawCardEffect = spawnSystem.spawnEntity("game/draw/drawCardEffect.template");
            ObjectMap<String, String> memory = drawCardEffect.getComponent(EffectMemoryComponent.class).getMemory();
            memory.put("player", decisionPlayer);
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

            Entity playerHeadquarter = cardFilteringSystem.findFirstCardInPlay("missionType(Headquarters),owner(username(" + decisionPlayer + "))");
            FaceUpCardInMissionComponent cardInMission = playerHeadquarter.getComponent(FaceUpCardInMissionComponent.class);
            Entity missionEntity = LazyEntityUtil.findEntityWithComponent(world, MissionComponent.class,
                    new Predicate<Entity>() {
                        @Override
                        public boolean evaluate(Entity entity) {
                            MissionComponent mission = entity.getComponent(MissionComponent.class);
                            return mission.getOwner().equals(cardInMission.getMissionOwner())
                                    && mission.getMissionIndex() == cardInMission.getMissionIndex();
                        }
                    });

            Entity playCardEffect = spawnSystem.spawnEntity("game/effect/playCardEffect.template");
            EffectMemoryComponent effectMemory = playCardEffect.getComponent(EffectMemoryComponent.class);
            Memory memory = new Memory(effectMemory.getMemory());
            memory.setValue("playedCardId", String.valueOf(cardId));
            memory.setValue("missionId", String.valueOf(missionEntity.getId()));
            stackSystem.stackEntity(playCardEffect);
        }
    }

    @Override
    protected void processSystem() {

    }
}
