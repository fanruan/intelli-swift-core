package com.fr.bi.web.service.action;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.utils.BILogHelper;
import com.fr.bi.base.BIBusinessPackagePersistThread;
import com.fr.bi.cluster.utils.BIUserAuthUtils;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.general.ComparatorUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/5/13.
 * edit by kary 2016/12/22  修改删除逻辑，没被其他表用到的source才会被删除
 */
public class BIDeleteAnalysisETLTableAction extends AbstractAnalysisETLAction {


    static BIBusinessPackagePersistThread biBusinessPackagePersistThread = new BIBusinessPackagePersistThread();
    static {
        biBusinessPackagePersistThread.start();
    }
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = BIUserAuthUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BusinessTable table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("*********Remove AnalysisETL table*******");
        BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("The removed table is: " + logTable(table));
        BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("*********Remove AnalysisETL table*******");
        BIAnalysisETLManagerCenter.getBusiPackManager().removeTable(tableId, userId);
        boolean isUsed = inUseCheck(userId, table);
        if (!isUsed) {
            BIAnalysisETLManagerCenter.getDataSourceManager().removeTableSource(table);
        }
        BIAnalysisETLManagerCenter.getAliasManagerProvider().getTransManager(userId).removeTransName(tableId);
        BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
        biBusinessPackagePersistThread.triggerWork(new BIBusinessPackagePersistThread.Action(){
            @Override
            public void work() {
                BIAnalysisETLManagerCenter.getDataSourceManager().persistData(userId);
                BIAnalysisETLManagerCenter.getAliasManagerProvider().persistData(userId);
                BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
            }
        });

    }

    /**
     * 检查该表的source是否被其他表使用
     * @param userId
     * @param table
     * @return
     * @throws BIKeyAbsentException
     */
    private boolean inUseCheck(long userId, BusinessTable table) throws BIKeyAbsentException {
        CubeTableSource source = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSource(table);
        for (BusinessTable businessTable : BIAnalysisETLManagerCenter.getBusiPackManager().getAllTables(userId)) {
            if (ComparatorUtils.equals(table.getID(), businessTable.getID())) {
                continue;
            }
            if (ComparatorUtils.equals(businessTable.getTableSource().getSourceID(), source.getSourceID())) {
                return true;
            }
        }
        return false;
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
