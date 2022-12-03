package com.gempukku.startrek.game.filter.source;

public interface ShortcutConsumer<T extends Object> {
    boolean accept(T value);
}
