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
    private static final int MISSION_TYPE_INDEX = 2;
    private static final int AFFILIATION_INDEX = 2;
    private static final int ICON_START_INDEX = 3;

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
            if (isNoun(cardDefinition))
                return SMALL_NOUN_TITLE_INDEX;
            return SMALL_VERB_TITLE_INDEX;
        } else {
            return -1;
        }
    }

    public static int getSubtitleTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            return -1;
        } else {
            return -1;
        }
    }

    public static int getCostTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            return -1;
        } else {
            return -1;
        }
    }

    public static int getTypeTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            return -1;
        } else {
            return -1;
        }
    }

    public static int getTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone)) {
            return -1;
        } else {
            return -1;
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
        if (isSmall(cardZone) && cardDefinition.getType() == CardType.Personnel)
            return SMALL_NOUN_STATS_INDEX;
        return -1;
    }

    public static int getShipStatsTextIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isSmall(cardZone) && cardDefinition.getType() == CardType.Ship)
            return SMALL_NOUN_STATS_INDEX;
        return -1;
    }

    public static TextureReference getTextureReference(Entity renderedEntity, int textureIndex) {
        SpriteComponent cardTemplateSprite = renderedEntity.getComponent(SpriteComponent.class);
        return (TextureReference) cardTemplateSprite.getSprites().get(textureIndex).getProperties().get("Texture");
    }

    public static int getTemplateTextureIndex(CardDefinition cardDefinition, CardZone cardZone) {
        return TEMPLATE_INDEX;
    }

    public static int getImageTextureIndex(CardDefinition cardDefinition, CardZone cardZone) {
        return IMAGE_INDEX;
    }

    public static int getMissionTypeTextureIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isMission(cardDefinition))
            return MISSION_TYPE_INDEX;
        return -1;
    }

    public static int getAffiliationTextureIndex(CardDefinition cardDefinition, CardZone cardZone) {
        if (isNoun(cardDefinition))
            return AFFILIATION_INDEX;
        return -1;
    }

    public static int getIconTextureIndex(CardDefinition cardDefinition, int iconIndex, CardZone cardZone) {
        if (isNoun(cardDefinition))
            return ICON_START_INDEX + iconIndex;
        return -1;
    }

    private static boolean isSmall(CardZone cardZone) {
        return cardZone == CardZone.Mission || cardZone == CardZone.Brig || cardZone == CardZone.Core;
    }

    private static boolean isMission(CardDefinition cardDefinition) {
        return cardDefinition.getType() == CardType.Mission;
    }

    private static boolean isNoun(CardDefinition cardDefinition) {
        return cardDefinition.getType() == CardType.Personnel || cardDefinition.getType() == CardType.Ship;
    }
}
