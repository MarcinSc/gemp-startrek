package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.Quadrant;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.MoveCostModifier;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

import java.util.function.Consumer;

public class InRangeFilterHandler extends CardFilterSystem {
    private CardFilteringSystem cardFilteringSystem;
    private CardAbilitySystem cardAbilitySystem;
    private CardLookupSystem cardLookupSystem;
    private AmountResolverSystem amountResolverSystem;
    private MissionOperations missionOperations;

    public InRangeFilterHandler() {
        super("inRange");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardInMissionComponent cardInMission = sourceEntity.getComponent(CardInMissionComponent.class);
                String missionOwner = cardInMission.getMissionOwner();
                int missionIndex = cardInMission.getMissionIndex();
                Entity fromMissionCardEntity = missionOperations.findMission(missionOwner, missionIndex);

                int shipRange = amountResolverSystem.resolveAmount(sourceEntity, memory, "shipRange");
                int requiredRange = calculateRangeBetweenMissions(sourceEntity, fromMissionCardEntity, cardEntity);

                return shipRange >= requiredRange;
            }
        };
    }

    private int calculateRangeBetweenMissions(Entity shipEntity, Entity fromMissionEntity, Entity toMissionEntity) {
        CardDefinition fromMission = cardLookupSystem.getCardDefinition(fromMissionEntity);
        CardDefinition toMission = cardLookupSystem.getCardDefinition(toMissionEntity);
        Quadrant fromQuadrant = fromMission.getQuadrant();
        int fromSpan = fromMission.getSpan();
        Quadrant toQuadrant = toMission.getQuadrant();
        int toSpan = toMission.getSpan();

        int[] moveCost = new int[1];
        moveCost[0] = fromSpan + toSpan;
        if (fromQuadrant != toQuadrant)
            moveCost[0] += 2;

        cardFilteringSystem.forEachCard(shipEntity, null, "inPlay",
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        for (MoveCostModifier modifierAbility : cardAbilitySystem.getCardAbilities(entity, MoveCostModifier.class)) {
                            CardFilter shipFilter = cardFilteringSystem.resolveCardFilter(modifierAbility.getShipFilter());
                            CardFilter fromFilter = cardFilteringSystem.resolveCardFilter(modifierAbility.getFromFilter());
                            CardFilter toFilter = cardFilteringSystem.resolveCardFilter(modifierAbility.getToFilter());
                            if (shipFilter.accepts(entity, null, shipEntity)
                                    && fromFilter.accepts(entity, null, fromMissionEntity)
                                    && toFilter.accepts(entity, null, toMissionEntity))
                                moveCost[0] += amountResolverSystem.resolveAmount(entity, null, modifierAbility.getAmount());
                        }
                    }
                }, "hasAbility(MoveCostModifier)");

        return moveCost[0];
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
