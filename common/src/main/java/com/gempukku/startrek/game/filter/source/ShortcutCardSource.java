package com.gempukku.startrek.game.filter.source;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.filter.CardFilter;

import java.util.function.Consumer;

public abstract class ShortcutCardSource implements CardSource {
    @Override
    public void forEach(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, CardFilter... filters) {
        forEachWithShortcut(sourceEntity, memory, new ShortcutConsumer<Entity>() {
            @Override
            public boolean accept(Entity value) {
                consumer.accept(value);
                return false;
            }
        }, filters);
    }

    @Override
    public Entity findFirst(Entity sourceEntity, Memory memory, CardFilter... filters) {
        Entity[] result = new Entity[1];
        forEachWithShortcut(sourceEntity, memory, new ShortcutConsumer<Entity>() {
            @Override
            public boolean accept(Entity value) {
                result[0] = value;
                return true;
            }
        }, filters);
        return result[0];
    }

    @Override
    public boolean hasCount(Entity sourceEntity, Memory memory, int required, CardFilter... filters) {
        int[] total = new int[1];
        forEachWithShortcut(sourceEntity, memory, new ShortcutConsumer<Entity>() {
            @Override
            public boolean accept(Entity value) {
                total[0]++;
                return total[0] >= required;
            }
        }, filters);
        return total[0] >= required;
    }

    @Override
    public int getCount(Entity sourceEntity, Memory memory, CardFilter... filters) {
        int[] result = new int[1];
        forEachWithShortcut(sourceEntity, memory, new ShortcutConsumer<Entity>() {
            @Override
            public boolean accept(Entity value) {
                result[0]++;
                return false;
            }
        }, filters);
        return result[0];
    }

    protected abstract void forEachWithShortcut(Entity sourceEntity, Memory memory, ShortcutConsumer<Entity> consumer, CardFilter... filters);
}
