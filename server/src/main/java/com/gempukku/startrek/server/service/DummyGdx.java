package com.gempukku.startrek.server.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.gempukku.libgdx.DummyApplication;
import org.springframework.stereotype.Service;

@Service
public class DummyGdx {
    public DummyGdx() {
        Gdx.files = new HeadlessFiles();

        Gdx.app = new DummyApplication();
    }
}
