package com.fr.bi.web.service.action;

import com.fr.bi.base.BIBusinessPackagePersistThread;
import com.fr.bi.cluster.utils.BIUserAuthUtils;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/13.
 */
public class BIRenameAnalysisETLTableAction extends AbstractAnalysisETLAction{


    private static BIBusinessPackagePersistThread biBusinessPackagePersistThread = new BIBusinessPackagePersistThread();
    static {
        biBusinessPackagePersistThread.start();
    }
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = BIUserAuthUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        String tableName = WebUtils.getHTTPRequestParameter(req, "name");
        String describe = WebUtils.getHTTPRequestParameter(req, "describe");
        BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId).setDescribe(describe);
        BIAnalysisETLManagerCenter.getAliasManagerProvider().setAliasName(tableId, tableName, userId);
        BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
        biBusinessPackagePersistThread.triggerWork(new BIBusinessPackagePersistThread.Action(){
            @Override
            public void work() {
                BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
                BIAnalysisETLManagerCenter.getAliasManagerProvider().persistData(userId);
            }
        });
    }

    @Override
    public String getCMD() {
        return "rename_table";
    }
}
