package com.gempukku.startrek.card;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class CardDefinition {
    private CardType type;
    private Affiliation affiliation;
    private int cost;
    private boolean unique;
    private String title;
    private String subtitle;
    private Array<JsonValue> abilities;
    private String lore;
    private Array<String> keywords;

    // Personnel specific
    private Species species;
    private Array<String> skills;
    private Array<CardIcon> icons;
    private int integrity;
    private int cunning;
    private int strength;

    // Mission specific
    private MissionType missionType;
    private String quadrant;
    private int span;
    private int points;
    private String requirements;
    private String requirementsText;
    private String affiliations;
    private String affiliationsText;

    // Dilemma specific
    private DilemmaType dilemmaType;

    public CardType getType() {
        return type;
    }

    public Affiliation getAffiliation() {
        return affiliation;
    }

    public int getCost() {
        return cost;
    }

    public boolean isUnique() {
        return unique;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Species getSpecies() {
        return species;
    }

    public Array<String> getSkills() {
        return skills;
    }

    public Array<String> getKeywords() {
        return keywords;
    }

    public Array<CardIcon> getIcons() {
        return icons;
    }

    public int getIntegrity() {
        return integrity;
    }

    public int getCunning() {
        return cunning;
    }

    public int getStrength() {
        return strength;
    }

    public Array<JsonValue> getAbilities() {
        return abilities;
    }

    public String getLore() {
        return lore;
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public String getQuadrant() {
        return quadrant;
    }

    public int getSpan() {
        return span;
    }

    public int getPoints() {
        return points;
    }

    public String getAffiliationsText() {
        return affiliationsText;
    }
}
