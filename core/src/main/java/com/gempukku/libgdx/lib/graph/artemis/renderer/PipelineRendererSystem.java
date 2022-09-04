package com.gempukku.libgdx.lib.graph.artemis.renderer;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.gempukku.libgdx.graph.pipeline.PipelineLoader;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.pipeline.RenderOutputs;
import com.gempukku.libgdx.graph.time.TimeProvider;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.graph.artemis.time.TimeKeepingSystem;

// This should be on the CameraSystem only, but there is a bug in Artemis
@Wire(failOnNull = false)
public class PipelineRendererSystem extends BaseEntitySystem {
    private final SimpleTimeProvider simpleTimeProvider = new SimpleTimeProvider();
    private PipelineRenderer pipelineRenderer;

    private TimeKeepingSystem timeKeepingSystem;
    @Wire(failOnNull = false)
    private CameraSystem cameraSystem;

    private Entity renderingEntity;

    public PipelineRendererSystem() {
        super(Aspect.all(PipelineRendererComponent.class));
    }

    @Override
    protected void inserted(int entityId) {
        renderingEntity = world.getEntity(entityId);

        PipelineRendererComponent pipelineRendererComponent = renderingEntity.getComponent(PipelineRendererComponent.class);
        pipelineRenderer = PipelineLoader.loadPipelineRenderer(Gdx.files.internal(pipelineRendererComponent.getPipelinePath()), simpleTimeProvider);
        String cameraProperty = pipelineRendererComponent.getCameraProperty();
        if (cameraProperty != null) {
            pipelineRenderer.setPipelineProperty(cameraProperty, cameraSystem.getCamera());
        }
    }

    public void setPipelineProperty(String name, Object value) {
        pipelineRenderer.setPipelineProperty(name, value);
    }

    @Override
    protected void processSystem() {
        TimeProvider timeProvider = timeKeepingSystem.getTimeProvider(renderingEntity);
        simpleTimeProvider.setDelta(timeProvider.getDelta());
        simpleTimeProvider.setTime(timeProvider.getTime());
        pipelineRenderer.render(RenderOutputs.drawToScreen);
    }

    public <T> T getPluginData(Class<T> clazz) {
        return pipelineRenderer.getPluginData(clazz);
    }

    @Override
    public void dispose() {
        if (pipelineRenderer != null)
            pipelineRenderer.dispose();
    }
}
