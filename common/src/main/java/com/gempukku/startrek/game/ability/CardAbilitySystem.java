package com.gempukku.startrek.game.ability;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;

public class CardAbilitySystem extends BaseSystem {
    private CardLookupSystem cardLookupSystem;

    private ObjectMap<String, CardAbilityHandler> handlerMap = new ObjectMap<>();

    private ObjectMap<String, Array<CardAbility>> abilitiesMap = new ObjectMap<>();

    private boolean initialized = false;

    public void registerCardAbilityHandler(String type, CardAbilityHandler handler) {
        handlerMap.put(type, handler);
    }


    public <T extends CardAbility> T getCardAbility(Entity cardEntity, Class<T> clazz) {
        return getCardAbility(cardEntity.getComponent(CardComponent.class).getCardId(), clazz);
    }

    public <T extends CardAbility> T getCardAbility(String cardId, Class<T> clazz) {
        Array<CardAbility> cardAbilities = abilitiesMap.get(cardId);
        for (CardAbility cardAbility : cardAbilities) {
            if (clazz.isAssignableFrom(cardAbility.getClass()))
                return (T) cardAbility;
        }
        return null;
    }

    @Override
    protected void processSystem() {
        if (!initialized) {
            CardData cardData = cardLookupSystem.getCardData();
            for (ObjectMap.Entry<String, CardDefinition> cardDefinitionPair : cardData.getCardDefinitions()) {
                String cardId = cardDefinitionPair.key;
                CardDefinition cardDefinition = cardDefinitionPair.value;
                Array<CardAbility> result = new Array<>();
                Array<JsonValue> abilities = cardDefinition.getAbilities();
                if (abilities != null) {
                    for (JsonValue ability : abilities) {
                        String abilityType = ability.getString("type");
                        CardAbilityHandler cardAbilityHandler = handlerMap.get(abilityType);
                        if (cardAbilityHandler == null)
                            throw new GdxRuntimeException("Unable to resolve card ability: " + abilityType);
                        result.add(cardAbilityHandler.resolveCardAbility(ability));
                    }
                }
                abilitiesMap.put(cardId, result);
            }
            initialized = true;
        }
    }
}
