package com.gempukku.startrek.server.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.*;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import org.junit.Test;

public class ValidateCardsTest extends AbstractGameTest {
    private CardAbilitySystem cardAbilitySystem;
    private CardFilteringSystem cardFilteringSystem;
    private ConditionResolverSystem conditionResolverSystem;

    @Test
    public void validateAllCards() {
        setupGame(createDeckWithMissions());

        CardLookupSystem cardLookupSystem = world.getSystem(CardLookupSystem.class);
        cardAbilitySystem = world.getSystem(CardAbilitySystem.class);
        cardFilteringSystem = world.getSystem(CardFilteringSystem.class);
        conditionResolverSystem = world.getSystem(ConditionResolverSystem.class);

        int errorCount = 0;

        CardData cardData = cardLookupSystem.getCardData();
        for (ObjectMap.Entry<String, CardDefinition> cardDefinitionEntry : cardData.getCardDefinitions()) {
            String cardId = cardDefinitionEntry.key;
            System.out.println("Card id: " + cardId);
            boolean valid = true;
            CardDefinition cardDefinition = cardDefinitionEntry.value;
            if (!validateAbilities(cardDefinition)) {
                errorCount++;
                valid = false;
            }

            if (!validateByCardType(cardDefinition)) {
                System.out.println("Invalid data based on card type - " + cardDefinition.getType());
                errorCount++;
                valid = false;
            }


            if (valid)
                System.out.println("Valid");
        }
        System.out.println("Error count: " + errorCount);
    }

    private boolean validateByCardType(CardDefinition cardDefinition) {
        CardType cardType = cardDefinition.getType();
        switch (cardType) {
            case Personnel:
                return validatePersonnel(cardDefinition);
            case Ship:
                return validateShip(cardDefinition);
            case Equipment:
                return validateEquipment(cardDefinition);
            case Event:
                return validateEvent(cardDefinition);
            case Interrupt:
                return validateInterrupt(cardDefinition);
            case Mission:
                return validateMission(cardDefinition);
            case Dilemma:
                return validateDilemma(cardDefinition);
        }
        return false;
    }

    private boolean validatePersonnel(CardDefinition cardDefinition) {
        if (cardDefinition.getAffiliation() == null)
            return false;
        if (cardDefinition.getCost() < 1)
            return false;
        if (cardDefinition.getSpecies() == null)
            return false;
        if (cardDefinition.getIntegrity() < 1)
            return false;
        if (cardDefinition.getCunning() < 1)
            return false;
        if (cardDefinition.getStrength() < 1)
            return false;
        if (cardDefinition.isUnique() == (cardDefinition.getSubtitle() == null))
            return false;
        return true;
    }

    private boolean validateShip(CardDefinition cardDefinition) {
        if (cardDefinition.getAffiliation() == null)
            return false;
        if (cardDefinition.getCost() < 1)
            return false;
        if (cardDefinition.getShipClass() == null)
            return false;
        if (cardDefinition.getRange() < 1)
            return false;
        if (cardDefinition.getWeapons() < 1)
            return false;
        if (cardDefinition.getShields() < 1)
            return false;
        if (cardDefinition.getStaffingRequirements() == null || cardDefinition.getStaffingRequirements().size == 0)
            return false;
        if (cardDefinition.isUnique() == (cardDefinition.getSubtitle() == null))
            return false;
        return true;
    }

    private boolean validateEquipment(CardDefinition cardDefinition) {
        if (cardDefinition.getCost() < 1)
            return false;
        return true;
    }

    private boolean validateEvent(CardDefinition cardDefinition) {
        if (cardDefinition.getCost() < 1)
            return false;
        return true;
    }

    private boolean validateInterrupt(CardDefinition cardDefinition) {
        return true;
    }

    private boolean validateMission(CardDefinition cardDefinition) {
        MissionType missionType = cardDefinition.getMissionType();
        if (missionType == null)
            return false;
        if (missionType != MissionType.Headquarters) {
            try {
                cardFilteringSystem.validateFilter(cardDefinition.getAffiliations());
                conditionResolverSystem.validateCondition(cardDefinition.getRequirements());
            } catch (Exception exp) {
                exp.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean validateDilemma(CardDefinition cardDefinition) {
        if (cardDefinition.getCost() < 1)
            return false;
        if (cardDefinition.getDilemmaType() == null)
            return false;
        return true;
    }

    private boolean validateAbilities(CardDefinition cardDefinition) {
        Array<JsonValue> abilities = cardDefinition.getAbilities();
        if (abilities != null) {
            for (JsonValue ability : abilities) {
                try {
                    cardAbilitySystem.validate(ability);
                } catch (RuntimeException exp) {
                    exp.printStackTrace();
                    return false;
//                        throw exp;
                }
            }
        }
        return true;
    }
}
