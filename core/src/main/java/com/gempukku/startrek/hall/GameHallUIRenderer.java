package com.gempukku.startrek.hall;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gempukku.libgdx.lib.artemis.camera.ScreenResized;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.startrek.common.ConnectionParamSystem;
import com.gempukku.startrek.common.FontProviderSystem;
import com.gempukku.startrek.common.ServerStateChanged;
import com.gempukku.startrek.hall.screen.HallScreen;
import com.gempukku.startrek.hall.screen.HomeScreen;
import com.gempukku.startrek.hall.screen.ProfileScreen;

public class GameHallUIRenderer extends BaseSystem {
    private boolean initialized = false;

    private StageSystem stageSystem;
    private EventSystem eventSystem;
    private TextureSystem textureSystem;
    private ConnectionParamSystem connectionParamSystem;
    private FontProviderSystem fontProviderSystem;
    private GameHallPlayerProviderSystem gameHallPlayerProviderSystem;
    private DeckBoxRenderingSystem deckBoxRenderingSystem;

    private Texture pixelTexture;

    private Table fullScreenTable;
    private Table topBar;
    private Table restOfScreen;

    private HomeScreen homeScreen;
    private ProfileScreen profileScreen;

    private Table currentTable;
    private HallScreen currentScreen;

    @Override
    public void initialize() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        pixelTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @EventListener
    public void serverStateChanged(ServerStateChanged serverStateChanged, Entity entity) {
        if (!initialized) {
            Entity playerEntity = gameHallPlayerProviderSystem.getPlayerEntity();
            if (playerEntity != null) {
                initialize(playerEntity);
                initialized = true;
            }
        }

        if (initialized) {
//            GameHallComponent gameHall = entityManager.getEntitiesWithComponents(GameHallComponent.class).iterator().next().getComponent(GameHallComponent.class);
//            userCountLabel.setText("Users in hall: " + gameHall.getUserCount());
//            gameCountLabel.setText("Games played: " + gameHall.getGameCount());
        }
    }

    @Override
    protected void dispose() {
        pixelTexture.dispose();
        if (initialized) {
            homeScreen.dispose();
        }
    }

    @EventListener
    public void screenResized(ScreenResized screenResized, Entity entity) {
        int screenWidth = screenResized.getWidth();
        int screenHeight = screenResized.getHeight();
        resizeUI(screenWidth, screenHeight);
    }

    private void resizeUI(int screenWidth, int screenHeight) {
        if (fullScreenTable != null) {
            int height = screenHeight;
            int width = 4 * height / 3;
            if (width > screenWidth) {
                width = screenWidth;
                height = 3 * width / 4;
            }
            fullScreenTable.setSize(width, height);
            fullScreenTable.setPosition((screenWidth - width) / 2, (screenHeight - height) / 2);
        }
    }

    private void initialize(Entity gameHallPlayerEntity) {
        Stage stage = stageSystem.getStage();
        Skin skin = stageSystem.getSkin();

        homeScreen = new HomeScreen(gameHallPlayerEntity, skin, pixelTexture, eventSystem, fontProviderSystem, textureSystem,
                stageSystem, deckBoxRenderingSystem, connectionParamSystem, world);
        profileScreen = new ProfileScreen();

        currentScreen = homeScreen;
        currentTable = homeScreen;
        currentScreen.awaken();

        fullScreenTable = new Table(skin);

        topBar = new Table(skin);
        final TextButton homeButton = new TextButton("Home", skin, "toggle");
        homeButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (currentScreen != homeScreen) {
                            switchToScreen(homeScreen, homeScreen);
                        }
                    }
                });
        homeButton.setChecked(true);

        TextButton decksButton = new TextButton("Decks", skin, "toggle");
        decksButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
//                        if (currentScreen != deckBuilderScreen) {
//                            switchToScreen(deckBuilderScreen, deckBuilderScreen);
//                        }
                    }
                });
        decksButton.setChecked(false);

        TextButton profileButton = new TextButton("Profile", skin, "toggle");
        profileButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (currentScreen != profileScreen) {
                            switchToScreen(profileScreen, profileScreen);
                        }
                    }
                });
        profileButton.setChecked(false);

        topBar.add(homeButton).size(100, 40);
        topBar.add();
        topBar.add(decksButton).size(100, 40);
        topBar.add();
        topBar.add(profileButton).size(100, 40);

        ButtonGroup buttonGroup = new ButtonGroup(homeButton, decksButton, profileButton);
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setMaxCheckCount(1);

        TextureRegion texture = textureSystem.getTextureRegion("images/hall/gear.png", "gear.png");
        ImageButton settingsButton = new ImageButton(skin, "default") {
            @Override
            public float getPrefWidth() {
                return 30;
            }

            @Override
            public float getPrefHeight() {
                return 30;
            }
        };
        settingsButton.getStyle().imageUp = new TextureRegionDrawable(texture);

        topBar.add().expandX();
        topBar.add(settingsButton).row();

        fullScreenTable.add(topBar).growX().pad(5);
        fullScreenTable.row();

        restOfScreen = new Table(skin);

        restOfScreen.add(currentTable).grow();
        restOfScreen.row();

        fullScreenTable.add(restOfScreen).grow();
        fullScreenTable.row();

        stage.addActor(fullScreenTable);

        resizeUI(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void switchToScreen(HallScreen screen, final Table table) {
        currentScreen.suspend();
        screen.awaken();

        currentTable.addAction(
                Actions.sequence(
                        Actions.fadeOut(0.3f),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                restOfScreen.clearChildren();
                                restOfScreen.add(table).grow().row();
                                table.setColor(1, 1, 1, 0);
                                table.addAction(
                                        Actions.fadeIn(0.3f));
                                return true;
                            }
                        }
                )
        );

        currentScreen = screen;
        currentTable = table;
    }

    @Override
    protected void processSystem() {

    }
}
