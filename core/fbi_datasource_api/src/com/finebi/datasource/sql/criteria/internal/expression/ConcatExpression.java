
package com.finebi.datasource.sql.criteria.internal.expression;

import java.io.Serializable;
import com.finebi.datasource.api.criteria.Expression;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

/**
 * A string concatenation.
 *
 * @author Steve Ebersole
 */
public class ConcatExpression extends ExpressionImpl<String> implements Serializable {
	private Expression<String> string1;
	private Expression<String> string2;

	public ConcatExpression(
			CriteriaBuilderImpl criteriaBuilder,
			Expression<String> expression1,
			Expression<String> expression2) {
		super( criteriaBuilder, String.class );
		this.string1 = expression1;
		this.string2 = expression2;
	}

	public ConcatExpression(
			CriteriaBuilderImpl criteriaBuilder,
			Expression<String> string1, 
			String string2) {
		this( criteriaBuilder, string1, wrap( criteriaBuilder, string2) );
	}

	private static Expression<String> wrap(CriteriaBuilderImpl criteriaBuilder, String string) {
		return new LiteralExpression<String>( criteriaBuilder, string );
	}

	public ConcatExpression(
			CriteriaBuilderImpl criteriaBuilder,
			String string1,
			Expression<String> string2) {
		this( criteriaBuilder, wrap( criteriaBuilder, string1), string2 );
	}

	public Expression<String> getString1() {
		return string1;
	}

	public Expression<String> getString2() {
		return string2;
	}

	public void registerParameters(ParameterRegistry registry) {
		Helper.possibleParameter( getString1(), registry );
		Helper.possibleParameter( getString2(), registry );
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
		return (RenderExtended) renderingContext.getRenderFactory().getConcatExpressionLiteralRender(this, "default");
	}
}
