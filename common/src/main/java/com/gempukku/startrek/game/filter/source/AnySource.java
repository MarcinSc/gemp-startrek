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
        return new ShortcutCardSource() {
            @Override
            protected void forEachWithShortcut(Entity sourceEntity, Memory memory, ShortcutConsumer<Entity> consumer, CardFilter... filters) {
                IntBag entities = cards.getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = world.getEntity(entities.get(i));
                    if (isAccepted(sourceEntity, memory, entity, filters))
                        if (consumer.accept(entity))
                            return;
                }
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
