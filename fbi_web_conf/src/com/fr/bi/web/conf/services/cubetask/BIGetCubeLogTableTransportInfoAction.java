package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BICubeLogJSONHelper;
import com.finebi.cube.conf.utils.BILogHelper;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * Created by zcf on 2017/2/28.
 */
public class BIGetCubeLogTableTransportInfoAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONObject tableTransportInfo = BICubeLogJSONHelper.getCubeLogTableTransportInfoJSON();
        JSONObject tableTransportJSON = getTableTransportInfo(tableTransportInfo);

        WebUtils.printAsJSON(res, tableTransportJSON);
    }

    private JSONObject getTableTransportInfo(JSONObject tableTransportInfo) {
        JSONObject tableTransportData = new JSONObject();
        try {
            Iterator iterator = tableTransportInfo.keys();
            JSONObject data = new JSONObject();
            JSONObject updating = new JSONObject();
            while (iterator.hasNext()) {
                JSONObject tableInfo = new JSONObject();
                String tableSourceId = (String) iterator.next();
                String tableSourceName = getTableSourceName(tableSourceId);
                tableInfo.put("name", tableSourceName);
                JSONObject timeObject = tableTransportInfo.getJSONObject(tableSourceId);
                if (isUpdatedTable(timeObject)) {
                    long time = getTableTransportTime(timeObject);
                    tableInfo.put("time", time);
                    data.put(tableSourceId, tableInfo);
                }
                if (isUpdatingTable(timeObject)) {
                    long time = timeObject.optLong(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START);
                    tableInfo.put("time", time);
                    updating.put(tableSourceId, tableInfo);
                }
            }
            tableTransportData.put("data", data);
            tableTransportData.put("updating", updating);
        } catch (JSONException e) {
            BILoggerFactory.getLogger(BICubeLogJSONHelper.class).error("create cube log get table Transport json error \n " + e.getMessage(), e);
        }
        return tableTransportData;
    }

    private boolean isUpdatingTable(JSONObject timeObject) {
        return timeObject.has(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START) && !timeObject.has(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END);
    }

    private boolean isUpdatedTable(JSONObject timeObject) {
        return timeObject.has(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START) && timeObject.has(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END);
    }

    private long getTableTransportTime(JSONObject timeObject) {
        long start = timeObject.optLong(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START);
        long end = timeObject.optLong(BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END);
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
        return "get_cube_log_table_transport_info";
    }
}
