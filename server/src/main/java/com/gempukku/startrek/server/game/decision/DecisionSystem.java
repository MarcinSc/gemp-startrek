package com.gempukku.startrek.server.game.decision;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.decision.DecisionMade;
import com.gempukku.startrek.decision.PlayerDecisionComponent;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.stack.StackSystem;

public class DecisionSystem extends EffectSystem {
    private StackSystem stackSystem;
    private PlayerResolverSystem playerResolverSystem;
    private ComponentMapper<PlayerDecisionComponent> playerDecisionComponentMapper;

    public DecisionSystem() {
        super("sendDecisionToPlayer");
    }

    @EventListener
    public void decisionMade(DecisionMade decisionMade, Entity entity) {
        PlayerDecisionComponent playerDecision = stackSystem.getTopMostStackEntity().getComponent(PlayerDecisionComponent.class);
        if (playerDecision != null) {
            // TODO
        }
    }

    @Override
    public boolean processEndingEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String player = gameEffect.getData().getString("player");
        Entity playerEntity = playerResolverSystem.resolvePlayer(player);

        Entity decisionEntity = world.createEntity();
        PlayerDecisionComponent decision = playerDecisionComponentMapper.create(decisionEntity);
        decision.setOwner(playerEntity.getComponent(GamePlayerComponent.class).getName());
        decision.setData(gameEffect.getData().get("data"));

        stackEffect(decisionEntity);

        return true;
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        // Ignore
    }
}
