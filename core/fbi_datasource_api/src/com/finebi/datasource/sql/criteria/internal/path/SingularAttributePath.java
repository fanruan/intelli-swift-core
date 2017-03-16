
package com.finebi.datasource.sql.criteria.internal.path;

import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.api.metamodel.Bindable;
import com.finebi.datasource.api.metamodel.ManagedType;
import com.finebi.datasource.api.metamodel.SingularAttribute;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.PathSource;

import java.io.Serializable;

/**
 * Models a path for a {@link SingularAttribute} generally obtained from a
 * {@link com.finebi.datasource.api.criteria.Path#get(SingularAttribute)} call
 *
 * @author Steve Ebersole
 */
public class SingularAttributePath<X> extends AbstractPathImpl<X> implements Serializable {
    private final SingularAttribute<?, X> attribute;
    private final ManagedType<X> managedType;

    @SuppressWarnings({"unchecked"})
    public SingularAttributePath(
            CriteriaBuilderImpl criteriaBuilder,
            Class<X> javaType,
            PathSource pathSource,
            SingularAttribute<?, X> attribute) {
        super(criteriaBuilder, javaType, pathSource);
        this.attribute = attribute;
        this.managedType = null;
    }

    private ManagedType<X> resolveManagedType(SingularAttribute<?, X> attribute) {
        return (ManagedType<X>) attribute.getOwnerType();

    }

    @Override
    public SingularAttribute<?, X> getAttribute() {
        return attribute;
    }

    @Override
    public Bindable<X> getModel() {
        return getAttribute();
    }

    @Override
    protected boolean canBeDereferenced() {
        return managedType != null;
    }

    @Override
    protected Attribute locateAttributeInternal(String attributeName) {
        final Attribute attribute = managedType.getAttribute(attributeName);
        // ManagedType.locateAttribute should throw exception rather than return
        // null, but just to be safe...
        if (attribute == null) {
            throw new IllegalArgumentException("Could not resolve attribute named " + attributeName);
        }
        return attribute;
    }


}
