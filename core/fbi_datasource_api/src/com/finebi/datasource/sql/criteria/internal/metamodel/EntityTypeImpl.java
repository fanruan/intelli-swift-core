
package com.finebi.datasource.sql.criteria.internal.metamodel;


import com.finebi.datasource.api.metamodel.EntityType;
import java.io.Serializable;

/**
 * Defines the Hibernate implementation of the JPA {@link EntityType} contract.
 *
 * @author Steve Ebersole
 * @author Emmanuel Bernard
 */
public class EntityTypeImpl<X> extends AbstractIdentifiableType<X> implements EntityType<X>, Serializable {
	private final String jpaEntityName;

	@SuppressWarnings("unchecked")
	public EntityTypeImpl(Class javaType, AbstractIdentifiableType<? super X> superType, EntityTypeProperty entityTypeProperty) {
		super(
				javaType,
				entityTypeProperty.getEntityName(),
				superType,
				entityTypeProperty.getDeclaredIdentifierMapper() != null || ( superType != null && superType.hasIdClass() ),
				entityTypeProperty.hasIdentifierProperty(),
				entityTypeProperty.isVersioned()
		);
		this.jpaEntityName = entityTypeProperty.getJpaEntityName();
	}

	@Override
	public String getName() {
		return jpaEntityName;
	}

	@Override
	public BindableType getBindableType() {
		return BindableType.ENTITY_TYPE;
	}

	@Override
	public Class<X> getBindableJavaType() {
		return getJavaType();
	}

	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.ENTITY;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityTypeImpl)) return false;

        EntityTypeImpl<?> that = (EntityTypeImpl<?>) o;

        return !(jpaEntityName != null ? !jpaEntityName.equals(that.jpaEntityName) : that.jpaEntityName != null);

    }

    @Override
    public int hashCode() {
        return jpaEntityName != null ? jpaEntityName.hashCode() : 0;
    }
}
