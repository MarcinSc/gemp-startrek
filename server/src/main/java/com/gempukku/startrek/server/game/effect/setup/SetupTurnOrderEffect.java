package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.stack.StackSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetupTurnOrderEffect extends EffectSystem {
    private ServerSpawnSystem spawnSystem;
    private StackSystem stackSystem;

    public SetupTurnOrderEffect() {
        super("setupTurnOrder");
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
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

        Collections.shuffle(orderedPlayers);
        return orderedPlayers;
    }
}
