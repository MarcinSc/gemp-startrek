package com.gempukku.startrek.card;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.template.JsonTemplateLoader;

public class CardData {
    private ObjectMap<String, CardDefinition> cardDefinitions = new ObjectMap<>();

    public void initializeCards() {
        Json json = new Json();
        json.setSerializer(JsonValue.class,
                new Json.Serializer<JsonValue>() {
                    @Override
                    public void write(Json json, JsonValue object, Class knownType) {

                    }

                    @Override
                    public JsonValue read(Json json, JsonValue jsonData, Class type) {
                        return jsonData;
                    }
                });

        JsonValue allCardsJson = JsonTemplateLoader.loadTemplateFromFile("card/cards.entities", new InternalFileHandleResolver());
        JsonValue setsJson = allCardsJson.get("sets");
        for (JsonValue setJson : setsJson) {
            JsonValue cardsJson = setJson.get("cards");
            for (JsonValue cardJson : cardsJson) {
                String cardId = cardJson.name();
                cardDefinitions.put(cardId, json.readValue(CardDefinition.class, cardJson));
            }
        }
    }

    public CardDefinition getCardDefinition(String cardId) {
        return cardDefinitions.get(cardId);
    }
}
