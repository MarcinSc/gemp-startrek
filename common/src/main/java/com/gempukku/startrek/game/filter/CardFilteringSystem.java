package com.gempukku.startrek.game.filter;

import com.artemis.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.expression.Expression;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.source.CardSource;
import com.gempukku.startrek.game.filter.source.CardSourceHandler;
import com.gempukku.startrek.game.zone.CardInHandComponent;

import java.util.function.Consumer;

public class CardFilteringSystem extends BaseSystem {
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private EntitySubscription cardInHandSubscription;
    private ComponentMapper<CardComponent> cardComponentMapper;
    private EntitySubscription cardSubscription;
    private ExpressionSystem expressionSystem;
    private ObjectMap<String, CardFilter> cardFilterCache = new ObjectMap<>();
    private ObjectMap<String, CardSource> cardSourceCache = new ObjectMap<>();

    private ObjectMap<String, CardFilterHandler> filterHandlers = new ObjectMap<>();
    private ObjectMap<String, CardSourceHandler> sourceHandlers = new ObjectMap<>();

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

    public void registerFilterHandler(String filterType, CardFilterHandler cardFilterHandler) {
        filterHandlers.put(filterType, cardFilterHandler);
    }

    public void registerSourceHandler(String sourceType, CardSourceHandler cardSourceHandler) {
        sourceHandlers.put(sourceType, cardSourceHandler);
    }

    public CardFilter resolveCardFilter(String value) {
        CardFilter cardFilter = cardFilterCache.get(value);
        if (cardFilter == null) {
            cardFilter = constructCardFilter(value);
            cardFilterCache.put(value, cardFilter);
        }
        return cardFilter;
    }

    public CardSource resolveCardSource(String value) {
        CardSource cardSource = cardSourceCache.get(value);
        if (cardSource == null) {
            cardSource = constructCardSource(value);
            cardSourceCache.put(value, cardSource);
        }
        return cardSource;
    }

    private CardSource constructCardSource(String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        if (expressions.size > 1) {
            throw new GdxRuntimeException("Unable to resolve card source with multiple values");
        } else {
            Expression expression = expressions.get(0);
            String type = expression.getType();
            CardSourceHandler sourceHandler = sourceHandlers.get(type);
            if (sourceHandler == null)
                throw new GdxRuntimeException("Unable to find source handler: " + type);

            return sourceHandler.resolveSource(expression.getParameters());
        }
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

    public void validateSource(String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        if (expressions.size > 1) {
            throw new GdxRuntimeException("Unable to resolve card source with multiple values");
        } else {
            Expression expression = expressions.get(0);
            String type = expression.getType();
            CardSourceHandler sourceHandler = sourceHandlers.get(type);
            if (sourceHandler == null)
                throw new GdxRuntimeException("Unable to find source handler: " + type);

            sourceHandler.validate(expression.getParameters());
        }
    }

    public void validateFilter(Array<String> parameters) {
        validateFilter(parameters, 0);
    }

    public void validateFilter(Array<String> parameters, int fromIndex) {
        for (int i = fromIndex; i < parameters.size; i++) {
            validateFilter(parameters.get(i));
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

    public Array<Entity> getAll(Entity sourceEntity, Memory memory, String source, String filter) {
        Array<Entity> result = new Array<>();
        forEachCard(sourceEntity, memory, source,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        result.add(entity);
                    }
                }, filter);
        return result;
    }

    public void forEachCard(Entity sourceEntity, Memory memory, String source, Consumer<Entity> consumer, String filter) {
        forEachCard(sourceEntity, memory, source, consumer, resolveCardFilter(filter));
    }

    public void forEachCard(Entity sourceEntity, Memory memory, String source, Consumer<Entity> consumer, CardFilter... cardFilters) {
        resolveCardSource(source).forEach(sourceEntity, memory, consumer, cardFilters);
    }

    public Entity findFirstCard(Entity sourceEntity, Memory memory, String source, String filter) {
        return findFirstCard(sourceEntity, memory, source, resolveCardFilter(filter));
    }

    public Entity findFirstCard(Entity sourceEntity, Memory memory, String source, CardFilter... cardFilters) {
        return resolveCardSource(source).findFirst(sourceEntity, memory, cardFilters);
    }

    public boolean hasCard(Entity sourceEntity, Memory memory, String source, String filter) {
        return hasCard(sourceEntity, memory, source, resolveCardFilter(filter));
    }

    public boolean hasCard(Entity sourceEntity, Memory memory, String source, CardFilter... cardFilters) {
        return hasCardCount(sourceEntity, memory, source, 1, cardFilters);
    }

    public boolean hasCardCount(Entity sourceEntity, Memory memory, String source, int count, String filter) {
        return hasCardCount(sourceEntity, memory, source, count, resolveCardFilter(filter));
    }

    public boolean hasCardCount(Entity sourceEntity, Memory memory, String source, int count, CardFilter... cardFilters) {
        return resolveCardSource(source).hasCount(sourceEntity, memory, count, cardFilters);
    }

    public int countMatchingCards(Entity sourceEntity, Memory memory, String source, CardFilter... cardFilters) {
        return resolveCardSource(source).getCount(sourceEntity, memory, cardFilters);
    }

    @Override
    protected void processSystem() {

    }
}
