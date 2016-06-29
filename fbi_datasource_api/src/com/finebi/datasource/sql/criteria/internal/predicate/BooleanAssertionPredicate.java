/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

import com.finebi.datasource.api.criteria.Expression;
import java.io.Serializable;

/**
 * Predicate to assert the explicit value of a boolean expression:<ul>
 * <li>x = true</li>
 * <li>x = false</li>
 * <li>x <> true</li>
 * <li>x <> false</li>
 * </ul>
 *
 * @author Steve Ebersole
 */
public class BooleanAssertionPredicate
		extends AbstractSimplePredicate
		implements Serializable {
	private final Expression<Boolean> expression;
	private final Boolean assertedValue;

	public BooleanAssertionPredicate(
			CriteriaBuilderImpl criteriaBuilder,
			Expression<Boolean> expression,
			Boolean assertedValue) {
		super( criteriaBuilder );
		this.expression = expression;
		this.assertedValue = assertedValue;
	}

	public Expression<Boolean> getExpression() {
		return expression;
	}

	public Boolean getAssertedValue() {
		return assertedValue;
	}

	@Override
	public void registerParameters(ParameterRegistry registry) {
		Helper.possibleParameter( expression, registry );
	}

	@Override
	public String render(boolean isNegated, RenderingContext renderingContext) {
		final String operator = isNegated ? " <> " : " = ";
		final String assertionLiteral = assertedValue ? "true" : "false";

		return ( (Renderable) expression ).render( renderingContext )
				+ operator
				+ assertionLiteral;
	}
}
