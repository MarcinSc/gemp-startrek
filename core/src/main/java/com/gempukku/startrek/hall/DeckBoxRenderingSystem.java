package com.gempukku.startrek.hall;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;

import java.util.LinkedList;
import java.util.List;

public class DeckBoxRenderingSystem extends BaseSystem {
    private String cardImageExternalFolder = "gempukku-overpower/download/";
    private int deckboxImageWidth = 150;
    private int deckboxImageHeight = 200;

    private TextureSystem textureSystem;

    private Model deckboxModel;
    private ModelBatch modelBatch;
    private Camera camera;
    private Environment environment;

    private List<Texture> createdTextures = new LinkedList<>();

    @Override
    public void initialize() {
        ModelBuilder modelBuilder = new ModelBuilder();
        deckboxModel = createBoxWithMaterials(modelBuilder, 0f, 0f, 0f, 0.5f, 0.75f, 0.3f);

        modelBatch = new ModelBatch();
        camera = new PerspectiveCamera();
        camera.viewportWidth = deckboxImageWidth;
        camera.viewportHeight = deckboxImageHeight;
        camera.position.set(0.45f, 0.1f, 0.75f);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.update();

        environment = new Environment();
        PointLight pointLight = new PointLight();
        pointLight.set(Color.WHITE, 2f, 2f, 2f, 8f);
        environment.add(pointLight);
        float ambiance = 0.5f;
        Color ambient = new Color(ambiance, ambiance, ambiance, 1f);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambient));
    }

    private static Model createBoxWithMaterials(ModelBuilder modelBuilder, float x, float y, float z, float width, float height, float depth) {
        final float hw = width * 0.5f;
        final float hh = height * 0.5f;
        final float hd = depth * 0.5f;

        final float x0 = x - hw, y0 = y - hh, z0 = z - hd, x1 = x + hw, y1 = y + hh, z1 = z + hd;
        modelBuilder.begin();
        modelBuilder.part("back", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                        new Material("back"))
                .rect(
                        new Vector3(x0, y0, z0), new Vector3(x0, y1, z0),
                        new Vector3(x1, y1, z0), new Vector3(x1, y0, z0),
                        new Vector3(0, 0, -1));
        modelBuilder.part("front", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                        new Material("front"))
                .rect(
                        new Vector3(x0, y0, z1), new Vector3(x1, y0, z1),
                        new Vector3(x1, y1, z1), new Vector3(x0, y1, z1),
                        new Vector3(0, 0, 1));
        modelBuilder.part("bottom", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                        new Material("bottom"))
                .rect(
                        new Vector3(x0, y0, z1), new Vector3(x0, y0, z0),
                        new Vector3(x1, y0, z0), new Vector3(x1, y0, z1),
                        new Vector3(0, -1, 0));
        modelBuilder.part("top", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                        new Material("top"))
                .rect(
                        new Vector3(x0, y1, z0), new Vector3(x0, y1, z1),
                        new Vector3(x1, y1, z1), new Vector3(x1, y1, z0),
                        new Vector3(0, 1, 0));
        modelBuilder.part("left", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                        new Material("left"))
                .rect(
                        new Vector3(x0, y0, z1), new Vector3(x0, y1, z1),
                        new Vector3(x0, y1, z0), new Vector3(x0, y0, z0),
                        new Vector3(-1, 0, 0));
        modelBuilder.part("right", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                        new Material("right"))
                .rect(
                        new Vector3(x1, y0, z0), new Vector3(x1, y1, z0),
                        new Vector3(x1, y1, z1), new Vector3(x1, y0, z1),
                        new Vector3(1, 0, 0));

        return modelBuilder.end();
    }

    public TextureRegion getDeckboxTexture(String cardId) {
        Texture texture = new Texture(drawDeckBox(cardId));
        createdTextures.add(texture);
        return new TextureRegion(texture);
    }

    @Override
    protected void dispose() {
        for (Texture createdTexture : createdTextures) {
            createdTexture.dispose();
        }

        modelBatch.dispose();
    }

    private Pixmap getCutImagePixmap(String cardId) {
        Pixmap source = new Pixmap(Gdx.files.external(cardImageExternalFolder + cardId));
        Pixmap result = new Pixmap(158, 230, Pixmap.Format.RGB888);
        result.drawPixmap(source, 105, 125, 395, 575, 0, 0, 158, 230);
        source.dispose();
        return result;
    }

    private Pixmap drawDeckBox(String cardId) {
        ModelInstance modelInstance = new ModelInstance(deckboxModel);
        Material right = modelInstance.getMaterial("right");
        right.set(TextureAttribute.createDiffuse(textureSystem.getTextureRegion("images/hall/deckbox-side.png", "deckbox-side")));

        Texture frontTexture = null;
        if (cardId != null) {
//            Material front = modelInstance.getMaterial("front");
//            String image = "cardImages/" + cardLibrary.getCardDefinition(cardId).getImage();
//            Pixmap cutImagePixmap = getCutImagePixmap(image);
//            frontTexture = new Texture(cutImagePixmap);
//            front.set(TextureAttribute.createDiffuse(frontTexture));
        }

        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, deckboxImageWidth, deckboxImageHeight, true);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(modelInstance, environment);
        modelBatch.end();

        if (frontTexture != null)
            frontTexture.dispose();

        Pixmap screenGrab = ScreenUtils.getFrameBufferPixmap(0, 0, deckboxImageWidth, deckboxImageHeight);
        frameBuffer.end();
        frameBuffer.dispose();

        Pixmap result = new Pixmap(deckboxImageWidth, deckboxImageHeight, Pixmap.Format.RGBA8888);

        result.setBlending(Pixmap.Blending.None);
        for (int x = 0; x < deckboxImageWidth; x++) {
            for (int y = 0; y < deckboxImageHeight; y++) {
                int pixel = screenGrab.getPixel(x, y);
                if (pixel == 255) {
                    pixel = 0;
                }
                result.drawPixel(x, deckboxImageHeight - y, pixel);
            }
        }

        screenGrab.dispose();

        return result;
    }

    @Override
    protected void processSystem() {

    }
}
