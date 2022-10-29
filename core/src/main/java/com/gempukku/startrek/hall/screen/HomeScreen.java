package com.gempukku.startrek.hall.screen;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.startrek.common.ConnectionParamSystem;
import com.gempukku.startrek.common.FontProviderSystem;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.hall.DeckBoxRenderingSystem;
import com.gempukku.startrek.hall.GameHallPlayerComponent;
import com.gempukku.startrek.hall.StarterDeckComponent;
import com.gempukku.startrek.hall.event.SearchForGame;
import com.gempukku.startrek.hall.event.StopSearchingForGame;
import com.gempukku.startrek.hall.ui.ContentWidget;
import com.gempukku.startrek.hall.ui.DeckBoxWidget;
import com.gempukku.startrek.hall.ui.ImageWithOverlayDrawable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.*;

public class HomeScreen extends Table implements HallScreen {
    private final EntitySubscription starterDeckSubscription;
    private Table rightTable;
    private Label userCountLabel;
    private Label gameCountLabel;
    private ContentWidget contentWidget;
    private Map<String, Texture> slideTextures = Collections.synchronizedMap(new HashMap<String, Texture>());
    private Texture pixelTexture;

    private TextureSystem textureAtlasProvider;
    private Entity gameHallPlayerEntity;
    private EventSystem eventSystem;
    private DeckBoxRenderingSystem deckBoxRenderingSystem;
    private FontProviderSystem fontProvider;
    private StageSystem stageProvider;
    private ConnectionParamSystem connectionParamSystem;
    private World world;

    private enum DisplayedUI {
        Join_Queue
    }

    private DisplayedUI displayedUI;

    private JoinQueueWidget joinQueueWidget;

    public HomeScreen(Entity gameHallPlayerEntity, Skin skin, Texture pixelTexture, EventSystem eventSystem,
                      FontProviderSystem fontProvider, TextureSystem textureSystem, StageSystem stageProvider,
                      DeckBoxRenderingSystem deckBoxRenderingSystem,
                      ConnectionParamSystem connectionParamSystem, World world) {
        super(skin);
        this.gameHallPlayerEntity = gameHallPlayerEntity;
        this.eventSystem = eventSystem;
        this.deckBoxRenderingSystem = deckBoxRenderingSystem;
        this.world = world;

        starterDeckSubscription = world.getAspectSubscriptionManager().get(Aspect.all(StarterDeckComponent.class));

        this.pixelTexture = pixelTexture;
        this.fontProvider = fontProvider;
        this.textureAtlasProvider = textureSystem;
        this.stageProvider = stageProvider;
        this.connectionParamSystem = connectionParamSystem;

        Table leftContent = new Table();
        leftContent.setLayoutEnabled(false);

        contentWidget = new ContentWidget(pixelTexture);
        loadContent();
        leftContent.add(contentWidget).grow().row();

        GameHallPlayerComponent gameHallPlayer = gameHallPlayerEntity.getComponent(GameHallPlayerComponent.class);

        Table middleContent = new Table(skin);
        Label.LabelStyle usernameStyle = new Label.LabelStyle();
        usernameStyle.font = fontProvider.getFont("font/LABTSECB.ttf", 40);
        Label usernameLabel = new Label(gameHallPlayer.getOwner(), usernameStyle);
        usernameLabel.setEllipsis(true);
        usernameLabel.setAlignment(Align.right);
        middleContent.add(usernameLabel).width(200).row();
        middleContent.add().height(30).row();

//        userCountLabel = new Label("Users in hall: 0", skin);
//        middleContent.add(userCountLabel).fillX().row();
//        gameCountLabel = new Label("Games played: 0", skin);
//        middleContent.add(gameCountLabel).fillX().row();
//        middleContent.add().height(10).row();

        createGameStartButtons(middleContent, skin);

        middleContent.add().expand().fillY().row();

        Table rightContent = new Table(skin);
        rightContent.add(new Image(textureSystem.getTextureRegion("images/portrait/" + gameHallPlayer.getAvatar() + ".png", gameHallPlayer.getAvatar()))).row();
        rightTable = new Table(skin);
        rightTable.setColor(1, 1, 1, 0);
        rightContent.add(rightTable).expand().fillY().row();

        add(leftContent).grow().pad(0, 5, 5, 5);
        add(middleContent).width(200).growY().pad(0, 0, 5, 5);
        add(rightContent).growY().pad(0, 0, 5, 5);

        row();

        Table bottomRow = new Table();

        add(bottomRow).colspan(3).height(150);
        row();
    }

