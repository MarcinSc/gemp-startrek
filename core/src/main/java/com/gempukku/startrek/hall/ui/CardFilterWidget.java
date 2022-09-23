package com.gempukku.startrek.hall.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class CardFilterWidget extends Table {
    private final DelayedRemovalArray<EventListener> filterChangeListeners = new DelayedRemovalArray(0);

    private final TextButton character;
    private final TextField name;
    private final TextButton power;
    private final TextButton universe;
    private final TextButton teamwork;
    private final TextButton training;
    private final TextButton special;

//    private Predicate<OverpowerCardDefinition> filter;

    public CardFilterWidget(Skin skin) {
        character = new TextButton("Character", skin, "toggle");
        character.setChecked(true);

        name = new TextField("", skin);
        name.setMessageText("Search...");

        power = new TextButton("Power", skin, "toggle");
        universe = new TextButton("Universe", skin, "toggle");
        teamwork = new TextButton("Teamwork", skin, "toggle");
        training = new TextButton("Training", skin, "toggle");
        special = new TextButton("Special", skin, "toggle");

        ClickListener updateFilterListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateFilter();
            }
        };

        character.addListener(updateFilterListener);
        name.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateFilter();
            }
        });
        power.addListener(updateFilterListener);
        universe.addListener(updateFilterListener);
        teamwork.addListener(updateFilterListener);
        training.addListener(updateFilterListener);
        special.addListener(updateFilterListener);

        updateFilter();

        this.add(character);
        this.add(name);
        this.add(power);
        this.add(universe);
        this.add(teamwork);
        this.add(training);
        this.add(special);
        this.row();
    }

    //    public Predicate<OverpowerCardDefinition> getFilter() {
//        return filter;
//    }
//
    private void updateFilter() {
//        Predicate<OverpowerCardDefinition> nameFilter = createNameFilter(name.getText().trim().toLowerCase());
//        Predicate<OverpowerCardDefinition> filter = createTypeFilter();
//
//        this.filter = Predicates.and(nameFilter, filter);
        fireFilterChanged();
    }
//
//    private Predicate<OverpowerCardDefinition> createNameFilter(final String text) {
//        if (text.equals(""))
//            return Predicates.alwaysTrue();
//
//        return new Predicate<OverpowerCardDefinition>() {
//            @Override
//            public boolean apply(@Nullable OverpowerCardDefinition input) {
//                OverpowerCardType type = input.getType();
//                if (type == OverpowerCardType.Power)
//                    return input.getPowerType().name().toLowerCase().contains(text);
//                else if (type == OverpowerCardType.Teamwork || type == OverpowerCardType.Training)
//                    return true;
//                else if (type == OverpowerCardType.Character)
//                    return input.getHero().name().toLowerCase().contains(text);
//                return input.getName().toLowerCase().contains(text);
//            }
//        };
//    }
//
//    private Predicate<OverpowerCardDefinition> createTypeFilter() {
//        Predicate<OverpowerCardDefinition> filter;
//        if (character.isChecked())
//            filter = new Predicate<OverpowerCardDefinition>() {
//                @Override
//                public boolean apply(@Nullable OverpowerCardDefinition input) {
//                    return input.getType() == OverpowerCardType.Character;
//                }
//            };
//        else {
//            if (power.isChecked() || universe.isChecked() || teamwork.isChecked() || training.isChecked()
//                    || special.isChecked()) {
//                filter = new Predicate<OverpowerCardDefinition>() {
//                    @Override
//                    public boolean apply(@Nullable OverpowerCardDefinition input) {
//                        OverpowerCardType type = input.getType();
//                        if (power.isChecked() && type == OverpowerCardType.Power)
//                            return true;
//                        else if (universe.isChecked() && type == OverpowerCardType.Basic)
//                            return true;
//                        else if (teamwork.isChecked() && type == OverpowerCardType.Teamwork)
//                            return true;
//                        else if (training.isChecked() && type == OverpowerCardType.Training)
//                            return true;
//                        else if (special.isChecked() && type == OverpowerCardType.Special)
//                            return true;
//                        return false;
//                    }
//                };
//            } else {
//                filter = Predicates.alwaysTrue();
//            }
//        }
//        return filter;
//    }

    public boolean addFilterChaneListener(EventListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null.");
        if (!filterChangeListeners.contains(listener, true)) {
            filterChangeListeners.add(listener);
            return true;
        }
        return false;
    }

    public boolean removeFilterChangeListener(EventListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null.");
        return filterChangeListeners.removeValue(listener, true);
    }

    private void fireFilterChanged() {
        FilterChangedEvent event = new FilterChangedEvent();
        filterChangeListeners.begin();
        for (int i = 0, n = filterChangeListeners.size; i < n; i++) {
            EventListener listener = filterChangeListeners.get(i);
            if (listener.handle(event)) {
                event.handle();
            }
        }
        filterChangeListeners.end();
    }

    public boolean isShowingCharacters() {
        return character.isChecked();
    }

    public static class FilterChangedEvent extends ChangeListener.ChangeEvent {

    }
}
