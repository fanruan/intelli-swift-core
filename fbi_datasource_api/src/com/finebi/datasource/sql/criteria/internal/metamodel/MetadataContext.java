/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Defines a context for storing information during the building of the {@link MetamodelImpl}.
 * <p/>
 * This contextual information includes data needing to be processed in a second pass as well as
 * cross-references into the built metamodel classes.
 * <p/>
 * At the end of the day, clients are interested in the {@link #getEntityTypeMap} and {@link #getEmbeddableTypeMap}
 * results, which represent all the registered {@linkplain #registerEntityType entities} and
 * {@linkplain #registerEmbeddedableType embeddables} respectively.
 *
 * @author Steve Ebersole
 * @author Emmanuel Bernard
 */
class MetadataContext {

	private final AttributeFactory attributeFactory = new AttributeFactory( this );

	private Map<Class<?>, EntityTypeImpl<?>> entityTypes = new HashMap<Class<?>, EntityTypeImpl<?>>();
	private Map<String, EntityTypeImpl<?>> entityTypesByEntityName = new HashMap<String, EntityTypeImpl<?>>();
	private Map<Class<?>, EmbeddableTypeImpl<?>> embeddables = new HashMap<Class<?>, EmbeddableTypeImpl<?>>();
	//this list contains MappedSuperclass and EntityTypes ordered by superclass first
	private List<Object> orderedMappings = new ArrayList<Object>();



	/*package*/ boolean isIgnoreUnsupported() {
		return false;
	}

	/**
	 * Retrieves the {@linkplain Class java type} to {@link EntityTypeImpl} map.
	 *
	 * @return The {@linkplain Class java type} to {@link EntityTypeImpl} map.
	 */
	public Map<Class<?>, EntityTypeImpl<?>> getEntityTypeMap() {
		return Collections.unmodifiableMap( entityTypes );
	}

	public Map<Class<?>, EmbeddableTypeImpl<?>> getEmbeddableTypeMap() {
		return Collections.unmodifiableMap( embeddables );
	}




	public EntityTypeImpl<?> locateEntityType(Class<?> javaType) {
		return entityTypes.get( javaType );
	}

	/**
	 * return null which could means that no such mapping exists at least at this time.
	 *
	 * @param entityName The entity-name.
	 *
	 * @return The corresponding JPA {@link org.hibernate.type.EntityType}, or null.
	 */
	public EntityTypeImpl<?> locateEntityType(String entityName) {
		return entityTypesByEntityName.get( entityName );
	}

	public Map<String, EntityTypeImpl<?>> getEntityTypesByEntityName() {
		return Collections.unmodifiableMap( entityTypesByEntityName );
	}



	private <X> void populateStaticMetamodel(AbstractManagedType<X> managedType) {
		final Class<X> managedTypeClass = managedType.getJavaType();
		if ( managedTypeClass == null ) {
			// should indicate MAP entity mode, skip...
			return;
		}
		final String metamodelClassName = managedTypeClass.getName() + "_";
		try {
			final Class metamodelClass = Class.forName( metamodelClassName, true, managedTypeClass.getClassLoader() );
			// we found the class; so populate it...
			registerAttributes( metamodelClass, managedType );
		}
		catch (ClassNotFoundException ignore) {
			// nothing to do...
		}

		// todo : this does not account for @MappeSuperclass, mainly because this is not being tracked in our
		// internal metamodel as populated from the annotatios properly
		AbstractManagedType<? super X> superType = managedType.getSupertype();
		if ( superType != null ) {
			populateStaticMetamodel( superType );
		}
	}

	private final Set<Class> processedMetamodelClasses = new HashSet<Class>();

	private <X> void registerAttributes(Class metamodelClass, AbstractManagedType<X> managedType) {
		if ( !processedMetamodelClasses.add( metamodelClass ) ) {
			return;
		}

		// push the attributes on to the metamodel class...
		for ( Attribute<X, ?> attribute : managedType.getDeclaredAttributes() ) {
			registerAttribute( metamodelClass, attribute );
		}

		if ( IdentifiableType.class.isInstance( managedType ) ) {
			final AbstractIdentifiableType<X> entityType = (AbstractIdentifiableType<X>) managedType;

			// handle version
			if ( entityType.hasDeclaredVersionAttribute() ) {
				registerAttribute( metamodelClass, entityType.getDeclaredVersion() );
			}

			// handle id-class mappings specially
			if ( entityType.hasIdClass() ) {
				final Set<SingularAttribute<? super X, ?>> attributes = entityType.getIdClassAttributesSafely();
				if ( attributes != null ) {
					for ( SingularAttribute<? super X, ?> attribute : attributes ) {
						registerAttribute( metamodelClass, attribute );
					}
				}
			}
		}
	}

	private <X> void registerAttribute(Class metamodelClass, Attribute<X, ?> attribute) {
		final String name = attribute.getName();
		try {
			// there is a shortcoming in the existing Hibernate code in terms of the way MappedSuperclass
			// support was bolted on which comes to bear right here when the attribute is an embeddable type
			// defined on a MappedSuperclass.  We do not have the correct information to determine the
			// appropriate attribute declarer in such cases and so the incoming metamodelClass most likely
			// does not represent the declarer in such cases.
			//
			// As a result, in the case of embeddable classes we simply use getField rather than get
			// getDeclaredField
			final boolean allowNonDeclaredFieldReference =
					attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED
							|| attribute.getDeclaringType().getPersistenceType() == Type.PersistenceType.EMBEDDABLE;

			final Field field = allowNonDeclaredFieldReference
					? metamodelClass.getField( name )
					: metamodelClass.getDeclaredField( name );
			try {
				// should be public anyway, but to be sure...
				field.setAccessible( true );
				field.set( null, attribute );
			}
			catch (IllegalAccessException e) {

			}
			catch (IllegalArgumentException e) {
				// most likely a mismatch in the type we are injecting and the defined field; this represents a
				// mismatch in how the annotation processor interpretted the attribute and how our metamodel
				// and/or annotation binder did.

//              This is particularly the case as arrays are nto handled propery by the StaticMetamodel generator

//				throw new AssertionFailure(
//						"Illegal argument on static metamodel field injection : " + metamodelClass.getName() + '#' + name
//								+ "; expected type :  " + attribute.getClass().getName()
//								+ "; encountered type : " + field.getType().getName()
//				);

			}
		}
		catch (NoSuchFieldException e) {
//			throw new AssertionFailure(
//					"Unable to locate static metamodel field : " + metamodelClass.getName() + '#' + name
//			);
		}
	}


}
