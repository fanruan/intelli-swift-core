
package com.finebi.datasource.sql.criteria.internal;
import com.finebi.datasource.api.criteria.Selection;

/**
 * Contract for query components capable of eirther being a parameter or containing parameters.
 *
 * @author Steve Ebersole
 */
public interface ParameterContainer {
	/**
	 * Register any parameters contained within this query component with the given registry.
	 *
	 * @param registry The parameter registry with which to register.
	 */
	public void registerParameters(ParameterRegistry registry);

	/**
	 * Helper to deal with potential parameter container nodes.
	 */
	public static class Helper {
		public static void possibleParameter(Selection selection, ParameterRegistry registry) {
			if ( ParameterContainer.class.isInstance( selection ) ) {
				( (ParameterContainer) selection ).registerParameters( registry );
			}
		}
	}
}
