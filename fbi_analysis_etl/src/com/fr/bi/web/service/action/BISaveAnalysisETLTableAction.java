package com.fr.bi.web.service.action;

import com.finebi.cube.api.BICubeManager;
import com.fr.base.FRContext;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceFactory;
import com.fr.bi.etl.analysis.data.AnalysisTableSource;
import com.fr.bi.etl.analysis.manager.AnalysisDataSourceManager;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class BISaveAnalysisETLTableAction extends AbstractAnalysisETLAction{
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        String newId = WebUtils.getHTTPRequestParameter(req, "new_id");
        String tableName = WebUtils.getHTTPRequestParameter(req, "name");
        AnalysisBusiTable table = null;
        AnalysisTableSource source = null;
        if (StringUtils.isEmpty(newId)){
            table  = new AnalysisBusiTable(tableId, userId);
            String tableJSON = WebUtils.getHTTPRequestParameter(req, "table");
            JSONObject jo = new JSONObject(tableJSON);
            JSONArray items = jo.getJSONArray(Constants.ITEMS);
            List<String> sheets = new ArrayList<String>();
            for (int i = 0; i < items.length(); i++){
                sheets.add(items.getJSONObject(i).getString("table_name"));
            }
            table.setSheets(sheets);
            BIConfigureManagerCenter.getAliasManager().setAliasName(tableId, tableName, userId);
            source = AnalysisETLSourceFactory.createTableSource(items, userId);
        } else {
            table  = new AnalysisBusiTable(newId, userId);
            BIConfigureManagerCenter.getAliasManager().setAliasName(newId, tableName, userId);
            AnalysisBusiTable oldTable = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            table.setSheets(oldTable.getSheets());
            source = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSourceByID(oldTable.getID(), new BIUser(userId));
        }
        FRContext.getCurrentEnv().writeResource(BIConfigureManagerCenter.getAliasManager().getTransManager(userId));
        BIAnalysisETLManagerCenter.getBusiPackManager().addTable(table);
        BIAnalysisETLManagerCenter.getDataSourceManager().addCore2SourceRelation(table.getID(),source , new BIUser(userId));
        BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
        ((AnalysisDataSourceManager)BIAnalysisETLManagerCenter.getDataSourceManager()).persistUserData(userId);
        JSONObject result = new JSONObject();
        JSONObject packages = BIAnalysisETLManagerCenter.getBusiPackManager().createPackageJSON(userId);
        JSONObject translations = new JSONObject();
        translations.put(tableId, tableName);
        JSONObject tableJSONWithFieldsInfo = table.createJSONWithFieldsInfo(BICubeManager.getInstance().fetchCubeLoader(userId));
        JSONObject tableFields = tableJSONWithFieldsInfo.getJSONObject("tableFields");
        JSONObject tables = new JSONObject();
        tables.put(tableId, tableFields);
        JSONObject fields = tableJSONWithFieldsInfo.getJSONObject("fieldsInfo");
        result.put("packages", packages);
        result.put("translations", translations);
        result.put("tables", tables);
        result.put("fields", fields);
        WebUtils.printAsJSON(res, result);
    }

    @Override
    public String getCMD() {
        return "save_table";
    }
}
