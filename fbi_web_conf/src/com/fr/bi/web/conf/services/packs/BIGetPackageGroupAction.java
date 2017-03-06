package com.fr.bi.web.conf.services.packs;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BIGetPackageGroupAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_business_package_group";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        BISystemPackageConfigurationProvider packageManager = BICubeConfigureCenter.getPackageManager();
        JSONObject packagesJO = packageManager.createPackageJSON(userId);
        JSONObject groupsJO = packageManager.createGroupJSON(userId);
        JSONObject jo = new JSONObject();
        if (!ComparatorUtils.equals(userId, UserControl.getInstance().getSuperManagerID())) {
            List<String> need2Remove = new ArrayList<String>();
            //业务包
            List<BIPackageID> authPacks = BIConfigureManagerCenter.getAuthorityManager().getAuthPackagesByUser(userId);
            Iterator<String> packKeys = packagesJO.keys();
            while (packKeys.hasNext()) {
                String packId = packKeys.next();
                IBusinessPackageGetterService packageGetterService = BICubeConfigureCenter.getPackageManager().getPackage(UserControl.getInstance().getSuperManagerID(), new BIPackageID(packId));
                BIUser user = packageGetterService.getOwner();
                //只展示有权限或者当前用户创建的
                if (!authPacks.contains(new BIPackageID(packId)) &&
                        !ComparatorUtils.equals(user.getUserId(), userId)) {
                    need2Remove.add(packId);
                }
            }
            for (String removeId : need2Remove) {
                packagesJO.remove(removeId);
            }

            //分组
            dealWithGroups(groupsJO, packagesJO);
        }
        jo.put("packages", packagesJO).put("groups", groupsJO);
        WebUtils.printAsJSON(res, jo);
    }

    /**
     * 只要该分组中存在有权限的业务包即展示
     *
     * @param groupsJO
     * @param packagesJO
     * @throws Exception
     */
    private void dealWithGroups(JSONObject groupsJO, JSONObject packagesJO) throws Exception {
        List<String> need2Remove = new ArrayList<String>();
        Iterator<String> groupKeys = groupsJO.keys();
        while (groupKeys.hasNext()) {
            String groupId = groupKeys.next();
            JSONObject groupJO = groupsJO.getJSONObject(groupId);
            JSONArray children = groupJO.getJSONArray("children");
            JSONArray newChildren = new JSONArray();
            for (int i = 0; i < children.length(); i++) {
                JSONObject childJO = children.getJSONObject(i);
                if (packagesJO.opt(childJO.getString("id")) != null) {
                    newChildren.put(childJO);
                }
            }
            groupJO.put("children", newChildren);
            if (newChildren.length() == 0) {
                need2Remove.add(groupId);
            }
        }
        for (String removeId : need2Remove) {
            groupsJO.remove(removeId);
        }
    }
}
