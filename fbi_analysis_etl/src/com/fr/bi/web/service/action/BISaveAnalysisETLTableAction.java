package com.fr.bi.web.service.action;

import com.fr.bi.base.BIUser;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceFactory;
import com.fr.bi.etl.analysis.manager.AnalysisDataSourceManager;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class BISaveAnalysisETLTableAction extends AbstractAnalysisETLAction{
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        String tableName = WebUtils.getHTTPRequestParameter(req, "name");
        String tableJSON = WebUtils.getHTTPRequestParameter(req, "table");
        AnalysisBusiTable table = new AnalysisBusiTable(tableId, userId, tableName);
        BIAnalysisETLManagerCenter.getBusiPackManager().addTable(table);
        JSONObject jo = new JSONObject(tableJSON);
        BIAnalysisETLManagerCenter.getDataSourceManager().addCore2SourceRelation(table.getID(), AnalysisETLSourceFactory.createTableSource(jo.getJSONArray(Constants.ITEMS), userId), new BIUser(userId));
        BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
        ((AnalysisDataSourceManager)BIAnalysisETLManagerCenter.getDataSourceManager()).persistUserData(userId);
    }

    @Override
    public String getCMD() {
        return "save_table";
    }
}
