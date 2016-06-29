/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.path;

import com.finebi.datasource.api.metamodel.*;
import com.finebi.datasource.sql.criteria.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.PathSource;

import java.io.Serializable;

/**
 * Models a path for a {@link PluralAttribute} generally obtained from a
 * {@link com.finebi.datasource.api.criteria.Path#get} call
 *
 * @author Steve Ebersole
 */
public class PluralAttributePath<X> extends AbstractPathImpl<X> implements Serializable {
    private final PluralAttribute<?, X, ?> attribute;

    public PluralAttributePath(
            CriteriaBuilderImpl criteriaBuilder,
            PathSource source,
            PluralAttribute<?, X, ?> attribute) {
        super(criteriaBuilder, attribute.getJavaType(), source);
        this.attribute = attribute;
    }


    private String resolveRole(PluralAttribute attribute) {
        Class<?> roleOwnerType = attribute.getDeclaringType().getJavaType();
        if (attribute.getDeclaringType().getPersistenceType() == Type.PersistenceType.MAPPED_SUPERCLASS) {
            // the attribute is declared in a mappedsuperclass
            if (getPathSource().getModel().getBindableType() == Bindable.BindableType.ENTITY_TYPE) {
                // the role will be assigned to the "nearest" EnityType subclass of the MappedSuperclassType
                EntityType entityTypeNearestDeclaringType = (EntityType) getPathSource().getModel();
                IdentifiableType superType = entityTypeNearestDeclaringType.getSupertype();
                IdentifiableType previousType = entityTypeNearestDeclaringType;
                while (superType != attribute.getDeclaringType()) {
                    if (superType == null) {
                        throw new IllegalStateException(
                                String.format(
                                        "Cannot determine nearest EntityType extending mapped superclass [%s]; [%s] extends [%s], but supertype of [%s] is null",
                                        attribute.getDeclaringType().getJavaType().getName(),
                                        ((EntityType) getPathSource().getModel()).getJavaType().getName(),
                                        previousType.getJavaType().getName(),
                                        previousType.getJavaType().getName()
                                )
                        );
                    }
                    if (superType.getPersistenceType() == Type.PersistenceType.ENTITY) {
                        entityTypeNearestDeclaringType = (EntityType) superType;
                    }
                    previousType = superType;
                    superType = superType.getSupertype();
                }
                roleOwnerType = entityTypeNearestDeclaringType.getJavaType();
            }
            // else throw an exception?
        }
        // TODO: still need to deal with a plural attribute declared in an embeddable (HHH-6562)
        return roleOwnerType.getName() +
                '.' + attribute.getName();
    }

    public PluralAttribute<?, X, ?> getAttribute() {
        return attribute;
    }


    @Override
    protected boolean canBeDereferenced() {
        // cannot be dereferenced
        return false;
    }

    @Override
    protected Attribute locateAttributeInternal(String attributeName) {
        throw new IllegalArgumentException("Plural attribute paths cannot be further dereferenced");
    }

    public Bindable<X> getModel() {
        // the issue here is the parameterized type; X is the collection
        // type (Map, Set, etc) while the "bindable" for a collection is the
        // elements.
        //
        // TODO : throw exception instead?
        return null;
    }

    @Override
    public <T extends X> PluralAttributePath<T> treatAs(Class<T> treatAsType) {
        throw new UnsupportedOperationException(
                "Plural attribute path [" + getPathSource().getPathIdentifier() + '.'
                        + attribute.getName() + "] cannot be dereferenced"
        );
    }
}
