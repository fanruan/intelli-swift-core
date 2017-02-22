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

}
