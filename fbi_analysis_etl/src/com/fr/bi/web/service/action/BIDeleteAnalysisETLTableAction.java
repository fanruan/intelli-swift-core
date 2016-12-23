package com.fr.bi.web.service.action;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.utils.BILogHelper;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/13.
 */
public class BIDeleteAnalysisETLTableAction extends AbstractAnalysisETLAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BusinessTable table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("*********Remove AnalysisETL table*******");
        BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("The removed table is: " + logTable(table));
        BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("*********Remove AnalysisETL table*******");
        BIAnalysisETLManagerCenter.getBusiPackManager().removeTable(tableId, userId);
        BIAnalysisETLManagerCenter.getDataSourceManager().removeTableSource(table);
        BIAnalysisETLManagerCenter.getAliasManagerProvider().getTransManager(userId).removeTransName(tableId);
        BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
        new Thread() {
            @Override
            public void run() {
                BIAnalysisETLManagerCenter.getDataSourceManager().persistData(userId);
                BIAnalysisETLManagerCenter.getAliasManagerProvider().persistData(userId);
                BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
            }
        }.start();

    }

    @Override
    public String getCMD() {
        return "delete_table";
    }

    private String logTable(BusinessTable table) {
        try {
            return BILogHelper.logAnalysisETLTable(table) +
                    "\n" + "*********Fields of AnalysisETL table*******" +
                    BILogHelper.logAnalysisETLTableField(table, "") +
                    "\n" + "*********Fields of AnalysisETL table*******";
        } catch (Exception e) {
            BILoggerFactory.getLogger(BIBusinessPackage.class).error(e.getMessage(), e);
            return "";
        }
    }
}
