
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.api.metamodel.Type;
import com.finebi.datasource.api.metamodel.AttributeType;

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
     * @param property  The Hibernate property descriptor for the attribute
     * @param <X>       The type of the owner
     * @param <Y>       The attribute type
     * @return The built attribute descriptor or null if the attribute is not part of the JPA 2 model (eg backrefs)
     */
    @SuppressWarnings({"unchecked"})
    public <X, Y> AttributeImplementor<X, Y> buildAttribute(AbstractManagedType<X> ownerType, AttributeProperty property) {

        final AttributeContext<X> attributeContext = wrap(ownerType, property);
        final AttributeMetadata<X, Y> attributeMetadata =
                determineAttributeMetadata(attributeContext, normalMemberResolver);
        if (attributeMetadata == null) {
            return null;
        }
        final SingularAttributeMetadata<X, Y> singularAttributeMetadata = (SingularAttributeMetadata<X, Y>) attributeMetadata;
        final Type<Y> metaModelType = getMetaModelType(singularAttributeMetadata.getValueContext());
        return new SingularAttributeImpl<X, Y>(
                attributeMetadata.getName(),
                attributeMetadata.getPropertyMapping().getAttributeType(),
                ownerType,
                false,
                false,
                property.isOptional()
        );
    }

    private <X> AttributeContext<X> wrap(final AbstractManagedType<X> ownerType, final AttributeProperty property) {
        return new AttributeContext<X>() {
            public AbstractManagedType<X> getOwnerType() {
                return ownerType;
            }

            public AttributeProperty getPropertyMapping() {
                return property;
            }
        };
    }

    /**
     * Build the identifier attribute descriptor
     *
     * @param ownerType The descriptor of the attribute owner (aka declarer).
     * @param property  The Hibernate property descriptor for the identifier attribute
     * @param <X>       The type of the owner
     * @param <Y>       The attribute type
     * @return The built attribute descriptor
     */
    @SuppressWarnings({"unchecked"})
    public <X, Y> SingularAttributeImpl<X, Y> buildIdAttribute(
            AbstractIdentifiableType<X> ownerType,
            AttributeProperty property) {
        final AttributeContext<X> attributeContext = wrap(ownerType, property);
        final SingularAttributeMetadata<X, Y> attributeMetadata =
                (SingularAttributeMetadata<X, Y>) determineAttributeMetadata(
                        attributeContext,
                        identifierMemberResolver
                );
        final Type<Y> metaModelType = getMetaModelType(attributeMetadata.getValueContext());
        return new SingularAttributeImpl.Identifier(
                property.getName(),
                ownerType,
                attributeMetadata.getPropertyMapping().getAttributeType()
        );
    }

    /**
     * Build the version attribute descriptor
     *
     * @param ownerType The descriptor of the attribute owner (aka declarer).
     * @param property  The Hibernate property descriptor for the version attribute
     * @param <X>       The type of the owner
     * @param <Y>       The attribute type
     * @return The built attribute descriptor
     */
    @SuppressWarnings({"unchecked"})
    public <X, Y> SingularAttributeImpl<X, Y> buildVersionAttribute(
            AbstractIdentifiableType<X> ownerType,
            AttributeProperty property) {
        final AttributeContext<X> attributeContext = wrap(ownerType, property);
        final SingularAttributeMetadata<X, Y> attributeMetadata =
                (SingularAttributeMetadata<X, Y>) determineAttributeMetadata(attributeContext, versionMemberResolver);
        final Type<Y> metaModelType = getMetaModelType(attributeMetadata.getValueContext());
        return new SingularAttributeImpl.Version(
                property.getName(),
                ownerType,
                attributeMetadata.getPropertyMapping().getAttributeType()
        );
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
     * of JPA ({@link #} and {@link #getOwnerType}), partially in
     * terms of Hibernate ({@link #getPropertyMapping}) and partially just in terms of the java
     * model itself ({@link #getName}, {@link #getMember} and {@link #}).
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
        public AttributeProperty getPropertyMapping();

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
        public AttributeProperty getPropertyMapping();
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
     * @param memberResolver   Strategy for how to resolve the member defining the attribute.
     * @param <X>              The owner type
     * @param <Y>              The attribute type
     * @return The attribute description
     */
    @SuppressWarnings({"unchecked"})
    private <X, Y> AttributeMetadata<X, Y> determineAttributeMetadata(
            AttributeContext<X> attributeContext,
            MemberResolver memberResolver) {

        return new SingularAttributeMetadataImpl<X, Y>(
                attributeContext.getPropertyMapping(),
                attributeContext.getOwnerType(),
                null
        );
    }


    private abstract class BaseAttributeMetadata<X, Y> implements AttributeMetadata<X, Y> {
        private final AttributeProperty propertyMapping;
        private final AbstractManagedType<X> ownerType;
        private final Member member;
        private final AttributeType<Y> attributeType;

        @SuppressWarnings({"unchecked"})
        protected BaseAttributeMetadata(
                AttributeProperty propertyMapping,
                AbstractManagedType<X> ownerType,
                Member member) {
            this.propertyMapping = propertyMapping;
            this.ownerType = ownerType;
            this.member = member;
            this.attributeType = propertyMapping.getAttributeType();

            final Class declaredType;

            if (member == null) {
                // assume we have a MAP entity-mode "class"
            } else if (Field.class.isInstance(member)) {
                declaredType = ((Field) member).getType();
            } else if (Method.class.isInstance(member)) {
                declaredType = ((Method) member).getReturnType();
            } else if (MapMember.class.isInstance(member)) {
                declaredType = ((MapMember) member).getType();
            } else {
                throw new IllegalArgumentException("Cannot determine java-type from given member [" + member + "]");
            }
        }

        public String getName() {
            return propertyMapping.getName();
        }

        public Member getMember() {
            return member;
        }

        public String getMemberDescription() {
            return determineMemberDescription(getMember());
        }

        public String determineMemberDescription(Member member) {
            return member.getDeclaringClass().getName() + '#' + member.getName();
        }

        public Class<Y> getJavaType() {
            return attributeType.getJavaType();
        }


        public AbstractManagedType<X> getOwnerType() {
            return ownerType;
        }

        public boolean isPlural() {
            return false;
        }

        public AttributeProperty getPropertyMapping() {
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
                AttributeProperty propertyMapping,
                AbstractManagedType<X> ownerType,
                Member member) {
            super(propertyMapping, ownerType, member);
            valueContext = new ValueContext() {
                public Value getValue() {
                    return null;
                }

                public Class getBindableType() {
                    return getAttributeMetadata().getJavaType();
                }

                public ValueClassification getValueClassification() {
                    return ValueClassification.ENTITY;
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
        if (Field.class.isInstance(member)) {
            type = ((Field) member).getGenericType();
        } else if (Method.class.isInstance(member)) {
            type = ((Method) member).getGenericReturnType();
        } else {
            type = ((MapMember) member).getType();
        }
        //this is a raw type
        if (type instanceof Class) {
            return null;
        }
        return (ParameterizedType) type;
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
