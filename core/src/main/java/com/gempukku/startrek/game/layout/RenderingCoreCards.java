package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.text.TextHorizontalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.text.TextVerticalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyleConstants;
import com.gempukku.startrek.game.render.zone.RenderedCardGroup;

public class RenderingCoreCards implements CardZoneCards {
    private TextStyle normalTextStyle;
    private RenderedCardGroup coreCards;

    public RenderingCoreCards(RenderedCardGroup coreCards, float lineSpacing) {
        this.coreCards = coreCards;

        normalTextStyle = new TextStyle();
        normalTextStyle.setAttribute(TextStyleConstants.AlignmentVertical, TextVerticalAlignment.center);
        normalTextStyle.setAttribute(TextStyleConstants.AlignmentHorizontal, TextHorizontalAlignment.center);
        normalTextStyle.setAttribute(TextStyleConstants.LineSpacing, lineSpacing);
    }

    @Override
    public int getLineCount() {
        return 1;
    }

    @Override
    public Array<Entity> getTopLevelCards(int lineIndex) {
        return coreCards.getRenderedCards();
    }

    @Override
    public Array<Entity> getAttachedCards(Entity entity) {
        return coreCards.getAttachedCards(entity).getRenderedCards();
    }

    @Override
    public int getAttachedCardCount(Entity entity) {
        // TODO: finish this!
        return 0;
    }

    @Override
    public TextStyle getCardTextStyle(int lineIndex, int cardIndex) {
        return normalTextStyle;
    }
}
