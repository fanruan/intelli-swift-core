package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.BinaryArithmeticOperation;
import com.finebi.datasource.sql.criteria.internal.expression.function.BasicFunctionExpression;
import com.taobao.api.request.TradesSoldIncrementGetRequest;

/**
 * This class created on 2016/7/6.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class BinaryArithmeticOperationLiteralRender extends BasicLiteralRender<BinaryArithmeticOperation>{
    public BinaryArithmeticOperationLiteralRender(BinaryArithmeticOperation delegate) {
        super(delegate);
    }
    @Override
    public String render(RenderingContext renderingContext) {
        return getDelegate().getOperator().apply(
                ((Renderable) getDelegate(). getLeftHandOperand()).render(renderingContext).toString(),
                ((Renderable) getDelegate().getRightHandOperand()).render(renderingContext).toString()
        );
    }

    @Override
    public String renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
