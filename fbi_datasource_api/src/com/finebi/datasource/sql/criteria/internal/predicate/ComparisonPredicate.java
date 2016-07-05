
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.ValueHandlerFactory;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.BinaryOperatorExpression;
import com.finebi.datasource.sql.criteria.internal.expression.LiteralExpression;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Models a basic relational comparison predicate.
 *
 * @author Steve Ebersole
 */
public class ComparisonPredicate
        extends AbstractSimplePredicate
        implements BinaryOperatorExpression<Boolean>, Serializable {
    private final ComparisonOperator comparisonOperator;
    private final Expression<?> leftHandSide;
    private final Expression<?> rightHandSide;

    public ComparisonPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            ComparisonOperator comparisonOperator,
            Expression<?> leftHandSide,
            Expression<?> rightHandSide) {
        super(criteriaBuilder);
        this.comparisonOperator = comparisonOperator;
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    @SuppressWarnings({"unchecked"})
    public ComparisonPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            ComparisonOperator comparisonOperator,
            Expression<?> leftHandSide,
            Object rightHandSide) {
        super(criteriaBuilder);
        this.comparisonOperator = comparisonOperator;
        this.leftHandSide = leftHandSide;
        if (ValueHandlerFactory.isNumeric(leftHandSide.getJavaType())) {
            this.rightHandSide = new LiteralExpression(
                    criteriaBuilder,
                    ValueHandlerFactory.convert(rightHandSide, (Class<Number>) leftHandSide.getJavaType())
            );
        } else {
            this.rightHandSide = new LiteralExpression(criteriaBuilder, rightHandSide);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <N extends Number> ComparisonPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            ComparisonOperator comparisonOperator,
            Expression<N> leftHandSide,
            Number rightHandSide) {
        super(criteriaBuilder);
        this.comparisonOperator = comparisonOperator;
        this.leftHandSide = leftHandSide;
        Class type = leftHandSide.getJavaType();
        if (Number.class.equals(type)) {
            this.rightHandSide = new LiteralExpression(criteriaBuilder, rightHandSide);
        } else {
            N converted = (N) ValueHandlerFactory.convert(rightHandSide, type);
            this.rightHandSide = new LiteralExpression<N>(criteriaBuilder, converted);
        }
    }

    public ComparisonOperator getComparisonOperator() {
        return getComparisonOperator(isNegated());
    }

    public ComparisonOperator getComparisonOperator(boolean isNegated) {
        return isNegated
                ? comparisonOperator.negated()
                : comparisonOperator;
    }

    @Override
    public Expression getLeftHandOperand() {
        return leftHandSide;
    }

    @Override
    public Expression getRightHandOperand() {
        return rightHandSide;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        Helper.possibleParameter(getLeftHandOperand(), registry);
        Helper.possibleParameter(getRightHandOperand(), registry);
    }

    /**
     * Defines the comparison operators.  We could also get away with
     * only 3 and use negation...
     */
    public static enum ComparisonOperator {
        EQUAL {
            public ComparisonOperator negated() {
                return NOT_EQUAL;
            }

            public String rendered() {
                return "=";
            }
        },
        NOT_EQUAL {
            public ComparisonOperator negated() {
                return EQUAL;
            }

            public String rendered() {
                return "<>";
            }
        },
        LESS_THAN {
            public ComparisonOperator negated() {
                return GREATER_THAN_OR_EQUAL;
            }

            public String rendered() {
                return "<";
            }
        },
        LESS_THAN_OR_EQUAL {
            public ComparisonOperator negated() {
                return GREATER_THAN;
            }

            public String rendered() {
                return "<=";
            }
        },
        GREATER_THAN {
            public ComparisonOperator negated() {
                return LESS_THAN_OR_EQUAL;
            }

            public String rendered() {
                return ">";
            }
        },
        GREATER_THAN_OR_EQUAL {
            public ComparisonOperator negated() {
                return LESS_THAN;
            }

            public String rendered() {
                return ">=";
            }
        };

        public abstract ComparisonOperator negated();

        public abstract String rendered();
    }


    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getComparisonPredicateLiteralRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);
    }
}
