
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.api.metamodel.ManagedType;
import com.finebi.datasource.api.metamodel.AttributeType;

import java.io.Serializable;

/**
 * Models the commonality of the JPA {@link Attribute} hierarchy.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractAttribute<X, Y>
        implements Attribute<X, Y>, AttributeImplementor<X, Y>, Serializable {
    private final String name;
    private final AttributeType<Y> attributeType;
    private final AbstractManagedType<X> ownerType;

    public AbstractAttribute(
            String name,
            AttributeType<Y> attributeType,
            AbstractManagedType<X> ownerType) {
        this.name = name;
        this.attributeType = attributeType;
        this.ownerType = ownerType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ManagedType<X> getOwnerType() {
        return ownerType;
    }

    @Override
    public Class<Y> getJavaType() {
        return attributeType.getJavaType();
    }


}
