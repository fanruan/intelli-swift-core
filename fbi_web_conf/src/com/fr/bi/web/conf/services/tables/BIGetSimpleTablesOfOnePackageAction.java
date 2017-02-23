package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.*;
import com.finebi.cube.conf.pack.group.IBusinessGroupGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.provider.BIAuthorityManageProvider;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.file.DatasourceManager;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

        Iterator connNames = DatasourceManager.getInstance().getConnectionNameIterator();
        JSONArray namesJA = new JSONArray();
        while (connNames.hasNext()) {
            namesJA.put(connNames.next());
        }
        namesJA.put(DBConstant.CONNECTION.SERVER_CONNECTION);

        JSONObject jo = new JSONObject();
        jo.put("tables", tablesJO);
        jo.put("connNames", namesJA);
        WebUtils.printAsJSON(res, jo);
    }

    //可能是新添加的分组、业务包
    private IBusinessPackageGetterService getPackage(String packageName, String groupName, String packageId, long userId) throws Exception {
        BIPackageID packageID = new BIPackageID(packageId);
        BISystemPackageConfigurationProvider packageConfigProvider = BICubeConfigureCenter.getPackageManager();
        IBusinessPackageGetterService pack;
        boolean isNewPackage = false;
        if (!packageConfigProvider.containPackageID(userId, packageID)) {
            BIBusinessPackage biBasicBusinessPackage = new BIBasicBusinessPackage(new BIPackageID(packageId), new BIPackageName(packageName), new BIUser(userId), System.currentTimeMillis());
            packageConfigProvider.addPackage(userId, biBasicBusinessPackage);
            pack = packageConfigProvider.getPackage(userId, packageID);
            isNewPackage = true;
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
        //添加业务包自动分配权限
        if (isNewPackage) {
            assignAuthority(userId, packageID, groupName);
        }
        return pack;
    }

    /**
     * 分配给所有拥有 当前业务包所在分组权限 的角色
     *
     * @param packageID
     */
    private void assignAuthority(long userId, BIPackageID packageID, String groupName) throws Exception {
        List<BIDataConfigAuthority> assignAuth = new ArrayList<BIDataConfigAuthority>();
        Set<BIDataConfigAuthority> authorities = BIConfigureManagerCenter.getDataConfigAuthorityManager().getAllDataConfigAuthorities();
        for (BIDataConfigAuthority authority : authorities) {
            String id = authority.getId();
            String pId = authority.getpId();
            if (ComparatorUtils.equals(pId, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.PACKAGE_GROUP)) {
                //未分组
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.UNGROUP_PACKAGE) &&
                        ComparatorUtils.equals(groupName, StringUtils.EMPTY)) {
                    assignAuth.add(authority);
                } else {
                    BIGroupTagName groupTagName = new BIGroupTagName(groupName);
                    IBusinessGroupGetterService groupGetterService = BICubeConfigureCenter.getPackageManager().getGroup(UserControl.getInstance().getSuperManagerID(), groupTagName);
                    long position = groupGetterService.getPosition();
                    String groupPos = id.substring(DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER_CHILDREN.PACKAGE_GROUP.length());
                    if (ComparatorUtils.equals(groupPos, GeneralUtils.objectToString(position))) {
                        assignAuth.add(authority);
                    }
                }
            }
        }
        List<BIPackageAuthority> packageAuthorities = new ArrayList<BIPackageAuthority>();
        for (BIDataConfigAuthority authority : assignAuth) {
            BIPackageAuthority packageAuthority = new BIPackageAuthority();
            packageAuthority.setRoleName(authority.getRoleName());
            packageAuthority.setRoleType(authority.getRoleType());
            packageAuthorities.add(packageAuthority);
        }
        BIAuthorityManageProvider manager = BIConfigureManagerCenter.getAuthorityManager();
        manager.savePackageAuth(packageID, packageAuthorities, userId);
        manager.persistData(userId);
    }

    @Override
    public String getCMD() {
        return "get_simple_tables_of_one_package";
    }

}
