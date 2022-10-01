package com.gempukku.startrek.server.game.player;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.game.turn.TurnComponent;

public class PlayerResolverSystem extends BaseSystem {
    public Entity resolvePlayer(String playerFilter) {
        if (playerFilter.equals("currentPlayer")) {
            String username = LazyEntityUtil.findEntityWithComponent(world, TurnComponent.class).getComponent(TurnComponent.class).getPlayer();
            return findPlayerEntity(username);
        }
        throw new RuntimeException("Unable to find player resolver for filter: " + playerFilter);
    }

    public Entity findPlayerEntity(String username) {
        return LazyEntityUtil.findEntityWithComponent(world, GamePlayerComponent.class,
                new Predicate<Entity>() {
                    @Override
                    public boolean evaluate(Entity arg0) {
                        return arg0.getComponent(GamePlayerComponent.class).getName().equals(username);
                    }
                });
    }

    @Override
    protected void processSystem() {

    }
}
