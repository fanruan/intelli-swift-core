package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BICubeLogJSONHelper;
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
public class BIGetCubeLogRelationIndexInfoAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONObject relationIndexInfo = BICubeLogJSONHelper.getCubeLogRelationIndexInfoJSON();
        JSONObject relationIndexJSON = getRelationIndexJSON(relationIndexInfo);

        WebUtils.printAsJSON(res, relationIndexJSON);
    }

    private JSONObject getRelationIndexJSON(JSONObject relationIndexInfo) {
        JSONObject relationIndexJSON = new JSONObject();
        try {
            Iterator iterable = relationIndexInfo.keys();
            JSONObject data = new JSONObject();
            JSONObject updating = new JSONObject();
            while (iterable.hasNext()) {
                JSONObject tableInfo = new JSONObject();
                String relationId = (String) iterable.next();
                String relationName = getRelationName(relationId);
                tableInfo.put("name", relationName);
                JSONObject timeObject = relationIndexInfo.getJSONObject(relationId);
                if (isUpdatedTable(timeObject)) {
                    long time = getRelationIndexTime(timeObject);
                    tableInfo.put("time", time);
                    data.put(relationId, tableInfo);
                }
                if (isUpdatingTable(timeObject)) {
                    long time = timeObject.optLong(BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_START);
                    tableInfo.put("time", time);
                    updating.put(relationId, tableInfo);
                }
            }
            relationIndexJSON.put("data", data);
            relationIndexJSON.put("updating", updating);
        } catch (JSONException e) {
            BILoggerFactory.getLogger(BICubeLogJSONHelper.class).error("create cube log get relation index json error \n " + e.getMessage(), e);
        }
        return relationIndexJSON;
    }

    private long getRelationIndexTime(JSONObject timeObject) {
        long start = timeObject.optLong(BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_START);
        long end = timeObject.optLong(BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_END);
        return end - start;
    }

    private boolean isUpdatedTable(JSONObject timeObject) {
        return timeObject.has(BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_START) && timeObject.has(BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_END);
    }

    private boolean isUpdatingTable(JSONObject timeObject) {
        return timeObject.has(BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_START) && !timeObject.has(BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_END);
    }

    private String getRelationName(String relationId) {//todo
        return relationId;
    }

    @Override
    public String getCMD() {
        return "get_cube_log_relation_index_info";
    }
}