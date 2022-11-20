package com.gempukku.startrek.game.filter;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.expression.Expression;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class CardFilterResolverSystem extends BaseSystem {
    private ExpressionSystem expressionSystem;
    private ObjectMap<String, CardFilter> cardFilterCache = new ObjectMap<>();

    private ObjectMap<String, CardFilterHandler> filterHandlers = new ObjectMap<>();

    public CardFilterResolverSystem() {
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
                            CardFilterResolverSystem.this.validateFilter(parameter);
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
                            CardFilterResolverSystem.this.validateFilter(parameter);
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
                        CardFilterResolverSystem.this.validateFilter(parameters.get(0));
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
    protected void processSystem() {

    }

}
