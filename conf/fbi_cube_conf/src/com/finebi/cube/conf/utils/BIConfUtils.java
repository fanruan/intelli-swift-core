package com.finebi.cube.conf.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class BIConfUtils {


    public static boolean isSameRelation(BITableSourceRelation one, BITableSourceRelation other, long userId) {
        if (one == null || other == null) {
            return false;
        }
        if (one.hashCode() == other.hashCode()) {
            return true;
        }
        return (ComparatorUtils.equals(one.getForeignKey().getFieldName(), other.getForeignKey().getFieldName()) && ComparatorUtils.equals(one.getPrimaryKey().getFieldName(), other.getPrimaryKey().getFieldName()));

    }

    public static List<BusinessField> parseField(JSONArray fieldsJA, BusinessTable table) {
        List<BusinessField> fields = new ArrayList<BusinessField>();
        for (int i = 0; i < fieldsJA.length(); i++) {
            try {
                JSONArray ja = fieldsJA.getJSONArray(i);
                for (int j = 0; j < ja.length(); j++) {
                    JSONObject fieldJO = ja.getJSONObject(j);
                    String field_name = null;
                    int fieldSize = 0;
                    int classType = 0;
                    if (fieldJO.has("field_name")) {
                        field_name = fieldJO.getString("field_name");
                    }
                    if (fieldJO.has("field_type")) {
                        int fieldType = fieldJO.getInt("field_type");
                        switch (fieldType) {
                            case DBConstant.COLUMN.STRING:
                                classType = DBConstant.CLASS.STRING;
                                break;
                            case DBConstant.COLUMN.NUMBER:
                                classType = DBConstant.CLASS.DOUBLE;
                                break;
                            case DBConstant.COLUMN.DATE:
                                classType = DBConstant.CLASS.DATE;
                                break;
                            default:
                                classType = DBConstant.CLASS.STRING;
                                break;
                        }
                    }
                    if (fieldJO.has("class_type")) {
                        classType = fieldJO.getInt("class_type");
                    }
                    if (fieldJO.has("field_size")) {
                        fieldSize = fieldJO.getInt("field_size");
                    }
                    BIBusinessField field = new BIBusinessField(table, new BIFieldID(fieldJO.getString("id")),
                            field_name, classType,
                            fieldSize, fieldJO.optBoolean("is_usable"), fieldJO.optBoolean("is_enable"));
                    fields.add(field);
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        return fields;
    }

}