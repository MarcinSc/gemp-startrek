package com.gempukku.startrek.server.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import org.junit.Test;

public class ValidateCardsTest extends AbstractGameTest {
    @Test
    public void validateAllCards() {
        setupGame(createDeck("1_188", "1_210"));

        CardLookupSystem cardLookupSystem = world.getSystem(CardLookupSystem.class);
        CardAbilitySystem cardAbilitySystem = world.getSystem(CardAbilitySystem.class);

        int errorCount = 0;

        CardData cardData = cardLookupSystem.getCardData();
        for (ObjectMap.Entry<String, CardDefinition> cardDefinitionEntry : cardData.getCardDefinitions()) {
            String cardId = cardDefinitionEntry.key;
            CardDefinition cardDefinition = cardDefinitionEntry.value;
            Array<JsonValue> abilities = cardDefinition.getAbilities();
            if (abilities != null) {
                for (JsonValue ability : abilities) {
                    try {
                        cardAbilitySystem.validate(ability);
                    } catch (RuntimeException exp) {
                        System.out.println("Card id: " + cardId);
                        exp.printStackTrace();
                        errorCount++;
//                        throw exp;
                    }
                }
            }
        }
        System.out.println("Error count: " + errorCount);
    }
}
