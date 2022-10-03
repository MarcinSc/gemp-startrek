package com.gempukku.startrek.server.game.stack;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class ExecutionStackComponent extends Component {
    private Array<Integer> entityIds = new Array<>();

    public Array<Integer> getEntityIds() {
        return entityIds;
    }
}
