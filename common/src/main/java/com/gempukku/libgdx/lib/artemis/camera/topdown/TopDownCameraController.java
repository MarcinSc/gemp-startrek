package com.gempukku.libgdx.lib.artemis.camera.topdown;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.camera.CameraController;
import com.gempukku.libgdx.lib.artemis.camera.ZAxisTiltCameraController;
import com.gempukku.libgdx.lib.artemis.camera.ZoomCameraController;

public class TopDownCameraController implements CameraController, ZoomCameraController, ZAxisTiltCameraController {
    private ComponentMapper<TopDownCameraComponent> topDownCameraComponentMapper;
    private EntitySubscription cameraEntitySubscription;
    private World world;

    private Array<Entity> newCameraEntities = new Array<>();

    private ObjectMap<String, PerspectiveCamera> perspectiveCameras = new ObjectMap<>();
    private ObjectMap<String, Entity> cameraEntities = new ObjectMap<>();

    @Override
    public void setupWithWorld(final World world) {
        topDownCameraComponentMapper = world.getMapper(TopDownCameraComponent.class);
        this.world = world;
        cameraEntitySubscription = world.getAspectSubscriptionManager().get(Aspect.all(TopDownCameraComponent.class));
        cameraEntitySubscription.
                addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0; i < entities.size(); i++) {
                                    newCameraEntities.add(world.getEntity(entities.get(i)));
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0; i < entities.size(); i++) {
                                    cameraRemoved(world.getEntity(entities.get(i)));
                                }
                            }
                        });
    }

    private void cameraRemoved(Entity cameraEntity) {
        TopDownCameraComponent topDownCamera = topDownCameraComponentMapper.get(cameraEntity);
        cameraEntities.remove(topDownCamera.getName());
        perspectiveCameras.remove(topDownCamera.getName());
    }

    @Override
    public void update(float deltaTime) {
        processNewCameras();

        for (ObjectMap.Entry<String, Entity> cameraEntityEntry : cameraEntities) {
            String cameraName = cameraEntityEntry.key;
            Entity cameraEntity = cameraEntityEntry.value;

            TopDownCameraComponent topDownCamera = topDownCameraComponentMapper.get(cameraEntity);

            PerspectiveCamera perspectiveCamera = perspectiveCameras.get(cameraName);

            perspectiveCamera.near = topDownCamera.getNear();
            perspectiveCamera.far = topDownCamera.getFar();
            perspectiveCamera.fieldOfView = topDownCamera.getFieldOfView();

            Vector3 position = perspectiveCamera.position;
            float oldDistance = topDownCamera.getOldDistance();

            float newDistance = MathUtils.lerp(oldDistance, topDownCamera.getDistance(), 5f * deltaTime);
            newDistance = MathUtils.clamp(newDistance,
                    Math.min(oldDistance, topDownCamera.getDistance()),
                    Math.max(oldDistance, topDownCamera.getDistance()));

            position.set(0, 1, 0);
            position.rotate(topDownCamera.getzAxisAngle(), 0, 0, 1);
            position.rotate(topDownCamera.getyAxisAngle(), 0, 1, 0);
            position.scl(newDistance);
            position.add(topDownCamera.getCenter());

            perspectiveCamera.up.set(0, 0, -1);
            perspectiveCamera.lookAt(topDownCamera.getCenter());
            perspectiveCamera.update();

            topDownCamera.setOldDistance(newDistance);
        }
    }

    private void processNewCameras() {
        for (Entity newCameraEntity : newCameraEntities) {
            TopDownCameraComponent topDownCamera = topDownCameraComponentMapper.get(newCameraEntity);
            topDownCamera.setOldDistance(topDownCamera.getDistance());
            cameraEntities.put(topDownCamera.getName(), newCameraEntity);
            perspectiveCameras.put(topDownCamera.getName(), new PerspectiveCamera());
        }
        newCameraEntities.clear();
    }

    @Override
    public PerspectiveCamera getCamera(String cameraName) {
        return perspectiveCameras.get(cameraName);
    }

    @Override
    public Entity getCameraEntity(String cameraName) {
        return cameraEntities.get(cameraName);
    }

    @Override
    public void screenResized(int width, int height) {
        for (PerspectiveCamera perspectiveCamera : perspectiveCameras.values()) {
            perspectiveCamera.viewportWidth = width;
            perspectiveCamera.viewportHeight = height;
        }
    }

    public void moveBy(String cameraName, float x, float z) {
        Entity cameraEntity = getCameraEntity(cameraName);

        TopDownCameraComponent topDownCamera = topDownCameraComponentMapper.get(cameraEntity);

        Rectangle bounds = topDownCamera.getBounds();
        Vector3 cameraCenter = topDownCamera.getCenter();

        float resultX = Math.min(bounds.x + bounds.width, Math.max(cameraCenter.x + x, bounds.x));
        float resultY = Math.min(bounds.y + bounds.height, Math.max(cameraCenter.z + z, bounds.y));

        cameraCenter.set(resultX, cameraCenter.y, resultY);
    }

    @Override
    public void zoom(String cameraName, float distance) {
        Entity cameraEntity = getCameraEntity(cameraName);

        TopDownCameraComponent topDownCamera = topDownCameraComponentMapper.get(cameraEntity);

        Vector2 distanceRange = topDownCamera.getDistanceRange();
        float cameraDistance = topDownCamera.getDistance();

        float resultDistance = Math.min(distanceRange.y, Math.max(cameraDistance + distance, distanceRange.x));

        topDownCamera.setDistance(resultDistance);
    }

    @Override
    public void moveZAxisAngle(String cameraName, float angle) {
        Entity cameraEntity = getCameraEntity(cameraName);

        TopDownCameraComponent topDownCamera = topDownCameraComponentMapper.get(cameraEntity);

        Vector2 zAxisAngleRange = topDownCamera.getzAxisAngleRange();
        float zAxisAngle = topDownCamera.getzAxisAngle();

        float resultAngle = Math.min(zAxisAngleRange.y, Math.max(zAxisAngle + angle, zAxisAngleRange.x));

        topDownCamera.setzAxisAngle(resultAngle);
    }
}
