package com.gempukku.libgdx.lib.artemis.evaluate;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;

public class EvaluateProperty implements EntityEvent {
    private EvaluableProperty propertyValue;
    private Object result;

    public EvaluableProperty getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(EvaluableProperty propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
