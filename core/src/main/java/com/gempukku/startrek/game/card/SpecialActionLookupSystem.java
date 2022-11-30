package com.gempukku.startrek.game.card;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.ObjectMap;

public class SpecialActionLookupSystem extends BaseSystem {

    private ObjectMap<String, String> titleMap = new ObjectMap<>();
    private ObjectMap<String, String> textMap = new ObjectMap<>();

    @Override
    protected void initialize() {
        titleMap.put("missionAttempt", "Mission Attempt");
        textMap.put("missionAttempt", "Text of a mission attempt.");
    }

    public String getTitle(String specialAction) {
        return titleMap.get(specialAction);
    }

    public String getText(String specialAction) {
        return textMap.get(specialAction);
    }

    @Override
    protected void processSystem() {

    }
}
