package com.gempukku.startrek.game.config;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectSet;
import com.gempukku.libgdx.lib.artemis.texture.RuntimeTextureHandler;
import com.gempukku.libgdx.lib.artemis.texture.SlotLoadingTextureHandler;
import com.gempukku.libgdx.lib.artemis.texture.TextureReference;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteComponent;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteDefinition;

public class ConfigureTextureSystem extends BaseEntitySystem {
    public static final String ATLAS_NAME = "cardImages";

    private TextureSystem textureSystem;
    private SlotLoadingTextureHandler slotLoadingTextureHandler;

    private RuntimeTextureHandler defaultTextureHandler;

    private ObjectSet<String> loadedPaths = new ObjectSet<>();

    public ConfigureTextureSystem() {
        super(Aspect.all(SpriteComponent.class));
    }

    @Override
    protected void inserted(int entityId) {
        Entity entity = world.getEntity(entityId);
        SpriteComponent sprite = entity.getComponent(SpriteComponent.class);
        for (SpriteDefinition spriteDefinition : sprite.getSprites()) {
            TextureReference texture = (TextureReference) spriteDefinition.getProperties().get("Texture");
            String path = texture.getRegion();
            if (texture.getAtlas().equals(ATLAS_NAME) && !loadedPaths.contains(path)) {
                loadedPaths.add(path);
                slotLoadingTextureHandler.addImage(path, new ImageLoadNotifier() {
                    @Override
                    public void textureLoaded(TextureRegion textureRegion) {
                        reloadSprite(path);
                    }

                    @Override
                    public void textureError() {

                    }
                });
            }
        }
    }

    private void reloadSprite(String path) {

    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
    }

    @Override
    protected void initialize() {
        defaultTextureHandler = new RuntimeTextureHandler();
        textureSystem.setDefaultTextureHandler(defaultTextureHandler);

        slotLoadingTextureHandler = new SlotLoadingTextureHandler(2048, 2048, 310, 200, null);
        textureSystem.addTextureHandler(ATLAS_NAME, slotLoadingTextureHandler);
    }

    @Override
    protected void processSystem() {
        // Iterate through all Sprites and remove those that are no longer used
        ObjectSet<String> usedSprites = new ObjectSet<>();
        IntBag entities = getSubscription().getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = world.getEntity(entities.get(i));
            SpriteComponent sprite = entity.getComponent(SpriteComponent.class);
            for (SpriteDefinition spriteDefinition : sprite.getSprites()) {
                TextureReference textureReference = (TextureReference) spriteDefinition.getProperties().get("Texture");
                if (textureReference.getAtlas().equals(ATLAS_NAME)) {
                    usedSprites.add(textureReference.getRegion());
                }
            }
        }

        for (String loadedPath : loadedPaths) {
            if (!usedSprites.contains(loadedPath)) {
                slotLoadingTextureHandler.removeImage(loadedPath);
                loadedPaths.remove(loadedPath);
            }
        }
    }

    @Override
    protected void dispose() {
        slotLoadingTextureHandler.dispose();
        defaultTextureHandler.dispose();
    }
}
