package com.fr.bi.web.service.action;

import com.fr.base.FRContext;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/13.
 */
public class BIRenameAnalysisETLTableAction extends AbstractAnalysisETLAction{
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        String tableName = WebUtils.getHTTPRequestParameter(req, "name");
        String describe = WebUtils.getHTTPRequestParameter(req, "describe");
        BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId).setDescribe(describe);
        BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
        BIConfigureManagerCenter.getAliasManager().setAliasName(tableId, tableName, userId);
        FRContext.getCurrentEnv().writeResource(BIConfigureManagerCenter.getAliasManager().getTransManager(userId));
    }

    @Override
    public String getCMD() {
        return "rename_table";
    }
}
