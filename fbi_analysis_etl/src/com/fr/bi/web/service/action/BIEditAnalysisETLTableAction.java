package com.fr.bi.web.service.action;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/13.
 */
public class BIEditAnalysisETLTableAction extends AbstractAnalysisETLAction{
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        JSONObject jo = new JSONObject();
        jo.put("id", tableId);
        jo.put("name", BICubeConfigureCenter.getAliasManager().getAliasName(tableId, userId));
        AnalysisBusiTable busiTable = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        jo.put("describe", busiTable.getDescribe());
        JSONObject source = busiTable.getSource().createJSON();
        JSONObject table;
        JSONArray items ;
        if (source.has(Constants.ITEMS)){
            table = source;
        } else {
            table = new JSONObject();
            items = new JSONArray();
            items.put(source);
            table.put(Constants.ITEMS, items);
        }
        jo.put("table",table);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "edit_table";
    }
}
