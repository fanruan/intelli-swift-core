
package com.finebi.datasource.sql.criteria.internal.path;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Path;
import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.api.metamodel.SingularAttribute;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.PathImplementor;
import com.finebi.datasource.sql.criteria.internal.PathSource;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.ExpressionImpl;
import com.finebi.datasource.sql.criteria.internal.expression.PathTypeExpression;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Convenience base class for various {@link Path} implementations.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractPathImpl<X>
        extends ExpressionImpl<X>
        implements Path<X>, PathImplementor<X>, Serializable {

    private final PathSource pathSource;
    private final Expression<Class<? extends X>> typeExpression;
    private Map<String, Path> attributePathRegistry;

    /**
     * Constructs a basic path instance.
     *
     * @param criteriaBuilder The criteria builder
     * @param javaType        The java type of this path
     * @param pathSource      The source (or origin) from which this path originates
     */
    @SuppressWarnings({"unchecked"})
    public AbstractPathImpl(
            CriteriaBuilderImpl criteriaBuilder,
            Class<X> javaType,
            PathSource pathSource) {
        super(criteriaBuilder, javaType);
        this.pathSource = pathSource;
        this.typeExpression = new PathTypeExpression(criteriaBuilder(), getJavaType(), this);
    }

    public PathSource getPathSource() {
        return pathSource;
    }

    @Override
    public PathSource<?> getParentPath() {
        return getPathSource();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Expression<Class<? extends X>> type() {
        return typeExpression;
    }

    @Override
    public String getPathIdentifier() {
        return getPathSource().getPathIdentifier() + "." + getAttribute().getName();
    }

    protected abstract boolean canBeDereferenced();

    protected final RuntimeException illegalDereference() {
        return new IllegalStateException(
                String.format(
                        "Illegal attempt to dereference path source [%s] of basic type",
                        getPathIdentifier()
                )
        );
//		String message = "Illegal attempt to dereference path source [";
//		if ( source != null ) {
//			message += " [" + getPathIdentifier() + "]";
//		}
//		return new IllegalArgumentException(message);
    }

    protected final RuntimeException unknownAttribute(String attributeName) {
        String message = "Unable to resolve attribute [" + attributeName + "] against path";
        PathSource<?> source = getPathSource();
        if (source != null) {
            message += " [" + source.getPathIdentifier() + "]";
        }
        return new IllegalArgumentException(message);
    }

    protected final Path resolveCachedAttributePath(String attributeName) {
        return attributePathRegistry == null
                ? null
                : attributePathRegistry.get(attributeName);
    }

    protected final void registerAttributePath(String attributeName, Path path) {
        if (attributePathRegistry == null) {
            attributePathRegistry = new HashMap<String, Path>();
        }
        attributePathRegistry.put(attributeName, path);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <Y> Path<Y> get(SingularAttribute<? super X, Y> attribute) {
        if (!canBeDereferenced()) {
            throw illegalDereference();
        }

        SingularAttributePath<Y> path = (SingularAttributePath<Y>) resolveCachedAttributePath(attribute.getName());
        if (path == null) {
            path = new SingularAttributePath<Y>(
                    criteriaBuilder(),
                    attribute.getJavaType(),
                    getPathSourceForSubPaths(),
                    attribute
            );
            registerAttributePath(attribute.getName(), path);
        }
        return path;
    }

    protected PathSource getPathSourceForSubPaths() {
        return this;
    }


    @Override
    @SuppressWarnings({"unchecked"})
    public <Y> Path<Y> get(String attributeName) {
        if (!canBeDereferenced()) {
            throw illegalDereference();
        }

        final Attribute attribute = locateAttribute(attributeName);
        return get((SingularAttribute<X, Y>) attribute);
    }

    /**
     * Get the attribute by name from the underlying model.  This allows subclasses to
     * define exactly how the attribute is derived.
     *
     * @param attributeName The name of the attribute to locate
     * @return The attribute; should never return null.
     * @throws IllegalArgumentException If no such attribute exists
     */
    protected final Attribute locateAttribute(String attributeName) {
        final Attribute attribute = locateAttributeInternal(attributeName);
        if (attribute == null) {
            throw unknownAttribute(attributeName);
        }
        return attribute;
    }

    /**
     * Get the attribute by name from the underlying model.  This allows subclasses to
     * define exactly how the attribute is derived.  Called from {@link #locateAttribute}
     * which also applies nullness checking for proper error reporting.
     *
     * @param attributeName The name of the attribute to locate
     * @return The attribute; may be null.
     * @throws IllegalArgumentException If no such attribute exists
     */
    protected abstract Attribute locateAttributeInternal(String attributeName);

    @Override
    public void registerParameters(ParameterRegistry registry) {
        // none to register
    }

    @Override
    public void prepareAlias(RenderingContext renderingContext) {
        // Make sure we delegate up to our source (eventually up to the path root) to
        // prepare the path properly.
        PathSource<?> source = getPathSource();
        if (source != null) {
            source.prepareAlias(renderingContext);
        }
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
        return (RenderExtended) renderingContext.getRenderFactory().getAbstractPathRender(this, "default");
    }
}
