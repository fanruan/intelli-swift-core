/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.api.metamodel.PluralAttribute;
import com.finebi.datasource.api.metamodel.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * A factory for building {@link Attribute} instances.  Exposes 3 main services for building<ol>
 * <li>{@link #buildAttribute normal attributes}</li>
 * <li>{@link #buildIdAttribute id attributes}</li>
 * <li>{@link #buildVersionAttribute version attributes}</li>
 * <ol>
 *
 * @author Steve Ebersole
 * @author Emmanuel Bernard
 */
public class AttributeFactory {

	private final MetadataContext context;

	public AttributeFactory(MetadataContext context) {
		this.context = context;
	}

	/**
	 * Build a normal attribute.
	 *
	 * @param ownerType The descriptor of the attribute owner (aka declarer).
	 * @param property The Hibernate property descriptor for the attribute
	 * @param <X> The type of the owner
	 * @param <Y> The attribute type
	 *
	 * @return The built attribute descriptor or null if the attribute is not part of the JPA 2 model (eg backrefs)
	 */
	@SuppressWarnings({"unchecked"})
	public <X, Y> AttributeImplementor<X, Y> buildAttribute(AbstractManagedType<X> ownerType, Property property) {

		final AttributeContext<X> attributeContext = wrap( ownerType, property );
		final AttributeMetadata<X, Y> attributeMetadata =
				determineAttributeMetadata( attributeContext, normalMemberResolver );
		if ( attributeMetadata == null ) {
			return null;
		}
		if ( attributeMetadata.isPlural() ) {
			return buildPluralAttribute( (PluralAttributeMetadata) attributeMetadata );
		}
		final SingularAttributeMetadata<X, Y> singularAttributeMetadata = (SingularAttributeMetadata<X, Y>) attributeMetadata;
		final Type<Y> metaModelType = getMetaModelType( singularAttributeMetadata.getValueContext() );
		return new SingularAttributeImpl<X, Y>(
				attributeMetadata.getName(),
				attributeMetadata.getJavaType(),
				ownerType,
				attributeMetadata.getMember(),
				false,
				false,
				property.isOptional(),
				metaModelType,
				attributeMetadata.getPersistentAttributeType()
		);
	}

	private <X> AttributeContext<X> wrap(final AbstractManagedType<X> ownerType, final Property property) {
		return new AttributeContext<X>() {
			public AbstractManagedType<X> getOwnerType() {
				return ownerType;
			}

			public Property getPropertyMapping() {
				return property;
			}
		};
	}

	/**
	 * Build the identifier attribute descriptor
	 *
	 * @param ownerType The descriptor of the attribute owner (aka declarer).
	 * @param property The Hibernate property descriptor for the identifier attribute
	 * @param <X> The type of the owner
	 * @param <Y> The attribute type
	 *
	 * @return The built attribute descriptor
	 */
	@SuppressWarnings({"unchecked"})
	public <X, Y> SingularAttributeImpl<X, Y> buildIdAttribute(
			AbstractIdentifiableType<X> ownerType,
			Property property) {
		final AttributeContext<X> attributeContext = wrap( ownerType, property );
		final SingularAttributeMetadata<X, Y> attributeMetadata =
				(SingularAttributeMetadata<X, Y>) determineAttributeMetadata(
						attributeContext,
						identifierMemberResolver
				);
		final Type<Y> metaModelType = getMetaModelType( attributeMetadata.getValueContext() );
		return new SingularAttributeImpl.Identifier(
				property.getName(),
				attributeMetadata.getJavaType(),
				ownerType,
				attributeMetadata.getMember(),
				metaModelType,
				attributeMetadata.getPersistentAttributeType()
		);
	}

	/**
	 * Build the version attribute descriptor
	 *
	 * @param ownerType The descriptor of the attribute owner (aka declarer).
	 * @param property The Hibernate property descriptor for the version attribute
	 * @param <X> The type of the owner
	 * @param <Y> The attribute type
	 *
	 * @return The built attribute descriptor
	 */
	@SuppressWarnings({"unchecked"})
	public <X, Y> SingularAttributeImpl<X, Y> buildVersionAttribute(
			AbstractIdentifiableType<X> ownerType,
			Property property) {
		final AttributeContext<X> attributeContext = wrap( ownerType, property );
		final SingularAttributeMetadata<X, Y> attributeMetadata =
				(SingularAttributeMetadata<X, Y>) determineAttributeMetadata( attributeContext, versionMemberResolver );
		final Type<Y> metaModelType = getMetaModelType( attributeMetadata.getValueContext() );
		return new SingularAttributeImpl.Version(
				property.getName(),
				attributeMetadata.getJavaType(),
				ownerType,
				attributeMetadata.getMember(),
				metaModelType,
				attributeMetadata.getPersistentAttributeType()
		);
	}

	@SuppressWarnings("unchecked")
	private <X, Y, E, K> AttributeImplementor<X, Y> buildPluralAttribute(PluralAttributeMetadata<X, Y, E> attributeMetadata) {
		final Type<E> elementType = getMetaModelType( attributeMetadata.getElementValueContext() );
		if ( java.util.Map.class.isAssignableFrom( attributeMetadata.getJavaType() ) ) {
			final Type<K> keyType = getMetaModelType( attributeMetadata.getMapKeyValueContext() );
			return PluralAttributeImpl.create(
					attributeMetadata.getOwnerType(),
					elementType,
					attributeMetadata.getJavaType(),
					keyType
			)
					.member( attributeMetadata.getMember() )
					.property( attributeMetadata.getPropertyMapping() )
					.persistentAttributeType( attributeMetadata.getPersistentAttributeType() )
					.build();
		}
		return PluralAttributeImpl.create(
				attributeMetadata.getOwnerType(),
				elementType,
				attributeMetadata.getJavaType(),
				null
		)
				.member( attributeMetadata.getMember() )
				.property( attributeMetadata.getPropertyMapping() )
				.persistentAttributeType( attributeMetadata.getPersistentAttributeType() )
				.build();
	}

	@SuppressWarnings("unchecked")
	private <Y> Type<Y> getMetaModelType(ValueContext typeContext) {
		return null;
	}



	/**
	 * A contract for defining the meta information about a {@link Value}
	 */
	private interface ValueContext {
		/**
		 * Enum of the simplified types a value might be.  These relate more to the Hibernate classification
		 * then the JPA classification
		 */
		enum ValueClassification {
			EMBEDDABLE,
			ENTITY,
			BASIC
		}

		/**
		 * Retrieve the value itself
		 *
		 * @return The value
		 */
		public Value getValue();

		public Class getBindableType();

		/**
		 * Retrieve the simplified value classification
		 *
		 * @return The value type
		 */
		public ValueClassification getValueClassification();

		/**
		 * Retrieve the metadata about the attribute from which this value comes
		 *
		 * @return The "containing" attribute metadata.
		 */
		public AttributeMetadata getAttributeMetadata();
	}

	/**
	 * Basic contract for describing an attribute.  The "description" is partially in terms
	 * of JPA ({@link #getPersistentAttributeType} and {@link #getOwnerType}), partially in
	 * terms of Hibernate ({@link #getPropertyMapping}) and partially just in terms of the java
	 * model itself ({@link #getName}, {@link #getMember} and {@link #getJavaType}).
	 *
	 * @param <X> The attribute owner type
	 * @param <Y> The attribute type.
	 */
	private interface AttributeMetadata<X, Y> {
		/**
		 * Retrieve the name of the attribute
		 *
		 * @return The attribute name
		 */
		public String getName();

		/**
		 * Retrieve the member defining the attribute
		 *
		 * @return The attribute member
		 */
		public Member getMember();

		/**
		 * Retrieve the attribute java type.
		 *
		 * @return The java type of the attribute.
		 */
		public Class<Y> getJavaType();

		/**
		 * Get the JPA attribute type classification for this attribute.
		 *
		 * @return The JPA attribute type classification
		 */
		public Attribute.PersistentAttributeType getPersistentAttributeType();

		/**
		 * Retrieve the attribute owner's metamodel information
		 *
		 * @return The metamodel information for the attribute owner
		 */
		public AbstractManagedType<X> getOwnerType();

		/**
		 * Retrieve the Hibernate property mapping related to this attribute.
		 *
		 * @return The Hibernate property mapping
		 */
		public Property getPropertyMapping();

		/**
		 * Is the attribute plural (a collection)?
		 *
		 * @return True if it is plural, false otherwise.
		 */
		public boolean isPlural();
	}

	/**
	 * Attribute metadata contract for a non-plural attribute.
	 *
	 * @param <X> The owner type
	 * @param <Y> The attribute type
	 */
	private interface SingularAttributeMetadata<X, Y> extends AttributeMetadata<X, Y> {
		/**
		 * Retrieve the value context for this attribute
		 *
		 * @return The attributes value context
		 */
		public ValueContext getValueContext();
	}

	/**
	 * Attribute metadata contract for a plural attribute.
	 *
	 * @param <X> The owner type
	 * @param <Y> The attribute type (the collection type)
	 * @param <E> The collection element type
	 */
	@SuppressWarnings("UnusedDeclaration")
	private interface PluralAttributeMetadata<X, Y, E> extends AttributeMetadata<X, Y> {
		/**
		 * Retrieve the JPA collection type classification for this attribute
		 *
		 * @return The JPA collection type classification
		 */
		public PluralAttribute.CollectionType getAttributeCollectionType();

		/**
		 * Retrieve the value context for the collection's elements.
		 *
		 * @return The value context for the collection's elements.
		 */
		public ValueContext getElementValueContext();

		/**
		 * Retrieve the value context for the collection's keys (if a map, null otherwise).
		 *
		 * @return The value context for the collection's keys (if a map, null otherwise).
		 */
		public ValueContext getMapKeyValueContext();
	}

	/**
	 * Bundle's a Hibernate property mapping together with the JPA metamodel information
	 * of the attribute owner.
	 *
	 * @param <X> The owner type.
	 */
	private interface AttributeContext<X> {
		/**
		 * Retrieve the attribute owner.
		 *
		 * @return The owner.
		 */
		public AbstractManagedType<X> getOwnerType();

		/**
		 * Retrieve the Hibernate property mapping.
		 *
		 * @return The Hibernate property mapping.
		 */
		public Property getPropertyMapping();
	}

	/**
	 * Contract for how we resolve the {@link Member} for a give attribute context.
	 */
	private interface MemberResolver {
		public Member resolveMember(AttributeContext attributeContext);
	}

	/**
	 * Here is most of the nuts and bolts of this factory, where we interpret the known JPA metadata
	 * against the known Hibernate metadata and build a descriptor for the attribute.
	 *
	 * @param attributeContext The attribute to be described
	 * @param memberResolver Strategy for how to resolve the member defining the attribute.
	 * @param <X> The owner type
	 * @param <Y> The attribute type
	 *
	 * @return The attribute description
	 */
	@SuppressWarnings({"unchecked"})
	private <X, Y> AttributeMetadata<X, Y> determineAttributeMetadata(
			AttributeContext<X> attributeContext,
			MemberResolver memberResolver) {

		final Member member = memberResolver.resolveMember( attributeContext );


		throw new UnsupportedOperationException( "oops, we are missing something: " + attributeContext.getPropertyMapping() );
	}



	private abstract class BaseAttributeMetadata<X, Y> implements AttributeMetadata<X, Y> {
		private final Property propertyMapping;
		private final AbstractManagedType<X> ownerType;
		private final Member member;
		private final Attribute.PersistentAttributeType persistentAttributeType;

		@SuppressWarnings({"unchecked"})
		protected BaseAttributeMetadata(
				Property propertyMapping,
				AbstractManagedType<X> ownerType,
				Member member,
				Attribute.PersistentAttributeType persistentAttributeType) {
			this.propertyMapping = propertyMapping;
			this.ownerType = ownerType;
			this.member = member;
			this.persistentAttributeType = persistentAttributeType;

			final Class declaredType;

			if ( member == null ) {
				// assume we have a MAP entity-mode "class"
			}
			else if ( Field.class.isInstance( member ) ) {
				declaredType = ( (Field) member ).getType();
			}
			else if ( Method.class.isInstance( member ) ) {
				declaredType = ( (Method) member ).getReturnType();
			}
			else if ( MapMember.class.isInstance( member ) ) {
				declaredType = ( (MapMember) member ).getType();
			}
			else {
				throw new IllegalArgumentException( "Cannot determine java-type from given member [" + member + "]" );
			}
		}

		public String getName() {
			return propertyMapping.getName();
		}

		public Member getMember() {
			return member;
		}

		public String getMemberDescription() {
			return determineMemberDescription( getMember() );
		}

		public String determineMemberDescription(Member member) {
			return member.getDeclaringClass().getName() + '#' + member.getName();
		}

		public Class<Y> getJavaType() {
			return null;
		}

		public Attribute.PersistentAttributeType getPersistentAttributeType() {
			return persistentAttributeType;
		}

		public AbstractManagedType<X> getOwnerType() {
			return ownerType;
		}

		public boolean isPlural() {
			return  false;
		}

		public Property getPropertyMapping() {
			return propertyMapping;
		}
	}

	@SuppressWarnings({"unchecked"})
	protected <Y> Class<Y> accountForPrimitiveTypes(Class<Y> declaredType) {
//		if ( !declaredType.isPrimitive() ) {
//			return declaredType;
//		}
//
//		if ( Boolean.TYPE.equals( declaredType ) ) {
//			return (Class<Y>) Boolean.class;
//		}
//		if ( Character.TYPE.equals( declaredType ) ) {
//			return (Class<Y>) Character.class;
//		}
//		if( Byte.TYPE.equals( declaredType ) ) {
//			return (Class<Y>) Byte.class;
//		}
//		if ( Short.TYPE.equals( declaredType ) ) {
//			return (Class<Y>) Short.class;
//		}
//		if ( Integer.TYPE.equals( declaredType ) ) {
//			return (Class<Y>) Integer.class;
//		}
//		if ( Long.TYPE.equals( declaredType ) ) {
//			return (Class<Y>) Long.class;
//		}
//		if ( Float.TYPE.equals( declaredType ) ) {
//			return (Class<Y>) Float.class;
//		}
//		if ( Double.TYPE.equals( declaredType ) ) {
//			return (Class<Y>) Double.class;
//		}
//
//		throw new IllegalArgumentException( "Unexpected type [" + declaredType + "]" );
		// if the field is defined as int, return int not Integer...
		return declaredType;
	}

	private class SingularAttributeMetadataImpl<X, Y>
			extends BaseAttributeMetadata<X, Y>
			implements SingularAttributeMetadata<X, Y> {
		private final ValueContext valueContext;

		private SingularAttributeMetadataImpl(
				Property propertyMapping,
				AbstractManagedType<X> ownerType,
				Member member,
				Attribute.PersistentAttributeType persistentAttributeType) {
			super( propertyMapping, ownerType, member, persistentAttributeType );
			valueContext = new ValueContext() {
				public Value getValue() {
					return null;
				}

				public Class getBindableType() {
					return getAttributeMetadata().getJavaType();
				}

				public ValueClassification getValueClassification() {
					switch ( getPersistentAttributeType() ) {
						case EMBEDDED: {
							return ValueClassification.EMBEDDABLE;
						}
						case BASIC: {
							return ValueClassification.BASIC;
						}
						default: {
							return ValueClassification.ENTITY;
						}
					}
				}

				public AttributeMetadata getAttributeMetadata() {
					return SingularAttributeMetadataImpl.this;
				}
			};
		}

		public ValueContext getValueContext() {
			return valueContext;
		}
	}



	public static ParameterizedType getSignatureType(Member member) {
		final java.lang.reflect.Type type;
		if ( Field.class.isInstance( member ) ) {
			type = ( (Field) member ).getGenericType();
		}
		else if ( Method.class.isInstance( member ) ) {
			type = ( (Method) member ).getGenericReturnType();
		}
		else {
			type = ( (MapMember) member ).getType();
		}
		//this is a raw type
		if ( type instanceof Class ) {
			return null;
		}
		return (ParameterizedType) type;
	}

	public static PluralAttribute.CollectionType determineCollectionType(Class javaType) {
		if ( java.util.List.class.isAssignableFrom( javaType ) ) {
			return PluralAttribute.CollectionType.LIST;
		}
		else if ( java.util.Set.class.isAssignableFrom( javaType ) ) {
			return PluralAttribute.CollectionType.SET;
		}
		else if ( java.util.Map.class.isAssignableFrom( javaType ) ) {
			return PluralAttribute.CollectionType.MAP;
		}
		else if ( java.util.Collection.class.isAssignableFrom( javaType ) ) {
			return PluralAttribute.CollectionType.COLLECTION;
		}
		else if ( javaType.isArray() ) {
			return PluralAttribute.CollectionType.LIST;
		}
		else {
			throw new IllegalArgumentException( "Expecting collection type [" + javaType.getName() + "]" );
		}
	}


	private final MemberResolver embeddedMemberResolver = null;


	private final MemberResolver virtualIdentifierMemberResolver = null;

	/**
	 * A {@link Member} resolver for normal attributes.
	 */
	private final MemberResolver normalMemberResolver = null;

	private final MemberResolver identifierMemberResolver = null;

	private final MemberResolver versionMemberResolver = null;
}
