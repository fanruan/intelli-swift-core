
package com.finebi.datasource.sql.criteria.internal.expression.function;

import java.io.Serializable;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.expression.ExpressionImpl;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

/**
 * Models the basic concept of a SQL function.
 *
 * @author Steve Ebersole
 */
public class BasicFunctionExpression<X>
		extends ExpressionImpl<X>
		implements FunctionExpression<X>, Serializable {

	private final String functionName;

	public BasicFunctionExpression(
			CriteriaBuilderImpl criteriaBuilder,
			Class<X> javaType,
			String functionName) {
		super( criteriaBuilder, javaType );
		this.functionName = functionName;
	}

	protected  static int properSize(int number) {
		return number + (int)( number*.75 ) + 1;
	}

	public String getFunctionName() {
		return functionName;
	}

	public boolean isAggregation() {
		return false;
	}

	public void registerParameters(ParameterRegistry registry) {
		// nothing to do here...
	}

	public Object render(RenderingContext renderingContext) {
		return delegateRender(renderingContext);
	}

	public Object renderProjection(RenderingContext renderingContext) {
		return render( renderingContext );
	}

	public Object delegateRender(RenderingContext renderingContext) {
		RenderExtended render = choseRender(renderingContext);
		return render.render(renderingContext);
	}

	protected RenderExtended choseRender(RenderingContext renderingContext) {
		return (RenderExtended) renderingContext.getRenderFactory().getBasicFunctionExpressionLiteralRender(this, "default");
	}
}
