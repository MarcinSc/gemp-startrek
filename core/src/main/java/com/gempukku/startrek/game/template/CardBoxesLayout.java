package com.gempukku.startrek.game.template;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.texture.TextureReference;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteComponent;
import com.gempukku.libgdx.lib.graph.artemis.text.TextBlock;
import com.gempukku.libgdx.lib.graph.artemis.text.TextComponent;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.zone.CardZone;

public class CardBoxesLayout {
    private static final int TEMPLATE_INDEX = 0;
    private static final int IMAGE_INDEX = 1;

    private static final int FULL_ICON_START_INDEX = 2;

    private static final int SMALL_MISSION_TYPE_INDEX = 2;
    private static final int SMALL_AFFILIATION_INDEX = 2;
    private static final int SMALL_ICON_START_INDEX = 3;

    private static final int FULL_TITLE_INDEX = 0;
    private static final int FULL_SUBTITLE_INDEX = 1;
    private static final int FULL_COST_INDEX = 2;
    private static final int FULL_TYPE_INDEX = 3;
    private static final int FULL_TEXT_INDEX = 4;
    private static final int FULL_STAT1_TYPE_INDEX = 5;
    private static final int FULL_STAT2_TYPE_INDEX = 6;
    private static final int FULL_STAT3_TYPE_INDEX = 7;

    private static final int FULL_UNAFFILIATED_COST_INDEX = 1;
    private static final int FULL_UNAFFILIATED_TYPE_INDEX = 2;
    private static final int FULL_UNAFFILIATED_TEXT_INDEX = 3;

    private static final int SMALL_MISSION_TITLE_INDEX = 0;
    private static final int SMALL_MISSION_POINTS_INDEX = 1;
    private static final int SMALL_MISSION_SPAN_INDEX = 2;
    private static final int SMALL_MISSION_AFFILIATION_INDEX = 3;

    private static final int SMALL_NOUN_TITLE_INDEX = 0;
    private static final int SMALL_NOUN_STATS_INDEX = 1;

    private static final int SMALL_VERB_TITLE_INDEX = 0;

    public static TextBlock getTextBlock(Entity renderedEntity, int blockIndex) {
        TextComponent text = renderedEntity.getComponent(TextComponent.class);
        return text.getTextBlocks().get(blockIndex);
    }

