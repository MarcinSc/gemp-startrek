package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.stack.StackSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetupTurnOrderEffect extends EffectSystem {
    private SpawnSystem spawnSystem;
    private StackSystem stackSystem;

    private boolean test;

    public SetupTurnOrderEffect(boolean test) {
        super("setupTurnOrder");
        this.test = test;
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        GameComponent game = LazyEntityUtil.findEntityWithComponent(world, GameComponent.class).getComponent(GameComponent.class);

        Entity turnSequenceEntity = spawnSystem.spawnEntity("game/turnSequence.template");
        Array<String> players = game.getPlayers();
        List<String> orderedPlayers = determinePlayerOrder(players);

        TurnSequenceComponent turnSequence = turnSequenceEntity.getComponent(TurnSequenceComponent.class);
        Array<String> turnPlayers = turnSequence.getPlayers();
        for (String orderedPlayer : orderedPlayers) {
            turnPlayers.add(orderedPlayer);
        }

        stackSystem.stackEntity(turnSequenceEntity);
    }

    private List<String> determinePlayerOrder(Array<String> players) {
        List<String> orderedPlayers = new ArrayList<>();
        for (String player : players) {
            orderedPlayers.add(player);
        }

        if (!test)
            Collections.shuffle(orderedPlayers);

        return orderedPlayers;
    }
}
