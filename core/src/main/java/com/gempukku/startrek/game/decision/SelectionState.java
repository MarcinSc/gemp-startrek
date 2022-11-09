package com.gempukku.startrek.game.decision;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionDefinition;
import com.gempukku.startrek.game.CardStorageSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilter;

import java.util.Set;
import java.util.function.Consumer;

public class SelectionState implements SelectionDefinition {
    private World world;
    private Entity userInputStateEntity;
    private CardFilter cardFilter;
    private Entity sourceEntity;
    private Memory memory;
    private SelectionCallback selectionCallback;

    private Array<Entity> selectionEntities = new Array<>();

    public SelectionState(World world, Entity userInputStateEntity, CardFilter cardFilter,
                          SelectionCallback selectionCallback) {
        this(world, userInputStateEntity, cardFilter, null, null, selectionCallback);
    }

    public SelectionState(World world, Entity userInputStateEntity, CardFilter cardFilter,
                          Entity sourceEntity, Memory memory,
                          SelectionCallback selectionCallback) {
        this.world = world;
        this.userInputStateEntity = userInputStateEntity;
        this.cardFilter = cardFilter;
        this.sourceEntity = sourceEntity;
        this.memory = memory;
        this.selectionCallback = selectionCallback;
    }

    @Override
    public boolean isSelectionTriggered() {
        UserInputStateComponent inputState = userInputStateEntity.getComponent(UserInputStateComponent.class);

        return inputState.getSignals().contains("selectToggle");
    }

    @Override
    public boolean canDeselect(Set<Entity> selectedEntities, Entity selected) {
        return world.getSystem(HierarchySystem.class).getChildren(selected).iterator().hasNext();
    }

    @Override
    public boolean canSelect(Set<Entity> selectedEntities, Entity newSelected) {
        return world.getSystem(HierarchySystem.class).getChildren(newSelected).iterator().hasNext();
    }

    @Override
    public void selectionChanged(Set<Entity> selectedEntities) {
        if (selectedEntities.size() == 1) {
            Entity selected = selectedEntities.iterator().next();
            Entity selection = null;
            HierarchySystem hierarchySystem = world.getSystem(HierarchySystem.class);
            for (Entity selectionEntity : selectionEntities) {
                if (hierarchySystem.getParent(selectionEntity) != selected) {
                    world.deleteEntity(selectionEntity);
                } else {
                    selection = selectionEntity;
                }
            }
            selectionEntities.clear();
            selectionEntities.add(selection);
        } else {
            for (Entity selectionEntity : selectionEntities) {
                world.deleteEntity(selectionEntity);
            }
            selectionEntities.clear();
            markPlayableCards();
        }
        selectionCallback.selectionChanged(selectionEntities);
    }

    public void markPlayableCards() {
        CardFilteringSystem cardFilteringSystem = world.getSystem(CardFilteringSystem.class);
        CardStorageSystem cardStorageSystem = world.getSystem(CardStorageSystem.class);
        SpawnSystem spawnSystem = world.getSystem(SpawnSystem.class);
        HierarchySystem hierarchySystem = world.getSystem(HierarchySystem.class);
        cardFilteringSystem.forEachCard(sourceEntity, memory, cardFilter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        Entity renderedCard = cardStorageSystem.findRenderedCard(cardEntity);
                        Entity selectionEntity = spawnSystem.spawnEntity("game/card-full-selection.template");
                        hierarchySystem.addHierarchy(renderedCard, selectionEntity);
                        selectionEntities.add(selectionEntity);
                    }
                });
    }

    public boolean hasSelectableEntities() {
        return !selectionEntities.isEmpty();
    }

    public void cleanup() {
        for (Entity selectionEntity : selectionEntities) {
            world.deleteEntity(selectionEntity);
        }
        selectionEntities.clear();
    }

    @Override
    public String getMask() {
        return "Selection";
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
