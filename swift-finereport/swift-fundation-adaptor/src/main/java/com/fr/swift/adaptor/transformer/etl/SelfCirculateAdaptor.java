package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.internalimp.analysis.bean.operator.circulate.CirculateOneFieldBean;
import com.finebi.conf.internalimp.analysis.bean.operator.circulate.CirculateTwoFieldValue;
import com.finebi.conf.internalimp.analysis.operator.circulate.FloorItem;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.conf.base.EngineComplexConfTable;
import com.fr.general.ComparatorUtils;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.etl.selfrelation.OneUnionRelationOperator;
import com.fr.swift.source.etl.selfrelation.TwoUnionRelationOperator;

import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class SelfCirculateAdaptor {
    private static int findFieldName(List<FineBusinessField> fields, String fieldID) {
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < fields.size(); i++) {
            if (ComparatorUtils.equals(fields.get(i).getId(), fieldID)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static OneUnionRelationOperator fromOneUnionRelationBean(CirculateOneFieldBean bean, FineBusinessTable table) {
        FineBusinessTable preTable = ((EngineComplexConfTable) table).getBaseTableBySelected(0);
        List<FineBusinessField> fields = preTable.getFields();
        CirculateTwoFieldValue value = bean.getValue();
        String idField = fields.get(findFieldName(fields, value.getIdField())).getName();
        LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < value.getFloors().size(); i++) {
            FloorItem item = value.getFloors().get(i);
            String tempName = item.getName();
            try {
                tempName = fields.get(findFieldName(fields, item.getName())).getName();
            } catch (ArrayIndexOutOfBoundsException ignore) {
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
            columns.put(tempName, item.getLength());
        }
        List<String> showFields = new ArrayList<String>();
        for (int i = 0; i < value.getShowFields().size(); i++) {
            String tempName = fields.get(findFieldName(fields, value.getShowFields().get(i))).getName();
            showFields.add(tempName);
        }
        return new OneUnionRelationOperator(idField, showFields, columns, value.getFieldType(), null);
    }

    public static TwoUnionRelationOperator fromTwoUnionRelationBean(CirculateOneFieldBean bean, FineBusinessTable table) {
        CirculateTwoFieldValue value = bean.getValue();
        FineBusinessTable preTable = ((EngineComplexConfTable) table).getBaseTableBySelected(0);
        List<FineBusinessField> fields = preTable.getFields();
        String idFieldName = fields.get(findFieldName(fields, value.getIdField())).getName();
        String parentIdFieldName = fields.get(findFieldName(fields, value.getParentIdField())).getName();
        LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < value.getFloors().size(); i++) {
            FloorItem item = value.getFloors().get(i);
            String tempName = item.getName();
            try {
                tempName = fields.get(findFieldName(fields, item.getName())).getName();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
            columns.put(tempName, item.getLength());
        }
        List<String> showFields = new ArrayList<String>();
        for (int i = 0; i < value.getShowFields().size(); i++) {
            String tempName = fields.get(findFieldName(fields, value.getShowFields().get(i))).getName();
            showFields.add(tempName);
        }
        return new TwoUnionRelationOperator(idFieldName, showFields, columns, Types.VARCHAR, null, parentIdFieldName);
    }
}