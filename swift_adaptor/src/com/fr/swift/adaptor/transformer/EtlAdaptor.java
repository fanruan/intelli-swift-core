package com.fr.swift.adaptor.transformer;

import com.finebi.base.constant.FineEngineType;
import com.finebi.base.stable.StableManager;
import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.constant.ConfConstant.AnalysisType;
import com.finebi.conf.exception.FineAnalysisOperationUnSafe;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.analysis.bean.operator.add.AddNewColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.AddNewColumnValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.EmptyAddNewColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.accumulative.AccumulativeItemBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.accumulative.AddAllAccumulativeValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.accumulative.group.GroupAccumulativeValue;
import com.finebi.conf.internalimp.analysis.bean.operator.add.allvalue.AddAllValueColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.allvalue.AllValueItemBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.allvalue.group.GroupAllValueValue;
import com.finebi.conf.internalimp.analysis.bean.operator.add.expression.AddExpressionValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.gettime.GetFieldTimeValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.gettime.GetFieldTimeValueItem;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.AddNumberCustomGroupBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberCustomGroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberCustomGroupValueNodeBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.string.AddStringCustomGroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.string.StringCustomGroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.rank.AddFieldRankColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.rank.AddFieldRankColumnItem;
import com.finebi.conf.internalimp.analysis.bean.operator.add.rank.group.GroupRankValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.timediff.TimeDiffValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.timediff.TimeDiffValueItem;
import com.finebi.conf.internalimp.analysis.bean.operator.circulate.CirculateOneFieldBean;
import com.finebi.conf.internalimp.analysis.bean.operator.circulate.CirculateTwoFieldValue;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.DataMiningBean;
import com.finebi.conf.internalimp.analysis.bean.operator.filter.FilterOperatorBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.CustomGroupValueItemBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.DimensionSelectValue;
import com.finebi.conf.internalimp.analysis.bean.operator.group.DimensionSrcValue;
import com.finebi.conf.internalimp.analysis.bean.operator.group.DimensionValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupSingleValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.ViewBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.custom.CustomGroupValueContent;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinBean;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinBeanValue;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinNameItem;
import com.finebi.conf.internalimp.analysis.bean.operator.select.SelectFieldBeanItem;
import com.finebi.conf.internalimp.analysis.bean.operator.select.SelectFieldPathItem;
import com.finebi.conf.internalimp.analysis.bean.operator.setting.FieldSettingBeanItem;
import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnInitalItem;
import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnRowTransBean;
import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnTransValue;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBean;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBeanValue;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBeanValueTable;
import com.finebi.conf.internalimp.analysis.operator.circulate.FloorItem;
import com.finebi.conf.internalimp.analysis.operator.select.SelectFieldOperator;
import com.finebi.conf.internalimp.analysis.operator.setting.FieldSettingOperator;
import com.finebi.conf.internalimp.analysis.table.FineAnalysisTableImpl;
import com.finebi.conf.internalimp.service.pack.FineConfManageCenter;
import com.finebi.conf.provider.SwiftRelationPathConfProvider;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.conf.base.EngineComplexConfTable;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.finebi.conf.utils.FineTableUtils;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.widget.group.GroupAdaptor;
import com.fr.swift.adaptor.widget.group.GroupTypeAdaptor;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.preview.MinorSegmentManager;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.EtlSource;
import com.fr.swift.source.etl.columnfilter.ColumnFilterOperator;
import com.fr.swift.source.etl.columnrowtrans.ColumnRowTransOperator;
import com.fr.swift.source.etl.columnrowtrans.NameText;
import com.fr.swift.source.etl.datamining.DataMiningOperator;
import com.fr.swift.source.etl.date.GetFromDateOperator;
import com.fr.swift.source.etl.datediff.DateDiffOperator;
import com.fr.swift.source.etl.detail.DetailOperator;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;
import com.fr.swift.source.etl.group.GroupAssignmentOperator;
import com.fr.swift.source.etl.group.GroupNumericOperator;
import com.fr.swift.source.etl.group.RestrictRange;
import com.fr.swift.source.etl.group.SingleGroup;
import com.fr.swift.source.etl.groupsum.SumByGroupDimension;
import com.fr.swift.source.etl.groupsum.SumByGroupOperator;
import com.fr.swift.source.etl.groupsum.SumByGroupTarget;
import com.fr.swift.source.etl.join.JoinColumn;
import com.fr.swift.source.etl.join.JoinOperator;
import com.fr.swift.source.etl.join.JoinType;
import com.fr.swift.source.etl.rowcal.accumulate.AccumulateRowOperator;
import com.fr.swift.source.etl.rowcal.alldata.AllDataRowCalculatorOperator;
import com.fr.swift.source.etl.rowcal.rank.RankRowOperator;
import com.fr.swift.source.etl.selfrelation.OneUnionRelationOperator;
import com.fr.swift.source.etl.selfrelation.TwoUnionRelationOperator;
import com.fr.swift.source.etl.union.UnionOperator;
import com.fr.swift.source.etl.utils.FormulaUtils;
import com.fr.swift.source.relation.RelationPathSourceImpl;
import com.fr.swift.source.relation.RelationSourceImpl;
import com.fr.swift.util.Crasher;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Handsome on 2018/1/30 0030 16:38
 */
