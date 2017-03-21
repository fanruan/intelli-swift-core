package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BICubeLogJSONHelper;
import com.finebi.cube.conf.utils.BILogHelper;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * Created by zcf on 2017/2/28.
 */
public class BIGetCubeLogTableFieldIndexInfoAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONObject tableFieldIndexInfo = BICubeLogJSONHelper.getCubeLogTableFieldIndexInfoJSON();
        JSONObject tableFieldIndexJSON = getTableFieldIndexJSON(tableFieldIndexInfo);
        WebUtils.printAsJSON(res, tableFieldIndexJSON);
    }

    private JSONObject getTableFieldIndexJSON(JSONObject tableFieldIndexInfo) {
        JSONObject tableFieldIndexJSON = new JSONObject();
        try {
            Iterator iterator = tableFieldIndexInfo.keys();
            JSONObject data = new JSONObject();
            JSONObject updating = new JSONObject();
            while (iterator.hasNext()) {
                JSONArray tableInfo = new JSONArray();
                JSONArray updatingInfo = new JSONArray();
                String tableSourceId = (String) iterator.next();
                String tableSourceName = getTableSourceName(tableSourceId);
                JSONObject fieldTimeObject = tableFieldIndexInfo.getJSONObject(tableSourceId);
                Iterator fieldTimeIterator = fieldTimeObject.keys();
                while (fieldTimeIterator.hasNext()) {
                    JSONObject fieldInfo = new JSONObject();
                    String keyInfo = (String) fieldTimeIterator.next();
                    if (keyInfo.endsWith(BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_START)) {
                        String fieldName = keyInfo.split("_FIELD_INDEX_EXECUTE_")[0];
                        String createEndString = fieldName + "_" + BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_END;
                        String tableFieldName = tableSourceName + "." + fieldName;
                        fieldInfo.put("name", tableFieldName);
                        if (fieldTimeObject.has(createEndString)) {
                            long time = getTableFieldIndexTime(fieldTimeObject, fieldName);
                            fieldInfo.put("time", time);
                            tableInfo.put(fieldInfo);
                        } else {
                            long time = fieldTimeObject.optLong(fieldName + "_" + BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_START);
                            fieldInfo.put("time", time);
                            updatingInfo.put(fieldInfo);
                        }
                    }
                }
                data.put(tableSourceId, tableInfo);
                updating.put(tableSourceId, updatingInfo);
            }
            tableFieldIndexJSON.put("data", data);
            tableFieldIndexJSON.put("updating", updating);
        } catch (JSONException e) {
            BILoggerFactory.getLogger(BICubeLogJSONHelper.class).error("create cube log get table field index json error \n " + e.getMessage(), e);
        }
        return tableFieldIndexJSON;
    }

    private long getTableFieldIndexTime(JSONObject fieldTimeObject, String fieldName) {
        long start = fieldTimeObject.optLong(fieldName + "_" + BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_START);
        long end = fieldTimeObject.optLong(fieldName + "_" + BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_END);
        return end - start;
    }

    private String getTableSourceName(String tableSourceId) {
        String tableSourceInfo = BILogHelper.logCubeLogTableSourceInfo(tableSourceId);
        String[] info = tableSourceInfo.split("\n");
        for (int i = 0; i < info.length; i++) {
            String s = info[i];
            if (s.contains("Table Source name")) {
                String[] sArray = s.split(":");
                return sArray[1];
            }
        }
        return "Don't find name";
    }

    @Override
    public String getCMD() {
        return "get_cube_log_table_field_index_info";
    }
}
