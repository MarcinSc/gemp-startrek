package com.gempukku.libgdx.lib.artemis.evaluate;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;

public class EvaluatePropertySystem extends BaseSystem {
    private static final EvaluateProperty evaluateProperty = new EvaluateProperty();

    private EventSystem eventSystem;

    @Override
    protected void processSystem() {

    }

    public <T> T evaluateProperty(Entity entity, Object property, Class<T> clazz) {
        if (property instanceof EvaluateProperty) {
            evaluateProperty.setResult(null);
            evaluateProperty.setPropertyValue((EvaluableProperty) property);
            eventSystem.fireEvent(evaluateProperty, entity);
            return (T) evaluateProperty.getResult();
        }
        return (T) property;
    }
}
