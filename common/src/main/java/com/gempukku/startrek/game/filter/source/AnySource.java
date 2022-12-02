package com.gempukku.startrek.game.filter.source;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;

import java.util.function.Consumer;

public class AnySource extends CardSourceSystem {
    private EntitySubscription cards;

    public AnySource() {
        super("any");
    }

    @Override
    protected void initialize() {
        super.initialize();
        cards = world.getAspectSubscriptionManager().get(Aspect.all(CardComponent.class));
    }

    @Override
    public CardSource resolveSource(Array<String> parameters) {
        return new CardSource() {
            @Override
            public void forEach(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, CardFilter... filters) {
                IntBag entities = cards.getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = world.getEntity(entities.get(i));
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        consumer.accept(entity);
                }
            }

            @Override
            public Entity findFirst(Entity sourceEntity, Memory memory, CardFilter... filters) {
                IntBag entities = cards.getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = world.getEntity(entities.get(i));
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        return entity;
                }
                return null;
            }

            @Override
            public boolean hasCount(Entity sourceEntity, Memory memory, int required, CardFilter... filters) {
                int count = 0;
                IntBag entities = cards.getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = world.getEntity(entities.get(i));
                    if (isAccepted(sourceEntity, memory, entity, filters)) {
                        count++;
                        if (count >= required)
                            return true;
                    }
                }
                return false;
            }

            @Override
            public int getCount(Entity sourceEntity, Memory memory, CardFilter... filters) {
                int result = 0;
                IntBag entities = cards.getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = world.getEntity(entities.get(i));
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        result++;
                }
                return result;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
