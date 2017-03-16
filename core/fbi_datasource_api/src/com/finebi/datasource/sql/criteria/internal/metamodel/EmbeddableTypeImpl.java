
package com.finebi.datasource.sql.criteria.internal.metamodel;

import java.io.Serializable;

/**
 * @author Emmanuel Bernard
 */
public class EmbeddableTypeImpl<X>
        extends AbstractManagedType<X>
        implements Serializable, com.finebi.datasource.api.metamodel.ManagedType<X> {

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
