package com.gempukku.startrek.game.render;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.TextComponent;

public class PromptRenderingSystem extends BaseSystem {
    private CameraSystem cameraSystem;
    private SpawnSystem spawnSystem;
    private TransformSystem transformSystem;

    private Entity promptEntity;

    public void setPrompt(String prompt) {
        if (promptEntity == null) {
            promptEntity = spawnSystem.spawnEntity("game/prompt.template");

            TextComponent text = promptEntity.getComponent(TextComponent.class);
            text.getTextBlocks().get(0).setText(prompt);

            Camera camera = cameraSystem.getCamera("main");

            transformSystem.setTransform(promptEntity,
                    new Matrix4()
                            .translate(new Vector3(camera.position))
                            .translate(new Vector3(camera.direction).scl(3f))
                            .rotate(1, 0, 0, 10f));
        }
    }

    public void removePrompt() {
        if (promptEntity != null) {
            world.deleteEntity(promptEntity);
            promptEntity = null;
        }
    }

    @Override
    protected void processSystem() {

    }
}
