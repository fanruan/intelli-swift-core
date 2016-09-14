package com.fr.bi.web.service.action;

import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.sql.analysis.conf.AnalysisSQLBusiTable;
import com.fr.bi.sql.analysis.data.AnalysisSQLSourceFactory;
import com.fr.bi.sql.analysis.manager.BIAnalysisSQLManagerCenter;
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
public class BISaveAnalysisETLTableAction extends AbstractAnalysisSQLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        String newId = WebUtils.getHTTPRequestParameter(req, "new_id");
        String tableName = WebUtils.getHTTPRequestParameter(req, "name");
        String describe = WebUtils.getHTTPRequestParameter(req, "describe");
        AnalysisSQLBusiTable table = null;
        CubeTableSource source = null;
        if (StringUtils.isEmpty(newId)) {
            table = new AnalysisSQLBusiTable(tableId, userId);
            table.setDescribe(describe);
            String tableJSON = WebUtils.getHTTPRequestParameter(req, "table");
            JSONObject jo = new JSONObject(tableJSON);
            JSONArray items = jo.getJSONArray(Constants.ITEMS);
            BIAnalysisSQLManagerCenter.getAliasManagerProvider().setAliasName(tableId, tableName, userId);
            source = AnalysisSQLSourceFactory.createTableSource(items, userId);
            table.setSource(source);
        } else {
            table = new AnalysisSQLBusiTable(newId, userId);
            BIAnalysisSQLManagerCenter.getAliasManagerProvider().setAliasName(newId, tableName, userId);
            AnalysisSQLBusiTable oldTable = BIAnalysisSQLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            source = oldTable.getSource();
            table.setSource(source);
            table.setDescribe(oldTable.getDescribe());
        }
        BIAnalysisSQLManagerCenter.getBusiPackManager().addTable(table);
        BIAnalysisSQLManagerCenter.getDataSourceManager().addTableSource(table, source);
        BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
        JSONObject result = JSONObject.create();
        JSONObject packages = BIAnalysisSQLManagerCenter.getBusiPackManager().createPackageJSON(userId);
        JSONObject translations = JSONObject.create();
        translations.put(table.getID().getIdentity(), tableName);
        JSONObject tableJSONWithFieldsInfo = table.createJSONWithFieldsInfo(userId);
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
                BIAnalysisSQLManagerCenter.getAliasManagerProvider().persistData(userId);
                BIAnalysisSQLManagerCenter.getBusiPackManager().persistData(userId);
                BIAnalysisSQLManagerCenter.getDataSourceManager().persistData(userId);
            }
        }.start();
    }

    @Override
    public String getCMD() {
        return "save_table";
    }
}
