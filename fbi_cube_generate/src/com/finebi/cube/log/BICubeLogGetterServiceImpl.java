package com.finebi.cube.log;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.fs.control.UserControl;

import java.util.*;

/**
 * Created by neil on 2017/4/12.
 */
public class BICubeLogGetterServiceImpl implements BICubeLogGetterService {

    private static final BILogger LOGGER = BILoggerFactory.getLogger(BICubeLogGetterServiceImpl.class);

    @Override
    public List<Map<String, Object>> getTableTransportLogInfo() {
        List<Map<String, Object>> tableTransportList = new ArrayList<Map<String, Object>>();
        Object normalInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_TABLE_NORMAL_INFO);
        if (normalInfoMap != null && normalInfoMap instanceof Map) {
            Iterator<Map.Entry<String, Map<String, Object>>> it = ((Map) normalInfoMap).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Map<String, Object>> entry = it.next();

                Map<String, Object> tablesouceMap = new HashMap<String, Object>();
                tablesouceMap.put("tableSourceId", entry.getKey());
                tablesouceMap.put("time", entry.getValue().get(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START));
                tablesouceMap.put("tableName", getTableSourceName(entry.getKey()));
                tablesouceMap.put("packageNames", getPackageNames(entry.getKey()));
                tableTransportList.add(tablesouceMap);
            }
        }
        return tableTransportList;
    }

    @Override
    public List<Map<String, Object>> getTableFieldIndexLogInfo() {
        List<Map<String, Object>> tableFieldList = new ArrayList<Map<String, Object>>();
        Object normalInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_FIELD_NORMAL_INFO);
        if (normalInfoMap != null && normalInfoMap instanceof Map) {
            Iterator<Map.Entry<String, Map<String, Object>>> it = ((Map) normalInfoMap).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Map<String, Object>> entry = it.next();

                Map<String, Object> tablesouceMap = new HashMap<String, Object>();
                tablesouceMap.put("tableSourceId", entry.getKey());
                tablesouceMap.put("tableName", getTableSourceName(entry.getKey()));
                tablesouceMap.put("packageNames", getPackageNames(entry.getKey()));
                tablesouceMap.put("fields", entry.getValue());
                tableFieldList.add(tablesouceMap);
            }
        }
        return tableFieldList;
    }

    @Override
    public List<Map<String, Object>> getRelationIndexLogInfo() {
        List<Map<String, Object>> relationList = new ArrayList<Map<String, Object>>();
        Object normalInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_RELATION_NORMAL_INFO);
        if (normalInfoMap != null && normalInfoMap instanceof Map) {
            Iterator<Map.Entry<String, Map<String, Object>>> it = ((Map) normalInfoMap).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Map<String, Object>> entry = it.next();

                Map<String, Object> tablesouceMap = new HashMap<String, Object>();
                tablesouceMap.put("relationId", entry.getKey());
                tablesouceMap.put("time", entry.getValue());
                BITableSourceRelation tableRelation = getTableRelationById(entry.getKey());
                if (tableRelation != null) {
                    tablesouceMap.put("primaryTable",getRelationMap(tableRelation.getPrimaryTable(),tableRelation.getPrimaryField()));
                    tablesouceMap.put("foreignTable",getRelationMap(tableRelation.getForeignTable(),tableRelation.getForeignField()));
                } else {
                    LOGGER.warn("table relation is null for relation id " + entry.getKey());
                }
                relationList.add(tablesouceMap);
            }
        }
        return relationList;
    }

    @Override
    public int getAllTableNeedTranslate() {
        int size = 0;
        Set allTables = BIConfigureManagerCenter.getLogManager().getAllTableSourceSet(UserControl.getInstance().getSuperManagerID());
        if (allTables != null) {
            size = allTables.size();
        }
        return size;
    }

    @Override
    public int getAllFieldNeedIndex() {
        int size = 0;
        Set<CubeTableSource> allTables = BIConfigureManagerCenter.getLogManager().getAllTableSourceSet(UserControl.getInstance().getSuperManagerID());
        if (allTables != null) {
            for (CubeTableSource tableSource : allTables) {
                Set<CubeTableSource> tableSourceSet = new HashSet<CubeTableSource>();
                tableSourceSet.add(tableSource);
                size += tableSource.getSelfFields(tableSourceSet).size();
            }
        }
        return size;
    }

    @Override
    public int getAllRelationNeedGenerate() {
        Set allPathSet = BIConfigureManagerCenter.getLogManager().getAllRelationPathSet(UserControl.getInstance().getSuperManagerID());
        Set allRelationSet = BIConfigureManagerCenter.getLogManager().getAllRelation(UserControl.getInstance().getSuperManagerID());

        int relationSize = 0;
        int pathSize = 0;

        if (allPathSet != null) {
            pathSize = allPathSet.size();
        }
        if (allRelationSet != null) {
            relationSize = allRelationSet.size();
        }
        return relationSize + pathSize;
    }

    @Override
    public int getTableAlreadyTranslate() {
        return getTableTransportLogInfo().size();
    }

    @Override
    public int getFieldAlreadyIndex() {
        int size = 0;
        List<Map<String, Object>> fieldList = getTableFieldIndexLogInfo();
        for (Map fieldMap : fieldList) {
            Map fields = (Map) fieldMap.get("fields");
            size += fields.size();
        }
        return size;
    }

    @Override
    public int getRelationAlreadyGenerate() {
        int size = 0;
        List<Map<String, Object>> fieldList = getTableFieldIndexLogInfo();
        for (Map fieldMap : fieldList) {
            Map fields = (Map) fieldMap.get("relations");
            size += fields.size();
        }
        return size;
    }

    private Set<String> getPackageNames(String tableSouceId) {
        Set<String> packageNames = new HashSet<String>();
        Set<BusinessTable> tables = getBusinessTablesByTableSouce(tableSouceId);
        for (BusinessTable table : tables) {
            packageNames.add(getPackageNameByBusinessTable(table.getID()));
        }
        return packageNames;
    }

    private CubeTableSource getTableSourceBySourceId(String sourceId) {
        Set<BusinessTable> businessTables = getAllBusinessTables();
        for (BusinessTable table : businessTables) {
            if (table.getTableSource() != null && table.getTableSource().getSourceID().equals(sourceId)) {
                return table.getTableSource();
            }
        }
        return null;
    }

    private Set<BusinessTable> getAllBusinessTables() {
        return BICubeConfigureCenter.getPackageManager().getAllTables(UserControl.getInstance().getSuperManagerID());
    }

    private String getTableSourceName(String sourceId) {
        String tableName = null;
        CubeTableSource tableSource = getTableSourceBySourceId(sourceId);
        if (tableSource != null) {
            tableName = tableSource.getTableName();
        }
        return tableName;
    }

    private Set<BusinessTable> getBusinessTablesByTableSouce(String tableSourceId) {
        Set<BusinessTable> returnTables = new HashSet<BusinessTable>();
        Set<BusinessTable> businessTables = getAllBusinessTables();
        for (BusinessTable table : businessTables) {
            if (table.getTableSource() != null && table.getTableSource().getSourceID().equals(tableSourceId)) {
                returnTables.add(table);
            }
        }
        return returnTables;
    }

    private String getPackageNameByBusinessTable(BITableID tableId) {
        Set<IBusinessPackageGetterService> packageGetterServices = BICubeConfigureCenter.getPackageManager().getAllPackages(UserControl.getInstance().getSuperManagerID());
        for (IBusinessPackageGetterService pack : packageGetterServices) {
            Set<BusinessTable> businessTables = pack.getBusinessTables();
            for (BusinessTable businessTable : businessTables) {
                if (businessTable.getID().equals(tableId)) {
                    return pack.getName().getName();
                }
            }
        }
        return null;
    }

    private BITableSourceRelation getTableRelationById(String relationId) {
        Object normalInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogConstant.LOG_CACHE_SUB_TAG.READ_ONLY_RELATION_MAP);
        Map<String, BITableSourceRelation> sourceMap = (Map<String, BITableSourceRelation>) normalInfoMap;
        return sourceMap.get(relationId);
    }

    private Map<String, Object> getRelationMap(CubeTableSource tableSource, ICubeFieldSource fieldSource) {
        Map<String, Object> relationMap = new HashMap<String, Object>();
        String tableName = getTableSourceName(tableSource.getSourceID());
        Set<String> packageNames = getPackageNames(tableSource.getSourceID());
        String fieldName = fieldSource.getFieldName();
        relationMap.put("tableName", tableName);
        relationMap.put("packageNames", packageNames);
        relationMap.put("fieldName", fieldName);
        return relationMap;
    }
}
