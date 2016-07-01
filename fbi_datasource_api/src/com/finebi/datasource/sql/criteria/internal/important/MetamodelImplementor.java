
package com.finebi.datasource.sql.criteria.internal.important;

import java.util.Set;

/**
 * @author Steve Ebersole
 */
public interface MetamodelImplementor extends Metamodel {

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




	void close();
}
