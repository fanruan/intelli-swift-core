package com.fr.bi.web.conf.services;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.base.FRContext;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        try {
            Iterator tableIt = BICubeConfigureCenter.getPackageManager().getPackage(userId, new BIPackageID(packageId)).getBusinessTables().iterator();
            ArrayList<BITableRelation> removeList = new ArrayList<BITableRelation>();
            while (tableIt.hasNext()) {
                BusinessTable table = (BusinessTable) tableIt.next();
                if (BICubeConfigureCenter.getTableRelationManager().containTablePrimaryRelation(userId, table)) {
                    IRelationContainer primaryContainer = BICubeConfigureCenter.getTableRelationManager().getPrimaryRelation(userId, table);
                    addToRemoveList(primaryContainer, removeList);
                }

                if (BICubeConfigureCenter.getTableRelationManager().containTableForeignRelation(userId, table)) {
                    IRelationContainer foreignContainer = BICubeConfigureCenter.getTableRelationManager().getForeignRelation(userId, table);
                    addToRemoveList(foreignContainer, removeList);
                }

                //删除权限配置
                BIConfigureManagerCenter.getAuthorityManager().removeAuthPackage(new BIPackageID(packageId));
            }
            for (int i = 0; i < removeList.size(); i++) {
                BICubeConfigureCenter.getTableRelationManager().removeTableRelation(userId, removeList.get(i));
            }


            BICubeConfigureCenter.getPackageManager().removePackage(userId, new BIPackageID(packageId));
        } catch (BIPackageAbsentException e) {

        }
    }

    private void addToRemoveList(IRelationContainer primaryContainer, List<BITableRelation> removeList) throws BIRelationAbsentException, BITableAbsentException {
        Iterator it = primaryContainer.getContainer().iterator();
        while (it.hasNext()) {
            BITableRelation relation = (BITableRelation) it.next();
            if (!removeList.contains(relation)) {
                removeList.add(relation);
            }
        }
    }
}