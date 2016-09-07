package com.fr.bi.web.service.action;

import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.sql.analysis.manager.BIAnalysisSQLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/13.
 */
public class BIRenameAnalysisETLTableAction extends AbstractAnalysisSQLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        String tableName = WebUtils.getHTTPRequestParameter(req, "name");
        String describe = WebUtils.getHTTPRequestParameter(req, "describe");
        BIAnalysisSQLManagerCenter.getBusiPackManager().getTable(tableId, userId).setDescribe(describe);
        BIAnalysisSQLManagerCenter.getAliasManagerProvider().setAliasName(tableId, tableName, userId);
        BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
        new Thread(){
            @Override
            public void run() {
                BIAnalysisSQLManagerCenter.getBusiPackManager().persistData(userId);
                BIAnalysisSQLManagerCenter.getAliasManagerProvider().persistData(userId);
            }
        }.start();
    }

    @Override
    public String getCMD() {
        return "rename_table";
    }
}
