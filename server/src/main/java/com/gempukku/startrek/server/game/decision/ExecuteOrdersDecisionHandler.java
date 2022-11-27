package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.OrderMoveRequirements;
import com.gempukku.startrek.game.OrderRequirements;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.zone.CardInPlayComponent;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class ExecuteOrdersDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;
    private ExecutionStackSystem stackSystem;
    private CardFilteringSystem cardFilteringSystem;
    private ServerEntityIdSystem serverEntityIdSystem;
    private SpawnSystem spawnSystem;

    @Override
    protected void initialize() {
        decisionSystem.registerDecisionTypeHandler("executeOrdersDecision", this);
    }

    @Override
    public boolean validateDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        try {
            String action = result.get("action");
            if (action == null)
                return false;
            if (action.equals("pass")) {
                return true;
            } else if (action.equals("beamFromMission")) {
                return validateBeamFromMission(decisionPlayer, result);
            } else if (action.equals("beamToMission")) {
                return validateBeamToMission(decisionPlayer, result);
            } else if (action.equals("beamBetweenShips")) {
                return validateBeamBetweenShips(decisionPlayer, result);
            } else if (action.equals("moveShip")) {
                return validateMoveShip(decisionPlayer, result);
            } else if (action.equals("attemptPlanetMission")) {
                return validateAttemptPlanetMission(decisionPlayer, result);
            } else if (action.equals("attemptSpaceMission")) {
                return validateAttemptSpaceMission(decisionPlayer, result);
            }
        } catch (Exception exp) {
            // Ignore
        }
        return false;
    }

    private boolean validateAttemptPlanetMission(String decisionPlayer, ObjectMap<String, String> result) {
        String missionId = result.get("missionId");
        Entity missionEntity = serverEntityIdSystem.findfromId(missionId);
        if (missionEntity == null)
            return false;

        Memory memory = new Memory(result);

        if (!cardFilteringSystem.resolveCardFilter("missionType(Planet)").
                accepts(null, memory, missionEntity))
            return false;

        CardFilter requirements = OrderRequirements.createAttemptMissionRequirements(decisionPlayer, cardFilteringSystem);
        if (!requirements.accepts(null, memory, missionEntity))
            return false;

        return true;
    }

    private boolean validateAttemptSpaceMission(String decisionPlayer, ObjectMap<String, String> result) {
        String missionId = result.get("missionId");
        Entity missionEntity = serverEntityIdSystem.findfromId(missionId);
        if (missionEntity == null)
            return false;

        Memory memory = new Memory(result);

        if (!cardFilteringSystem.resolveCardFilter("missionType(Space)").
                accepts(null, memory, missionEntity))
            return false;

        CardFilter requirements = OrderRequirements.createAttemptMissionRequirements(decisionPlayer, cardFilteringSystem);
        if (!requirements.accepts(null, memory, missionEntity))
            return false;

        CardFilter shipMissionAffiliationsRequirements = OrderRequirements.createMissionAffiliationsShipRequirements(cardFilteringSystem);

        String[] shipIds = StringUtils.split(result.get("shipIds"), ",");
        if (shipIds.length < 1)
            return false;
        boolean matchesAffiliation = false;
        CardFilter shipRequirements = OrderRequirements.createAttemptMissionShipsRequirements(missionEntity, cardFilteringSystem);
        for (String shipId : shipIds) {
            Entity shipEntity = serverEntityIdSystem.findfromId(shipId);
            if (!shipRequirements.accepts(null, memory, shipEntity))
                return false;
            if (shipMissionAffiliationsRequirements.accepts(null, memory, shipEntity))
                matchesAffiliation = true;
        }

        if (!matchesAffiliation)
            return false;

        return true;
    }

    private boolean validateMoveShip(String decisionPlayer, ObjectMap<String, String> result) {
        String shipId = result.get("shipId");
        Entity shipEntity = serverEntityIdSystem.findfromId(shipId);
        if (shipEntity == null)
            return false;
        String missionId = result.get("missionId");
        Entity toMissionCardEntity = serverEntityIdSystem.findfromId(missionId);
        if (toMissionCardEntity == null)
            return false;

        CardFilter moveShipRequirements = OrderMoveRequirements.createMoveShipRequirements(decisionPlayer, cardFilteringSystem);
        if (!moveShipRequirements.accepts(null, null, shipEntity))
            return false;

        CardFilter missionRequirements = OrderMoveRequirements.createMoveShipMissionRequirements(decisionPlayer, shipEntity, cardFilteringSystem);
        if (!missionRequirements.accepts(shipEntity, null, toMissionCardEntity))
            return false;

        return true;
    }

    private boolean validateBeamBetweenShips(String decisionPlayer, ObjectMap<String, String> result) {
        String fromShipId = result.get("fromShipId");
        Entity fromShipEntity = serverEntityIdSystem.findfromId(fromShipId);
        if (fromShipEntity == null)
            return false;
        String toShipId = result.get("toShipId");
        Entity toShipEntity = serverEntityIdSystem.findfromId(toShipId);
        if (toShipEntity == null)
            return false;

        CardFilter firstShipRequirements = OrderMoveRequirements.createBeamFromMissionShipRequirements(
                decisionPlayer, cardFilteringSystem);
        if (!firstShipRequirements.accepts(null, null, fromShipEntity))
            return false;

        CardFilter secondShipRequirements = OrderMoveRequirements.createBeamSelectAnotherShipRequirements(
                decisionPlayer, fromShipEntity, cardFilteringSystem);
        if (!secondShipRequirements.accepts(null, null, toShipEntity))
            return false;

        String beamedId = result.get("beamedId");
        Array<Entity> beamedEntities = new Array<>();
        for (String id : StringUtils.split(beamedId, ",")) {
            beamedEntities.add(serverEntityIdSystem.findfromId(id));
        }

        if (beamedEntities.size == 0)
            return false;

        CardFilter cardFilter = OrderMoveRequirements.createBeamBetweenShipsRequirements(
                decisionPlayer, fromShipEntity, toShipEntity, cardFilteringSystem);
        for (Entity beamedEntity : beamedEntities) {
            if (!cardFilter.accepts(null, null, beamedEntity))
                return false;
            CardInPlayComponent cardInPlay = beamedEntity.getComponent(CardInPlayComponent.class);
            if (!fromShipId.equals(cardInPlay.getAttachedToId()))
                return false;
        }
        return true;
    }

    private boolean validateBeamToMission(String decisionPlayer, ObjectMap<String, String> result) {
        String shipId = result.get("shipId");
        Entity shipEntity = serverEntityIdSystem.findfromId(shipId);
        if (shipEntity == null)
            return false;

        CardFilter shipRequirements = OrderMoveRequirements.createBeamToMissionShipRequirements(
                decisionPlayer, cardFilteringSystem);
        if (!shipRequirements.accepts(null, null, shipEntity))
            return false;

        String beamedId = result.get("beamedId");
        Array<Entity> beamedEntities = new Array<>();
        for (String id : StringUtils.split(beamedId, ",")) {
            beamedEntities.add(serverEntityIdSystem.findfromId(id));
        }

        if (beamedEntities.size == 0)
            return false;

        CardFilter cardFilter = OrderMoveRequirements.createBeamToMissionRequirements(
                decisionPlayer, shipEntity, cardFilteringSystem);
        for (Entity beamedEntity : beamedEntities) {
            if (!cardFilter.accepts(null, null, beamedEntity))
                return false;
            CardInPlayComponent cardInPlay = beamedEntity.getComponent(CardInPlayComponent.class);
            if (!shipId.equals(cardInPlay.getAttachedToId()))
                return false;
        }
        return true;
    }

    private boolean validateBeamFromMission(String decisionPlayer, ObjectMap<String, String> result) {
        Entity shipEntity = serverEntityIdSystem.findfromId(result.get("shipId"));
        if (shipEntity == null)
            return false;

        CardFilter shipRequirements = OrderMoveRequirements.createBeamFromMissionShipRequirements(
                decisionPlayer, cardFilteringSystem);
        if (!shipRequirements.accepts(null, null, shipEntity))
            return false;

        String beamedId = result.get("beamedId");
        Array<Entity> beamedEntities = new Array<>();
        for (String id : StringUtils.split(beamedId, ",")) {
            beamedEntities.add(serverEntityIdSystem.findfromId(id));
        }

        if (beamedEntities.size == 0)
            return false;

        CardFilter cardFilter = OrderMoveRequirements.createBeamFromMissionRequirements(
                decisionPlayer, shipEntity, cardFilteringSystem);
        for (Entity beamedEntity : beamedEntities) {
            if (!cardFilter.accepts(null, null, beamedEntity))
                return false;
        }
        return true;
    }

    @Override
    public void processDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        String action = result.get("action");
        if (action.equals("pass")) {
            Entity effectMemoryEntity = stackSystem.getTopMostStackEntityWithComponent(EffectMemoryComponent.class);
            EffectMemoryComponent effectMemory = effectMemoryEntity.getComponent(EffectMemoryComponent.class);
            effectMemory.getMemory().put("playerFinished", "true");
        } else if (action.equals("beamFromMission")) {
            Entity beamFromMissionEffect = spawnSystem.spawnEntity("game/effect/beam/beamFromMissionEffect.template");
            EffectMemoryComponent effectMemory = beamFromMissionEffect.getComponent(EffectMemoryComponent.class);
            Memory memory = new Memory(effectMemory.getMemory());
            memory.setValue("shipId", result.get("shipId"));
            memory.setValue("beamedIds", result.get("beamedId"));
            stackSystem.stackEntity(beamFromMissionEffect);
        } else if (action.equals("beamToMission")) {
            Entity beamToMissionEffect = spawnSystem.spawnEntity("game/effect/beam/beamToMissionEffect.template");
            EffectMemoryComponent effectMemory = beamToMissionEffect.getComponent(EffectMemoryComponent.class);
            Memory memory = new Memory(effectMemory.getMemory());
            memory.setValue("shipId", result.get("shipId"));
            memory.setValue("beamedIds", result.get("beamedId"));
            stackSystem.stackEntity(beamToMissionEffect);
        } else if (action.equals("beamBetweenShips")) {
            Entity beamBetweenShipsEffect = spawnSystem.spawnEntity("game/effect/beam/beamBetweenShipsEffect.template");
            EffectMemoryComponent effectMemory = beamBetweenShipsEffect.getComponent(EffectMemoryComponent.class);
            Memory memory = new Memory(effectMemory.getMemory());
            memory.setValue("fromShipId", result.get("fromShipId"));
            memory.setValue("toShipId", result.get("toShipId"));
            memory.setValue("beamedIds", result.get("beamedId"));
            stackSystem.stackEntity(beamBetweenShipsEffect);
        } else if (action.equals("moveShip")) {
            Entity beamBetweenShipsEffect = spawnSystem.spawnEntity("game/effect/beam/moveShipEffect.template");
            EffectMemoryComponent effectMemory = beamBetweenShipsEffect.getComponent(EffectMemoryComponent.class);
            Memory memory = new Memory(effectMemory.getMemory());
            memory.setValue("shipId", result.get("shipId"));
            memory.setValue("missionId", result.get("missionId"));
            stackSystem.stackEntity(beamBetweenShipsEffect);
        } else if (action.equals("attemptPlanetMission")) {
            Entity attemptPlanetMissionEffect = spawnSystem.spawnEntity("game/effect/mission/attemptPlanetMission.template");
            EffectMemoryComponent effectMemory = attemptPlanetMissionEffect.getComponent(EffectMemoryComponent.class);
            Memory memory = new Memory(effectMemory.getMemory());
            memory.setValue("missionId", result.get("missionId"));
            stackSystem.stackEntity(attemptPlanetMissionEffect);
        } else if (action.equals("attemptSpaceMission")) {
            Entity attemptPlanetMissionEffect = spawnSystem.spawnEntity("game/effect/mission/attemptSpaceMission.template");
            EffectMemoryComponent effectMemory = attemptPlanetMissionEffect.getComponent(EffectMemoryComponent.class);
            Memory memory = new Memory(effectMemory.getMemory());
            memory.setValue("missionId", result.get("missionId"));
            stackSystem.stackEntity(attemptPlanetMissionEffect);
        }
    }

    @Override
    protected void processSystem() {

    }
}
