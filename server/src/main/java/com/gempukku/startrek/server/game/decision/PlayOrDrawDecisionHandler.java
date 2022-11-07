package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.stack.StackSystem;

public class PlayOrDrawDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;
    private PlayerResolverSystem playerResolverSystem;
    private SpawnSystem spawnSystem;
    private StackSystem stackSystem;
    private EventSystem eventSystem;

    @Override
    protected void initialize() {
        decisionSystem.registerDecisionTypeHandler("playOrDrawDecision", this);
    }

    @Override
    public boolean validateDecision(String decisionPlayer, JsonValue decisionData, ObjectMap<String, String> result) {
        String action = result.get("action");
        if (action == null)
            return false;
        Entity playerEntity = playerResolverSystem.findPlayerEntity(decisionPlayer);
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        if (action.equals("draw") && publicStats.getCounterCount() > 0 && publicStats.getDeckCount() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void processDecision(String decisionPlayer, JsonValue decisionData, ObjectMap<String, String> result) {
        String action = result.get("action");
        if (action.equals("draw")) {
            Entity playerEntity = playerResolverSystem.findPlayerEntity(decisionPlayer);
            PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            publicStats.setCounterCount(publicStats.getCounterCount() - 1);
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

            Entity drawCardEffect = spawnSystem.spawnEntity("game/effect/drawCardEffect.template");
            GameEffectComponent gameEffect = drawCardEffect.getComponent(GameEffectComponent.class);
            JsonValue data = gameEffect.getClonedData();
            data.addChild("player", new JsonValue("username(" + decisionPlayer + ")"));
            gameEffect.setData(data);
            stackSystem.stackEntity(drawCardEffect);
        }
    }

    @Override
    protected void processSystem() {

    }
}
