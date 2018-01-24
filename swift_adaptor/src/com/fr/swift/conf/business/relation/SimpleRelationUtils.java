package com.fr.swift.conf.business.relation;

import com.finebi.conf.internalimp.relation.FineBusinessTableRelationIml;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.general.ComparatorUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-24 11:31:19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SimpleRelationUtils {

    public static SimpleBusinessRelation transformRelation(FineBusinessTableRelation fineRelation) {
        String relationName = fineRelation.getRelationName();
        List<FineBusinessField> primaryFieldList = fineRelation.getPrimaryBusinessField();
        List<FineBusinessField> foreignFieldList = fineRelation.getForeignBusinessField();
        FineBusinessTable primaryTable = fineRelation.getPrimaryBusinessTable();
        FineBusinessTable foreignTable = fineRelation.getForeignBusinessTable();
        SimpleBusinessRelation simpleBusinessRelation = new SimpleBusinessRelation(relationName, transformFields(primaryFieldList),
                transformFields(foreignFieldList), primaryTable.getId(), foreignTable.getId(), fineRelation.getRelationType());
        return simpleBusinessRelation;
    }

    public static List<String> transformFields(List<FineBusinessField> fieldList) {
        List<String> fieldIds = new ArrayList<String>();
        for (FineBusinessField field : fieldList) {
            fieldIds.add(field.getId());
        }
        return fieldIds;
    }

    public static List<FineBusinessTableRelation> transforSimpleRelarions(Map<String, Map> map, List<FineBusinessTable> businessTableList) throws IOException {
        List<FineBusinessTableRelation> result = new ArrayList<FineBusinessTableRelation>();
        for (Map.Entry<String, Map> entry : map.entrySet()) {
            SimpleBusinessRelation simpleBusinessRelation = new SimpleBusinessRelation(entry.getValue());
            FineBusinessTable primaryTable = null;
            FineBusinessTable foreignTable = null;
            for (FineBusinessTable fineBusinessTable : businessTableList) {
                if (ComparatorUtils.equals(fineBusinessTable.getId(), simpleBusinessRelation.getPrimaryTable())) {
                    primaryTable = fineBusinessTable;
                }
                if (ComparatorUtils.equals(fineBusinessTable.getId(), simpleBusinessRelation.getForeignTable())) {
                    foreignTable = fineBusinessTable;
                }
            }

            List<FineBusinessField> primaryFields = new ArrayList<FineBusinessField>();
            List<FineBusinessField> foreignFields = new ArrayList<FineBusinessField>();

            for (String fieldId : simpleBusinessRelation.getPrimaryFields()) {
                if (primaryTable.getFieldByFieldId(fieldId) != null) {
                    primaryFields.add(primaryTable.getFieldByFieldId(fieldId));
                }
            }
            for (String fieldId : simpleBusinessRelation.getForeignFields()) {
                if (foreignTable.getFieldByFieldId(fieldId) != null) {
                    foreignFields.add(foreignTable.getFieldByFieldId(fieldId));
                }
            }
            FineBusinessTableRelation fineBusinessTableRelation = new FineBusinessTableRelationIml(primaryFields, foreignFields, primaryTable, foreignTable, simpleBusinessRelation.getRelationType());
            result.add(fineBusinessTableRelation);
        }
        return result;
    }
}
