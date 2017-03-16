
package com.finebi.datasource.sql.criteria.internal.compile;

import com.finebi.datasource.api.criteria.ParameterExpression;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactory;

/**
 * Used to provide a context and services to the rendering.
 *
 * @author Steve Ebersole
 */
public interface RenderingContext<R> {
    /**
     * Generate a correlation name.
     *
     * @return The generated correlation name
     */
    public String generateAlias();

    /**
     * Register parameters explicitly encountered in the criteria query.
     *
     * @param criteriaQueryParameter The parameter expression
     * @return The JPA-QL parameter name
     */
    public ExplicitParameterInfo registerExplicitParameter(ParameterExpression<?> criteriaQueryParameter);

    /**
     * Register a parameter that was not part of the criteria query (at least not as a parameter).
     *
     * @param literal  The literal value
     * @param javaType The java type as whcih to handle the literal value.
     * @return The JPA-QL parameter name
     */
    public String registerLiteralParameterBinding(Object literal, Class javaType);

    /**
     * Given a java type, determine the proper cast type name.
     *
     * @param javaType The java type.
     * @return The cast type name.
     */
    public String getCastType(Class javaType);

    RenderFactory getRenderFactory();
}
