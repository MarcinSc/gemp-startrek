package com.gempukku.startrek.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gempukku.startrek.GameSceneProvider;

public class DualGameApplication extends ApplicationAdapter {
    private GameSceneProvider game1;
    private GameSceneProvider game2;

    private GameSceneProvider currentGame;

    private boolean operateOnFirstContext = true;

    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private ApplicationProfiler profiler = new ApplicationProfiler("skin/startrek/startrek.json");
    private boolean profile;

    public DualGameApplication(GameSceneProvider game1, GameSceneProvider game2) {
        this.game1 = game1;
        this.game2 = game2;

        currentGame = game1;
    }

    private String getScreenName() {
        if (operateOnFirstContext)
            return "Screen 1";
        else
            return "Screen 2";
    }

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();

        game1.startup();
        game2.startup();

        currentGame.setActive();
    }

    @Override
    public void resize(int width, int height) {
        profiler.resized(width, height);
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);

        currentGame.resize(width, height);
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            if (profile)
                disableProfiler();
            else
                enableProfiler();
        }

        if (profile)
            profiler.startFrame();

        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) {
            currentGame.setInactive();
            operateOnFirstContext = !operateOnFirstContext;
            System.out.println("Switching context: " + (operateOnFirstContext ? "1" : "2"));
            currentGame = operateOnFirstContext ? game1 : game2;

            currentGame.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            currentGame.setActive();
        }

        currentGame.processContext();

        spriteBatch.begin();
        font.draw(spriteBatch, getScreenName(), 10, 20);
        spriteBatch.end();

        if (profile)
            profiler.endFrame();
    }

    private void enableProfiler() {
        profiler.enableProfiler();
        profile = true;
    }

    private void disableProfiler() {
        profiler.disableProfiler();
        profile = false;
    }

    @Override
    public void dispose() {
        game1.shutdown();
        game2.shutdown();

        spriteBatch.dispose();
        font.dispose();
    }
}