    private void createGameStartButtons(final Table table, final Skin skin) {
        TextureRegion challengeIcon = textureAtlasProvider.getTextureRegion("images/hall/challenge.png", "challenge");
        ImageButton.ImageButtonStyle style1 = new ImageButton.ImageButtonStyle(skin.get("default", ImageButton.ImageButtonStyle.class));
        style1.imageUp = new ImageWithOverlayDrawable(fontProvider, pixelTexture, challengeIcon, "Challenge Player");
        ImageButton button1 = new ImageButton(style1);
        table.add(button1).growX().row();

        TextureRegion enqueueIcon = textureAtlasProvider.getTextureRegion("images/hall/enqueue.png", "enqueue");
        ImageButton.ImageButtonStyle style2 = new ImageButton.ImageButtonStyle(skin.get("default", ImageButton.ImageButtonStyle.class));
        style2.imageUp = new ImageWithOverlayDrawable(fontProvider, pixelTexture, enqueueIcon, "Join Queue");
        ImageButton button2 = new ImageButton(style2);
        table.add(button2).growX().row();

        joinQueueWidget = new JoinQueueWidget(skin);

        button2.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (displayedUI != DisplayedUI.Join_Queue) {
                            displayedUI = DisplayedUI.Join_Queue;
                            rightTable.addAction(
                                    Actions.sequence(
                                            Actions.fadeOut(0.3f),
                                            new Action() {
                                                @Override
                                                public boolean act(float delta) {
                                                    rightTable.clearChildren();
                                                    rightTable.add(joinQueueWidget);
                                                    rightTable.row();

                                                    return true;
                                                }
                                            },
                                            Actions.fadeIn(0.3f)));
                        }
                    }
                });
    }

    private void loadContent() {
        Net.HttpRequest httpRequest = new Net.HttpRequest("GET");
        httpRequest.setUrl(connectionParamSystem.getHallContentUrl());
        Gdx.net.sendHttpRequest(httpRequest,
                new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        if (httpResponse.getStatus().getStatusCode() == HttpStatus.SC_OK) {
                            try {
                                JSONParser jsonParser = new JSONParser();
                                InputStream stream = httpResponse.getResultAsStream();
                                try {
                                    JSONObject slidesObject = (JSONObject) jsonParser.parse(new InputStreamReader(stream));
                                    List<JSONObject> slides = (List<JSONObject>) slidesObject.get("slides");

                                    List<String> imagesToDownload = new LinkedList<String>();
                                    for (JSONObject slide : slides) {
                                        String backgroundUrl = (String) slide.get("backgroundUrl");
                                        imagesToDownload.add(backgroundUrl);
                                    }

                                    downloadImages(imagesToDownload, slides);
                                } finally {
                                    stream.close();
                                }
                            } catch (ParseException exp) {
                            } catch (IOException exp) {
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable t) {

                    }

                    @Override
                    public void cancelled() {

                    }
                });
    }

    private void downloadImages(final List<String> imagesToDownload, final List<JSONObject> slides) {
        if (!imagesToDownload.isEmpty()) {
            final String image = imagesToDownload.remove(0);
            Net.HttpRequest httpRequest = new Net.HttpRequest("GET");
            httpRequest.setUrl("http://" + connectionParamSystem.getServerHost() + ":" + connectionParamSystem.getServerPort() +
                    image);
            Gdx.net.sendHttpRequest(httpRequest,
                    new Net.HttpResponseListener() {
                        @Override
                        public void handleHttpResponse(Net.HttpResponse httpResponse) {
                            System.out.println("Image status: " + httpResponse.getStatus().getStatusCode());
                            if (httpResponse.getStatus().getStatusCode() == HttpStatus.SC_OK) {
                                byte[] data = httpResponse.getResult();
                                final Pixmap pixmap = new Pixmap(data, 0, data.length);
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        Texture texture = new Texture(pixmap);
                                        slideTextures.put(image, texture);

                                        downloadImages(imagesToDownload, slides);
                                    }
                                });
                            }
                        }

                        @Override
                        public void failed(Throwable t) {

                        }

                        @Override
                        public void cancelled() {

                        }
                    });
        } else {
            // Finished downloading images
            Gdx.app.postRunnable(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSONObject slide = slides.get(0);
                            String backgroundUrl = (String) slide.get("backgroundUrl");
                            Texture texture = slideTextures.get(backgroundUrl);
                            Image image = new Image(texture);
                            JSONArray texts = (JSONArray) slide.get("text");
                            Label[] labels = new Label[texts.size()];
                            for (int i = 0; i < labels.length; i++) {
                                JSONObject textInfo = (JSONObject) texts.get(i);
                                String text = (String) textInfo.get("text");
                                String font = (String) textInfo.get("font");
                                int size = ((Number) textInfo.get("size")).intValue();
                                Label.LabelStyle labelStyle = new Label.LabelStyle();
                                labelStyle.font = fontProvider.getFont(font, size);
                                labels[i] = new Label(text, labelStyle);
                            }
                            contentWidget.setDrawable(image, labels);
                        }
                    }
            );
        }
    }

    @Override
    public void suspend() {
        joinQueueWidget.suspend();
    }

    @Override
    public void awaken() {

    }

    @Override
    public void dispose() {
        for (Texture value : slideTextures.values()) {
            value.dispose();
        }
    }

    private class JoinQueueWidget extends Table {
        private boolean searchingForGame;
        private final TextButton startGame;
        private Skin skin;

        public JoinQueueWidget(final Skin skin) {
            this.skin = skin;
            final DeckBoxWidget deckChooser = new DeckBoxWidget(deckBoxRenderingSystem, skin);
            deckChooser.setDefaultNameLabel("Choose deck");

            startGame = new TextButton("Join", skin, UISettings.mainButtonStyle);
            startGame.setTouchable(Touchable.disabled);
            startGame.setDisabled(true);

            deckChooser.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (searchingForGame)
                                cancelGame();

                            final Window window = new Window("Choose deck", skin);
                            window.setSize(700, 500);
                            window.setModal(true);

                            HorizontalGroup horizontalGroup = new HorizontalGroup();
                            horizontalGroup.wrap(true);
                            horizontalGroup.rowAlign(Align.left);

                            IntBag starterDeckEntities = starterDeckSubscription.getEntities();
                            for (int i = 0; i < starterDeckEntities.size(); i++) {
                                Entity starterDeckEntity = world.getEntity(starterDeckEntities.get(i));
                                StarterDeckComponent starterDeck = starterDeckEntity.getComponent(StarterDeckComponent.class);
                                final String deckId = starterDeck.getDeckId();
                                DeckBoxWidget deckDisplay = new DeckBoxWidget(deckBoxRenderingSystem, skin);
                                deckDisplay.addListener(
                                        new ClickListener() {
                                            @Override
                                            public void clicked(InputEvent event, float x, float y) {
                                                deckChooser.setDeckId(deckId);
                                                startGame.setTouchable(Touchable.enabled);
                                                startGame.setDisabled(false);

                                                window.remove();
                                            }
                                        });
                                deckDisplay.setDeckId(deckId);
                                horizontalGroup.addActor(deckDisplay);
                            }

                            ScrollPane scrollPane = new ScrollPane(horizontalGroup, skin);
                            scrollPane.setFadeScrollBars(false);

                            window.add(scrollPane).grow().row();

                            Stage stage = stageProvider.getStage();
                            stage.addActor(window);
                            window.setPosition(
                                    (stage.getWidth() - window.getWidth()) / 2,
                                    (stage.getHeight() - window.getHeight()) / 2);
                        }
                    });

            startGame.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (searchingForGame) {
                                cancelGame();
                            } else {
                                startGame(deckChooser.getDeckId());
                            }
                        }
                    });
            add(deckChooser).width(150).maxWidth(150).row();
            add(startGame).fillX().row();
        }

        private void startGame(String deckId) {
            searchingForGame = true;
            eventSystem.fireEvent(new SearchForGame(deckId), gameHallPlayerEntity);

            startGame.setText("Cancel");
            startGame.setStyle(skin.get(UISettings.alternativeButtonStyle, TextButton.TextButtonStyle.class));
        }

        private void cancelGame() {
            searchingForGame = false;
            eventSystem.fireEvent(new StopSearchingForGame(), gameHallPlayerEntity);

            startGame.setText("Join");
            startGame.setStyle(skin.get(UISettings.mainButtonStyle, TextButton.TextButtonStyle.class));
        }

        public void suspend() {
            if (searchingForGame) {
                cancelGame();
            }
        }
    }
}
