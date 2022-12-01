package com.gempukku.libgdx.lib.artemis.camera.orthographic;

import com.artemis.Component;

public class OrthographicCameraComponent extends Component {
    private String name;
    private boolean yDown;
    private float near;
    private float far;

    private float height;

    public boolean isYDown() {
        return yDown;
    }

    public void setYDown(boolean yDown) {
        this.yDown = yDown;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
