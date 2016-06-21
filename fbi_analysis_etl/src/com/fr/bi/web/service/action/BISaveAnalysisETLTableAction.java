package com.fr.bi.web.service.action;

import com.finebi.cube.api.BICubeManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceFactory;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class BISaveAnalysisETLTableAction extends AbstractAnalysisETLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        String newId = WebUtils.getHTTPRequestParameter(req, "new_id");
        String tableName = WebUtils.getHTTPRequestParameter(req, "name");
        String describe = WebUtils.getHTTPRequestParameter(req, "describe");
        AnalysisBusiTable table = null;
        CubeTableSource source = null;
        if (StringUtils.isEmpty(newId)) {
            table = new AnalysisBusiTable(tableId, userId);
            table.setDescribe(describe);
            String tableJSON = WebUtils.getHTTPRequestParameter(req, "table");
            JSONObject jo = new JSONObject(tableJSON);
            JSONArray items = jo.getJSONArray(Constants.ITEMS);
            BIAnalysisETLManagerCenter.getAliasManagerProvider().setAliasName(tableId, tableName, userId);
            source = AnalysisETLSourceFactory.createTableSource(items, userId);
            table.setSource(source);
        } else {
            table = new AnalysisBusiTable(newId, userId);
            BIAnalysisETLManagerCenter.getAliasManagerProvider().setAliasName(newId, tableName, userId);
            AnalysisBusiTable oldTable = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            source = oldTable.getSource();
            table.setSource(source);
            table.setDescribe(oldTable.getDescribe());
        }
        BIAnalysisETLManagerCenter.getBusiPackManager().addTable(table);
        BIAnalysisETLManagerCenter.getDataSourceManager().addTableSource(table, source);
        BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().checkTableIndex((AnalysisCubeTableSource) source, new BIUser(userId));
        BIConfigureManagerCenter.getLogManager().logVersion(userId);
        JSONObject result = new JSONObject();
        JSONObject packages = BIAnalysisETLManagerCenter.getBusiPackManager().createPackageJSON(userId);
        JSONObject translations = new JSONObject();
        translations.put(table.getID().getIdentity(), tableName);
        JSONObject tableJSONWithFieldsInfo = table.createJSONWithFieldsInfo(BICubeManager.getInstance().fetchCubeLoader(userId));
        JSONObject tableFields = tableJSONWithFieldsInfo.getJSONObject("tableFields");
        JSONObject tables = new JSONObject();
        tables.put(table.getID().getIdentity(), tableFields);
        JSONObject fields = tableJSONWithFieldsInfo.getJSONObject("fieldsInfo");
        result.put("packages", packages);
        result.put("translations", translations);
        result.put("tables", tables);
        result.put("fields", fields);
        WebUtils.printAsJSON(res, result);
        new Thread (){
            public void  run () {
                BIAnalysisETLManagerCenter.getAliasManagerProvider().persistData(userId);
                BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
                BIAnalysisETLManagerCenter.getDataSourceManager().persistData(userId);
            }
        }.start();
    }

    @Override
    public String getCMD() {
        return "save_table";
    }
}
