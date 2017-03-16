
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterContainer;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Models unary arithmetic operation (unary plus and unary minus).
 *
 * @author Steve Ebersole
 */
public class UnaryArithmeticOperation<T>
        extends ExpressionImpl<T>
        implements UnaryOperatorExpression<T>, Serializable {

    public static enum Operation {
        UNARY_PLUS, UNARY_MINUS
    }

    private final Operation operation;
    private final Expression<T> operand;

    @SuppressWarnings({"unchecked"})
    public UnaryArithmeticOperation(
            CriteriaBuilderImpl criteriaBuilder,
            Operation operation,
            Expression<T> operand) {
        super(criteriaBuilder, (Class) operand.getJavaType());
        this.operation = operation;
        this.operand = operand;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public Expression<T> getOperand() {
        return operand;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(getOperand(), registry);
    }

    @Override
    public Object render(RenderingContext renderingContext) {
        return delegateRender(renderingContext);
    }

    @Override
    public Object renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
    public Object delegateRender(RenderingContext renderingContext) {
        RenderExtended render = choseRender(renderingContext);
        return render.render(renderingContext);
    }

    protected RenderExtended choseRender(RenderingContext renderingContext) {
        return (RenderExtended) renderingContext.getRenderFactory().getUnaryArithmeticOperationLiteralRender(this, "default");
    }
}
