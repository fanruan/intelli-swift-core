
package com.finebi.datasource.sql.criteria.internal.compile;

/**
 * @author Steve Ebersole
 */
public interface CompilableCriteria {

	public void validate();

	public CriteriaInterpretation interpret(RenderingContext renderingContext);
}
