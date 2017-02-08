package com.finebi.cube.conf.utils;

import com.finebi.cube.common.log.BILogExceptionInfo;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.structure.collection.map.ConcurrentCacheHashMap;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Connery on 10/25/2016.
 */
public class BILogHelper {
    private static BILogger logger = BILoggerFactory.getLogger(BILogHelper.class);

    public static String logBusinessTable(BusinessTable table) {
        long userID = UserControl.getInstance().getSuperManagerID();
        try {
            table = BusinessTableHelper.getBusinessTable(table.getID());
            return BIStringUtils.append(
                    "\n" + "       Package Name:", BusinessTableHelper.getPackageNameByTableId(table.getID().getIdentityValue()),
                    "\n" + "       Business Table Alias Name:", BICubeConfigureCenter.getAliasManager().getAliasName(table.getID().getIdentityValue(), userID),
                    "\n" + "       Business Table ID:", table.getID().getIdentity(),
                    "\n" + "       Corresponding  Table Source name:", table.getTableSource().getTableName(),
                    "\n" + "       Corresponding  Table Source ID:", table.getTableSource().getSourceID()
            );
        } catch (Exception e) {
            logger.debug("get businessTable info error, the table ID is: " + table.getID().getIdentityValue() + "the table name is: " + BICubeConfigureCenter.getAliasManager().getAliasName(table.getID().getIdentityValue(), userID));
            logger.debug(e.getMessage(), e);
            return "";
        }
    }

