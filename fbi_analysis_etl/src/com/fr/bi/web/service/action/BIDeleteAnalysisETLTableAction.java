package com.fr.bi.web.service.action;

import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/13.
 */
public class BIDeleteAnalysisETLTableAction extends AbstractAnalysisETLAction{
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BIAnalysisETLManagerCenter.getBusiPackManager().removeTable(tableId, userId);
        BIAnalysisETLManagerCenter.getDataSourceManager().removeBusinessTable(new BITableID(tableId));
        BIAnalysisETLManagerCenter.getAliasManagerProvider().getTransManager(userId).removeTransName(tableId);
        new Thread(){
            @Override
            public void run() {
                BIAnalysisETLManagerCenter.getDataSourceManager().persistData(userId);
                BIAnalysisETLManagerCenter.getAliasManagerProvider().persistData(userId);
                BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
            }
        }.run();

    }

    @Override
    public String getCMD() {
        return "delete_table";
    }
}