class EtlAdaptor {
    static DataSource adaptEtlDataSource(FineBusinessTable table) throws Exception {
        FineAnalysisTable analysis = ((FineAnalysisTable) table);
        FineOperator op = analysis.getOperator();
        if (op.getType() == AnalysisType.SELECT_FIELD) {
            return adaptSelectField(analysis);
        }
        //排序没用，只能当作预览的属性和某些新增列的属性
        if (op.getType() == AnalysisType.SORT) {
            return adaptEtlDataSource(analysis.getBaseTable());
        }
        if (op.getType() == AnalysisType.FIELD_SETTING) {
            return adaptFieldSetting(analysis);
        }
        List<DataSource> dataSources = new ArrayList<DataSource>();
        FineBusinessTable baseTable = analysis.getBaseTable();
        try {
            if (baseTable != null) {
                dataSources.add(DataSourceFactory.getDataSource(baseTable));
            }
            dataSources.addAll(fromOperator(op));
            return new EtlSource(dataSources, adaptEtlOperator(op, table));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return DataSourceFactory.getDataSource(baseTable);
        }
    }

    private static DataSource adaptFieldSetting(FineAnalysisTable analysis) throws Exception {
        FineOperator op = analysis.getOperator();
        List<FieldSettingOperator> fieldSettingOperatorList = new ArrayList<FieldSettingOperator>();
        while (op.getType() == AnalysisType.FIELD_SETTING) {
            fieldSettingOperatorList.add((FieldSettingOperator) analysis.getOperator());
            analysis = analysis.getBaseTable();
            op = analysis.getOperator();
            if (op.getType() == AnalysisType.SELECT_FIELD) {
                break;
            }
        }
        EtlSource source = (EtlSource) DataSourceFactory.getDataSource(analysis);
        return new EtlSource(source.getBasedSources(), source.getOperator(), createFieldsInfo(fieldSettingOperatorList, source));
    }

    private static Map<Integer, String> createFieldsInfo(List<FieldSettingOperator> fieldSettingOperatorList, EtlSource source) throws SwiftMetaDataException {
        Map<Integer, String> sourceFieldsInfo = source.getFieldsInfo();
        Map<Integer, String> sourceFullFieldInfo = new TreeMap<Integer, String>();
        Map<Integer, String> fieldInfo = new TreeMap<Integer, String>();
        //如果为空，就根据metadata创建
        if (sourceFieldsInfo == null || sourceFieldsInfo.isEmpty()) {
            for (int i = 0; i < source.getMetadata().getColumnCount(); ) {
                sourceFullFieldInfo.put(i, source.getMetadata().getColumnName(++i));
            }
        } else {
            sourceFullFieldInfo.putAll(sourceFieldsInfo);
        }
        for (int i = fieldSettingOperatorList.size() - 1; i >= 0; i--) {
            List<FieldSettingBeanItem> fieldSettings = fieldSettingOperatorList.get(i).getValue().getValue();
            Iterator<Map.Entry<Integer, String>> fullFieldInfoIter = sourceFullFieldInfo.entrySet().iterator();
            for (int j = 0; j < fieldSettings.size(); j++) {
                FieldSettingBeanItem setting = fieldSettings.get(j);
                Map.Entry<Integer, String> entry = fullFieldInfoIter.next();
                if (setting.isUsed()) {
                    fieldInfo.put(entry.getKey(), setting.getName());
                }
            }
        }
        return fieldInfo.isEmpty() ? sourceFieldsInfo : fieldInfo;
    }

