package com.finebi.datasource.sql.criteria;

import com.finebi.datasource.api.metamodel.AttributeType;
import com.finebi.datasource.api.metamodel.Type;

/**
 * This class created on 2016/7/3.
 *
 * @author Connery
 * @since 4.0
 */
public class AttributeTypeImpl<X> implements AttributeType<X> {
    private Class<X> javaType;
    private InnerType innerType;

    public AttributeTypeImpl(Class<X> javaType) {
        this.javaType = javaType;
    }

    public AttributeTypeImpl(InnerType innerType) {
        this.javaType = innerType.getValueType();
        this.innerType = innerType;
    }



    @Override
    public Class<X> getJavaType() {
        return javaType;
    }

    @Override
    public Type.PersistenceType getPersistenceType() {
        return Type.PersistenceType.BASIC;
    }
}
