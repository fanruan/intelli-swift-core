
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.SingularAttribute;
import com.finebi.datasource.api.metamodel.Type;
import com.finebi.datasource.sql.criteria.AttributeType;

import java.io.Serializable;

/**
 * @author Emmanuel Bernard
 * @author Steve Ebersole
 */
public class SingularAttributeImpl<X, Y>
        extends AbstractAttribute<X, Y>
        implements SingularAttribute<X, Y>, Serializable {
    private final boolean isIdentifier;
    private final boolean isVersion;
    private final boolean isOptional;
    private final AttributeType<Y> attributeType;

    public SingularAttributeImpl(
            String name,
            AttributeType<Y> attributeType,
            AbstractManagedType<X> declaringType,
            boolean isIdentifier,
            boolean isVersion,
            boolean isOptional,
            PersistentAttributeType persistentAttributeType) {
        super(name, attributeType, declaringType, persistentAttributeType);
        this.isIdentifier = isIdentifier;
        this.isVersion = isVersion;
        this.isOptional = isOptional;
        this.attributeType = attributeType;
    }

    /**
     * Subclass used to simply instantiation of singular attributes representing an entity's
     * identifier.
     */
    public static class Identifier<X, Y> extends SingularAttributeImpl<X, Y> {
        public Identifier(
                String name,
                AttributeType<Y> attributeType,
                AbstractManagedType<X> declaringType,
                PersistentAttributeType persistentAttributeType) {
            super(name, attributeType, declaringType, true, false, false, persistentAttributeType);
        }
    }

    /**
     * Subclass used to simply instantiation of singular attributes representing an entity's
     * version.
     */
    public static class Version<X, Y> extends SingularAttributeImpl<X, Y> {
        public Version(
                String name,
                AttributeType<Y> attributeType,
                AbstractManagedType<X> declaringType,
                PersistentAttributeType persistentAttributeType) {
            super(name, attributeType, declaringType, false, true, false, persistentAttributeType);
        }
    }

    @Override
    public boolean isId() {
        return isIdentifier;
    }

    @Override
    public boolean isVersion() {
        return isVersion;
    }

    @Override
    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public Type<Y> getType() {
        return null;
    }

    @Override
    public boolean isAssociation() {
        return getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE
                || getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public BindableType getBindableType() {
        return BindableType.SINGULAR_ATTRIBUTE;
    }

    @Override
    public Class<Y> getBindableJavaType() {
        return attributeType.getJavaType();
    }
}
