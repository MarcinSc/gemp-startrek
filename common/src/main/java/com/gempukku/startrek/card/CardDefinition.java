package com.gempukku.startrek.card;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class CardDefinition {
    private String cardImagePath;

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
    private Array<PersonnelSkill> skills;
    private Array<CardIcon> icons;
    private int integrity;
    private int cunning;
    private int strength;

    // Ship specific
    private String shipClass;
    private Array<CardIcon> staffingRequirements;
    private int range;
    private int weapons;
    private int shields;

    // Mission specific
    private MissionType missionType;
    private Quadrant quadrant;
    private int span;
    private int points;
    private String requirements;
    private String requirementsText;
    private String affiliations;
    private String affiliationsText;

    // Dilemma specific
    private DilemmaType dilemmaType;

    public String getCardImagePath() {
        return cardImagePath;
    }

    public void setCardImagePath(String cardImagePath) {
        this.cardImagePath = cardImagePath;
    }

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

    public String getShipClass() {
        return shipClass;
    }

    public Array<PersonnelSkill> getSkills() {
        return skills;
    }

    public Array<String> getKeywords() {
        return keywords;
    }

    public Array<CardIcon> getIcons() {
        return icons;
    }

    public int getRange() {
        return range;
    }

    public int getWeapons() {
        return weapons;
    }

    public int getShields() {
        return shields;
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

    public Quadrant getQuadrant() {
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
