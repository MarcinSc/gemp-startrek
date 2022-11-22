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
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class InRangeFilterHandler extends CardFilterSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private CardAbilitySystem cardAbilitySystem;
    private CardLookupSystem cardLookupSystem;
    private AmountResolverSystem amountResolverSystem;

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
                Entity fromMissionCardEntity = cardFilteringSystem.findFirstCardInPlay(cardEntity, memory,
                        "type(Mission),inMission(username(" + missionOwner + ")," + missionIndex + ")");

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

        int moveCost = fromSpan + toSpan;
        if (fromQuadrant != toQuadrant)
            moveCost += 2;

        for (Entity modifierCardEntity : cardFilteringSystem.findAllInPlay(shipEntity, null, "hasAbility(MoveCostModifier)")) {
            for (MoveCostModifier modifierAbility : cardAbilitySystem.getCardAbilities(modifierCardEntity, MoveCostModifier.class)) {
                CardFilter shipFilter = cardFilterResolverSystem.resolveCardFilter(modifierAbility.getShipFilter());
                CardFilter fromFilter = cardFilterResolverSystem.resolveCardFilter(modifierAbility.getFromFilter());
                CardFilter toFilter = cardFilterResolverSystem.resolveCardFilter(modifierAbility.getToFilter());
                if (shipFilter.accepts(modifierCardEntity, null, shipEntity)
                        && fromFilter.accepts(modifierCardEntity, null, fromMissionEntity)
                        && toFilter.accepts(modifierCardEntity, null, toMissionEntity))
                    moveCost += amountResolverSystem.resolveAmount(modifierCardEntity, null, modifierAbility.getAmount());
            }
        }
        return moveCost;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
