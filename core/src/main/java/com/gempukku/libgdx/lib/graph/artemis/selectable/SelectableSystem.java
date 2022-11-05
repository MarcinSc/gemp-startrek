package com.gempukku.libgdx.lib.graph.artemis.selectable;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;

public class SelectableSystem extends BaseEntitySystem {
    public SelectableSystem() {
        super(Aspect.all(SelectableComponent.class));
    }

    @Override
    protected void inserted(int entityId) {

    }

    @Override
    protected void processSystem() {

    }
}