    private static DataSource adaptSelectField(FineAnalysisTable analysis) throws Exception {
        Map<String, List<ColumnKey>> sourceKeyColumnMap = new LinkedHashMap<String, List<ColumnKey>>();
        Map<String, DataSource> sourceKeyDataSourceMap = new LinkedHashMap<String, DataSource>();
        SelectFieldOperator selectFieldOperator = analysis.getOperator();
        List<SelectFieldBeanItem> selectFieldBeanItemList = selectFieldOperator.getValue().getValue();
        FineConfManageCenter fineConfManageCenter = StableManager.getContext().getObject("fineConfManageCenter");
        SwiftRelationPathConfProvider relationProvider = (SwiftRelationPathConfProvider) fineConfManageCenter.getRelationPathProvider().get(FineEngineType.Cube);
        String baseTable = getBaseTable(relationProvider, selectFieldBeanItemList);
        for (SelectFieldBeanItem selectFieldBeanItem : selectFieldBeanItemList) {
            FineBusinessTable fineBusinessTable = FineTableUtils.getTableByFieldId(selectFieldBeanItem.getField());
            FineBusinessField fineBusinessField = fineBusinessTable.getFieldByFieldId(selectFieldBeanItem.getField());
            DataSource baseDataSource = DataSourceFactory.getDataSource(fineBusinessTable);
            List<SelectFieldPathItem> path = selectFieldBeanItem.getPath();
            ColumnKey columnKey = new ColumnKey(fineBusinessField.getName());
            if (baseTable != null && !ComparatorUtils.equals(baseTable, fineBusinessTable.getId())) {
                columnKey.setRelation(getRelation(selectFieldBeanItem.getPath(), baseTable, fineBusinessTable.getId(), relationProvider));
            }
            if (sourceKeyColumnMap.containsKey(baseDataSource.getSourceKey().getId())) {
                sourceKeyColumnMap.get(baseDataSource.getSourceKey().getId()).add(columnKey);
            } else {
                sourceKeyColumnMap.put(baseDataSource.getSourceKey().getId(), new ArrayList<ColumnKey>());
                sourceKeyColumnMap.get(baseDataSource.getSourceKey().getId()).add(columnKey);
            }
            if (!sourceKeyDataSourceMap.containsKey(baseDataSource.getSourceKey().getId())) {
                sourceKeyDataSourceMap.put(baseDataSource.getSourceKey().getId(), baseDataSource);
            }
        }
        List<DataSource> baseDatas = new ArrayList<DataSource>();
        if (analysis.getBaseTable() != null) {
            baseDatas.add(DataSourceFactory.getDataSource(analysis.getBaseTable()));
        }
        if (sourceKeyDataSourceMap.size() == 1) {
            //选字段只选了一张表的情况
            return getSingleTableSelectFieldSource(sourceKeyColumnMap, sourceKeyDataSourceMap, baseDatas);
        } else {
            FineBusinessTable table = FineTableUtils.getTableByName(baseTable);
            DataSource baseDataSource = DataSourceFactory.getDataSource(table);
            return getMultiTableSelectFieldSource(sourceKeyColumnMap, sourceKeyDataSourceMap, baseDatas, baseDataSource.getSourceKey().getId());
        }
    }

    private static RelationSource getRelation(List<SelectFieldPathItem> path, String baseTable, String table, SwiftRelationPathConfProvider relationProvider) {
        if (path != null) {
            //@yee todo 暂时不知道path穿过来是什么样子的
        }
        List<FineBusinessTableRelationPath> relation = relationProvider.getRelationPaths(table, baseTable);
        if (relation == null || relation.isEmpty()) {
            return Crasher.crash("invalid relation tables");
        }
        FineBusinessTableRelationPath p = relation.get(0);
        return getRelation(p);
    }

