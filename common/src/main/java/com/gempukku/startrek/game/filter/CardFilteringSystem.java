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

    public void forEachCard(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, String filter) {
        forEachCard(sourceEntity, memory, consumer, resolveCardFilter(filter));
    }

    public void forEachCard(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, CardFilter... cardFilters) {
        IntBag entities = cardSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardEntity = world.getEntity(entities.get(i));
            if (matchesFilters(sourceEntity, memory, cardEntity, cardFilters))
                consumer.accept(cardEntity);
        }
    }

    public Array<Entity> getAllCards(Entity sourceEntity, Memory memory, String filter) {
        return getAllCards(sourceEntity, memory, resolveCardFilter(filter));
    }

    public Array<Entity> getAllCards(Entity sourceEntity, Memory memory, CardFilter... cardFilters) {
        Array<Entity> result = new Array<>();
        forEachCard(sourceEntity, memory, new Consumer<Entity>() {
            @Override
            public void accept(Entity entity) {
                result.add(entity);
            }
        }, cardFilters);
        return result;
    }

    public Entity findFirstCard(Entity sourceEntity, Memory memory, String filter) {
        return findFirstCard(sourceEntity, memory, resolveCardFilter(filter));
    }

    public Entity findFirstCard(Entity sourceEntity, Memory memory, CardFilter... cardFilters) {
        IntBag entities = cardSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardEntity = world.getEntity(entities.get(i));
            if (matchesFilters(sourceEntity, memory, cardEntity, cardFilters))
                return cardEntity;
        }
        return null;
    }

    public boolean hasCard(Entity sourceEntity, Memory memory, String filter) {
        return hasCard(sourceEntity, memory, resolveCardFilter(filter));
    }

    public boolean hasCard(Entity sourceEntity, Memory memory, CardFilter... cardFilters) {
        return findFirstCard(sourceEntity, memory, cardFilters) != null;
    }

    public boolean hasCardCount(Entity sourceEntity, Memory memory, int count, CardFilter... cardFilters) {
        int total = 0;
        IntBag entities = cardSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardEntity = world.getEntity(entities.get(i));
            if (matchesFilters(sourceEntity, memory, cardEntity, cardFilters)) {
                total++;
                if (total >= count)
                    return true;
            }
        }
        return false;
    }

    public int countMatchingCards(Entity sourceEntity, Memory memory, CardFilter... cardFilters) {
        int total = 0;
        IntBag entities = cardSubscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cardEntity = world.getEntity(entities.get(i));
            if (matchesFilters(sourceEntity, memory, cardEntity, cardFilters)) {
                total++;
            }
        }
        return total;
    }

    private boolean matchesFilters(Entity sourceEntity, Memory memory, Entity cardEntity, CardFilter... cardFilters) {
        for (CardFilter cardFilter : cardFilters) {
            if (!cardFilter.accepts(sourceEntity, memory, cardEntity))
                return false;
        }
        return true;
    }

    public void forEachCardInHand(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, String filter) {
        forEachCard(sourceEntity, memory, consumer, inHandFilter(), resolveCardFilter(filter));
    }

    public boolean hasCardCountInPlay(Entity sourceEntity, Memory memory, int count, String filter) {
        return hasCardCount(sourceEntity, memory, count, inPlayFilter(), resolveCardFilter(filter));
    }

    private CardFilter inHandFilter() {
        return resolveCardFilter("zone(Hand)");
    }

    private CardFilter inPlayFilter() {
        return resolveCardFilter("inPlay");
    }

    public boolean hasCardInPlay(Entity sourceEntity, Memory memory, String filter) {
        return hasCardInPlay(sourceEntity, memory, resolveCardFilter(filter));
    }

    public boolean hasCardInPlay(Entity sourceEntity, Memory memory, CardFilter cardFilter) {
        return hasCard(sourceEntity, memory, inPlayFilter(), cardFilter);
    }

    public Entity findFirstCardInPlay(Entity sourceEntity, Memory memory, String filter) {
        return findFirstCardInPlay(sourceEntity, memory, resolveCardFilter(filter));
    }

    public Entity findFirstCardInPlay(Entity sourceEntity, Memory memory, CardFilter cardFilter) {
        return findFirstCard(sourceEntity, memory, inPlayFilter(), cardFilter);
    }

    public void forEachCardInPlay(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, String filter) {
        forEachCardInPlay(sourceEntity, memory, consumer, resolveCardFilter(filter));
    }

    public void forEachCardInPlay(Entity sourceEntity, Memory memory, Consumer<Entity> consumer, CardFilter cardFilter) {
        forEachCard(sourceEntity, memory, consumer, inPlayFilter(), cardFilter);
    }

    public Array<Entity> getAllCardsInPlay(Entity sourceEntity, Memory memory, String filter) {
        return getAllCards(sourceEntity, memory, inPlayFilter(), resolveCardFilter(filter));
    }

    public Array<Entity> getAllCardsInPlay(Entity sourceEntity, Memory memory, CardFilter filter) {
        return getAllCards(sourceEntity, memory, inPlayFilter(), filter);
    }

    @Override
    protected void processSystem() {

    }
}
