package com.fr.bi.web.service.action;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.sql.analysis.conf.AnalysisSQLBusiTable;
import com.fr.bi.sql.analysis.data.AnalysisSQLTableSource;
import com.fr.bi.sql.analysis.manager.BIAnalysisSQLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/5/13.
 */
public class BIEditAnalysisETLTableAction extends AbstractAnalysisSQLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        JSONObject jo = new JSONObject();
        jo.put("id", tableId);
        jo.put("name", BIAnalysisSQLManagerCenter.getAliasManagerProvider().getAliasName(tableId, userId));
        AnalysisSQLBusiTable busiTable = BIAnalysisSQLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        jo.put("describe", busiTable.getDescribe());
        JSONObject source = busiTable.getSource().createJSON();
        JSONObject table;
        JSONArray items ;
        if (source.has(Constants.ITEMS)){
            table = source;
        } else {
            table = JSONObject.create();
            items = JSONArray.create();
            items.put(source);
            table.put(Constants.ITEMS, items);
        }

        for (BusinessTable businessTable : BIAnalysisSQLManagerCenter.getDataSourceManager().getAllBusinessTable()){
            AnalysisSQLTableSource ss = (AnalysisSQLTableSource) businessTable.getTableSource();
            Set<AnalysisSQLTableSource> sources = new HashSet<AnalysisSQLTableSource>();
            ss.getSourceUsedAnalysisSQLSource(sources, new HashSet<AnalysisSQLTableSource>());
            if (!ComparatorUtils.equals(ss, busiTable.getTableSource()) && sources.contains(busiTable.getTableSource())){
                jo.put("used", true);
            }
        }
        jo.put("table",table);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "edit_table";
    }
}
