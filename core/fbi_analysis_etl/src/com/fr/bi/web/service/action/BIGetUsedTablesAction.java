package com.fr.bi.web.service.action;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
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
public class BIGetUsedTablesAction extends AbstractAnalysisETLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BusinessTable table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        JSONObject jo = new JSONObject();
        for (BusinessTable businessTable : BIAnalysisETLManagerCenter.getDataSourceManager().getAllBusinessTable()) {
            AnalysisCubeTableSource ss = (AnalysisCubeTableSource) businessTable.getTableSource();
            Set<AnalysisCubeTableSource> sources = new HashSet<AnalysisCubeTableSource>();
            ss.getSourceUsedAnalysisETLSource(sources);
            if (!ComparatorUtils.equals(ss, table.getTableSource()) && sources.contains(table.getTableSource())) {
                long createBy = ((AnalysisBusiTable) businessTable).getUserId();
                String transName = BIAnalysisETLManagerCenter.getAliasManagerProvider().getTransManager(createBy).getTransName(businessTable.getID().getIdentity());
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