package com.gempukku.startrek.game.layout;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.DefaultGlyphOffseter;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.RenderedCardGroup;

public class CoreLayout {
    private static final float MAXIMUM_SCALE = 1.3f;

    private final static float CORE_CENTER_X_DISTANCE = 2.7f;
    private final static float CORE_CENTER_Y_DISTANCE = 0.005f;
    private final static float CORE_CENTER_Z_DISTANCE = 3f;

    private static final float STACK_HORIZONTAL_GAP = 0.03f;
    private static final float STACK_VERTICAL_GAP = 0.03f;
    private static final float STACK_WIDTH = 0.497570f;
    private static final float STACK_HEIGHT = 0.497570f;
    private static final float STACK_STICKOUT_PERC = 0.25f;

    private static final float CORE_SPACE_WIDTH = 2f;
    private static final float CORE_SPACE_HEIGHT = 0.8f;

    private static final Matrix4 tmpMatrix = new Matrix4();

    public static void layoutCore(CardRenderingSystem cardRenderingSystem, RenderedCardGroup coreCards, PlayerPosition playerPosition,
                                  TransformSystem transformSystem) {
        DefaultGlyphOffseter defaultGlyphOffseter = new DefaultGlyphOffseter();

        float verticalTranslate = (playerPosition == PlayerPosition.Lower) ?
                CORE_CENTER_Z_DISTANCE : -CORE_CENTER_Z_DISTANCE;
        float yRotateDegrees = (playerPosition == PlayerPosition.Lower) ? 0f : 180f;

        // Move to mission center
        tmpMatrix.idt()
                .translate(0, CORE_CENTER_Y_DISTANCE, verticalTranslate)
                .rotate(new Vector3(0, 1, 0), yRotateDegrees)
                .translate(CORE_CENTER_X_DISTANCE, 0, 0);

        RenderingCoreCards renderingMissionCards = new RenderingCoreCards(cardRenderingSystem, coreCards, STACK_VERTICAL_GAP);

        CardZoneParsedText missionParsedText = new CardZoneParsedText(renderingMissionCards,
                STACK_HEIGHT, STACK_WIDTH, STACK_HORIZONTAL_GAP, STACK_STICKOUT_PERC);

        CardsInZoneLayout.layoutCards(transformSystem, defaultGlyphOffseter, tmpMatrix, missionParsedText,
                MAXIMUM_SCALE, CORE_SPACE_WIDTH, CORE_SPACE_HEIGHT);
    }
}
