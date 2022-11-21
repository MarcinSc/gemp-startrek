package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayRequirements;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.zone.CardInPlayComponent;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class ExecuteOrdersDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;
    private ExecutionStackSystem stackSystem;
    private CardFilterResolverSystem cardFilterResolverSystem;
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
            }
        } catch (Exception exp) {
            // Ignore
        }
        return false;
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

        CardFilter firstShipRequirements = PlayRequirements.createBeamFromMissionShipRequirements(
                decisionPlayer, cardFilterResolverSystem);
        if (!firstShipRequirements.accepts(null, null, fromShipEntity))
            return false;

        CardFilter secondShipRequirements = PlayRequirements.createBeamSelectAnotherShipRequirements(
                decisionPlayer, fromShipEntity, cardFilterResolverSystem);
        if (!secondShipRequirements.accepts(null, null, toShipEntity))
            return false;

        String beamedId = result.get("beamedId");
        Array<Entity> beamedEntities = new Array<>();
        for (String id : StringUtils.split(beamedId, ",")) {
            beamedEntities.add(serverEntityIdSystem.findfromId(id));
        }

        if (beamedEntities.size == 0)
            return false;

        CardFilter cardFilter = PlayRequirements.createBeamBetweenShipsRequirements(
                decisionPlayer, fromShipEntity, toShipEntity, cardFilterResolverSystem);
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

        CardFilter shipRequirements = PlayRequirements.createBeamToMissionShipRequirements(
                decisionPlayer, cardFilterResolverSystem);
        if (!shipRequirements.accepts(null, null, shipEntity))
            return false;

        String beamedId = result.get("beamedId");
        Array<Entity> beamedEntities = new Array<>();
        for (String id : StringUtils.split(beamedId, ",")) {
            beamedEntities.add(serverEntityIdSystem.findfromId(id));
        }

        if (beamedEntities.size == 0)
            return false;

        CardFilter cardFilter = PlayRequirements.createBeamToMissionRequirements(
                decisionPlayer, shipEntity, cardFilterResolverSystem);
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

        CardFilter shipRequirements = PlayRequirements.createBeamFromMissionShipRequirements(
                decisionPlayer, cardFilterResolverSystem);
        if (!shipRequirements.accepts(null, null, shipEntity))
            return false;

        String beamedId = result.get("beamedId");
        Array<Entity> beamedEntities = new Array<>();
        for (String id : StringUtils.split(beamedId, ",")) {
            beamedEntities.add(serverEntityIdSystem.findfromId(id));
        }

        if (beamedEntities.size == 0)
            return false;

        CardFilter cardFilter = PlayRequirements.createBeamFromMissionRequirements(
                decisionPlayer, shipEntity, cardFilterResolverSystem);
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
            memory.setValue("shipId", result.get("shipId"));
            memory.setValue("beamedIds", result.get("beamedId"));
            stackSystem.stackEntity(beamBetweenShipsEffect);
        }
    }

    @Override
    protected void processSystem() {

    }
}
