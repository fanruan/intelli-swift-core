
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Defines commonality for the JPA {@link ManagedType} hierarchy of interfaces.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractManagedType<X>
        extends AbstractType<X>
        implements ManagedType<X>, Serializable {

    private final AbstractManagedType<? super X> superType;

    private final Map<String, Attribute<X, ?>> declaredAttributes
            = new HashMap<String, Attribute<X, ?>>();
    private final Map<String, SingularAttribute<X, ?>> declaredSingularAttributes
            = new HashMap<String, SingularAttribute<X, ?>>();


    protected AbstractManagedType(Class<X> javaType, String typeName, AbstractManagedType<? super X> superType) {
        super(javaType, typeName);
        this.superType = superType;
    }

    protected AbstractManagedType<? super X> getSupertype() {
        return superType;
    }

    private boolean locked;

    public Builder<X> getBuilder() {
        if (locked) {
            throw new IllegalStateException("Type has been locked");
        }
        return new Builder<X>() {
            @Override
            @SuppressWarnings("unchecked")
            public void addAttribute(Attribute<X, ?> attribute) {
                declaredAttributes.put(attribute.getName(), attribute);
                final Bindable.BindableType bindableType = ((Bindable) attribute).getBindableType();
                switch (bindableType) {
                    case SINGULAR_ATTRIBUTE: {
                        declaredSingularAttributes.put(attribute.getName(), (SingularAttribute<X, ?>) attribute);
                        break;
                    }
                    case PLURAL_ATTRIBUTE: {
                        break;
                    }
                    default: {
                        throw new RuntimeException("unknown bindable type: " + bindableType);
                    }
                }
            }
        };
    }

    public void lock() {
        locked = true;
    }

    public static interface Builder<X> {
        public void addAttribute(Attribute<X, ?> attribute);
    }


    @Override
    @SuppressWarnings({"unchecked"})
    public Set<Attribute<? super X, ?>> getAttributes() {
        HashSet attributes = new HashSet<Attribute<X, ?>>(declaredAttributes.values());
        if (getSupertype() != null) {
            attributes.addAll(getSupertype().getAttributes());
        }
        return attributes;
    }

    @Override
    public Set<Attribute<X, ?>> getDeclaredAttributes() {
        return new HashSet<Attribute<X, ?>>(declaredAttributes.values());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Attribute<? super X, ?> getAttribute(String name) {
        Attribute<? super X, ?> attribute = declaredAttributes.get(name);
        if (attribute == null && getSupertype() != null) {
            attribute = getSupertype().getAttribute(name);
        }
        checkNotNull("Attribute ", attribute, name);
        return attribute;
    }

    @Override
    public Attribute<X, ?> getDeclaredAttribute(String name) {
        Attribute<X, ?> attr = declaredAttributes.get(name);
        checkNotNull("Attribute ", attr, name);
        return attr;
    }

    private void checkNotNull(String attributeType, Attribute<?, ?> attribute, String name) {

        if (attribute == null) {
            throw new IllegalArgumentException(
                    String.format(
                            "Unable to locate %s with the the given name [%s] on this ManagedType [%s]",
                            attributeType,
                            name,
                            getTypeName()
                    )
            );
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
        HashSet attributes = new HashSet<SingularAttribute<X, ?>>(declaredSingularAttributes.values());
        if (getSupertype() != null) {
            attributes.addAll(getSupertype().getSingularAttributes());
        }
        return attributes;
    }

    @Override
    public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
        return new HashSet<SingularAttribute<X, ?>>(declaredSingularAttributes.values());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
        SingularAttribute<? super X, ?> attribute = declaredSingularAttributes.get(name);
        if (attribute == null && getSupertype() != null) {
            attribute = getSupertype().getSingularAttribute(name);
        }
        checkNotNull("SingularAttribute ", attribute, name);
        return attribute;
    }

    @Override
    public SingularAttribute<X, ?> getDeclaredSingularAttribute(String name) {
        final SingularAttribute<X, ?> attr = declaredSingularAttributes.get(name);
        checkNotNull("SingularAttribute ", attr, name);
        return attr;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
        SingularAttribute<? super X, ?> attribute = declaredSingularAttributes.get(name);
        if (attribute == null && getSupertype() != null) {
            attribute = getSupertype().getSingularAttribute(name);
        }
        checkTypeForSingleAttribute("SingularAttribute ", attribute, name, type);
        return (SingularAttribute<? super X, Y>) attribute;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> javaType) {
        final SingularAttribute<X, ?> attr = declaredSingularAttributes.get(name);
        checkTypeForSingleAttribute("SingularAttribute ", attr, name, javaType);
        return (SingularAttribute<X, Y>) attr;
    }

    private <Y> void checkTypeForSingleAttribute(
            String attributeType,
            SingularAttribute<?, ?> attribute,
            String name,
            Class<Y> javaType) {
        if (attribute == null || (javaType != null && !attribute.getBindableJavaType().equals(javaType))) {
            if (isPrimitiveVariant(attribute, javaType)) {
                return;
            }
            throw new IllegalArgumentException(
                    attributeType + " named " + name
                            + (javaType != null ? " and of type " + javaType.getName() : "")
                            + " is not present"
            );
        }
    }

    @SuppressWarnings({"SimplifiableIfStatement"})
    protected <Y> boolean isPrimitiveVariant(SingularAttribute<?, ?> attribute, Class<Y> javaType) {
        if (attribute == null) {
            return false;
        }
        Class declaredType = attribute.getBindableJavaType();

        if (declaredType.isPrimitive()) {
            return (Boolean.class.equals(javaType) && Boolean.TYPE.equals(declaredType))
                    || (Character.class.equals(javaType) && Character.TYPE.equals(declaredType))
                    || (Byte.class.equals(javaType) && Byte.TYPE.equals(declaredType))
                    || (Short.class.equals(javaType) && Short.TYPE.equals(declaredType))
                    || (Integer.class.equals(javaType) && Integer.TYPE.equals(declaredType))
                    || (Long.class.equals(javaType) && Long.TYPE.equals(declaredType))
                    || (Float.class.equals(javaType) && Float.TYPE.equals(declaredType))
                    || (Double.class.equals(javaType) && Double.TYPE.equals(declaredType));
        }

        if (javaType.isPrimitive()) {
            return (Boolean.class.equals(declaredType) && Boolean.TYPE.equals(javaType))
                    || (Character.class.equals(declaredType) && Character.TYPE.equals(javaType))
                    || (Byte.class.equals(declaredType) && Byte.TYPE.equals(javaType))
                    || (Short.class.equals(declaredType) && Short.TYPE.equals(javaType))
                    || (Integer.class.equals(declaredType) && Integer.TYPE.equals(javaType))
                    || (Long.class.equals(declaredType) && Long.TYPE.equals(javaType))
                    || (Float.class.equals(declaredType) && Float.TYPE.equals(javaType))
                    || (Double.class.equals(declaredType) && Double.TYPE.equals(javaType));
        }

        return false;
    }


}
