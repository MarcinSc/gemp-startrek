package com.gempukku.startrek.login;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.gempukku.libgdx.lib.artemis.camera.ScreenResized;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.property.PropertySystem;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.startrek.common.*;
import com.gempukku.startrek.hall.HallGameScene;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LoginScreenRenderer extends BaseSystem {
    private boolean uiDebug = false;
    private String storagePath = "gempukku-overpower/download";

    private StageSystem uiSystem;
    private TextureSystem textureSystem;
    private PropertySystem propertySystem;
    private ConnectionParamSystem connectionParamSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private GameSceneSystem gameSceneSystem;

    private Table mainTable;
    private Table loginTable;
    private Table registerTable;

    private Label loginErrorLabel;
    private Label registerErrorLabel;
    private TextButton loginButton;

    private Label messageLabel;
    private TextButton registerButton;
    private Table containerTable;

    private boolean initialized;
    private TextButton goToRegisterButton;
    private TextButton goToLoginButton;

    private void navigateToLoginScreen() {
        registerTable.addAction(
                Actions.sequence(
                        Actions.fadeOut(0.1f),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                containerTable.clearChildren();
                                loginTable.setColor(1, 1, 1, 0);
                                containerTable.add(loginTable).row();
                                loginTable.addAction(Actions.fadeIn(0.1f));
                                return true;
                            }
                        }
                ));

    }

    private void navigateToRegisterScreen() {
        loginTable.addAction(
                Actions.sequence(
                        Actions.fadeOut(0.1f),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                containerTable.clearChildren();
                                registerTable.setColor(1, 1, 1, 0);
                                containerTable.add(registerTable).row();
                                registerTable.addAction(Actions.fadeIn(0.1f));
                                return true;
                            }
                        }
                ));
    }

    private void createLoginTable(Skin skin, int logoWidth) {
        loginTable = new Table();
        loginTable.setDebug(uiDebug);

        loginErrorLabel = new Label(null, skin, UISettings.errorLabelStyle);
        loginErrorLabel.setAlignment(Align.center);
        final TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");
        final TextField passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        messageLabel = new Label(null, skin);

        goToRegisterButton = new TextButton("Register >", skin, UISettings.alternativeButtonStyle);
        goToRegisterButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        navigateToRegisterScreen();
                    }
                });
        loginButton = new TextButton("Login", skin, UISettings.mainButtonStyle);
        loginButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        processLogin(usernameField.getText(), passwordField.getText());
                    }
                });

        loginTable.setWidth(Math.max(580, logoWidth));

        loginTable.add(loginErrorLabel).colspan(2).fillX().row();
        loginTable.add(usernameField).colspan(2).width(280).pad(5).fillX().row();
        loginTable.add(passwordField).colspan(2).width(280).pad(5).fillX().row();
        loginTable.add(loginButton).pad(10);
        loginTable.add(goToRegisterButton).pad(10).row();
        loginTable.add(messageLabel).colspan(2).fillX().row();
    }

    private void createRegisterTable(Skin skin, int logoWidth) {
        registerTable = new Table();
        registerTable.setDebug(uiDebug);
        registerErrorLabel = new Label(null, skin, UISettings.errorLabelStyle);
        registerErrorLabel.setAlignment(Align.center);
        final TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");
        final TextField emailField = new TextField("", skin);
        emailField.setMessageText("E-mail");
        final TextField passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        final TextField passwordRepeatedField = new TextField("", skin);
        passwordRepeatedField.setMessageText("Password repeated");
        passwordRepeatedField.setPasswordMode(true);
        passwordRepeatedField.setPasswordCharacter('*');

        goToLoginButton = new TextButton("< Login", skin, UISettings.alternativeButtonStyle);
        goToLoginButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        navigateToLoginScreen();
                    }
                });
        registerButton = new TextButton("Register", skin, UISettings.mainButtonStyle);
        registerButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String username = usernameField.getText().trim();
                        String email = emailField.getText().trim();
                        String password = passwordField.getText();
                        String password2 = passwordRepeatedField.getText();
                        try {
                            if (!password.equals(password2))
                                throw new UserValidation.UserValidationException("Passwords do not match");
                            UserValidation.validateUser(username, email, password);

                            processRegister(username, email, password);
                        } catch (UserValidation.UserValidationException exp) {
                            registerErrorLabel.setText(exp.getMessage());
                        }
                    }
                });

        registerTable.setWidth(Math.max(580, logoWidth));

        registerTable.add(registerErrorLabel).colspan(2).fillX().row();
        registerTable.add(usernameField).colspan(2).width(280).pad(5).fillX().row();
        registerTable.add(emailField).colspan(2).width(280).pad(5).fillX().row();
        registerTable.add(passwordField).colspan(2).width(280).pad(5).fillX().row();
        registerTable.add(passwordRepeatedField).colspan(2).width(280).pad(5).fillX().row();
        registerTable.add(goToLoginButton).pad(10);
        registerTable.add(registerButton).pad(10).row();
    }

    private void processLogin(final String username, String password) {
        loginButton.setTouchable(Touchable.disabled);
        loginButton.setDisabled(true);
        goToRegisterButton.setTouchable(Touchable.disabled);
        goToRegisterButton.setDisabled(true);

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("version", connectionParamSystem.getClientVersion());

        Net.HttpRequest httpRequest = new Net.HttpRequest("POST");
        httpRequest.setUrl(connectionParamSystem.getLoginUrl());
        httpRequest.setContent(HttpParametersUtils.convertHttpParameters(parameters));
        Gdx.net.sendHttpRequest(httpRequest,
                new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        int statusCode = httpResponse.getStatus().getStatusCode();
                        if (statusCode == HttpStatus.SC_OK) {
                            String authentication = httpResponse.getHeader("Authorization");
                            loginSuccessful(username, authentication);
                        } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                            try {
                                String result = httpResponse.getResultAsString();
                                JSONObject jsonObject = (JSONObject) (new JSONParser().parse(result));
                                setLoginError((String) jsonObject.get("message"));
                            } catch (ParseException e) {
                                // Ignore
                            }
                        } else {
                            setLoginError("Unable to login - error: " + statusCode);
                        }
                    }

                    @Override
                    public void failed(Throwable t) {
                        setLoginError("Unable to login - " + t.getMessage());
                    }

                    @Override
                    public void cancelled() {

                    }
                });
    }

    private void processRegister(final String username, String email, String password) {
        registerButton.setTouchable(Touchable.disabled);
        registerButton.setDisabled(true);
        goToLoginButton.setTouchable(Touchable.disabled);
        goToLoginButton.setDisabled(true);

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", username);
        parameters.put("email", email);
        parameters.put("password", password);

        Net.HttpRequest httpRequest = new Net.HttpRequest("POST");
        httpRequest.setUrl(connectionParamSystem.getRegisterUrl());
        httpRequest.setContent(HttpParametersUtils.convertHttpParameters(parameters));
        Gdx.net.sendHttpRequest(httpRequest,
                new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        int statusCode = httpResponse.getStatus().getStatusCode();
                        if (statusCode == HttpStatus.SC_CREATED) {
                            registerSuccessful();
                        } else {
                            if (statusCode == HttpStatus.SC_CONFLICT)
                                setRegisterError("Unable to register - this username or email already exists in the system");
                            else
                                setRegisterError("Unable to register: " + statusCode);
                        }
                    }

                    @Override
                    public void failed(Throwable t) {
                        setRegisterError("Unable to register - " + t.getMessage());
                    }

                    @Override
                    public void cancelled() {

                    }
                });
    }

    private void registerSuccessful() {
        Gdx.app.postRunnable(
                new Runnable() {
                    @Override
                    public void run() {
                        resetRegistrationButtons();
                        navigateToLoginScreen();
                        setLoginError("User successfully registered");
                    }
                }
        );
    }

    private void loginSuccessful(final String username, final String authentication) {
        if (propertySystem.getProperty("server.file.list.download").equals("false")) {
            processFilesAndGoToScene(Collections.emptyList(), username, authentication);
        } else {
            Net.HttpRequest httpRequest = new Net.HttpRequest("GET");
            httpRequest.setUrl(connectionParamSystem.getFileListUrl());
            Gdx.net.sendHttpRequest(httpRequest,
                    new Net.HttpResponseListener() {
                        @Override
                        public void handleHttpResponse(Net.HttpResponse httpResponse) {
                            int statusCode = httpResponse.getStatus().getStatusCode();
                            if (statusCode == HttpStatus.SC_OK) {
                                try {
                                    JSONParser jsonParser = new JSONParser();
                                    InputStream stream = httpResponse.getResultAsStream();
                                    try {
                                        JSONObject filesObject = (JSONObject) jsonParser.parse(new InputStreamReader(stream));
                                        List<JSONObject> files = (List<JSONObject>) filesObject.get("files");

                                        List<JSONObject> filesToDownload = new LinkedList<JSONObject>();

                                        for (JSONObject file : files) {
                                            String folder = (String) file.get("folderCheck");
                                            FileHandle externalFolder = Gdx.files.external(storagePath + "/" + folder);
                                            if (!externalFolder.exists()) {
                                                filesToDownload.add(file);
                                            }
                                        }

                                        processFilesAndGoToScene(filesToDownload, username, authentication);
                                    } finally {
                                        stream.close();
                                    }
                                } catch (ParseException exp) {
                                    setLoginError("Unable to parse list of files");
                                } catch (IOException exp) {
                                    setLoginError("Unable to parse list of files");
                                }
                            } else {
                                setLoginError("Unable to load file list: " + statusCode);
                            }
                        }

                        @Override
                        public void failed(Throwable t) {
                            setLoginError("Unable to load file list " + t.getMessage());
                        }

                        @Override
                        public void cancelled() {

                        }
                    });
        }
    }

    private void processFilesAndGoToScene(final List<JSONObject> filesToDownload, final String username, final String authentication) {
        if (filesToDownload.size() == 0) {
            Gdx.app.postRunnable(
                    new Runnable() {
                        @Override
                        public void run() {
                            authenticationHolderSystem.setUsername(username);
                            authenticationHolderSystem.setAuthenticationToken(authentication);
                            gameSceneSystem.setNextGameScene(new HallGameScene());
                        }
                    });
        } else {
            setMessage("Downloading images...");
            final JSONObject fileToDownload = filesToDownload.remove(0);
            String url = (String) fileToDownload.get("path");
//            final Net.HttpRequest httpRequest = new Net.HttpRequest("GET");
//            httpRequest.setUrl(url);
            try {
                URLConnection connection = new URL(url).openConnection();
                connection.connect();

                NumberFormat mbFormat = new DecimalFormat("0.0");
                String sizeMbytes = mbFormat.format(((Number) fileToDownload.get("size")).intValue() / (1024f * 1024f));

                byte[] buffer = new byte[100 * 1024];
                FileHandle external = Gdx.files.external("temp.zip");
                try {
                    OutputStream outputStream = external.write(false);
                    try {
                        InputStream inputStream = connection.getInputStream();
                        try {
                            int total = 0;
                            int cnt;
                            while ((cnt = inputStream.read(buffer)) != -1) {
                                total += cnt;
                                outputStream.write(buffer, 0, cnt);
                                String mbytes = mbFormat.format(total / (1024f * 1024f));
                                setMessage("Downloading images " + mbytes + "/" + sizeMbytes + "MB");
                            }
                        } finally {
                            inputStream.close();
                        }
                    } finally {
                        outputStream.close();
                    }

                    setMessage("Unzipping images...");
                    InputStream read = external.read();
                    try {
                        unzip(read, storagePath + "/" + fileToDownload.get("unzipFolder"));
                    } finally {
                        read.close();
                    }
                } finally {
                    external.delete();
                }
                processFilesAndGoToScene(filesToDownload, username, authentication);
            } catch (IOException exp) {
                setLoginError("Unable to download file: " + exp.getMessage());
            }
        }
    }

    private void setMessage(final String message) {
        Gdx.app.postRunnable(
                new Runnable() {
                    @Override
                    public void run() {
                        messageLabel.setText(message);
                    }
                });
    }

    private void setRegisterError(final String registerError) {
        Gdx.app.postRunnable(
                new Runnable() {
                    @Override
                    public void run() {
                        resetRegistrationButtons();
                        registerErrorLabel.setText(registerError);
                    }
                });
    }

    private void resetRegistrationButtons() {
        registerButton.setTouchable(Touchable.enabled);
        registerButton.setDisabled(false);
        goToLoginButton.setTouchable(Touchable.enabled);
        goToLoginButton.setDisabled(false);
    }

    private void setLoginError(final String loginError) {
        Gdx.app.postRunnable(
                new Runnable() {
                    @Override
                    public void run() {
                        resetLoginButtons();
                        loginErrorLabel.setText(loginError);
                    }
                });
    }

    private void resetLoginButtons() {
        loginButton.setTouchable(Touchable.enabled);
        loginButton.setDisabled(false);
        goToRegisterButton.setTouchable(Touchable.enabled);
        goToRegisterButton.setDisabled(false);
    }

    @EventListener
    public void screenResized(ScreenResized screenResized, Entity entity) {
        if (initialized) {
            mainTable.setPosition(
                    (screenResized.getWidth() - mainTable.getWidth()) / 2,
                    (screenResized.getHeight() - mainTable.getHeight()) / 2);
        }
    }

    private static void unzip(InputStream inputStream, String externalPath) throws IOException {
        FileHandle destDir = Gdx.files.external(externalPath);
        destDir.mkdirs();

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            FileHandle newFile = newFile(destDir, zipEntry);
            if (zipEntry.getName().endsWith("/")) {
                newFile.mkdirs();
            } else {
                OutputStream fos = newFile.write(false);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static FileHandle newFile(FileHandle destinationDir, ZipEntry zipEntry) throws IOException {
        return destinationDir.child(zipEntry.getName());
    }

    @Override
    protected void processSystem() {
        if (!initialized) {
            Skin skin = uiSystem.getSkin();

            TextureRegion logoTexture = textureSystem.getTextureRegion("images/logo.png", "logo");
            Image logo = new Image(logoTexture);
            logo.setScaling(Scaling.fill);

            mainTable = new Table(skin);
            mainTable.setDebug(uiDebug);
            mainTable.add(logo).size(logoTexture.getRegionWidth(), logoTexture.getRegionHeight()).row();

            createLoginTable(skin, logoTexture.getRegionWidth());
            createRegisterTable(skin, logoTexture.getRegionWidth());

            containerTable = new Table(skin);
            containerTable.setDebug(uiDebug);

            containerTable.add(loginTable).grow().row();

            mainTable.add(containerTable).height(350).width(500).row();

            Stage stage = uiSystem.getStage();
            stage.addActor(mainTable);

            mainTable.setPosition(
                    (Gdx.graphics.getWidth() - mainTable.getWidth()) / 2,
                    (Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);

            initialized = true;

            //processLogin("test1", "testtest");
        }
    }
}
