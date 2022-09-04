package com.gempukku.startrek;

public interface GameSceneProvider {
    void startup();

    void setActive();

    void setInactive();

    void resize(int width, int height);

    void processContext();

    void shutdown();
}
