package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.text.TextHorizontalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.text.TextVerticalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyleConstants;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.MissionCards;

public class RenderingMissionCards implements CardZoneCards {
    private CardRenderingSystem cardRenderingSystem;
    private MissionCards missionCards;
    private TextStyle normalTextStyle;
    private TextStyle missionTextStyle;

    public RenderingMissionCards(CardRenderingSystem cardRenderingSystem, MissionCards missionCards, float lineSpacing) {
        this.cardRenderingSystem = cardRenderingSystem;
        this.missionCards = missionCards;

        normalTextStyle = new TextStyle();
        normalTextStyle.setAttribute(TextStyleConstants.AlignmentVertical, TextVerticalAlignment.center);
        normalTextStyle.setAttribute(TextStyleConstants.AlignmentHorizontal, TextHorizontalAlignment.center);
        normalTextStyle.setAttribute(TextStyleConstants.LineSpacing, lineSpacing);

        missionTextStyle = normalTextStyle.duplicate();
        missionTextStyle.setAttribute(TextStyleConstants.GlyphScale, 1.2f);
    }

    @Override
    public int getLineCount() {
        return 3;
    }

    @Override
    public Array<Entity> getTopLevelCards(int lineIndex) {
        switch (lineIndex) {
            case 0:
                return missionCards.getOpposingCards().getRenderedCards();
            case 1:
                return missionCards.getMissionCards().getRenderedCards();
            case 2:
                return missionCards.getMissionOwnerCards().getRenderedCards();
        }
        return null;
    }

    @Override
    public Array<Entity> getAttachedCards(Entity entity) {
        return cardRenderingSystem.getAttachedRenderedcards(entity);
    }

    @Override
    public int getAttachedCardCount(Entity entity) {
        return getAttachedCards(entity).size;
    }

    @Override
    public TextStyle getCardTextStyle(int lineIndex, int cardIndex) {
        if (lineIndex == 1)
            return missionTextStyle;
        return normalTextStyle;
    }
}
