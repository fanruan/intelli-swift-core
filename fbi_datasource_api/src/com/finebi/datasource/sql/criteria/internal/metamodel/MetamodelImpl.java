/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.EmbeddableType;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.ManagedType;
import com.finebi.datasource.api.metamodel.MappedSuperclassType;
import com.finebi.datasource.sql.criteria.internal.ArrayHelper;
import com.finebi.datasource.sql.criteria.internal.CollectionHelper;
import com.finebi.datasource.sql.criteria.internal.important.MetamodelImplementor;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hibernate implementation of the JPA {@link com.finebi.datasource.api.metamodel.Metamodel} contract.
 *
 * @author Steve Ebersole
 * @author Emmanuel Bernard
 */
public class MetamodelImpl implements MetamodelImplementor, Serializable {
    private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();


    private final Map<String, String> imports = new ConcurrentHashMap<String, String>();
    private final Map<Class, String> entityProxyInterfaceMap = new ConcurrentHashMap<Class, String>();
    private final Map<String, Set<String>> collectionRolesByEntityParticipant = new ConcurrentHashMap<String, Set<String>>();


    private final Map<Class<?>, EntityTypeImpl<?>> jpaEntityTypeMap = new ConcurrentHashMap<Class<?>, EntityTypeImpl<?>>();
    private final Map<Class<?>, EmbeddableTypeImpl<?>> jpaEmbeddableTypeMap = new ConcurrentHashMap<Class<?>, EmbeddableTypeImpl<?>>();
    private final Map<Class<?>, MappedSuperclassType<?>> jpaMappedSuperclassTypeMap = new ConcurrentHashMap<Class<?>, MappedSuperclassType<?>>();
    private final Map<String, EntityTypeImpl<?>> jpaEntityTypesByEntityName = new ConcurrentHashMap<String, EntityTypeImpl<?>>();


    @Override
    @SuppressWarnings({"unchecked"})
    public <X> EntityType<X> entity(Class<X> cls) {
        final EntityType<?> entityType = jpaEntityTypeMap.get(cls);
        if (entityType == null) {
            throw new IllegalArgumentException("Not an entity: " + cls);
        }
        return (EntityType<X>) entityType;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <X> ManagedType<X> managedType(Class<X> cls) {
        ManagedType<?> type = jpaEntityTypeMap.get(cls);
        if (type == null) {
            type = jpaMappedSuperclassTypeMap.get(cls);
        }
        if (type == null) {
            type = jpaEmbeddableTypeMap.get(cls);
        }
        if (type == null) {
            throw new IllegalArgumentException("Not a managed type: " + cls);
        }
        return (ManagedType<X>) type;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <X> EmbeddableType<X> embeddable(Class<X> cls) {
        final EmbeddableType<?> embeddableType = jpaEmbeddableTypeMap.get(cls);
        if (embeddableType == null) {
            throw new IllegalArgumentException("Not an embeddable: " + cls);
        }
        return (EmbeddableType<X>) embeddableType;
    }

    @Override
    public Set<ManagedType<?>> getManagedTypes() {
        final int setSize = CollectionHelper.determineProperSizing(
                jpaEntityTypeMap.size() + jpaMappedSuperclassTypeMap.size() + jpaEmbeddableTypeMap.size()
        );
        final Set<ManagedType<?>> managedTypes = new HashSet<ManagedType<?>>(setSize);
        managedTypes.addAll(jpaEntityTypeMap.values());
        managedTypes.addAll(jpaMappedSuperclassTypeMap.values());
        managedTypes.addAll(jpaEmbeddableTypeMap.values());
        return managedTypes;
    }

    @Override
    public Set<EntityType<?>> getEntities() {
        return new HashSet<EntityType<?>>(jpaEntityTypesByEntityName.values());
    }

    @Override
    public Set<EmbeddableType<?>> getEmbeddables() {
        return new HashSet<EmbeddableType<?>>(jpaEmbeddableTypeMap.values());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> EntityType<X> entity(String entityName) {
        return (EntityType<X>) jpaEntityTypesByEntityName.get(entityName);
    }

    @Override
    public String getImportedClassName(String className) {
        String result = imports.get(className);
        if (result == null) {
            try {
                imports.put(className, className);
                return className;
            } catch (Exception cnfe) {
                return null;
            }
        } else {
            return result;
        }
    }

    /**
     * Given the name of an entity class, determine all the class and interface names by which it can be
     * referenced in an HQL query.
     *
     * @param className The name of the entity class
     * @return the names of all persistent (mapped) classes that extend or implement the
     * given class or interface, accounting for implicit/explicit polymorphism settings
     * and excluding mapped subclasses/joined-subclasses of other classes in the result.
     */
    public String[] getImplementors(String className) {


        return null;
    }


    @Override
    public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
        return collectionRolesByEntityParticipant.get(entityName);
    }

    @Override
    public String[] getAllEntityNames() {
        return ArrayHelper.toStringArray(jpaEntityTypesByEntityName.keySet());
    }


    @Override
    public void close() {
        // anything to do ?
    }
}
