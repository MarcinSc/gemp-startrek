package com.gempukku.libgdx.lib.graph.artemis.renderer;

import com.artemis.Component;

public class PipelineRendererComponent extends Component {
    private String pipelinePath;
    private String cameraProperty;
    private String cameraName;

    public String getPipelinePath() {
        return pipelinePath;
    }

    public String getCameraProperty() {
        return cameraProperty;
    }

    public String getCameraName() {
        return cameraName;
    }
}