    public static int getTitleTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            if (isMission(cardDefinition))
                return SMALL_MISSION_TITLE_INDEX;
            if (isAffiliated(cardDefinition))
                return SMALL_NOUN_TITLE_INDEX;
            return SMALL_VERB_TITLE_INDEX;
        } else {
            return FULL_TITLE_INDEX;
        }
    }

    public static int getSubtitleTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            return -1;
        } else {
            if (isAffiliated(cardDefinition))
                return FULL_SUBTITLE_INDEX;
            return -1;
        }
    }

    public static int getCostTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            return -1;
        } else {
            if (hasCost(cardDefinition)) {
                if (isAffiliated(cardDefinition))
                    return FULL_COST_INDEX;
                else
                    return FULL_UNAFFILIATED_COST_INDEX;
            }
            return -1;
        }
    }

    public static int getTypeTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            return -1;
        } else {
            if (!isAffiliated(cardDefinition))
                return FULL_UNAFFILIATED_TYPE_INDEX;
            return -1;
        }
    }

    public static int getSpeciesTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (!isSmall(cardZone) && isPersonnel(cardDefinition))
            return FULL_TYPE_INDEX;
        return -1;
    }

    public static int getShipClassTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (!isSmall(cardZone) && isShip(cardDefinition))
            return FULL_TYPE_INDEX;
        return -1;
    }

    public static int getIntegrityTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (!isSmall(cardZone) && isPersonnel(cardDefinition))
            return FULL_STAT1_TYPE_INDEX;
        return -1;
    }

    public static int getCunningTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (!isSmall(cardZone) && isPersonnel(cardDefinition))
            return FULL_STAT2_TYPE_INDEX;
        return -1;
    }

    public static int getStrengthTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (!isSmall(cardZone) && isPersonnel(cardDefinition))
            return FULL_STAT3_TYPE_INDEX;
        return -1;
    }

    public static int getRangeTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (!isSmall(cardZone) && isShip(cardDefinition))
            return FULL_STAT1_TYPE_INDEX;
        return -1;
    }

    public static int getWeaponsTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (!isSmall(cardZone) && isShip(cardDefinition))
            return FULL_STAT2_TYPE_INDEX;
        return -1;
    }

    public static int getShieldsTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (!isSmall(cardZone) && isShip(cardDefinition))
            return FULL_STAT3_TYPE_INDEX;
        return -1;
    }

    public static int getTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            return -1;
        } else {
            if (isAffiliated(cardDefinition))
                return FULL_TEXT_INDEX;
            return FULL_UNAFFILIATED_TEXT_INDEX;
        }
    }

    public static int getPointsTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone) && isMission(cardDefinition))
            return SMALL_MISSION_POINTS_INDEX;
        return -1;
    }

    public static int getSpanTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone) && isMission(cardDefinition))
            return SMALL_MISSION_SPAN_INDEX;
        return -1;
    }

    public static int getAffiliationsTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone) && isMission(cardDefinition))
            return SMALL_MISSION_AFFILIATION_INDEX;
        return -1;
    }

    public static int getPersonnelStatsTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone) && isPersonnel(cardDefinition))
            return SMALL_NOUN_STATS_INDEX;
        return -1;
    }

    public static int getShipStatsTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone) && isShip(cardDefinition))
            return SMALL_NOUN_STATS_INDEX;
        return -1;
    }

    public static TextureReference getTextureReference(Entity renderedEntity, int textureIndex) {
        SpriteComponent cardTemplateSprite = renderedEntity.getComponent(SpriteComponent.class);
        return (TextureReference) cardTemplateSprite.getSprites().get(textureIndex).getProperties().get("Texture");
    }

    public static int getAffiliationTemplateTextureIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            return -1;
        } else {
            return TEMPLATE_INDEX;
        }
    }

    public static int getImageTextureIndex(CardDefinition cardDefinition, CardZone cardZone) {
        return IMAGE_INDEX;
    }

    public static int getMissionTypeTextureIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isMission(cardDefinition))
            return SMALL_MISSION_TYPE_INDEX;
        return -1;
    }

    public static int getAffiliationTextureIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone) && isAffiliated(cardDefinition))
            return SMALL_AFFILIATION_INDEX;
        return -1;
    }

    public static int getIconTextureIndex(CardDefinition cardDefinition, int iconIndex, CardZone cardZone) {
        if (isSmall(cardZone)) {
            if (isAffiliated(cardDefinition))
                return SMALL_ICON_START_INDEX + iconIndex;
        } else {
            if (isAffiliated(cardDefinition))
                return FULL_ICON_START_INDEX + iconIndex;
        }
        return -1;
    }

    private static boolean isSmall(CardZone cardZone) {
        return cardZone == CardZone.Mission || cardZone == CardZone.Brig || cardZone == CardZone.Core;
    }

    private static boolean isMission(CardDefinition cardDefinition) {
        return cardDefinition.getType() == CardType.Mission;
    }

    private static boolean isAffiliated(CardDefinition cardDefinition) {
        return isPersonnel(cardDefinition) || isShip(cardDefinition);
    }

    private static boolean isNoun(CardDefinition cardDefinition) {
        return isPersonnel(cardDefinition) || isShip(cardDefinition) || cardDefinition.getType() == CardType.Equipment;
    }

    private static boolean hasCost(CardDefinition cardDefinition) {
        return isNoun(cardDefinition) || cardDefinition.getType() == CardType.Event;
    }

    private static boolean isPersonnel(CardDefinition cardDefinition) {
        return cardDefinition.getType() == CardType.Personnel;
    }

    private static boolean isShip(CardDefinition cardDefinition) {
        return cardDefinition.getType() == CardType.Ship;
    }
}
