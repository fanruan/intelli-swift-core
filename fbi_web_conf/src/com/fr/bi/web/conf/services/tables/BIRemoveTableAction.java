package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIWriteConfigResourcesUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/12/20.
 */
public class BIRemoveTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String packageId = WebUtils.getHTTPRequestParameter(req, "packageId");
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");

        BusinessTable table = BICubeConfigureCenter.getPackageManager().getPackage(userId, new BIPackageID(packageId)).getSpecificTable(new BITableID(tableId));
        BusinessTableHelper.removeTableRelation(table, userId);
        BICubeConfigureCenter.getPackageManager().removeTable(userId, new BIPackageID(packageId), new BITableID(tableId));

        BIWriteConfigResourcesUtils.writeResourceAsync(userId);

    }

    @Override
    public String getCMD() {
        return "remove_table";
    }
}
