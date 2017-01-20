package com.finebi.cube.conf.utils;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.*;

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
            String sourceId = businessTable.getTableSource().getSourceID();
            if (sourceMap.containsKey(sourceId)) {
                sourceMap.get(sourceId).add(businessTable.getID().getIdentityValue());
            } else {
                HashSet<String> businessTableSet = new HashSet<String>();
                businessTableSet.add(businessTable.getID().getIdentityValue());
                sourceMap.put(sourceId, businessTableSet);
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

    public static String logCubeGeneratingTableSourceInfoByTableSourceID(String tableSourceID) {
        Object cacheValue = BILoggerFactory.getLoggerCacheValue("Cube Generate Info", "ReadOnlyBusinessTablesOfTableSourceMap");
        String logInfo = StringUtils.EMPTY;
        if (cacheValue instanceof Map) {
            try {
                Map<String, Set<String>> ReadOnlyBusinessTablesOfTableSourceMap = (Map<String, Set<String>>) cacheValue;
                if (ReadOnlyBusinessTablesOfTableSourceMap.containsKey(tableSourceID)) {
                    StringBuffer sb = new StringBuffer();
                    Iterator<String> it = ReadOnlyBusinessTablesOfTableSourceMap.get(tableSourceID).iterator();
                    while (it.hasNext()) {
                        String businessTableID = it.next();
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


}
