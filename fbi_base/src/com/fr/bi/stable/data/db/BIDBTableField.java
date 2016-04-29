package com.fr.bi.stable.data.db;


import com.fr.general.ComparatorUtils;

/**
 * Created by Young's on 2015/11/19.
 */
public class BIDBTableField  {
    private String tableName;
    private String schema;
    private String fieldName;


    public BIDBTableField(String tableName, String schema, String fieldName) {
        this.tableName = tableName;
        this.schema = schema;
        this.fieldName = fieldName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSchema() {
        return schema;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (ComparatorUtils.equals(this, o)){
            return true;
        }
        if (o == null || !ComparatorUtils.equals(getClass(), o.getClass())) {
            return false;
        }

        BIDBTableField that = (BIDBTableField) o;
        if(ComparatorUtils.equals(fieldName, null) ?
                !ComparatorUtils.equals(that.fieldName, null) : !ComparatorUtils.equals(fieldName, that.fieldName)){
            return false;
        }
        if(ComparatorUtils.equals(schema, null) ?
                !ComparatorUtils.equals(that.schema, null) : !ComparatorUtils.equals(schema, that.schema)){
            return false;
        }
        if(ComparatorUtils.equals(tableName, null) ?
                !ComparatorUtils.equals(that.tableName, null) : !ComparatorUtils.equals(tableName, that.tableName)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = tableName != null ? tableName.hashCode() : 0;
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
        return result;
    }
}