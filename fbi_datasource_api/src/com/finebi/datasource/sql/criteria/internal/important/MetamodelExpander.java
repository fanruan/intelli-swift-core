
package com.finebi.datasource.sql.criteria.internal.important;

import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.Metamodel;

import java.util.Set;

/**
 * @author Connery
 */
public interface MetamodelExpander extends Metamodel {


    /**
     * Access to an entity supporting Hibernate's entity-name feature
     *
     * @param entityName The entity-name
     *
     * @return The entity descriptor
     */
    <X> EntityType<X> entity(String entityName);

    String getImportedClassName(String className);

    /**
     * Get the names of all persistent classes that implement/extend the given interface/class
     */
    String[] getImplementors(String entityName);

	/**
	 * Retrieves a set of all the collection roles in which the given entity is a participant, as either an
	 * index or an element.
	 *
	 * @param entityName The entity name for which to get the collection roles.
	 *
	 * @return set of all the collection roles in which the given entityName participates.
	 */
	Set<String> getCollectionRolesByEntityParticipant(String entityName);

	/**
	 * Get the names of all entities known to this Metamodel
	 *
	 * @return All of the entity names
	 */
	String[] getAllEntityNames();

}
