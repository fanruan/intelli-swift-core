package com.fr.swift.adaptor.transformer.etl;

import com.finebi.base.constant.FineEngineType;
import com.finebi.base.stable.StableManager;
import com.finebi.conf.internalimp.analysis.bean.operator.select.SelectFieldBeanItem;
import com.finebi.conf.internalimp.analysis.bean.operator.select.SelectFieldPathItem;
import com.finebi.conf.internalimp.analysis.operator.select.SelectFieldOperator;
import com.finebi.conf.internalimp.service.pack.FineConfManageCenter;
import com.finebi.conf.provider.SwiftRelationPathConfProvider;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.conf.dashboard.DashboardRelationPathService;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.EtlSource;
import com.fr.swift.source.etl.detail.DetailOperator;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class SelectFieldAdaptor {
    /**
     * 选字段
     *
     * @param analysis
     * @return
     * @throws Exception
     */
    public static DataSource adaptSelectField(FineAnalysisTable analysis) throws Exception {
        Map<String, List<ColumnKey>> sourceKeyColumnMap = new LinkedHashMap<String, List<ColumnKey>>();
        Map<String, DataSource> sourceKeyDataSourceMap = new LinkedHashMap<String, DataSource>();
        SelectFieldOperator selectFieldOperator = analysis.getOperator();
        List<SelectFieldBeanItem> selectFieldBeanItemList = selectFieldOperator.getValue().getValue();
//        FineConfManageCenter fineConfManageCenter = StableManager.getContext().getObject("fineConfManageCenter");
//        SwiftRelationPathConfProvider relationProvider = (SwiftRelationPathConfProvider) fineConfManageCenter.getRelationPathProvider().get(FineEngineType.Cube);
        DashboardRelationPathService relationProvider = DashboardRelationPathService.getService();
        String baseTable = RelationAdaptor.getBaseTable(relationProvider, selectFieldBeanItemList);
        for (SelectFieldBeanItem selectFieldBeanItem : selectFieldBeanItemList) {
            FineBusinessTable fineBusinessTable = BusinessTableUtils.getTableByFieldId(selectFieldBeanItem.getField());
            FineBusinessField fineBusinessField = fineBusinessTable.getFieldByFieldId(selectFieldBeanItem.getField());
            DataSource baseDataSource = DataSourceFactory.getDataSourceInCache(fineBusinessTable);
            List<SelectFieldPathItem> path = selectFieldBeanItem.getPath();
            handleSelectPath(path, sourceKeyDataSourceMap);
            ColumnKey columnKey = new ColumnKey(fineBusinessField.getName());
            if (baseTable != null && !ComparatorUtils.equals(baseTable, fineBusinessTable.getId())) {
                columnKey.setRelation(RelationAdaptor.getRelation(path, baseTable, fineBusinessTable.getId(), relationProvider));
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
            baseDatas.add(DataSourceFactory.getDataSourceInCache(analysis.getBaseTable()));
        }
        if (sourceKeyDataSourceMap.size() == 1) {
            //选字段只选了一张表的情况
            return getSingleTableSelectFieldSource(sourceKeyColumnMap, sourceKeyDataSourceMap, baseDatas);
        } else {
            FineBusinessTable table = BusinessTableUtils.getTableByTableName(baseTable);
            DataSource baseDataSource = DataSourceFactory.getDataSourceInCache(table);
            return getMultiTableSelectFieldSource(sourceKeyColumnMap, sourceKeyDataSourceMap, baseDatas, baseDataSource.getSourceKey().getId());
        }
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

    private static void handleSelectPath(List<SelectFieldPathItem> path, Map<String, DataSource> sourceKeyDataSourceMap) throws Exception {
        if (path != null && !path.isEmpty()) {
            for (SelectFieldPathItem item : path) {
                List<String> from = item.getRelationship().getFrom();
                if (null != from && !from.isEmpty()) {
                    FineBusinessTable table = BusinessTableUtils.getTableByFieldId(from.get(0));
                    dealSourceWithSelectPath(table, item.getTable(), sourceKeyDataSourceMap);
                }
                List<String> to = item.getRelationship().getTo();
                if (null != to && !to.isEmpty()) {
                    FineBusinessTable table = BusinessTableUtils.getTableByFieldId(to.get(0));
                    dealSourceWithSelectPath(table, item.getTable(), sourceKeyDataSourceMap);
                }
            }
        }
    }

    private static void dealSourceWithSelectPath(FineBusinessTable table, String tableName, Map<String, DataSource> sourceKeyDataSourceMap) throws Exception {
        if (ComparatorUtils.equals(table.getName(), tableName)) {
            DataSource dataSource = DataSourceFactory.getDataSourceInCache(table);
            String sourceKey = dataSource.getSourceKey().getId();
            if (!sourceKeyDataSourceMap.containsKey(sourceKey)) {
                sourceKeyDataSourceMap.put(sourceKey, dataSource);
            }
        }
    }
}