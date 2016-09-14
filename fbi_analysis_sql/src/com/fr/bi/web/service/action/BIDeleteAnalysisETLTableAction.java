package com.fr.bi.web.service.action;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.sql.analysis.manager.BIAnalysisSQLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/13.
 */
public class BIDeleteAnalysisETLTableAction extends AbstractAnalysisSQLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BusinessTable table = BIAnalysisSQLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        BIAnalysisSQLManagerCenter.getBusiPackManager().removeTable(tableId, userId);
        BIAnalysisSQLManagerCenter.getDataSourceManager().removeTableSource(table);
        BIAnalysisSQLManagerCenter.getAliasManagerProvider().getTransManager(userId).removeTransName(tableId);
        BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
        new Thread(){
            @Override
            public void run() {
                BIAnalysisSQLManagerCenter.getDataSourceManager().persistData(userId);
                BIAnalysisSQLManagerCenter.getAliasManagerProvider().persistData(userId);
                BIAnalysisSQLManagerCenter.getBusiPackManager().persistData(userId);
            }
        }.run();

    }

    @Override
    public String getCMD() {
        return "delete_table";
    }
}
