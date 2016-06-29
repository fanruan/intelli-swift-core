package com.finebi.datasource.sql.criteria;

/**
 * This class created on 2016/6/29.
 *
 * @author Connery
 * @since 4.0
 */
public enum JavaPrimitiveType {

    Boolean(java.lang.Boolean.class),

    Date(java.sql.Date.class),

    Double(java.lang.Double.class),

    Float(java.lang.Float.class),

    Integer(java.lang.Integer.class),

    Long(java.lang.Long.class),

    String(java.lang.String.class),

    Timestamp(java.sql.Timestamp.class),

    Time(java.sql.Time.class);

    private Class valueClass;

    JavaPrimitiveType(Class valueClass) {
        this.valueClass = valueClass;
    }

    public Class getValueType() {
        return this.valueClass;
    }


}
