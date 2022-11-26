package com.gempukku.startrek.game.layout;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.DefaultGlyphOffseter;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.MissionCards;
import com.gempukku.startrek.game.render.zone.PlayerZones;

public class MissionsLayout {
    private static final float MAXIMUM_SCALE = 1.3f;

    private static final float STACK_HORIZONTAL_GAP = 0.03f;
    private static final float STACK_VERTICAL_GAP = 0.03f;
    private static final float STACK_WIDTH = 0.497570f;
    private static final float STACK_HEIGHT = 0.497570f;
    private static final float STACK_STICKOUT_PERC = 0.25f;

    private static final float MISSION_SPACE_WIDTH = 2f;
    private static final float MISSION_SPACE_HEIGHT = 1.55f;
    private static final float MISSION_SPACE_GAP = 0.2f;
    private static final float MISSION_CENTER_Y_DISTANCE = 0.005f;
    private static final float MISSION_CENTER_Z_DISTANCE = 1.6f;

    private static final Matrix4 tmpMatrix = new Matrix4();

    public static void layoutMission(CardRenderingSystem cardRenderingSystem, PlayerZones playerZones, int missionIndex, PlayerPosition playerPosition,
                                     TransformSystem transformSystem) {
        DefaultGlyphOffseter defaultGlyphOffseter = new DefaultGlyphOffseter();

        float verticalTranslate = (playerPosition == PlayerPosition.Lower) ?
                MISSION_CENTER_Z_DISTANCE : -MISSION_CENTER_Z_DISTANCE;
        float horizontalTranslate = (missionIndex - 2) * (MISSION_SPACE_WIDTH + MISSION_SPACE_GAP);
        float yRotateDegrees = (playerPosition == PlayerPosition.Lower) ? 0f : 180f;

        // Move to mission center
        tmpMatrix.idt()
                .translate(0, MISSION_CENTER_Y_DISTANCE, verticalTranslate)
                .rotate(new Vector3(0, 1, 0), yRotateDegrees)
                .translate(horizontalTranslate, 0, 0);

        MissionCards missionCards = playerZones.getMissionCards(missionIndex);
        RenderingMissionCards renderingMissionCards = new RenderingMissionCards(cardRenderingSystem, missionCards,
                STACK_VERTICAL_GAP);

        CardZoneParsedText missionParsedText = new CardZoneParsedText(renderingMissionCards,
                STACK_HEIGHT, STACK_WIDTH, STACK_HORIZONTAL_GAP, STACK_STICKOUT_PERC);

        CardsInZoneLayout.layoutCards(transformSystem, defaultGlyphOffseter, tmpMatrix, missionParsedText,
                MAXIMUM_SCALE, MISSION_SPACE_WIDTH, MISSION_SPACE_HEIGHT);
    }
}
