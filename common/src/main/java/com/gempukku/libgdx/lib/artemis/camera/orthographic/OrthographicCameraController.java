package com.gempukku.libgdx.lib.artemis.camera.orthographic;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.camera.CameraController;

public class OrthographicCameraController implements CameraController {
    private ComponentMapper<OrthographicCameraComponent> orthographicCameraComponentMapper;
    private EntitySubscription cameraEntitySubscription;
    private World world;

    private Array<Entity> newCameraEntities = new Array<>();

    private ObjectMap<String, OrthographicCamera> orthographicCameras = new ObjectMap<>();
    private ObjectMap<String, Entity> cameraEntities = new ObjectMap<>();

    @Override
    public void setupWithWorld(final World world) {
        orthographicCameraComponentMapper = world.getMapper(OrthographicCameraComponent.class);
        this.world = world;
        cameraEntitySubscription = world.getAspectSubscriptionManager().get(Aspect.all(OrthographicCameraComponent.class));
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
        OrthographicCameraComponent orthographicCamera = orthographicCameraComponentMapper.get(cameraEntity);
        cameraEntities.remove(orthographicCamera.getName());
        orthographicCameras.remove(orthographicCamera.getName());
    }

    @Override
    public void update(float deltaTime) {
        processNewCameras();

        for (ObjectMap.Entry<String, Entity> cameraEntityEntry : cameraEntities) {
            String cameraName = cameraEntityEntry.key;
            Entity cameraEntity = cameraEntityEntry.value;

            OrthographicCameraComponent orthographicCamera = orthographicCameraComponentMapper.get(cameraEntity);

            OrthographicCamera camera = orthographicCameras.get(cameraName);

            camera.near = orthographicCamera.getNear();
            camera.far = orthographicCamera.getFar();

            float aspectRatio = 1f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight();

            float viewportWidth = aspectRatio * orthographicCamera.getHeight();
            float viewportHeight = orthographicCamera.getHeight();

            if (orthographicCamera.isYDown()) {
                camera.up.set(0, -1, 0);
                camera.direction.set(0, 0, 1);
            } else {
                camera.up.set(0, 1, 0);
                camera.direction.set(0, 0, -1);
            }
            camera.position.set(0, 0, 0);

            camera.viewportWidth = viewportWidth;
            camera.viewportHeight = viewportHeight;
            camera.update(true);
        }
    }

    private void processNewCameras() {
        for (Entity newCameraEntity : newCameraEntities) {
            OrthographicCameraComponent orthographicCamera = orthographicCameraComponentMapper.get(newCameraEntity);
            cameraEntities.put(orthographicCamera.getName(), newCameraEntity);

            OrthographicCamera camera = new OrthographicCamera();

            orthographicCameras.put(orthographicCamera.getName(), camera);
        }
        newCameraEntities.clear();
    }

    @Override
    public OrthographicCamera getCamera(String cameraName) {
        return orthographicCameras.get(cameraName);
    }

    @Override
    public Entity getCameraEntity(String cameraName) {
        return cameraEntities.get(cameraName);
    }

    @Override
    public CameraController getCameraController(String cameraName) {
        return this;
    }

    @Override
    public void screenResized(int width, int height) {
    }
}
