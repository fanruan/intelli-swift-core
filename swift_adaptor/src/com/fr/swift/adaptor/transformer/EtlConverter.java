package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.analysis.bean.operator.filter.FilterOperatorBean;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinBean;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinBeanValue;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinNameItem;
import com.finebi.conf.internalimp.analysis.bean.operator.select.SelectFieldBean;
import com.finebi.conf.internalimp.analysis.bean.operator.select.SelectFieldBeanItem;
import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnInitalItem;
import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnRowTransBean;
import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnTransValue;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBean;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBeanValue;
import com.finebi.conf.internalimp.analysis.operator.select.SelectFieldOperator;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.utils.FineTableUtils;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ETLDataSource;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.columnfilter.ColumnFilterOperator;
import com.fr.swift.source.etl.columnrowtrans.ColumnRowTransOperator;
import com.fr.swift.source.etl.columnrowtrans.NameText;
import com.fr.swift.source.etl.detail.DetailOperator;
import com.fr.swift.source.etl.join.JoinColumn;
import com.fr.swift.source.etl.join.JoinOperator;
import com.fr.swift.source.etl.union.UnionOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Handsome on 2018/1/30 0030 16:38
 */
class EtlConverter {
    public static ETLDataSource transformEtlDataSource(FineBusinessTable table) throws Exception {
        FineAnalysisTable analysis = ((FineAnalysisTable) table);
        List<DataSource> dataSources = new ArrayList<DataSource>();
        FineBusinessTable baseTable = analysis.getBaseTable();
        if (baseTable != null) {
            dataSources.add(IndexingDataSourceFactory.transformDataSource(baseTable));
        } else {
            assert (analysis.getOperator().getType() == ConfConstant.AnalysisType.SELECT_FIELD);

            LinkedHashMap<String, List<ColumnKey>> sourceKeyColumnMap = new LinkedHashMap<String, List<ColumnKey>>();
            LinkedHashMap<String, DataSource> sourceKeyDataSourceMap = new LinkedHashMap<String, DataSource>();
            SelectFieldOperator selectFieldOperator = analysis.getOperator();
            List<SelectFieldBeanItem> selectFieldBeanItemList = selectFieldOperator.getValue().getValue();
            for (SelectFieldBeanItem selectFieldBeanItem : selectFieldBeanItemList) {
                FineBusinessTable fineBusinessTable = FineTableUtils.getTableByFieldId(selectFieldBeanItem.getField());
                FineBusinessField fineBusinessField = fineBusinessTable.getFieldByFieldId(selectFieldBeanItem.getField());
                DataSource baseDataSource = IndexingDataSourceFactory.transformDataSource(fineBusinessTable);

                if (sourceKeyColumnMap.containsKey(baseDataSource.getSourceKey().getId())) {
                    sourceKeyColumnMap.get(baseDataSource.getSourceKey().getId()).add(new ColumnKey(fineBusinessField.getName()));
                } else {
                    sourceKeyColumnMap.put(baseDataSource.getSourceKey().getId(), new ArrayList<ColumnKey>());
                    sourceKeyColumnMap.get(baseDataSource.getSourceKey().getId()).add(new ColumnKey(fineBusinessField.getName()));
                }
                if (!sourceKeyDataSourceMap.containsKey(baseDataSource.getSourceKey().getId())) {
                    sourceKeyDataSourceMap.put(baseDataSource.getSourceKey().getId(), baseDataSource);
                }
            }
            List<DataSource> baseDatas = new ArrayList<DataSource>();
            List<SwiftMetaData> swiftMetaDatas = new ArrayList<SwiftMetaData>();
            List<ColumnKey[]> fields = new ArrayList<ColumnKey[]>();

            if (analysis.getBaseTable() != null) {
                baseDatas.add(IndexingDataSourceFactory.transformDataSource(analysis.getBaseTable()));
            }

            for (Map.Entry<String, List<ColumnKey>> entry : sourceKeyColumnMap.entrySet()) {
                DataSource dataSource = sourceKeyDataSourceMap.get(entry.getKey());
                baseDatas.add(dataSource);
                swiftMetaDatas.add(dataSource.getMetadata());
                fields.add(entry.getValue().toArray(new ColumnKey[entry.getValue().size()]));
            }
            ETLOperator operator = new DetailOperator(fields, swiftMetaDatas);
            Map<Integer, String> fieldsInfo = new HashMap<Integer, String>();
            ETLSource etlSource = new ETLSource(baseDatas, operator);
            for (ColumnKey[] columnKeys : fields) {
                for (ColumnKey columnKey : columnKeys) {
                    int index = etlSource.getMetadata().getColumnIndex(columnKey.getName());
                    fieldsInfo.put(index, columnKey.getName());
                }
            }
            ETLSource dataSource = new ETLSource(baseDatas, operator, fieldsInfo);
            return dataSource;
        }
        return new ETLSource(dataSources, convertEtlOperator(analysis.getOperator()));
    }

