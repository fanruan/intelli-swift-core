package com.fr.bi.web.conf.services;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.base.FRContext;
import com.fr.bi.cal.BICubeManager;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.bridge.StableFactory;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.logging.Level;

public class BIRemoveBusinessPackagesAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "remove_business_package";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String packageId = WebUtils.getHTTPRequestParameter(req, "id");
        doRemoveBusinessPackage(packageId, req, res);
    }

    private void doRemoveBusinessPackage(String packageId, HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        removePackageByName(packageId, userId);
        try {
            BICubeConfigureCenter.getPackageManager().persistData(userId);
            BIConfigureManagerCenter.getAuthorityManager().persistData(userId);
            BICubeManager biCubeManager= StableFactory.getMarkedObject(BICubeManagerProvider.XML_TAG,BICubeManager.class);
            biCubeManager.resetCubeGenerationHour(userId);
        } catch (Exception e) {
            FRContext.getLogger().log(Level.WARNING, e.getMessage(), e);
        }
    }

    /**
     * 移除业务包
     *
     * @param packageId 业务包名
     */
    public void removePackageByName(String packageId, long userId) throws BIRelationAbsentException, BITableAbsentException {
        if (StringUtils.isEmpty(packageId)) {
            return;
        }
        //删除权限配置
        BIConfigureManagerCenter.getAuthorityManager().removeAuthPackage(new BIPackageID(packageId));
        try {
            Iterator tableIt = BICubeConfigureCenter.getPackageManager().getPackage(userId, new BIPackageID(packageId)).getBusinessTables().iterator();
            while (tableIt.hasNext()) {
                BusinessTable table = (BusinessTable) tableIt.next();
                BusinessTableHelper.removeTableRelation(table, userId);
            }
            saveUpdateSettings(packageId, userId);
            BICubeConfigureCenter.getPackageManager().removePackage(userId, new BIPackageID(packageId));
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    private void saveUpdateSettings(String packageId, long userId) throws BIPackageAbsentException {
        Iterator tableIt = BICubeConfigureCenter.getPackageManager().getPackage(userId, new BIPackageID(packageId)).getBusinessTables().iterator();
        while (tableIt.hasNext()) {
            BusinessTable table = (BusinessTable) tableIt.next();
            BIConfigureManagerCenter.getUpdateFrequencyManager().removeUpdateSetting(table.getTableSource().getSourceID(),userId);
        }
        BIConfigureManagerCenter.getUpdateFrequencyManager().persistData(userId);
    }
}