    private static RelationSource getRelation(FineBusinessTableRelationPath path) {
        try {
            List<RelationSource> relationSources = pathConvert2RelationSource(path);
            if (relationSources.isEmpty()) {
                return null;
            }
            if (1 == relationSources.size()) {
                return relationSources.get(0);
            }
            return new RelationPathSourceImpl(relationSources);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<RelationSource> pathConvert2RelationSource(FineBusinessTableRelationPath path) throws Exception {
        List<FineBusinessTableRelation> relations = path.getFineBusinessTableRelations();
        List<RelationSource> result = new ArrayList<RelationSource>();
        for (FineBusinessTableRelation relation : relations) {
            FineBusinessTable primaryTable;
            FineBusinessTable foreignTable;
            List<FineBusinessField> primaryFields;
            List<FineBusinessField> foreignFields;
            if (relation.getRelationType() == 3) {
                primaryTable = relation.getForeignBusinessTable();
                foreignTable = relation.getPrimaryBusinessTable();
                primaryFields = relation.getForeignBusinessField();
                foreignFields = relation.getPrimaryBusinessField();
            } else {
                primaryTable = relation.getPrimaryBusinessTable();
                foreignTable = relation.getForeignBusinessTable();
                primaryFields = relation.getPrimaryBusinessField();
                foreignFields = relation.getForeignBusinessField();
            }
            SourceKey primary = DataSourceFactory.getDataSource(primaryTable).getSourceKey();
            SourceKey foreign = DataSourceFactory.getDataSource(foreignTable).getSourceKey();
            List<String> primaryKey = new ArrayList<String>();
            List<String> foreignKey = new ArrayList<String>();

            for (FineBusinessField field : primaryFields) {
                primaryKey.add(field.getName());
            }

            for (FineBusinessField field : foreignFields) {
                foreignKey.add(field.getName());
            }
            result.add(new RelationSourceImpl(primary, foreign, primaryKey, foreignKey));
        }
        return result;
    }

    private static String getBaseTable(SwiftRelationPathConfProvider relationProvider, List<SelectFieldBeanItem> selectFieldBeanItemList) {
        Set<String> tables = new HashSet<String>();
        for (SelectFieldBeanItem selectFieldBeanItem : selectFieldBeanItemList) {
            tables.add(selectFieldBeanItem.getTableName());
            //设置了关联与子表的都直接返回
            List<String> baseTables = selectFieldBeanItem.getCommonTable();
            if (baseTables != null && !baseTables.isEmpty()) {
                return baseTables.get(0);
            }
            List<SelectFieldPathItem> path = selectFieldBeanItem.getPath();
            if (path != null) {
                return path.get(path.size() - 1).getTable();
            }
        }
        for (FineBusinessTableRelationPath path : relationProvider.getAllRelationPaths()) {
            if (tables.size() == 1) {
                break;
            }
            List<FineBusinessTableRelation> relations = path.getFineBusinessTableRelations();
            FineBusinessTableRelation firstRelation = relations.get(0);
            FineBusinessTableRelation lastRelation = relations.get(relations.size() - 1);
            String prim;
            if (firstRelation.getRelationType() == 3) {
                prim = firstRelation.getForeignBusinessTable().getId();
            } else {
                prim = firstRelation.getPrimaryBusinessTable().getId();
            }
            String foreign;
            if (lastRelation.getRelationType() == 3) {
                foreign = lastRelation.getPrimaryBusinessTable().getId();
            } else {
                foreign = lastRelation.getForeignBusinessTable().getId();
            }
            if (tables.contains(prim) && tables.contains(foreign)) {
                tables.remove(prim);
            }
        }
        if (tables.size() != 1) {
            return Crasher.crash("wrong relation, foreign table size is" + tables.size());
        }
        return tables.iterator().next();
    }

    private static DataSource getSingleTableSelectFieldSource(Map<String, List<ColumnKey>> sourceKeyColumnMap, Map<String, DataSource> sourceKeyDataSourceMap, List<DataSource> baseDatas) {
        List<ColumnKey> fields = sourceKeyColumnMap.values().iterator().next();
        ETLOperator operator = new DetailOperator(new ArrayList<ColumnKey[]>(), fields, new ArrayList<SwiftMetaData>());
        baseDatas.add(sourceKeyDataSourceMap.values().iterator().next());
        return new EtlSource(baseDatas, operator);
    }


    private static DataSource getMultiTableSelectFieldSource(Map<String, List<ColumnKey>> sourceKeyColumnMap, Map<String, DataSource> sourceKeyDataSourceMap, List<DataSource> baseDatas, String baseSoruceKey) {
        List<SwiftMetaData> swiftMetaDatas = new ArrayList<SwiftMetaData>();
        List<ColumnKey[]> fields = new ArrayList<ColumnKey[]>();
        List<ColumnKey> baseFields = sourceKeyColumnMap.get(baseSoruceKey);
        baseDatas.add(sourceKeyDataSourceMap.get(baseSoruceKey));
        for (Map.Entry<String, List<ColumnKey>> entry : sourceKeyColumnMap.entrySet()) {
            if (ComparatorUtils.equals(baseSoruceKey, entry.getKey())) {
                continue;
            }
            baseDatas.add(sourceKeyDataSourceMap.get(entry.getKey()));
            DataSource dataSource = sourceKeyDataSourceMap.get(entry.getKey());
            swiftMetaDatas.add(dataSource.getMetadata());
            fields.add(entry.getValue().toArray(new ColumnKey[entry.getValue().size()]));
        }
        ETLOperator operator = new DetailOperator(fields, baseFields, swiftMetaDatas);
        return new EtlSource(baseDatas, operator);
    }

    private static List<DataSource> fromOperator(FineOperator op) throws Exception {
        List<DataSource> dataSources = new ArrayList<DataSource>();
        switch (op.getType()) {
            case AnalysisType.JOIN: {
                JoinBeanValue jbv = op.<JoinBean>getValue().getValue();
                FineBusinessTable busiTable = FineTableUtils.getTableByName(jbv.getTable().getName());
                dataSources.add(DataSourceFactory.getDataSource(busiTable));
                break;
            }
            case AnalysisType.UNION:
                UnionBeanValue ubv = op.<UnionBean>getValue().getValue();
                for (UnionBeanValueTable table : ubv.getTables()) {
                    try {
                        FineBusinessTable busiTable = FineTableUtils.getTableByName(table.getName());
                        dataSources.add(DataSourceFactory.getDataSource(busiTable));
                    } catch (Exception e) {
                        continue;
                    }
                }
                break;
            default:
        }
        return dataSources;
    }

    private static SwiftMetaData toMeta(FineBusinessTable table) {
        String name = table.getName();
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (FineBusinessField field : table.getFields()) {
            columns.add(new MetaDataColumn(field.getName(), field.getType(), field.getSize()));
        }
        return new SwiftMetaDataImpl(name, columns);
    }

    private static JoinOperator fromJoinBean(JoinBean jb) throws FineEngineException {
        JoinBeanValue jbv = jb.getValue();

        if (jbv.getBasis().isEmpty()) {
            throw new FineAnalysisOperationUnSafe("");
        }
        List<JoinColumn> joinColumns = new ArrayList<JoinColumn>();
        for (JoinNameItem joinName : jbv.getResult()) {
            JoinColumn jc = new JoinColumn(joinName.getName(), joinName.getIsLeft(), joinName.getColumnName());
            joinColumns.add(jc);
        }

        List<ColumnKey> leftColumns = new ArrayList<ColumnKey>();
        List<ColumnKey> rightColumns = new ArrayList<ColumnKey>();

        for (List<String> leftRight : jbv.getBasis()) {
            leftColumns.add(new ColumnKey(leftRight.get(0)));
            rightColumns.add(new ColumnKey(leftRight.get(1)));

        }

//        for (String column : jbv.getBasis().get(0).get(0)) {
//            leftColumns.add(new ColumnKey(column));
//        }
//        for (String column : jbv.getBasis().get(0).get(1)) {
//            rightColumns.add(new ColumnKey(column));
//        }

        int type = jbv.getStyle();

        return new JoinOperator(
                joinColumns,
                leftColumns.toArray(new ColumnKey[leftColumns.size()]),
                rightColumns.toArray(new ColumnKey[rightColumns.size()]),
                getJoinType(type));
    }

    private static JoinType getJoinType(int type) {
        switch (type) {
            case BIConfConstants.CONF.JOIN.INNER:
                return JoinType.INNER;
            case BIConfConstants.CONF.JOIN.OUTER:
                return JoinType.OUTER;
            case BIConfConstants.CONF.JOIN.RIGHT:
                return JoinType.RIGHT;
            default:
                return JoinType.LEFT;
        }
    }

    private static UnionOperator fromUnionBean(UnionBean ub) {
        UnionBeanValue ubv = ub.getValue();

        List<List<String>> basis = ubv.getBasis();
        int basisSize = basis.size();
        List<List<String>> listsOfColumn = new ArrayList<List<String>>(basisSize);

        for (int i = 0; i < ubv.getResult().size(); i++) {
            listsOfColumn.add(new ArrayList<String>());
            listsOfColumn.get(i).add(ubv.getResult().get(i));
            for (List<String> columnKeys : basis) {
                String columnName = columnKeys.get(i);
                if (ComparatorUtils.equals(columnName, BIConfConstants.CONF.EMPTY_FIELD)) {
                    listsOfColumn.get(i).add(null);
                } else {
                    listsOfColumn.get(i).add(columnName);
                }
            }
        }

        return new UnionOperator(listsOfColumn);
    }

    private static DataMiningOperator fromDataMiningBean(DataMiningBean dmb) {
        AlgorithmBean dmbv = dmb.getValue();

        return new DataMiningOperator(dmbv);
    }

    public static ETLOperator adaptEtlOperator(FineOperator op, FineBusinessTable table) throws Exception {
        switch (op.getType()) {
            case AnalysisType.JOIN:
                return fromJoinBean(op.<JoinBean>getValue());
            case AnalysisType.UNION:
                return fromUnionBean(op.<UnionBean>getValue());
            case AnalysisType.FILTER:
                return fromColumnFilterBean(op.<FilterOperatorBean>getValue(), table);
            case AnalysisType.CIRCLE_ONE_FIELD_CALCULATE:
                return fromOneUnionRelationBean(op.<CirculateOneFieldBean>getValue(), table);
            case AnalysisType.CIRCLE_TWO_FIELD_CALCULATE:
                return fromTwoUnionRelationBean(op.<CirculateOneFieldBean>getValue(), table);
            case AnalysisType.COLUMN_ROW_TRANS:
                return fromColumnRowTransBean(op.<ColumnRowTransBean>getValue(), table);
            case AnalysisType.ADD_COLUMN:
                return fromAddNewColumnBean(op.<AddNewColumnBean>getValue(), table);
            case AnalysisType.GROUP:
                return fromSumByGroupBean(op.<GroupBean>getValue());
            case AnalysisType.DATA_MINING:
                return fromDataMiningBean(op.<DataMiningBean>getValue());
            default:
        }
        return null;
    }

    private static SumByGroupOperator fromSumByGroupBean(GroupBean bean) {
        GroupValueBean valueBean = bean.getValue();
        Map<String, DimensionValueBean> dimensionBean = valueBean.getDimensions();
        ViewBean viewBean = valueBean.getView();
        List<String> dimensions = viewBean.getDimension();
        List<String> views = viewBean.getViews();
        if (dimensionBean.isEmpty()) {
            return null;
        }

        SumByGroupDimension[] groupDimensions = null;
        SumByGroupTarget[] groupTargets = null;
        if (null != views) {
            groupTargets = new SumByGroupTarget[views.size()];
        }
        if (null != dimensions) {
            groupDimensions = new SumByGroupDimension[dimensions.size()];
        }
        for (int i = 0; i < groupDimensions.length; i++) {
            DimensionValueBean tempBean = dimensionBean.get(dimensions.get(i));
            DimensionSrcValue srcValue = tempBean.getSrc();
            List<DimensionSelectValue> value = tempBean.getValue();
            SumByGroupDimension sumByGroupDimension = new SumByGroupDimension();
            sumByGroupDimension.setColumnType(ColumnTypeAdaptor.adaptColumnType(tempBean.getFieldType()));
            sumByGroupDimension.setGroup(GroupAdaptor.adaptGroup(value.get(0)));
            sumByGroupDimension.setName(srcValue.getFieldName());
            sumByGroupDimension.setNameText(tempBean.getName());
            groupDimensions[i] = sumByGroupDimension;
        }
        for (int i = 0; i < groupTargets.length; i++) {
            DimensionValueBean tempBean = dimensionBean.get(views.get(i));
            DimensionSrcValue srcValue = tempBean.getSrc();
            SumByGroupTarget sumByGroupTarget = new SumByGroupTarget();
            sumByGroupTarget.setName(srcValue.getFieldName());
            sumByGroupTarget.setNameText(tempBean.getName());
            int type = BIConfConstants.CONF.GROUP.NUMBER.SUM;
            switch (tempBean.getValue().get(0).getType()) {
                case BIConfConstants.CONF.GROUP.TYPE.SINGLE:
                    type = ((GroupSingleValueBean) tempBean.getValue().get(0)).getValue();
                    break;
                case BIConfConstants.CONF.GROUP.TYPE.DOUBLE:
                    // TODO
                    break;
                default:
                    //TODO
                    break;
            }
            AggregatorType aggregatorType = AggregatorAdaptor.transformAggregatorType(tempBean.getFieldType(), type);
            sumByGroupTarget.setAggregator(AggregatorFactory.createAggregator(aggregatorType));
            sumByGroupTarget.setColumnType(ColumnTypeAdaptor.adaptColumnType(tempBean.getFieldType()));
            if (aggregatorType == AggregatorType.COUNT || aggregatorType == AggregatorType.DISTINCT) {
                sumByGroupTarget.setColumnType(ColumnTypeConstants.ColumnType.NUMBER);
            }
            sumByGroupTarget.setSumType(type);
            groupTargets[i] = sumByGroupTarget;
        }
        return new SumByGroupOperator(groupTargets, groupDimensions);
    }

    private static AccumulateRowOperator getAccumulateRowOperator(AddNewColumnValueBean value) {
        AccumulativeItemBean tempBean = ((AddAllAccumulativeValueBean) value).getValue();
        String columnName = value.getName();
        ColumnKey columnKey = new ColumnKey(tempBean.getOrigin());
        if (tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.NOT_IN_GROUP) {
            return new AccumulateRowOperator(columnKey, columnName, null);
        } else {
            List<String> selects = ((GroupAccumulativeValue) tempBean).getSelects();
            ColumnKey[] dimensions = new ColumnKey[selects.size()];
            for (int i = 0; i < selects.size(); i++) {
                dimensions[i] = new ColumnKey(selects.get(i));
            }
            return new AccumulateRowOperator(columnKey, columnName, dimensions);
        }
    }

    private static AllDataRowCalculatorOperator getAllDataRowCalculatorOperator(AddNewColumnValueBean value) {
        String columnName = value.getName();
        AllValueItemBean tempBean = ((AddAllValueColumnBean) value).getValue();
        String columnKey = tempBean.getOrigin();
        int summary = tempBean.getSummary();
        AggregatorType aggregatorType = AggregatorAdaptor.transformAllValuesAggregatorType(summary);
        if (tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.NOT_IN_GROUP) {
            return new AllDataRowCalculatorOperator(columnName, ColumnTypeAdaptor.adaptColumnType(32), columnKey, null, aggregatorType);
        } else {
            List<String> selects = ((GroupAllValueValue) tempBean).getSelects();
            ColumnKey[] dimensions = new ColumnKey[selects.size()];
            for (int i = 0; i < dimensions.length; i++) {
                dimensions[i] = new ColumnKey(selects.get(i));
            }
            return new AllDataRowCalculatorOperator(columnName, ColumnTypeAdaptor.adaptColumnType(32), columnKey, dimensions, aggregatorType);
        }
    }

    private static RankRowOperator getRankRowOperator(AddNewColumnValueBean value) {
        String columnName = value.getName();
        AddFieldRankColumnItem tempBean = ((AddFieldRankColumnBean) value).getValue();
        ColumnKey columnKey = new ColumnKey(tempBean.getOrigin());
        //nice job! foundation
        SortType sortType = tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.RANKING.ASC || tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.RANKING.ASC_IN_GROUP ? SortType.ASC : SortType.DESC;
        if (tempBean.getRule() == BIConfConstants.CONF.ADD_COLUMN.RANKING.ASC_IN_GROUP) {
            List<String> selects = ((GroupRankValueBean) tempBean).getSelects();
            ColumnKey[] dimensions = new ColumnKey[selects.size()];
            for (int i = 0; i < dimensions.length; i++) {
                dimensions[i] = new ColumnKey(selects.get(0));
            }
            return new RankRowOperator(columnName, sortType, columnKey, dimensions);
        } else {
            return new RankRowOperator(columnName, sortType, columnKey, null);
        }
    }

    private static GetFromDateOperator getFromDataOperator(AddNewColumnValueBean value) {
        String columnName = value.getName();
        GetFieldTimeValueItem tempBean = ((GetFieldTimeValueBean) value).getValue();
        String fieldName = tempBean.getFieldName();
        GroupType type = GroupTypeAdaptor.adaptDateUnit(tempBean.getUnit());
        return new GetFromDateOperator(fieldName, type, columnName);
    }

    private static DateDiffOperator getDateDiffOperator(AddNewColumnValueBean value) {
        TimeDiffValueItem tempBean = ((TimeDiffValueBean) value).getValue();
        String columnName = value.getName();
        String field1 = tempBean.getMinuend();
        String field2 = tempBean.getMinus();
        return new DateDiffOperator(field1, field2, GroupTypeAdaptor.adaptDateGapUnit(tempBean.getUnit()), columnName);
    }

    private static GroupAssignmentOperator getAutoGroupOperator(AddNewColumnValueBean value) {
        StringCustomGroupValueBean bean = ((AddStringCustomGroupValueBean) value).getValue();
        String columnName = value.getName();
        String useOther = bean.getUseOther();
        String field = bean.getField();
        List<CustomGroupValueContent> details = bean.getDetails();
        List<SingleGroup> group = new ArrayList<SingleGroup>();
        Iterator<CustomGroupValueContent> iterator = details.iterator();
        while (iterator.hasNext()) {
            CustomGroupValueContent content = iterator.next();
            String id = content.getId();
            String name = content.getValue();
            List<CustomGroupValueItemBean> itemBean = content.getContent();
            List<String> dataList = new ArrayList<String>();
            Iterator<CustomGroupValueItemBean> iter = itemBean.iterator();
            while (iter.hasNext()) {
                CustomGroupValueItemBean tempValue = iter.next();
                dataList.add(tempValue.getValue());
            }
            SingleGroup singleGroup = new SingleGroup();
            singleGroup.setName(name);
            singleGroup.setList(dataList);
            group.add(singleGroup);
        }
        return new GroupAssignmentOperator(columnName, useOther, new ColumnKey(field), group);
    }

    private static GroupNumericOperator getGroupNumericOperator(AddNewColumnValueBean value) {
        String columnName = value.getName();
        NumberCustomGroupValueBean bean = ((AddNumberCustomGroupBean) value).getValue();
        String field = bean.getField();
        double max = Double.parseDouble(bean.getMax());
        double min = Double.parseDouble(bean.getMin());
        String other = bean.getUseOther();
        List<NumberCustomGroupValueNodeBean> nodes = bean.getNodes();
        Iterator<NumberCustomGroupValueNodeBean> iterator = nodes.iterator();
        List<RestrictRange> list = new ArrayList<RestrictRange>();
        while (iterator.hasNext()) {
            NumberCustomGroupValueNodeBean nodeBean = iterator.next();
            boolean closemax = nodeBean.isClosemax();
            boolean closemin = nodeBean.isClosemin();
            String groupName = nodeBean.getGroupName();
            double nodeMax = Double.parseDouble(nodeBean.getMax());
            double nodeMin = Double.parseDouble(nodeBean.getMin());
            boolean valid = nodeBean.isValid();
            RestrictRange restrictRange = new RestrictRange(closemax, closemin, groupName, field, nodeMax, nodeMin, valid);
            list.add(restrictRange);
        }
        return new GroupNumericOperator(columnName,
                new ColumnKey(field), max, min, other, list);
    }

    private static AbstractOperator fromAddNewColumnBean(AddNewColumnBean bean, FineBusinessTable table) throws Exception {
        if (bean.getValue() instanceof EmptyAddNewColumnBean) {
            throw new FineAnalysisOperationUnSafe("");
        }

        AddNewColumnValueBean value = bean.getValue();
        DataSource source = adaptEtlDataSource(((FineAnalysisTableImpl) table).getBaseTable());
        switch (value.getType()) {
            case BIConfConstants.CONF.ADD_COLUMN.FORMULA.TYPE: {
                String expression = ((AddExpressionValueBean) value).getValue();
                return new ColumnFormulaOperator(value.getName(), FormulaUtils.getColumnType(source.getMetadata(), expression), expression);
            }
            case BIConfConstants.CONF.ADD_COLUMN.ACCUMULATIVE_VALUE.TYPE: {
                return getAccumulateRowOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.ALL_VALUES.TYPE: {
                return getAllDataRowCalculatorOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.RANKING.TYPE: {
                return getRankRowOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.TIME.TYPE: {
                return getFromDataOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.TIME_GAP.TYPE: {
                return getDateDiffOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.GROUP.TYPE_STRING: {
                return getAutoGroupOperator(value);
            }
            case BIConfConstants.CONF.ADD_COLUMN.GROUP.TYPE_NUMBER_CUSTOM: {
                return getGroupNumericOperator(value);
            }
            default:
        }
        return null;
    }

    private static ColumnFilterOperator fromColumnFilterBean(FilterOperatorBean bean, FineBusinessTable table) {
        List<Segment> segments = new ArrayList<Segment>();
        try {
            DataSource source = adaptEtlDataSource(((FineAnalysisTableImpl) table).getBaseTable());
            // TODO: 2018/3/16 这边直接通过minor来拿有问题。不能区分当前是部分数据还是全部数据的预览。需要anchore在上层处理:)
            segments = MinorSegmentManager.getInstance().getSegment(source.getSourceKey());
        } catch (Exception e) {

        }
        FilterInfo filterInfo = FilterInfoFactory.transformFilterBean(bean.getValue(), segments);
        return new ColumnFilterOperator(filterInfo);
    }

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

    private static ColumnRowTransOperator fromColumnRowTransBean(ColumnRowTransBean bean, FineBusinessTable table) {
        ColumnTransValue value = bean.getValue();
        FineBusinessTable preTable = ((EngineComplexConfTable) table).getBaseTableBySelected(0);
        List<FineBusinessField> fields = preTable.getFields();
        String groupName = fields.get(findFieldName(fields, value.getAccordingField())).getName();
        String lcName = fields.get(findFieldName(fields, value.getFieldId())).getName();
        List<NameText> lcValue = new ArrayList<NameText>();
        for (int i = 0; i < value.getValues().size(); i++) {
            ColumnInitalItem item = value.getValues().get(i);
            NameText nameText = new NameText(item.getOldValue(), item.getNewValue());
            lcValue.add(nameText);
        }
        List<NameText> columns = new ArrayList<NameText>();
        List<String> otherColumnNames = new ArrayList<String>();
        for (int i = 0; i < value.getInitialFields().size(); i++) {
            ColumnInitalItem item = value.getInitialFields().get(i);
            if (!groupName.equals(item.getOldValue()) && !lcName.equals(item.getOldValue())) {

                if (item.isSelected()) {
                    columns.add(new NameText(item.getOldValue(), item.getNewValue()));
                } else {
                    otherColumnNames.add(item.getOldValue());
                }
            }
        }
        return new ColumnRowTransOperator(groupName, lcName, lcValue, columns, otherColumnNames);
    }

    private static OneUnionRelationOperator fromOneUnionRelationBean(CirculateOneFieldBean bean, FineBusinessTable table) {
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
            } catch (Exception e) {

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

    private static TwoUnionRelationOperator fromTwoUnionRelationBean(CirculateOneFieldBean bean, FineBusinessTable table) {
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