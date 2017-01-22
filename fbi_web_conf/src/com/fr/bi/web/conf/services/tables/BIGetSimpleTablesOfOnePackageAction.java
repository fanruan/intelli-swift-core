package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.*;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * 进入业务包的时候获取简略的表信息
 * 包含表id、原始表名和转义名
 * Created by Young's on 2016/12/15.
 */
public class BIGetSimpleTablesOfOnePackageAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String packageId = WebUtils.getHTTPRequestParameter(req, "id");
        String packageName = WebUtils.getHTTPRequestParameter(req, "packageName");
        String groupName = WebUtils.getHTTPRequestParameter(req, "groupName");

        //找不到业务包的时候认为是新添加的，同样分组找不到也认为是新添加的
        IBusinessPackageGetterService businessPackage = getPackage(packageName, groupName, packageId, userId);

        Set<BusinessTable> tables = businessPackage.getBusinessTables();
        JSONObject tablesJO = new JSONObject();
        for (BusinessTable table : tables) {
            String tableId = table.getID().getIdentityValue();
            CubeTableSource source = BusinessTableHelper.getTableDataSource(new BITableID(tableId));
            JSONObject tableJO = source.createJSON();
            tableJO.put("id", tableId);
            tablesJO.put(tableId, tableJO);
        }
        WebUtils.printAsJSON(res, tablesJO);
    }

    //可能是新添加的分组、业务包
    private IBusinessPackageGetterService getPackage(String packageName, String groupName, String packageId, long userId) throws Exception {
        BIPackageID packageID = new BIPackageID(packageId);
        BISystemPackageConfigurationProvider packageConfigProvider = BICubeConfigureCenter.getPackageManager();
        IBusinessPackageGetterService pack;
        if (!packageConfigProvider.containPackageID(userId, packageID)) {
            BIBusinessPackage biBasicBusinessPackage = new BIBasicBusinessPackage(new BIPackageID(packageId), new BIPackageName(packageName), new BIUser(userId), System.currentTimeMillis());
            packageConfigProvider.addPackage(userId, biBasicBusinessPackage);
            pack = packageConfigProvider.getPackage(userId, packageID);
        } else {
            pack = packageConfigProvider.getPackage(userId, packageID);
            if (!ComparatorUtils.equals(packageName, pack.getName().getValue())) {
                BICubeConfigureCenter.getPackageManager().renamePackage(userId, packageID, new BIPackageName(packageName));
            }
        }
        if (!"".equals(groupName)) {
            BIGroupTagName groupTagName = new BIGroupTagName(groupName);
            if (!packageConfigProvider.containGroup(userId, groupTagName)) {
                packageConfigProvider.createEmptyGroup(userId, groupTagName, System.currentTimeMillis());
            }
            if (!packageConfigProvider.isPackageTaggedSpecificGroup(userId, packageID, groupTagName)) {
                packageConfigProvider.stickGroupTagOnPackage(userId, packageID, groupTagName);
            }

        }
        return pack;
    }

    @Override
    public String getCMD() {
        return "get_simple_tables_of_one_package";
    }

}
