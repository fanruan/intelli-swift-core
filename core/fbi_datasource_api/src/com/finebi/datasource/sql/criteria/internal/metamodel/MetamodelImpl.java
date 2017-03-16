
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.Metamodel;
import com.finebi.datasource.sql.criteria.internal.ArrayHelper;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hibernate implementation of the JPA {@link com.finebi.datasource.api.metamodel.Metamodel} contract.
 *
 * @author Steve Ebersole
 * @author Emmanuel Bernard
 */
public class MetamodelImpl implements Metamodel, Serializable {
    private final Map<String, EntityTypeImpl> EntityTypesByEntityName = new ConcurrentHashMap<String, EntityTypeImpl>();
    private boolean closed = false;

    public Builder build() {
        if (!isImmutable()) {
            return new Builder();
        } else {
            throw BINonValueUtils.beyondControl("Current model is immutable");
        }
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public boolean isImmutable() {
        return closed;
    }

    public class Builder {
        public void addEntityType(EntityType entityType) {
            EntityTypesByEntityName.put(entityType.getName(), (EntityTypeImpl) entityType);
        }
    }

    @Override
    public Set<EntityType> getEntities() {
        return new HashSet<EntityType>(EntityTypesByEntityName.values());
    }


    @Override
    public EntityType entity(String entityName) {
        return EntityTypesByEntityName.get(entityName);
    }

    @Override
    public boolean contain(EntityType entityType) {
        return contain(entityType.getName());
    }

    @Override
    public boolean contain(String entityTypeName) {
        return EntityTypesByEntityName.containsKey(entityTypeName);
    }

    @Override
    public String[] getAllEntityNames() {
        return ArrayHelper.toStringArray(EntityTypesByEntityName.keySet());
    }


}
