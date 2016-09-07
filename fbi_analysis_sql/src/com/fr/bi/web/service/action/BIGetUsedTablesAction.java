package com.fr.bi.web.service.action;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.sql.analysis.conf.AnalysisSQLBusiTable;
import com.fr.bi.sql.analysis.data.AnalysisSQLTableSource;
import com.fr.bi.sql.analysis.manager.BIAnalysisSQLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/6/30.
 */
public class BIGetUsedTablesAction extends AbstractAnalysisSQLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BusinessTable table = BIAnalysisSQLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        JSONObject jo = JSONObject.create();
        for (BusinessTable businessTable : BIAnalysisSQLManagerCenter.getDataSourceManager().getAllBusinessTable()) {
            AnalysisSQLTableSource ss = (AnalysisSQLTableSource) businessTable.getTableSource();
            Set<AnalysisSQLTableSource> sources = new HashSet<AnalysisSQLTableSource>();
            ss.getSourceUsedAnalysisSQLSource(sources, new HashSet<AnalysisSQLTableSource>());
            if (!ComparatorUtils.equals(ss, table.getTableSource()) && sources.contains(table.getTableSource())) {
                long createBy = ((AnalysisSQLBusiTable) businessTable).getUserId();
                String transName = BIAnalysisSQLManagerCenter.getAliasManagerProvider().getTransManager(createBy).getTransName(businessTable.getID().getIdentity());
                jo.put("table", transName);
                break;
            }
        }
        WebUtils.printAsJSON(res, jo);

    }

    @Override
    public String getCMD() {
        return "get_used_tables";
    }
}