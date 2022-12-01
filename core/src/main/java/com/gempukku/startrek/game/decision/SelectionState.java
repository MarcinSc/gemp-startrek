package com.gempukku.startrek.game.decision;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickableComponent;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionDefinition;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.zone.CardInHandComponent;

import java.util.function.Consumer;

public class SelectionState implements SelectionDefinition {
    private final World world;
    private final Entity userInputStateEntity;
    private final CardFilter cardFilter;
    private final Entity sourceEntity;
    private final Memory memory;
    private final SelectionCallback selectionCallback;
    private final int maxSelected;

    private final ObjectSet<Entity> matchingRenderedCards = new ObjectSet<>();
    private final ObjectSet<Entity> selectionEntities = new ObjectSet<>();
    private final ObjectSet<Entity> markedEntities = new ObjectSet<>();
    private final String selectionMask = "Selection";

    private String cameraName = "main";

    public SelectionState(World world, Entity userInputStateEntity, CardFilter cardFilter,
                          SelectionCallback selectionCallback) {
        this(world, userInputStateEntity, cardFilter, selectionCallback, 1);
    }

    public SelectionState(World world, Entity userInputStateEntity, CardFilter cardFilter,
                          SelectionCallback selectionCallback, int maxSelected) {
        this(world, userInputStateEntity, cardFilter, null, null, selectionCallback, maxSelected);
    }

    public SelectionState(World world, Entity userInputStateEntity, CardFilter cardFilter,
                          Entity sourceEntity, Memory memory,
                          SelectionCallback selectionCallback) {
        this(world, userInputStateEntity, cardFilter, sourceEntity, memory, selectionCallback, 1);
    }

    public SelectionState(World world, Entity userInputStateEntity, CardFilter cardFilter,
                          Entity sourceEntity, Memory memory,
                          SelectionCallback selectionCallback, int maxSelected) {
        this.world = world;
        this.userInputStateEntity = userInputStateEntity;
        this.cardFilter = cardFilter;
        this.sourceEntity = sourceEntity;
        this.memory = memory;
        this.selectionCallback = selectionCallback;
        this.maxSelected = maxSelected;
    }

    @Override
    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    @Override
    public boolean isSelectionTriggered() {
        UserInputStateComponent inputState = userInputStateEntity.getComponent(UserInputStateComponent.class);

        return inputState.getSignals().contains("selectToggle");
    }

    @Override
    public boolean canDeselect(ObjectSet<Entity> selectedEntities, Entity selected) {
        return true;
    }

    @Override
    public boolean canSelect(ObjectSet<Entity> selectedEntities, Entity newSelected) {
        return true;
    }

    @Override
    public void selectionChanged(ObjectSet<Entity> selectedRenderedCards) {
        HierarchySystem hierarchySystem = world.getSystem(HierarchySystem.class);
        SpawnSystem spawnSystem = world.getSystem(SpawnSystem.class);
        for (Entity matchingEntity : matchingRenderedCards) {
            if (selectedRenderedCards.contains(matchingEntity)) {
                // This entity is selected
                Entity selectionEntity = findSelectionEntity(matchingEntity);
                if (selectionEntity != null) {
                    selectionEntities.remove(selectionEntity);
                    world.deleteEntity(selectionEntity);
                    Entity selectedEntity = spawnSystem.spawnEntity("game/card/card-" + getSize(matchingEntity) + "-selected.template");
                    hierarchySystem.addHierarchy(matchingEntity, selectedEntity);
                    markedEntities.add(selectedEntity);
                }
            } else {
                if (selectedRenderedCards.size == maxSelected) {
                    Entity selectionEntity = findSelectionEntity(matchingEntity);
                    if (selectionEntity != null) {
                        selectionEntities.remove(selectionEntity);
                        world.deleteEntity(selectionEntity);
                        matchingEntity.getComponent(ShapePickableComponent.class).getPickingMask().removeValue(selectionMask, false);
                    }
                } else {
                    Entity markedEntity = findMarkedEntity(matchingEntity);
                    if (markedEntity != null) {
                        markedEntities.remove(markedEntity);
                        world.deleteEntity(markedEntity);
                        Entity selectionEntity = spawnSystem.spawnEntity("game/card/card-" + getSize(matchingEntity) + "-selection.template");
                        hierarchySystem.addHierarchy(matchingEntity, selectionEntity);
                        selectionEntities.add(selectionEntity);
                    }
                    Entity selectionEntity = findSelectionEntity(matchingEntity);
                    if (selectionEntity == null) {
                        selectionEntity = spawnSystem.spawnEntity("game/card/card-" + getSize(matchingEntity) + "-selection.template");
                        hierarchySystem.addHierarchy(matchingEntity, selectionEntity);
                        selectionEntities.add(selectionEntity);
                        matchingEntity.getComponent(ShapePickableComponent.class).getPickingMask().add(selectionMask);
                    }
                }
            }
        }
        selectionCallback.selectionChanged(selectedRenderedCards);
    }