    private static DetailOperator fromSelectFieldBean(SelectFieldBean sfb) throws FineEngineException {
        List<List<ColumnKey>> fieldsList = new ArrayList<List<ColumnKey>>();
        List<SwiftMetaData> metas = new ArrayList<SwiftMetaData>();

        for (SelectFieldBeanItem selectField : sfb.getValue()) {
            String fieldId = selectField.getField();
            FineBusinessTable table = FineTableUtils.getTableByFieldId(fieldId);

            SwiftMetaData meta = toMeta(table);
            String columnName = table.getFieldByFieldId(fieldId).getName();

            if (!metas.contains(meta)) {
                metas.add(meta);
                List<ColumnKey> columns = new ArrayList<ColumnKey>();
                columns.add(new ColumnKey(columnName));
                fieldsList.add(columns);
            } else {
                int index = metas.indexOf(meta);
                fieldsList.get(index).add(new ColumnKey(columnName));
            }
        }

        List<ColumnKey[]> columnsList = new ArrayList<ColumnKey[]>();
        for (List<ColumnKey> columns : fieldsList) {
            columnsList.add(columns.toArray(new ColumnKey[columns.size()]));
        }
        return new DetailOperator(columnsList, metas);
    }

    private static SwiftMetaData toMeta(FineBusinessTable table) {
        String name = table.getName();
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (FineBusinessField field : table.getFields()) {
            columns.add(new MetaDataColumn(field.getName(), field.getType(), field.getSize()));
        }
        return new SwiftMetaDataImpl(name, columns);
    }

    private static JoinOperator fromJoinBean(JoinBean jb) {
        JoinBeanValue jbv = jb.getValue();

        List<JoinColumn> joinColumns = new ArrayList<JoinColumn>();
        for (JoinNameItem joinName : jbv.getNames()) {
            JoinColumn jc = new JoinColumn(joinName.getName(), joinName.isLeft(), joinName.getColumnName());
            joinColumns.add(jc);
        }

        List<ColumnKey> leftColumns = new ArrayList<ColumnKey>();
        for (String column : jbv.getFields().get(0)) {
            leftColumns.add(new ColumnKey(column));
        }
        List<ColumnKey> rightColumns = new ArrayList<ColumnKey>();
        for (String column : jbv.getFields().get(1)) {
            rightColumns.add(new ColumnKey(column));
        }

        int type = jbv.getStyle();

        return new JoinOperator(
                joinColumns,
                leftColumns.toArray(new ColumnKey[leftColumns.size()]),
                rightColumns.toArray(new ColumnKey[rightColumns.size()]),
                type);
    }

