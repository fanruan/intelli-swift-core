
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.EmbeddableType;

import java.io.Serializable;

/**
 * @author Emmanuel Bernard
 */
public class EmbeddableTypeImpl<X>
        extends AbstractManagedType<X>
        implements EmbeddableType<X>, Serializable {

    private final AbstractManagedType parent;

    public EmbeddableTypeImpl(Class<X> javaType, AbstractManagedType parent) {
        super(javaType, null, null);
        this.parent = parent;
    }

    public PersistenceType getPersistenceType() {
        return PersistenceType.EMBEDDABLE;
    }

    public AbstractManagedType getParent() {
        return parent;
    }

}
