package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.*;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.file.DatasourceManager;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;

/**
 * 进入业务包的时候获取简略的表信息
 * 包含表id、原始表名和转义名
 * Created by Young's on 2016/12/15.
 */
public class BIGetSimpleTablesOfOnePackageAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = UserControl.getInstance().getSuperManagerID();
        String packageId = WebUtils.getHTTPRequestParameter(req, BIJSONConstant.JSON_KEYS.ID);
        String packageName = WebUtils.getHTTPRequestParameter(req, BIJSONConstant.JSON_KEYS.PACKAGE_NAME);
        String groupName = WebUtils.getHTTPRequestParameter(req, BIJSONConstant.JSON_KEYS.GROUP_NAME);

        //找不到业务包的时候认为是新添加的，同样分组找不到也认为是新添加的
        IBusinessPackageGetterService businessPackage = getPackage(packageName, groupName, packageId, userId);

        Set<BusinessTable> tables = businessPackage.getBusinessTables();
        Iterator<BusinessTable> tableIterator = tables.iterator();
        JSONObject tablesJO = new JSONObject();
        JSONArray tableIds = new JSONArray();
        while (tableIterator.hasNext()) {
            BusinessTable table = tableIterator.next();
            tableIds.put(table.getID().getIdentityValue());
            table.createJSON();
            String tableId = table.getID().getIdentityValue();
            CubeTableSource source = BusinessTableHelper.getTableDataSource(new BITableID(tableId));
            JSONObject tableJO = source.createJSON();
            tableJO.put(BIJSONConstant.JSON_KEYS.ID, tableId);
            tableJO.put(BIJSONConstant.JSON_KEYS.TRAN_NAME, BICubeConfigureCenter.getAliasManager().getTransManager(userId).getTransName(tableId));
            tablesJO.put(tableId, tableJO);
        }

        Iterator connNames = DatasourceManager.getInstance().getConnectionNameIterator();
        JSONArray namesJA = new JSONArray();
        while (connNames.hasNext()) {
            namesJA.put(connNames.next());
        }
        namesJA.put(DBConstant.CONNECTION.SERVER_CONNECTION);

        JSONObject jo = new JSONObject();
        jo.put("tables", tablesJO);
        jo.put("tableIds", tableIds);
        jo.put("connNames", namesJA);
        WebUtils.printAsJSON(res, jo);
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
        if (!ComparatorUtils.equals(groupName, StringUtils.EMPTY)) {
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
