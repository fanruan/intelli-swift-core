package com.finebi.datasource.api.metamodel;

import com.finebi.datasource.api.metamodel.Type;

/**
 * This class created on 2016/6/29.
 *
 * @author Connery
 * @since 4.0
 */
public interface AttributeType<X> {
    enum InnerType {
        Boolean(java.lang.Boolean.class),

        Date(java.sql.Date.class),

        Double(java.lang.Double.class),

        Float(java.lang.Float.class),

        Integer(java.lang.Integer.class),

        Long(java.lang.Long.class),

        String(java.lang.String.class),

        Timestamp(java.sql.Timestamp.class),

        Time(java.sql.Time.class);

        private Class attributeJavaType;

        InnerType(Class attributeJavaType) {
            this.attributeJavaType = attributeJavaType;
        }

        public Class getValueType() {
            return this.attributeJavaType;
        }


    }

    /**
     * Return the represented Java type.
     *
     * @return Java type
     */
    Class<X> getJavaType();


    /**
     *  Return the persistence type.
     *  @return persistence type
     */
    Type.PersistenceType getPersistenceType();
}
