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

public class ClientCardAbilitySystem extends BaseSystem {
    private CardLookupSystem cardLookupSystem;

    private ObjectMap<String, ClientCardAbilityHandler> handlerMap = new ObjectMap<>();

    private ObjectMap<String, Array<ClientCardAbility>> abilitiesMap = new ObjectMap<>();

    private boolean initialized = false;

    public void registerClientCardAbilityHandler(String type, ClientCardAbilityHandler handler) {
        handlerMap.put(type, handler);
    }


    public <T extends ClientCardAbility> T getClientCardAbility(Entity cardEntity, Class<T> clazz) {
        return getClientCardAbility(cardEntity.getComponent(CardComponent.class).getCardId(), clazz);
    }

    public <T extends ClientCardAbility> T getClientCardAbility(String cardId, Class<T> clazz) {
        Array<ClientCardAbility> clientCardAbilities = abilitiesMap.get(cardId);
        for (ClientCardAbility clientCardAbility : clientCardAbilities) {
            if (clazz.isAssignableFrom(clientCardAbility.getClass()))
                return (T) clientCardAbility;
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
                Array<ClientCardAbility> result = new Array<>();
                Array<JsonValue> abilities = cardDefinition.getAbilities();
                if (abilities != null) {
                    for (JsonValue ability : abilities) {
                        String abilityType = ability.getString("type");
                        ClientCardAbilityHandler clientCardAbilityHandler = handlerMap.get(abilityType);
                        if (clientCardAbilityHandler == null)
                            throw new GdxRuntimeException("Unable to resolve client card ability: " + abilityType);
                        result.add(clientCardAbilityHandler.resolveClientCardAbility(ability));
                    }
                }
                abilitiesMap.put(cardId, result);
            }
            initialized = true;
        }
    }
}
