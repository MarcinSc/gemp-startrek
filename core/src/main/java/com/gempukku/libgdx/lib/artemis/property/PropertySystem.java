package com.gempukku.libgdx.lib.artemis.property;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluableProperty;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluateProperty;
import com.gempukku.libgdx.lib.artemis.event.EventListener;

public class PropertySystem extends BaseSystem {
    private ObjectMap<String, String> properties;

    public PropertySystem(ObjectMap<String, String> properties) {
        this.properties = properties;
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    @EventListener
    public void evaluateProperty(EvaluateProperty evaluateProperty, Entity entity) {
        EvaluableProperty propertyValue = evaluateProperty.getPropertyValue();
        if (propertyValue instanceof PropertyEvaluable) {
            evaluateProperty.setResult(properties.get(((PropertyEvaluable) propertyValue).getName()));
        }
    }

    @Override
    protected void processSystem() {

    }
}
