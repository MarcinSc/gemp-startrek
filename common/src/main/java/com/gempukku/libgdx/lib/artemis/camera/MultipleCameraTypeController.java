package com.gempukku.libgdx.lib.artemis.camera;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Camera;

public class MultipleCameraTypeController implements CameraController {
    private CameraController[] cameraControllers;

    public MultipleCameraTypeController(CameraController... cameraControllers) {
        this.cameraControllers = cameraControllers;
    }

    @Override
    public void setupWithWorld(World world) {
        for (CameraController cameraController : cameraControllers) {
            cameraController.setupWithWorld(world);
        }
    }

    @Override
    public void update(float deltaTime) {
        for (CameraController cameraController : cameraControllers) {
            cameraController.update(deltaTime);
        }
    }

    @Override
    public Camera getCamera(String cameraName) {
        for (CameraController cameraController : cameraControllers) {
            Camera result = cameraController.getCamera(cameraName);
            if (result != null)
                return result;
        }

        return null;
    }

    @Override
    public Entity getCameraEntity(String cameraName) {
        for (CameraController cameraController : cameraControllers) {
            Entity result = cameraController.getCameraEntity(cameraName);
            if (result != null)
                return result;
        }

        return null;
    }

    @Override
    public CameraController getCameraController(String cameraName) {
        for (CameraController cameraController : cameraControllers) {
            Entity result = cameraController.getCameraEntity(cameraName);
            if (result != null)
                return cameraController;
        }

        return null;
    }

    @Override
    public void screenResized(int width, int height) {
        for (CameraController cameraController : cameraControllers) {
            cameraController.screenResized(width, height);
        }
    }
}
