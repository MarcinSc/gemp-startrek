package com.gempukku.startrek.common;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.gempukku.libgdx.lib.artemis.texture.RuntimeTextureHandler;
import com.gempukku.libgdx.lib.artemis.texture.SlotLoadingTextureHandler;
import com.gempukku.libgdx.lib.artemis.texture.TextureReference;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteComponent;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteDefinition;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteSystem;
import com.gempukku.startrek.game.config.ImageLoadNotifier;

@Wire(failOnNull = false)
public class ConfigureTextureSystem extends BaseEntitySystem {
    public static final String ATLAS_NAME = "cardImages";

    private TextureSystem textureSystem;
    @Wire(failOnNull = false)
    private SpriteSystem spriteSystem;
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
        IntBag entities = getSubscription().getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = world.getEntity(entities.get(i));
            SpriteComponent sprite = entity.getComponent(SpriteComponent.class);

            Array<SpriteDefinition> spriteDefinitions = sprite.getSprites();
            for (int spriteIndex = 0; spriteIndex < spriteDefinitions.size; spriteIndex++) {
                SpriteDefinition spriteDefinition = spriteDefinitions.get(spriteIndex);
                TextureReference textureReference = (TextureReference) spriteDefinition.getProperties().get("Texture");
                if (textureReference.getAtlas().equals(ATLAS_NAME) && textureReference.getRegion().equals(path)) {
                    spriteSystem.updateSprite(entity.getId(), spriteIndex);
                }
            }
        }
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
    }

    @Override
    protected void initialize() {
        defaultTextureHandler = new RuntimeTextureHandler();
        textureSystem.setDefaultTextureHandler(defaultTextureHandler);

        slotLoadingTextureHandler = new SlotLoadingTextureHandler(2048, 2048, 323, 200,
                new LocalFileHandleResolver(), textureSystem.getTextureRegion("atlas/icons.atlas", "Blank"));
        textureSystem.addTextureHandler(ATLAS_NAME, slotLoadingTextureHandler);
    }

    @Override
    protected void processSystem() {
        slotLoadingTextureHandler.update();
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

        ObjectSet.ObjectSetIterator<String> iterator = loadedPaths.iterator();
        while (iterator.hasNext()) {
            String loadedPath = iterator.next();
            if (!usedSprites.contains(loadedPath)) {
                slotLoadingTextureHandler.removeImage(loadedPath);
                iterator.remove();
            }
        }
    }

    @Override
    protected void dispose() {
        slotLoadingTextureHandler.dispose();
        defaultTextureHandler.dispose();
    }
}