    public static String logBusinessTableField(BusinessTable table, String prefix) {
        try {
            table = BusinessTableHelper.getBusinessTable(table.getID());
            StringBuffer sb = new StringBuffer();
            int count = 0;
            for (BusinessField field : table.getFields()) {
                sb.append("\n" + prefix + "Field" + ++count + " :");
                sb.append(logBusinessField(field, prefix + "     "));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return "";
        }
    }

    public static String logBusinessField(BusinessField field, String prefix) {
        try {
            long userID = UserControl.getInstance().getSuperManagerID();
            return BIStringUtils.append(
                    "\n" + prefix + " Business Field Alias Name:", BICubeConfigureCenter.getAliasManager().getAliasName(field.getFieldID().getIdentityValue(), userID),
                    "\n" + prefix + " Business Field Name:", field.getFieldName(),
                    "\n" + prefix + " Business Field ID:", field.getFieldID().getIdentity()
            );
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return "";
        }

    }

    public static String logTableSource(CubeTableSource table, String prefix) {
        try {
            return BIStringUtils.append(
                    "" + prefix + " Table Source Name:", table.getTableName(),
                    "" + prefix + " Table Source ID:", table.getSourceID()
            );
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return "";
        }
    }

    public static String logAnalysisETLTable(BusinessTable table) {
        try {
            return BIStringUtils.append(
                    "\n" + "AnalysisETLTable is: ", table.getTableName(),
                    "\n" + "AnalysisETLTable ID is: ", table.getID().getIdentityValue(),
                    "\n" + "AnalysisETLTable Source is: ", table.getTableSource().getTableName(),
                    "\n" + "AnalysisETLTable SourceID is: ", table.getTableSource().getSourceID()
            );
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return "";
        }
    }


    public static String logAnalysisETLTableField(BusinessTable table, String prefix) {
        try {
            StringBuffer sb = new StringBuffer();
            int count = 0;
            for (BusinessField field : table.getFields()) {
                sb.append("\n" + prefix + "AnalysisETLTable Field" + ++count + " :");
                sb.append(logAnalysisETLBusinessField(field, prefix + "     "));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return "";
        }
    }


    public static String logAnalysisETLBusinessField(BusinessField field, String prefix) {
        try {
            return BIStringUtils.append(
                    "\n" + prefix + " AnalysisETL Business Field Name:", field.getFieldName(),
                    "\n" + prefix + " AnalysisETL Business Field ID:", field.getFieldID().getIdentity()
            );
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return "";
        }

    }

    public static String logTableRelation(JSONObject relationJson) {
        try {
            JSONObject primaryJson = relationJson.getJSONObject("primaryKey");
            JSONObject foreignJson = relationJson.getJSONObject("foreignKey");
            String primaryFieldID = primaryJson.getString("field_id");
            String foreignFieldID = foreignJson.getString("field_id");

            return BIStringUtils.append(
                    "\n" + "primaryFieldID is :" + primaryFieldID,
                    "\n" + "foreignFieldID is :" + foreignFieldID
            );
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return "";
        }
    }

    public static Map<String, Set<String>> getReadOnlyBusinessTablesOfTableSourceMap() {
        HashMap<String, Set<String>> sourceMap = new HashMap<String, Set<String>>();
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(UserControl.getInstance().getSuperManagerID())) {
            List<Set<CubeTableSource>> tableSourceSetList = businessTable.getTableSource().createGenerateTablesList();
            for (Set<CubeTableSource> tableSourceSet : tableSourceSetList) {
                Iterator<CubeTableSource> tableSourceIterator = tableSourceSet.iterator();
                while (tableSourceIterator.hasNext()) {
                    CubeTableSource tableSource = tableSourceIterator.next();
                    String sourceId = tableSource.getSourceID();
                    if (sourceMap.containsKey(sourceId)) {
                        sourceMap.get(sourceId).add(businessTable.getID().getIdentityValue());
                    } else {
                        HashSet<String> businessTableSet = new HashSet<String>();
                        businessTableSet.add(businessTable.getID().getIdentityValue());
                        sourceMap.put(sourceId, businessTableSet);
                    }
                }
            }
        }
        return Collections.unmodifiableMap(sourceMap);
    }

    public static String logBusinessTableByBusinessTableID(String businessTableID) {
        String tableInfo = StringUtils.EMPTY;
        try {
            BusinessTable table = BusinessTableHelper.getBusinessTable(new BITableID(businessTableID));
            tableInfo = logBusinessTable(table);
        } catch (Exception e) {
            logger.info("get businessTable info error the tableID is: " + businessTableID);
            logger.info(e.getMessage(), e);
        }
        return tableInfo;
    }

    public static String logCubeLogTableSourceInfo(String tableSourceID) {
        Object cacheValue = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogConstant.LOG_CACHE_SUB_TAG.READ_ONLY_BUSINESS_TABLES_OF_TABLE_SOURCE_MAP);
        String logInfo = StringUtils.EMPTY;
        if (cacheValue instanceof Map) {
            try {
                Map<String, Set<String>> ReadOnlyBusinessTablesOfTableSourceMap = (Map<String, Set<String>>) cacheValue;
                if (ReadOnlyBusinessTablesOfTableSourceMap.containsKey(tableSourceID)) {
                    StringBuffer sb = new StringBuffer();
                    Iterator<String> it = ReadOnlyBusinessTablesOfTableSourceMap.get(tableSourceID).iterator();
                    int businessTableCount = 0;
                    while (it.hasNext()) {
                        businessTableCount++;
                        String businessTableID = it.next();
                        sb.append("\n" + "*******************" + "BusinessTable " + businessTableCount + " *******************");
                        sb.append(logBusinessTableByBusinessTableID(businessTableID));
                    }
                    logInfo = sb.toString();
                }
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        } else {
            logger.info("the ReadOnlyBusinessTablesOfTableSourceMap is not instanceof Map");
        }
        return logInfo;
    }


    public static Vector<BILogExceptionInfo> getCubeLogExceptionList(String key, String subTag) {
        try {
            Object exceptionList;
            Object exceptionInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_EXCEPTION_INFO, subTag);
            if (exceptionInfoMap != null && exceptionInfoMap instanceof Map) {
                Map<String, Vector<BILogExceptionInfo>> tableExceptionMap = (Map<String, Vector<BILogExceptionInfo>>) exceptionInfoMap;
                if (tableExceptionMap.containsKey(key)) {
                    exceptionList = tableExceptionMap.get(key);
                } else {
                    return new Vector<BILogExceptionInfo>();
                }
            } else {
                return new Vector<BILogExceptionInfo>();
            }

            if (null == exceptionList) {
                return new Vector<BILogExceptionInfo>();
            }
            if (exceptionList instanceof Vector) {
                return (Vector<BILogExceptionInfo>) exceptionList;
            } else {
                BILoggerFactory.getLogger(BILogHelper.class).warn("The cubeLogExceptionList is not a Vector,the tableSourceId is: " + key + ", create a new Vector instead");
                return new Vector<BILogExceptionInfo>();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(BILogHelper.class).warn(e.getMessage(), e);
            BILoggerFactory.getLogger(BILogHelper.class).warn("Get cubeLogExceptionList error, the tableSourceId is: " + key + ", create a new Vector Instead");
            return new Vector<BILogExceptionInfo>();
        }
    }

    public static void cacheCubeLogTableNormalInfo(String tableSourceID, String infoType, Object info) {
        cacheCubeLogNormalInfo(tableSourceID, infoType, info, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_TABLE_NORMAL_INFO);
    }

    public static void cacheCubeLogFieldNormalInfo(String tableSourceID, String fieldName, String infoType, Object info) {
        String fieldInfoType = fieldName + '_' + infoType;
        cacheCubeLogNormalInfo(tableSourceID, fieldInfoType, info, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_FIELD_NORMAL_INFO);
    }

    public static void cacheCubeLogRelationNormalInfo(String relationID, String infoType, Object info) {
        cacheCubeLogNormalInfo(relationID, infoType, info, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_RELATION_NORMAL_INFO);
    }


    private static void cacheCubeLogNormalInfo(String tableSourceID, String infoType, Object info, String subTag) {
        try {
            Object normalInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, subTag);
            if (normalInfoMap != null && normalInfoMap instanceof Map) {
                Map<String, Map<String, Object>> cubeTableNormalInfoMap = (Map<String, Map<String, Object>>) normalInfoMap;
                if (cubeTableNormalInfoMap.containsKey(tableSourceID)) {
                    cubeTableNormalInfoMap.get(tableSourceID).put(infoType, info);
                } else {
                    Map<String, Object> specificTableInfoMap = new ConcurrentHashMap<String, Object>();
                    specificTableInfoMap.put(infoType, info);
                    cubeTableNormalInfoMap.put(tableSourceID, specificTableInfoMap);
                }
            } else {
                Map<String, Object> specificTableInfoMap = new ConcurrentHashMap<String, Object>();
                Map<String, Map> cubeTableNormalInfoMap = new ConcurrentHashMap<String, Map>();
                specificTableInfoMap.put(infoType, info);
                cubeTableNormalInfoMap.put(tableSourceID, specificTableInfoMap);
                BILoggerFactory.cacheLoggerInfo(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, subTag, cubeTableNormalInfoMap);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(BILogHelper.class).warn("Cache Cube Info Error,the error tableSourceId is: " + tableSourceID);
            BILoggerFactory.getLogger(BILogHelper.class).warn(e.getMessage(), e);
        }

    }


    public static void cacheCubeLogTableException(String tableSourceID, BILogExceptionInfo exceptionInfo) {
        cacheCubeLogException(tableSourceID, exceptionInfo, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_TABLE_EXCEPTION_INFO);
    }

    public static void cacheCubeLogRelationException(String relationID, BILogExceptionInfo exceptionInfo) {
        cacheCubeLogException(relationID, exceptionInfo, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_RELATION_EXCEPTION_INFO);
    }

    public static void cacheCubeLogException(String key, BILogExceptionInfo exceptionInfo, String subTag) {
        try {
            Vector<BILogExceptionInfo> exceptionList = BILogHelper.getCubeLogExceptionList(key, subTag);
            exceptionList.add(exceptionInfo);

            Object exceptionInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, subTag);
            if (exceptionInfoMap != null && exceptionInfoMap instanceof Map) {
                Map<String, Vector> cubeExceptionMap = (Map<String, Vector>) exceptionInfoMap;
                cubeExceptionMap.put(key, exceptionList);
            } else {
                Map<String, Vector> cubeExceptionMap = new ConcurrentHashMap<String, Vector>();
                cubeExceptionMap.put(key, exceptionList);
                BILoggerFactory.cacheLoggerInfo(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_EXCEPTION_INFO, subTag, cubeExceptionMap);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(BILogHelper.class).warn("Cache Cube Exception Error, the error tableSourceId is; " + key);
            BILoggerFactory.getLogger(BILogHelper.class).warn(e.getMessage(), e);
        }

    }

    public static ConcurrentCacheHashMap<String, Object> getCubeLogNormalInfoMap(String tableSourceID) {
        try {
            Object normalInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_TABLE_NORMAL_INFO);
            if (null == normalInfoMap) {
                BILoggerFactory.cacheLoggerInfo(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_TABLE_NORMAL_INFO, new ConcurrentCacheHashMap<String, Map>());
                return new ConcurrentCacheHashMap<String, Object>();
            }
            if (normalInfoMap instanceof Map) {
                return (ConcurrentCacheHashMap<String, Object>) normalInfoMap;
            } else {
                BILoggerFactory.getLogger(BILogHelper.class).warn("The cubeLogNormalInfoMap is not a Map, the tableSourceID is: " + tableSourceID + ", create a new Map instead");
                return new ConcurrentCacheHashMap<String, Object>();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(BILogHelper.class).warn(e.getMessage(), e);
            BILoggerFactory.getLogger(BILogHelper.class).warn("Get cubeLogNormalInfoMap error, the tableSourceId is: " + tableSourceID + ", create a new Map instead");
            return new ConcurrentCacheHashMap<String, Object>();
        }
    }


}