    private static UnionOperator fromUnionBean(UnionBean ub) {
        UnionBeanValue ubv = ub.getValue();

        List<List<String>> basis = ubv.getBasis();
        int basisSize = basis.size();
        List<List<ColumnKey>> listsOfColumn = new ArrayList<List<ColumnKey>>(basisSize);
        for (int i = 0; i < basisSize; i++) {
            List<String> columns = basis.get(i);
            for (String column : columns) {
                listsOfColumn.get(i).add(new ColumnKey(column));
            }
        }

        return new UnionOperator(listsOfColumn);
    }

    private static ETLOperator convertEtlOperator(FineOperator op) throws FineEngineException {
        switch (op.getType()) {
            case ConfConstant.AnalysisType.SELECT_FIELD:
                return fromSelectFieldBean(op.<SelectFieldBean>getValue());
            case ConfConstant.AnalysisType.JOIN:
                return fromJoinBean(op.<JoinBean>getValue());
            case ConfConstant.AnalysisType.UNION:
                return fromUnionBean(op.<UnionBean>getValue());
            case ConfConstant.AnalysisType.FILTER:
                // TODO 过滤那边还没弄好，要等他们
                return null;
//            case ConfConstant.AnalysisType.CIRCLE_ONE_FIELD_CALCULATE:
//                return fromOneUnionRelationBean(op.<CirculateOneFieldBean>getValue());
//            case ConfConstant.AnalysisType.CIRCLE_TWO_FIELD_CALCULATE:
//                return fromTwoUnionRelationBean(op.<CirculateTwoFieldBean>getValue());
            case ConfConstant.AnalysisType.COLUMN_ROW_TRANS:
                return fromColumnRowTransBean(op.<ColumnRowTransBean>getValue());
            default:
        }
        return null;
    }

    private static ColumnFilterOperator fromColumnFilterBean(FilterOperatorBean bean) {
        // TODO
        return null;
    }

    private static ColumnRowTransOperator fromColumnRowTransBean(ColumnRowTransBean bean) {
        ColumnTransValue value = bean.getValue();
        String groupName = value.getAccordingField();
        String lcName = value.getFieldId();
        List<NameText> lc_value = new ArrayList<NameText>();
        for (int i = 0; i < value.getValues().size(); i++) {
            ColumnInitalItem item = value.getValues().get(i);
            NameText nameText = new NameText(item.getNewValue(), item.getOldValue());
            lc_value.add(nameText);
        }
        List<NameText> columns = new ArrayList<NameText>();
        List<String> otherColumnNames = new ArrayList<String>();
        for (int i = 0; i < value.getInitialFields().size(); i++) {
            ColumnInitalItem item = value.getValues().get(i);
            if (!groupName.equals(item.getNewValue()) && !lcName.equals(item.getNewValue())) {
                if (item.isSelected()) {
                    columns.add(new NameText(item.getNewValue(), item.getOldValue()));
                } else {
                    otherColumnNames.add(item.getNewValue());
                }
            }
        }
        return null;
//        return new ColumnRowTransOperator(groupName, lcName, lc_value, columns, otherColumnNames);
    }

//    private static OneUnionRelationOperator fromOneUnionRelationBean(CirculateOneFieldBean bean) {
//        LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
//        for (int i = 0; i < bean.getFloors().size(); i++) {
//            FloorItem item = bean.getFloors().get(i);
//            columns.put(item.getName(), item.getLength());
//        }
//        return new OneUnionRelationOperator(bean.getIdFieldName(), bean.getShowFields(), columns, bean.getFieldType(), null);
//    }
//
//    private static TwoUnionRelationOperator fromTwoUnionRelationBean(CirculateTwoFieldBean bean) {
//        LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>();
//        for (int i = 0; i < bean.getFloors().size(); i++) {
//            FloorItem item = bean.getFloors().get(i);
//            columns.put(item.getName(), item.getLength());
//        }
//        return new TwoUnionRelationOperator(bean.getIdFieldName(), bean.getShowFields(), columns, bean.getType(), null, bean.getParentIdFieldName());
//    }
}
