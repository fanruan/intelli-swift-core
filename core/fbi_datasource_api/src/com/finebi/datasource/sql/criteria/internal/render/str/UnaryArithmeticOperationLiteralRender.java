package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.UnaryArithmeticOperation;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class UnaryArithmeticOperationLiteralRender extends BasicLiteralRender<UnaryArithmeticOperation> {
    public UnaryArithmeticOperationLiteralRender(UnaryArithmeticOperation delegate) {
        super(delegate);
    }
    @Override
    public String render(RenderingContext renderingContext) {
        return (getDelegate().getOperation() == UnaryArithmeticOperation.Operation.UNARY_MINUS ? '-' : '+')
                + ((Renderable)getDelegate().getOperand()).render(renderingContext).toString();
    }

    @Override
    public String renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
