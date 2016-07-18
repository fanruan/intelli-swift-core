
package com.finebi.datasource.sql.criteria.internal.expression;

import java.io.Serializable;
import com.finebi.datasource.api.criteria.Subquery;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

/**
 * Represents a {@link Modifier#ALL}, {@link Modifier#ANY}, {@link Modifier#SOME} modifier appplied to a subquery as
 * part of a comparison.
 *
 * @author Steve Ebersole
 */
public class SubqueryComparisonModifierExpression<Y>
		extends ExpressionImpl<Y>
		implements Serializable {
	public static enum Modifier {
		ALL {
			public String rendered() {
				return "all ";
			}
		},
		SOME {
			public String rendered() {
				return "some ";
			}
		},
		ANY {
			public String rendered() {
				return "any ";
			}
		};
		public abstract String rendered();
	}

	private final Subquery<Y> subquery;
	private final Modifier modifier;

	public SubqueryComparisonModifierExpression(
			CriteriaBuilderImpl criteriaBuilder,
			Class<Y> javaType,
			Subquery<Y> subquery,
			Modifier modifier) {
		super( criteriaBuilder, javaType);
		this.subquery = subquery;
		this.modifier = modifier;
	}

	public Modifier getModifier() {
		return modifier;
	}

	public Subquery<Y> getSubquery() {
		return subquery;
	}

	public void registerParameters(ParameterRegistry registry) {
		// nothing to do (the subquery should be handled directly, and the modified itself is not parameterized)
	}

	public String render(RenderingContext renderingContext) {
		return (String)delegateRender(renderingContext);
	}

	public String renderProjection(RenderingContext renderingContext) {
		return render( renderingContext );
	}
	public Object delegateRender(RenderingContext renderingContext) {
		RenderExtended render = choseRender(renderingContext);
		return render.render(renderingContext);
	}

	protected RenderExtended choseRender(RenderingContext renderingContext) {
		return (RenderExtended) renderingContext.getRenderFactory().getSubqueryComparisonModifierExpressionLiteralRender(this, "default");
	}
}
