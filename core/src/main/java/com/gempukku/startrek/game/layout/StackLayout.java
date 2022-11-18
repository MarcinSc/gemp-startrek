package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.game.render.zone.CommonZones;

public class StackLayout {
    private static final float HORIZONTAL_DISTANCE = 1.3f;
    private static final float DISTANCE_FROM_CAMERA = 3f;
    private static final float CARD_SEPARATION = -0.15f;
    private static final float CARD_SCALE = 0.4f;
    private static final float STACK_DEPTH = 0.01f;
    private static final float STACK_SCALE = 0.01f;

    public static void layoutStack(CommonZones commonZones,
                                   Camera camera, TransformSystem transformSystem) {
        Vector3 cameraRight = new Vector3(camera.direction).crs(camera.up);
        Vector3 stackPosition =
                new Vector3(camera.position)
                        .add(new Vector3(camera.direction).scl(DISTANCE_FROM_CAMERA))
                        .add(new Vector3(cameraRight).scl(HORIZONTAL_DISTANCE));


        Array<Entity> objectsOnStack = commonZones.getObjectsOnStack();
        int stackSize = objectsOnStack.size;
        for (int i = 0; i < stackSize; i++) {
            Vector3 depth = new Vector3(camera.direction).scl((stackSize - i - 1) * STACK_DEPTH);
            float scale = CARD_SCALE - (stackSize - i - 1) * STACK_SCALE;
            Vector3 separation = new Vector3(cameraRight).scl((stackSize - i - 1) * CARD_SEPARATION);

            Entity objectOnStack = objectsOnStack.get(i);
            transformSystem.setTransform(objectOnStack, new Matrix4()
                    .translate(stackPosition)
                    .translate(separation)
                    .translate(depth)
                    .scl(scale)
                    .rotate(1, 0, 0, 10));
        }
    }
}
