package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnInitalItem;
import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnRowTransBean;
import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnTransValue;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.stable.StringUtils;
import com.fr.swift.source.etl.columnrowtrans.ColumnRowTransOperator;
import com.fr.swift.structure.Pair;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class ColumnRowTransAdaptor {
    public static ColumnRowTransOperator fromColumnRowTransBean(ColumnRowTransBean bean, FineBusinessTable table) {
        ColumnTransValue value = bean.getValue();
        String groupName = BusinessTableUtils.getFieldNameByFieldId(value.getAccordingField());
        String lcName = BusinessTableUtils.getFieldNameByFieldId(value.getFieldId());
        List<Pair<String, String>> lcValue = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < value.getValues().size(); i++) {
            ColumnInitalItem item = value.getValues().get(i);
            if (item.isSelected()) {
                lcValue.add(Pair.of(item.getOldValue(), StringUtils.isEmpty(item.getNewValue()) ? item.getOldValue() : item.getNewValue()));
            }
        }
        List<Pair<String, String>> columns = new ArrayList<Pair<String, String>>();
        List<Pair<String, String>> otherColumnNames = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < value.getInitialFields().size(); i++) {
            ColumnInitalItem item = value.getInitialFields().get(i);
            if (item.isSelected()) {
                columns.add(Pair.of(item.getOldValue(), StringUtils.isEmpty(item.getNewValue()) ? item.getOldValue() : item.getNewValue()));
            } else if(!isGroupOrLcName(lcName, groupName, item.getOldValue())){
                otherColumnNames.add(Pair.of(item.getOldValue(), StringUtils.isEmpty(item.getNewValue()) ? item.getOldValue() : item.getNewValue()));
            }
        }
        return new ColumnRowTransOperator(groupName, lcName, lcValue, columns, otherColumnNames);
    }
    private static boolean isGroupOrLcName(String lcnName, String groupName, String columnName) {
        return columnName.equals(lcnName) || columnName.equals(groupName);

    }
}