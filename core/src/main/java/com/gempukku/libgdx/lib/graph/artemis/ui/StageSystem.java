package com.gempukku.libgdx.lib.graph.artemis.ui;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gempukku.libgdx.graph.plugin.ui.UIPluginPublicData;
import com.gempukku.libgdx.lib.artemis.camera.ScreenResized;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.input.InputProcessorProvider;
import com.gempukku.libgdx.lib.graph.artemis.renderer.PipelineRendererSystem;

public class StageSystem extends EntitySystem implements InputProcessorProvider, Disposable {
    private PipelineRendererSystem pipelineRendererSystem;

    private Stage stage;
    private Skin skin;
    private int processorPriority;

    public StageSystem(int processorPriority) {
        super(Aspect.all(StageComponent.class));

        this.processorPriority = processorPriority;

        stage = new Stage(new ScreenViewport());
    }

    @Override
    protected void initialize() {
    }

    @Override
    public void inserted(Entity e) {
        StageComponent stageComponent = e.getComponent(StageComponent.class);

        UIPluginPublicData uiData = pipelineRendererSystem.getPluginData(UIPluginPublicData.class);
        uiData.setStage(stageComponent.getStageName(), stage);

        skin = new Skin(Gdx.files.classpath(stageComponent.getSkinPath()));
    }

    public Skin getSkin() {
        return skin;
    }

    public Stage getStage() {
        return stage;
    }

    @EventListener
    public void screenResized(ScreenResized screenResized, Entity entity) {
        stage.getViewport().update(screenResized.getWidth(), screenResized.getHeight(), true);
    }

    @Override
    public int getInputPriority() {
        return processorPriority;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    @Override
    protected void processSystem() {
        stage.act(world.getDelta());
    }

    @Override
    public void dispose() {
        if (skin != null)
            skin.dispose();
        stage.dispose();
    }
}