    private Entity findMarkedEntity(Entity parent) {
        HierarchySystem hierarchySystem = world.getSystem(HierarchySystem.class);
        for (Entity selectableEntity : markedEntities) {
            if (hierarchySystem.getParent(selectableEntity) == parent)
                return selectableEntity;
        }
        return null;
    }

    private Entity findSelectionEntity(Entity parent) {
        HierarchySystem hierarchySystem = world.getSystem(HierarchySystem.class);
        for (Entity selectableEntity : selectionEntities) {
            if (hierarchySystem.getParent(selectableEntity) == parent)
                return selectableEntity;
        }
        return null;
    }

    public void markSelectableCards() {
        CardFilteringSystem cardFilteringSystem = world.getSystem(CardFilteringSystem.class);
        CardRenderingSystem cardRenderingSystem = world.getSystem(CardRenderingSystem.class);
        SpawnSystem spawnSystem = world.getSystem(SpawnSystem.class);
        HierarchySystem hierarchySystem = world.getSystem(HierarchySystem.class);
        cardFilteringSystem.forEachCard(sourceEntity, memory, new Consumer<Entity>() {
            @Override
            public void accept(Entity cardEntity) {
                Entity renderedCardEntity = cardRenderingSystem.findRenderedCard(cardEntity);
                Entity selectionEntity = spawnSystem.spawnEntity("game/card/card-" + getSize(renderedCardEntity) + "-selection.template");
                hierarchySystem.addHierarchy(renderedCardEntity, selectionEntity);
                selectionEntities.add(selectionEntity);
                matchingRenderedCards.add(renderedCardEntity);
                renderedCardEntity.getComponent(ShapePickableComponent.class).getPickingMask().add(selectionMask);
            }
        }, cardFilter);
    }

    private String getSize(Entity renderedCardEntity) {
        Entity cardEntity = world.getEntity(renderedCardEntity.getComponent(ServerCardReferenceComponent.class).getEntityId());
        CardInHandComponent cardInHand = cardEntity.getComponent(CardInHandComponent.class);
        if (cardInHand != null)
            return "full";
        else
            return "small";
    }

    public boolean hasSelectableEntities() {
        return !selectionEntities.isEmpty();
    }

    public void cleanup() {
        for (Entity matchingRenderedCard : matchingRenderedCards) {
            matchingRenderedCard.getComponent(ShapePickableComponent.class).getPickingMask().removeValue(selectionMask, false);
        }

        for (Entity selectionEntity : selectionEntities) {
            world.deleteEntity(selectionEntity);
        }
        selectionEntities.clear();
        for (Entity markedEntity : markedEntities) {
            world.deleteEntity(markedEntity);
        }
        markedEntities.clear();
        matchingRenderedCards.clear();
    }

    @Override
    public String getMask() {
        return selectionMask;
    }

    @Override
    public Predicate<Entity> getEntityPredicate() {
        return new Predicate<Entity>() {
            @Override
            public boolean evaluate(Entity arg0) {
                return true;
            }
        };
    }
}
