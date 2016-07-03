
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
            AbstractManagedType<X> ownerType,
            boolean isIdentifier,
            boolean isVersion,
            boolean isOptional) {
        super(name, attributeType, ownerType);
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
                AbstractManagedType<X> ownerType,
                AttributeType<Y> attributeType) {
            super(name, attributeType, ownerType, true, false, false);
        }
    }

    /**
     * Subclass used to simply instantiation of singular attributes representing an entity's
     * version.
     */
    public static class Version<X, Y> extends SingularAttributeImpl<X, Y> {
        public Version(
                String name,
                AbstractManagedType<X> ownerType,
                AttributeType<Y> attributeType) {
            super(name, attributeType, ownerType, false, true, false);
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
