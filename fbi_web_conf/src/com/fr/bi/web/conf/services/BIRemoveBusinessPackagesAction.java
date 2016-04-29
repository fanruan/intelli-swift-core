package com.fr.bi.web.conf.services;

import com.fr.base.FRContext;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.base.relation.relation.IRelationContainer;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.relation.BITableRelation;
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
            BIConfigureManagerCenter.getPackageManager().persistData(userId);
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
            Iterator tableIt = BIConfigureManagerCenter.getPackageManager().getPackage(userId, new BIPackageID(packageId)).getBusinessTables().iterator();
            ArrayList<BITableRelation> removeList = new ArrayList<BITableRelation>();
            while (tableIt.hasNext()) {
                BITable table = (BITable) tableIt.next();
                if (BIConfigureManagerCenter.getTableRelationManager().containTablePrimaryRelation(userId, table)) {
                    IRelationContainer primaryContainer = BIConfigureManagerCenter.getTableRelationManager().getPrimaryRelation(userId, table);
                    addToRemoveList(primaryContainer, removeList);
                }

                if (BIConfigureManagerCenter.getTableRelationManager().containTableForeignRelation(userId, table)) {
                    IRelationContainer foreignContainer = BIConfigureManagerCenter.getTableRelationManager().getForeignRelation(userId, table);
                    addToRemoveList(foreignContainer, removeList);
                }
            }
            for (int i = 0; i < removeList.size(); i++) {
                BIConfigureManagerCenter.getTableRelationManager().removeTableRelation(userId, removeList.get(i));
            }
            //TODO Connery: 又是名字
            BIConfigureManagerCenter.getPackageManager().removePackage(userId, new BIPackageID(packageId));
        } catch (BIPackageAbsentException e) {
            //TODO Connery: 异常应该相应的处理，通过前端显示给用户，而不是捕获记录日志
//            BILogger.getLogger().error(e.getMessage(), e);
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