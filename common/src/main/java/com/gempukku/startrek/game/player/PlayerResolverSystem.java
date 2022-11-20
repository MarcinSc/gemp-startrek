package com.gempukku.startrek.game.player;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.expression.Expression;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;

public class PlayerResolverSystem extends BaseSystem {
    private ExpressionSystem expressionSystem;

    public String resolvePlayerUsername(Entity sourceEntity, Memory memory, String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        if (expressions.size > 1)
            throw new RuntimeException("Invalid number of expressoins to resolve player");

        Expression expression = expressions.get(0);
        if (expression.getType().equals("currentPlayer")) {
            return LazyEntityUtil.findEntityWithComponent(world, TurnSequenceComponent.class).getComponent(TurnSequenceComponent.class).getCurrentPlayer();
        } else if (expression.getType().equals("username")) {
            return expression.getParameters().get(0);
        } else if (expression.getType().equals("owner")) {
            return sourceEntity.getComponent(CardComponent.class).getOwner();
        } else if (expression.getType().equals("memory")) {
            return memory.getValue(expression.getParameters().get(0));
        }
        throw new RuntimeException("Unable to find player resolver for filter: " + value);
    }

    public Entity resolvePlayer(Entity sourceEntity, Memory memory, String value) {
        return findPlayerEntity(resolvePlayerUsername(sourceEntity, memory, value));
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

    public void validatePlayer(String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        if (expressions.size > 1)
            throw new RuntimeException("Invalid number of expressoins to resolve player");

        Expression expression = expressions.get(0);
        if (expression.getType().equals("currentPlayer")) {
            return;
        } else if (expression.getType().equals("username")) {
            return;
        } else if (expression.getType().equals("owner")) {
            return;
        } else if (expression.getType().equals("memory")) {
            return;
        }
        throw new RuntimeException("Unable to find player resolver for filter: " + value);
    }

    @Override
    protected void processSystem() {

    }
}
