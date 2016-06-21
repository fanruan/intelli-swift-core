package com.fr.bi.web.service.action;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceFactory;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TreeSet;

/**
 * Created by 小灰灰 on 2016/5/23.
 */
public class BIAnalysisETLGetFieldMinMaxValueAction extends AbstractAnalysisETLAction{

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        String field = WebUtils.getHTTPRequestParameter(req, "field");
        if(StringUtils.isEmpty(field)){
            WebUtils.printAsJSON(res, new JSONObject());
            return;
        }
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableJSON = WebUtils.getHTTPRequestParameter(req, "table");
        JSONObject jo = new JSONObject(tableJSON);
        JSONArray items = jo.getJSONArray(Constants.ITEMS);
        UserCubeTableSource source = AnalysisETLSourceFactory.createTableSource(items, userId).createUserTableSource(userId);
        ICubeTableService service = PartCubeDataLoader.getInstance(userId, source).getTableIndex(source);
        BIKey key = new IndexKey(field);
        TreeSet tSet = new TreeSet(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
        for (int i = 0; i < service.getRowCount(); i++){
            Object ob = service.getRow(key, i);
            if (ob != null){
                tSet.add(ob);
            }
        }
        JSONObject json = new JSONObject();
        if (tSet.isEmpty()){
            tSet.add(0);
        }
        json.put(BIJSONConstant.JSON_KEYS.FIELD_MIN_VALUE, tSet.first());
        json.put(BIJSONConstant.JSON_KEYS.FILED_MAX_VALUE, tSet.last());
        WebUtils.printAsJSON(res, json);
    }


    @Override
    public String getCMD() {
        return "get_field_min_max_value";
    }
}
