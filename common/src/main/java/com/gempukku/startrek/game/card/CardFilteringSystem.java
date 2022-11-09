package com.gempukku.startrek.game.card;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.zone.CardInHandComponent;
import com.gempukku.startrek.game.zone.CardZone;

import java.util.function.Consumer;

public class CardFilteringSystem extends BaseSystem {
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private EntitySubscription cardInHandSubscription;
    private ComponentMapper<CardComponent> cardComponentMapper;
    private EntitySubscription cardSubscription;

    private CardFilterResolverSystem cardFilterResolverSystem;

    @Override
    protected void initialize() {
        cardInHandSubscription = world.getAspectSubscriptionManager().get(Aspect.all(CardInHandComponent.class));
        cardSubscription = world.getAspectSubscriptionManager().get(Aspect.all(CardComponent.class));
    }

    public void forEachCardInHand(String username, Consumer<Entity> consumer) {
        IntBag entities = cardInHandSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardInHandEntity = world.getEntity(entities.get(i));
            CardInHandComponent cardInHand = cardInHandComponentMapper.get(cardInHandEntity);
            if (cardInHand.getOwner().equals(username)) {
                consumer.accept(cardInHandEntity);
            }
        }
    }

    public void forEachCard(Entity sourceEntity, Memory memory, String filter, Consumer<Entity> consumer) {
        CardFilter cardFilter = cardFilterResolverSystem.resolveCardFilter(filter);
        IntBag entities = cardSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardEntity = world.getEntity(entities.get(i));
            if (cardFilter.accepts(sourceEntity, memory, cardEntity)) {
                consumer.accept(cardEntity);
            }
        }
    }

    public void forEachCardInPlay(Entity sourceEntity, Memory memory, String filter, Consumer<Entity> consumer) {
        CardFilter cardFilter = cardFilterResolverSystem.resolveCardFilter(filter);
        forEachCardInPlay(sourceEntity, memory, cardFilter, consumer);
    }

    public void forEachCardInPlay(Entity sourceEntity, Memory memory, CardFilter cardFilter, Consumer<Entity> consumer) {
        IntBag entities = cardSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardEntity = world.getEntity(entities.get(i));
            CardComponent card = cardComponentMapper.get(cardEntity);
            CardZone cardZone = card.getCardZone();
            if (cardZone == CardZone.CORE || cardZone == CardZone.BRIG || cardZone == CardZone.MISSIONS
                    && cardFilter.accepts(sourceEntity, memory, cardEntity)) {
                consumer.accept(cardEntity);
            }
        }
    }

    public Entity findFirstCardInPlay(String filter) {
        return findFirstCardInPlay(null, null, filter);
    }

    public Entity findFirstCardInPlay(Entity sourceEntity, Memory memory, String filter) {
        Array<Entity> result = new Array<>();
        forEachCardInPlay(sourceEntity, memory, filter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        if (result.size < 1) {
                            result.add(cardEntity);
                        }
                    }
                });
        if (result.size > 0)
            return result.get(0);
        return null;
    }

    public Entity findFirstCardInPlay(Entity sourceEntity, Memory memory, CardFilter cardFilter) {
        Array<Entity> result = new Array<>();
        forEachCardInPlay(sourceEntity, memory, cardFilter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        if (result.size < 1) {
                            result.add(cardEntity);
                        }
                    }
                });
        if (result.size > 0)
            return result.get(0);
        return null;
    }


    @Override
    protected void processSystem() {

    }
}
