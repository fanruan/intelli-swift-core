package com.fr.bi.web.service.action;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
        jo.put("name", BIConfigureManagerCenter.getAliasManager().getAliasName(tableId, userId));
        List<String> sheets = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId).getSheets();
        JSONObject source = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSourceByID(new BITableID(tableId), new BIUser(userId)).createJSON();
        JSONObject table;
        JSONArray items ;
        if (source.has(Constants.ITEMS)){
            table = source;
            items = source.getJSONArray(Constants.ITEMS);
        } else {
            table = new JSONObject();
            items = new JSONArray();
            items.put(source);
            table.put(Constants.ITEMS, items);
        }
        boolean incorrect = sheets == null || sheets.size() < items.length();
        for (int i = 0; i < items.length(); i++){
            items.getJSONObject(i).put("table_name", incorrect ? "sheet" + (i+1) : sheets.get(i));
        }
        jo.put("table",table);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "edit_table";
    }
}
