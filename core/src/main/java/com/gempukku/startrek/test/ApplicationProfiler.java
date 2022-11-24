package com.gempukku.startrek.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gempukku.libgdx.graph.util.SimpleNumberFormatter;

public class ApplicationProfiler {
    private final FPSLogger fpsLogger = new FPSLogger();
    private GLProfiler profiler;
    private Skin profileSkin;
    private Stage profileStage;
    private Label profileLabel;

    private long start;
    private String skinPath;

    public ApplicationProfiler(String skinPath) {
        this.skinPath = skinPath;
    }

    public void enableProfiler() {
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();

        profileSkin = new Skin(Gdx.files.internal(skinPath));
        profileStage = new Stage(new ScreenViewport());
        profileLabel = new Label("", profileSkin);

        Table tbl = new Table(profileSkin);

        tbl.setFillParent(true);
        tbl.align(Align.topRight);

        tbl.add(profileLabel).pad(10f);
        tbl.row();

        profileStage.addActor(tbl);
    }

    public void disableProfiler() {
        profileSkin.dispose();
        profileStage.dispose();
        profiler.disable();

        profileSkin = null;
        profileStage = null;
        profileLabel = null;
    }

    public void resized(int width, int height) {
        if (profileStage != null) {
            profileStage.getViewport().update(width, height, true);
        }
    }

    public void startFrame() {
        fpsLogger.log();

        profiler.reset();
        start = System.nanoTime();
    }

    public void endFrame() {
        float ms = (System.nanoTime() - start) / 1000000f;

        StringBuilder sb = new StringBuilder();
        sb.append("Time: " + SimpleNumberFormatter.format(ms) + "ms\n");
        sb.append("GL Calls: " + profiler.getCalls() + "\n");
        sb.append("Draw calls: " + profiler.getDrawCalls() + "\n");
        sb.append("Shader switches: " + profiler.getShaderSwitches() + "\n");
        sb.append("Texture bindings: " + profiler.getTextureBindings() + "\n");
        sb.append("Vertex calls: " + profiler.getVertexCount().total + "\n");
        long memory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        sb.append("Used memory: " + memory + "MB");
        profileLabel.setText(sb.toString());

        profileStage.draw();
    }
}
