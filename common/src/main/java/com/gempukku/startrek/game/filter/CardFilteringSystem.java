package com.gempukku.startrek.game.filter;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.expression.Expression;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.CardInHandComponent;
import com.gempukku.startrek.game.zone.CardZone;

import java.util.function.Consumer;

public class CardFilteringSystem extends BaseSystem {
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private EntitySubscription cardInHandSubscription;
    private ComponentMapper<CardComponent> cardComponentMapper;
    private EntitySubscription cardSubscription;
    private ExpressionSystem expressionSystem;
    private ObjectMap<String, CardFilter> cardFilterCache = new ObjectMap<>();

    private ObjectMap<String, CardFilterHandler> filterHandlers = new ObjectMap<>();

    public CardFilteringSystem() {
        registerFilterHandler("and",
                new CardFilterHandler() {
                    @Override
                    public CardFilter resolveFilter(Array<String> parameters) {
                        return createAndFilter(parameters);
                    }

                    @Override
                    public void validate(Array<String> parameters) {
                        ValidateUtil.atLeast(parameters, 2);
                        for (String parameter : parameters) {
                            validateFilter(parameter);
                        }
                    }
                });
        registerFilterHandler("or",
                new CardFilterHandler() {
                    @Override
                    public CardFilter resolveFilter(Array<String> parameters) {
                        Array<CardFilter> filters = new Array<>();
                        for (String parameter : parameters) {
                            filters.add(resolveCardFilter(parameter));
                        }

                        return new OrCardFilter(filters);
                    }

                    @Override
                    public void validate(Array<String> parameters) {
                        ValidateUtil.atLeast(parameters, 2);
                        for (String parameter : parameters) {
                            validateFilter(parameter);
                        }
                    }
                });
        registerFilterHandler("any",
                new CardFilterHandler() {
                    @Override
                    public CardFilter resolveFilter(Array<String> parameters) {
                        return new CardFilter() {
                            @Override
                            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                                return true;
                            }
                        };
                    }

                    @Override
                    public void validate(Array<String> parameters) {
                        ValidateUtil.exactly(parameters, 0);
                    }
                });
        registerFilterHandler("not",
                new CardFilterHandler() {
                    @Override
                    public CardFilter resolveFilter(Array<String> parameters) {
                        CardFilter opposite = resolveCardFilter(parameters.get(0));
                        return new CardFilter() {
                            @Override
                            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                                return !opposite.accepts(sourceEntity, memory, cardEntity);
                            }
                        };
                    }

                    @Override
                    public void validate(Array<String> parameters) {
                        ValidateUtil.exactly(parameters, 1);
                        validateFilter(parameters.get(0));
                    }
                });
        registerFilterHandler("self",
                new CardFilterHandler() {
                    @Override
                    public CardFilter resolveFilter(Array<String> parameters) {
                        return new CardFilter() {
                            @Override
                            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                                return sourceEntity == cardEntity;
                            }
                        };
                    }

                    @Override
                    public void validate(Array<String> parameters) {
                        ValidateUtil.exactly(parameters, 0);
                    }
                });
    }

    public CardFilter createAndFilter(Array<String> parameters, int startIndex) {
        Array<CardFilter> filters = new Array<>();
        for (int i = startIndex; i < parameters.size; i++) {
            String parameter = parameters.get(i);
            filters.add(resolveCardFilter(parameter));
        }

        return new AndCardFilter(filters);
    }

    public CardFilter createAndFilter(Array<String> parameters) {
        return createAndFilter(parameters, 0);
    }

    public void registerFilterHandler(String effectType, CardFilterHandler conditionHandler) {
        filterHandlers.put(effectType, conditionHandler);
    }

    public CardFilter resolveCardFilter(String value) {
        CardFilter cardFilter = cardFilterCache.get(value);
        if (cardFilter == null) {
            cardFilter = constructCardFilter(value);
            cardFilterCache.put(value, cardFilter);
        }
        return cardFilter;
    }

    private CardFilter constructCardFilter(String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        if (expressions.size > 1) {
            Array<CardFilter> filters = new Array<>();
            for (Expression expression : expressions) {
                String type = expression.getType();
                CardFilterHandler filterHandler = filterHandlers.get(type);
                if (filterHandler == null)
                    throw new GdxRuntimeException("Unable to find filter handler: " + type);

                filters.add(filterHandler.resolveFilter(expression.getParameters()));
            }
            return new AndCardFilter(filters);
        } else {
            Expression expression = expressions.get(0);
            String type = expression.getType();
            CardFilterHandler filterHandler = filterHandlers.get(type);
            if (filterHandler == null)
                throw new GdxRuntimeException("Unable to find filter handler: " + type);

            return filterHandler.resolveFilter(expression.getParameters());
        }
    }

    public void validateFilter(String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        if (expressions.size > 1) {
            Array<CardFilter> filters = new Array<>();
            for (Expression expression : expressions) {
                String type = expression.getType();
                CardFilterHandler filterHandler = filterHandlers.get(type);
                if (filterHandler == null)
                    throw new GdxRuntimeException("Unable to find filter handler: " + type);

                filterHandler.validate(expression.getParameters());
            }
        } else {
            Expression expression = expressions.get(0);
            String type = expression.getType();
            CardFilterHandler filterHandler = filterHandlers.get(type);
            if (filterHandler == null)
                throw new GdxRuntimeException("Unable to find filter handler: " + type);

            filterHandler.validate(expression.getParameters());
        }
    }


    @Override
    protected void initialize() {
        cardInHandSubscription = world.getAspectSubscriptionManager().get(Aspect.all(CardInHandComponent.class));
        cardSubscription = world.getAspectSubscriptionManager().get(Aspect.all(CardComponent.class));
    }

    public void forEachCardInHand(String username, Consumer<Entity> consumer) {
        IntBag entities = cardInHandSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardInHandEntity = world.getEntity(entities.get(i));
            CardComponent card = cardInHandEntity.getComponent(CardComponent.class);
            if (card.getOwner().equals(username)) {
                consumer.accept(cardInHandEntity);
            }
        }
    }

    public void forEachCard(Entity sourceEntity, Memory memory, String filter, Consumer<Entity> consumer) {
        CardFilter cardFilter = resolveCardFilter(filter);
        forEachCard(sourceEntity, memory, cardFilter, consumer);
    }

    public void forEachCard(Entity sourceEntity, Memory memory, CardFilter cardFilter, Consumer<Entity> consumer) {
        IntBag entities = cardSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardEntity = world.getEntity(entities.get(i));
            if (cardFilter.accepts(sourceEntity, memory, cardEntity)) {
                consumer.accept(cardEntity);
            }
        }
    }

    public boolean hasMatchingInPlay(Entity sourceEntity, Memory memory, CardFilter cardFilter, int count) {
        int result = 0;
        IntBag entities = cardSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardEntity = world.getEntity(entities.get(i));
            CardComponent card = cardComponentMapper.get(cardEntity);
            CardZone cardZone = card.getCardZone();
            if (cardZone == CardZone.Core || cardZone == CardZone.Brig || cardZone == CardZone.Mission
                    && cardFilter.accepts(sourceEntity, memory, cardEntity)) {
                result++;
                if (result >= count)
                    return true;
            }
        }
        return false;
    }

    public void forEachCardInPlay(Entity sourceEntity, Memory memory, String filter, Consumer<Entity> consumer) {
        CardFilter cardFilter = resolveCardFilter(filter);
        forEachCardInPlay(sourceEntity, memory, cardFilter, consumer);
    }

    public void forEachCardInPlay(Entity sourceEntity, Memory memory, CardFilter cardFilter, Consumer<Entity> consumer) {
        IntBag entities = cardSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardEntity = world.getEntity(entities.get(i));
            CardComponent card = cardComponentMapper.get(cardEntity);
            CardZone cardZone = card.getCardZone();
            if (cardZone == CardZone.Core || cardZone == CardZone.Brig || cardZone == CardZone.Mission
                    && cardFilter.accepts(sourceEntity, memory, cardEntity)) {
                consumer.accept(cardEntity);
            }
        }
    }

    public Entity findFirstCardInPlay(String filter) {
        return findFirstCardInPlay(null, null, filter);
    }

    public boolean cantFindCard(Entity sourceEntity, Memory memory, CardFilter cardFilter) {
        return findFirstCard(sourceEntity, memory, cardFilter) == null;
    }

    public Array<Entity> findAllInPlay(Entity sourceEntity, Memory memory, String filter) {
        Array<Entity> result = new Array<>();
        forEachCardInPlay(sourceEntity, memory, filter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        result.add(entity);
                    }
                });
        return result;
    }

    public Entity findFirstCard(Entity sourceEntity, Memory memory, String filter) {
        return findFirstCard(sourceEntity, memory, resolveCardFilter(filter));
    }

    public Entity findFirstCard(Entity sourceEntity, Memory memory, CardFilter filter) {
        Array<Entity> result = new Array<>();
        forEachCard(sourceEntity, memory, filter,
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
