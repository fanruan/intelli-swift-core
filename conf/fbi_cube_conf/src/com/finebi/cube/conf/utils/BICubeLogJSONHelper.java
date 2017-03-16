package com.finebi.cube.conf.utils;

import com.finebi.cube.common.log.BILogExceptionInfo;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by roy on 2017/2/22.
 */
public class BICubeLogJSONHelper extends BILogHelper {


    public static JSONObject getCubeLogExceptionInfoJSON() {
        JSONObject tableExceptionInfoJSON = new JSONObject();
        initExceptionJson(tableExceptionInfoJSON, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_TABLE_EXCEPTION_INFO);
        initExceptionJson(tableExceptionInfoJSON, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_RELATION_EXCEPTION_INFO);
        return tableExceptionInfoJSON;
    }

    public static JSONObject getCubeLogTableTransportInfoJSON() {
        JSONObject tableNormalInfoJSON = new JSONObject();
        initTableInfoJSON(tableNormalInfoJSON, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_TABLE_NORMAL_INFO);
        return tableNormalInfoJSON;
    }


    public static JSONObject getCubeLogTableFieldIndexInfoJSON() {
        JSONObject tableFieldIndexInfoJSON = new JSONObject();
        initTableInfoJSON(tableFieldIndexInfoJSON, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_FIELD_NORMAL_INFO);
        return tableFieldIndexInfoJSON;
    }

    public static JSONObject getCubeLogRelationIndexInfoJSON() {
        JSONObject relationIndexInfoJSON = new JSONObject();
        initTableInfoJSON(relationIndexInfoJSON, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_RELATION_NORMAL_INFO);
        return relationIndexInfoJSON;
    }


    private static void initExceptionJson(JSONObject tableExceptionInfoJSON, String exceptionSubTag) {
        Object tableExceptionInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_EXCEPTION_INFO, exceptionSubTag);
        if (tableExceptionInfoMap != null && tableExceptionInfoMap instanceof Map) {
            Iterator<Map.Entry<String, Vector<BILogExceptionInfo>>> it = ((Map) tableExceptionInfoMap).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Vector<BILogExceptionInfo>> entry = it.next();
                Vector<BILogExceptionInfo> exceptionVector = entry.getValue();
                String[] exceptionInfoStringArray = new String[exceptionVector.size()];
                for (int i = 0; i < exceptionVector.size(); i++) {
                    exceptionInfoStringArray[i] = exceptionVector.get(i).toString();
                }
                try {
                    tableExceptionInfoJSON.put(entry.getKey(), exceptionInfoStringArray);
                } catch (JSONException e) {
                    BILoggerFactory.getLogger(BICubeLogJSONHelper.class).error("create cube log exception json error \n " + e.getMessage(), e);
                }
            }
        }
    }

    public static void initTableInfoJSON(JSONObject tableNormalInfoJSON, String normalInfoSubTag) {
        Object normalInfoMap = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, normalInfoSubTag);
        if (normalInfoMap != null && normalInfoMap instanceof Map) {
            Iterator<Map.Entry<String, Map<String, Object>>> it = ((Map) normalInfoMap).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Map<String, Object>> entry = it.next();
                String tableSourceId = entry.getKey();
                try {
                    tableNormalInfoJSON.put(tableSourceId, entry.getValue());
                } catch (JSONException e) {
                    BILoggerFactory.getLogger(BICubeLogJSONHelper.class).error("create cube log normal json error \n " + e.getMessage(), e);
                }
            }
        }
    }


}
