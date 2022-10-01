package com.gempukku.libgdx.network.server.config.annotation;

import com.badlogic.gdx.utils.Array;

public interface OwnedByMultipleComponent {
    Array<String> getOwners();

    void setOwners(Array<String> owners);
}
