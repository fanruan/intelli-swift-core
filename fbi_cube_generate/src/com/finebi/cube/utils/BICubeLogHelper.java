package com.finebi.cube.utils;

import com.finebi.cube.common.log.BILogExceptionInfo;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.log.BICubeLogGetterService;
import com.finebi.cube.log.BICubeLogGetterServiceImpl;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by roy on 2017/2/22.
 */
public class BICubeLogHelper extends BILogHelper {

    private static BICubeLogGetterService cubeLogGetterService = new BICubeLogGetterServiceImpl();

    public static JSONObject getCubeLogExceptionInfoJSON() {
        JSONObject tableExceptionInfoJSON = new JSONObject();
        initExceptionJson(tableExceptionInfoJSON, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_TABLE_EXCEPTION_INFO);
        initExceptionJson(tableExceptionInfoJSON, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_RELATION_EXCEPTION_INFO);
        return tableExceptionInfoJSON;
    }

    public static List<Map<String, Object>> getTableTransportLogInfo() {
        return cubeLogGetterService.getTableTransportLogInfo();
    }

    public static List<Map<String, Object>> getTableFieldIndexLogInfo() {
        return cubeLogGetterService.getTableFieldIndexLogInfo();
    }

    public static List<Map<String, Object>> getRelationIndexLogInfo() {
        return cubeLogGetterService.getRelationIndexLogInfo();
    }

    public static JSONObject getTableTranslateProcess() throws JSONException {
        JSONObject tableTranslateProcess = new JSONObject();
        tableTranslateProcess.put("allTranslateTable", cubeLogGetterService.getAllTableNeedTranslate());
        tableTranslateProcess.put("alreadyTranslateTable", cubeLogGetterService.getTableAlreadyTranslate());
        return tableTranslateProcess;
    }

    public static JSONObject getTableFieldIndexProcess() throws JSONException {
        JSONObject tableFieldIndexProcess = new JSONObject();
        tableFieldIndexProcess.put("allFieldIndex", cubeLogGetterService.getAllFieldNeedIndex());
        tableFieldIndexProcess.put("alreadyGenerateFieldIndex", cubeLogGetterService.getFieldAlreadyIndex());
        return tableFieldIndexProcess;
    }

    public static JSONObject getRelationGenerateProcess() throws JSONException {
        JSONObject relationGenerateProcess = new JSONObject();
        relationGenerateProcess.put("allRelation", cubeLogGetterService.getAllRelationNeedGenerate());
        relationGenerateProcess.put("alreadyGenerateRelation", cubeLogGetterService.getRelationAlreadyGenerate());
        return relationGenerateProcess;
    }

    public static JSONObject getCubeLogGenerateStartJSON() {
        JSONObject generateStartJSON = new JSONObject();
        initGenerateTimeInfoJSON(generateStartJSON, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_START);
        return generateStartJSON;
    }

    public static JSONObject getCubeLogGenerateEndJSON() {
        JSONObject generateEndJSON = new JSONObject();
        initGenerateTimeInfoJSON(generateEndJSON, BILogConstant.LOG_CACHE_SUB_TAG.CUBE_GENERATE_END);
        return generateEndJSON;
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
                    BILoggerFactory.getLogger(BICubeLogHelper.class).error("create FineIndex log exception json error \n " + e.getMessage(), e);
                }
            }
        }
    }

    private static void initGenerateTimeInfoJSON(JSONObject timeInfoJSON, String normalInfoSubTag) {
        Object normalInfoTime = BILoggerFactory.getLoggerCacheValue(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, normalInfoSubTag);
        if (normalInfoTime != null) {
            try {
                long time = Long.valueOf(normalInfoTime.toString());
                timeInfoJSON.put("time", time);
            } catch (JSONException e) {
                BILoggerFactory.getLogger(BICubeLogHelper.class).error("create cube log time json error \n " + e.getMessage(), e);
            } catch (NumberFormatException e) {

            }
        }
    }
}
