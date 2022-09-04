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
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);

        currentGame.resize(width, height);
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) {
            System.out.println("Switching context");
            currentGame.setInactive();
            operateOnFirstContext = !operateOnFirstContext;
            currentGame = operateOnFirstContext ? game1 : game2;

            currentGame.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            currentGame.setActive();
        }

        currentGame.processContext();

        spriteBatch.begin();
        font.draw(spriteBatch, getScreenName(), 10, 20);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        game1.shutdown();
        game2.shutdown();

        spriteBatch.dispose();
        font.dispose();
    }
}
