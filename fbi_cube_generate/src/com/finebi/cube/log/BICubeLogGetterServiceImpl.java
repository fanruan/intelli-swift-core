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
                tablesouceMap.put("time", calculateTransportCostTime(entry.getValue()));
                tablesouceMap.put("tableName", getTableSourceName(entry.getKey()));
                tablesouceMap.put("packageNames", getPackageNames(entry.getKey()));

                if (tablesouceMap.get("time") != null) {
                    tableTransportList.add(tablesouceMap);
                }
            }
        }
        return tableTransportList;
    }

    //TODO 这个map不应该出现有结束时间而没有开始时间，这种情况是个bug，要去排查
    private Long calculateTransportCostTime(Map<String, Object> transportMap) {
        Long cost = null;
        if (transportMap.containsKey(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END) && transportMap.containsKey(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START)) {
            cost = (Long) transportMap.get(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END) - (Long) transportMap.get(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START);
        }
        return cost;
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
                tablesouceMap.put("fields", calculateFields(entry.getValue()));
                tableFieldList.add(tablesouceMap);
            }
        }
        return tableFieldList;
    }

    private Map<String, Object> calculateFields(Map<String, Object> inputFields) {
        Map<String, Object> returnFields = new HashMap<String, Object>();

        Iterator<Map.Entry<String, Object>> iterator = inputFields.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();

            if (key.contains(BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_END)) {
                String fieldName = getFieldName(key);
                Long endTime = (Long) entry.getValue();
                String fieldStartWrapper = getFieldStartWrapper(fieldName);
                Long startTime = (Long) inputFields.get(fieldStartWrapper);
                returnFields.put(fieldName, endTime - startTime);
            }

        }

        return returnFields;
    }

    private Set<String> filterCalculatingFields(Set<String> calculatedFields, Set<String> allFields) {
        Set<String> calculatingFields = new HashSet<String>();
        for (String field : allFields) {
            if (!calculatedFields.contains(getFieldName(field))) {
                calculatedFields.add(field);
            }
        }
        return calculatingFields;
    }

    private String getFieldName(String fieldNameWrapper) {
        String startWrapper = "_" + BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_START;
        String endWrapper = "_" + BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_END;
        if (fieldNameWrapper.contains(startWrapper)) {
            return fieldNameWrapper.replace(startWrapper, "");
        } else if (fieldNameWrapper.contains(endWrapper)) {
            return fieldNameWrapper.replace(endWrapper, "");
        } else {
            return fieldNameWrapper;
        }
    }

    private String getFieldStartWrapper(String fieldName) {
        return fieldName + "_" + BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_START;
    }

    private String getFieldEndWrapper(String fieldName) {
        return fieldName + "_" + BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_END;
    }

    @Override
    public List<Map<String, Object>> getRelationIndexLogInfo() {
        List<Map<String, Object>> relationList = new ArrayList<Map<String, Object>>();
        Object normalInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_RELATION_NORMAL_INFO);
        if (normalInfoMap != null && normalInfoMap instanceof Map) {
            Iterator<Map.Entry<String, Map<String, Object>>> it = ((Map) normalInfoMap).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Map<String, Object>> entry = it.next();
                Map<String, Object> relationMap = calculateRelationCostTime(entry);
                if(relationMap.get("time") != null) {
                    relationList.add(relationMap);
                }
            }
        }
        return relationList;
    }

    private Map<String, Object> calculateRelationCostTime(Map.Entry<String, Map<String, Object>> relationEntry) {

        Map<String, Object> tablesouceMap = new HashMap<String, Object>();
        tablesouceMap.put("relationId", relationEntry.getKey());

        BITableSourceRelation tableRelation = getTableRelationById(relationEntry.getKey());
        if (tableRelation != null) {
            tablesouceMap.put("primaryTable", getRelationTableMap(tableRelation.getPrimaryTable(), tableRelation.getPrimaryField()));
            tablesouceMap.put("foreignTable", getRelationTableMap(tableRelation.getForeignTable(), tableRelation.getForeignField()));
        } else {
            LOGGER.warn("table relation is null for relation id " + relationEntry.getKey());
        }

        Iterator<Map.Entry<String, Object>> iterator = relationEntry.getValue().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            if (entry.getKey().equals(BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_END)) {
                Long endTime = (Long) entry.getValue();
                Long startTime = (Long) relationEntry.getValue().get(BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_START);
                tablesouceMap.put("time", endTime - startTime);
                break;
            } else {
                tablesouceMap.put("time", null);
            }
        }

        return tablesouceMap;
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

    private Map<String, Object> getRelationTableMap(CubeTableSource tableSource, ICubeFieldSource fieldSource) {
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
