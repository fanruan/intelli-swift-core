package com.fr.bi.web.conf.utils;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.field.BusinessFieldHelper;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/22.
 */
public class BIWebConfUtils {
    public static boolean checkCubeVersion(CubeTableSource source, long userId) {
        if (source == null) {
            return false;
        }
        ICubeDataLoader loader = BICubeManager.getInstance().fetchCubeLoader(userId);
        Iterator<CubeTableSource> it = source.createSourceMap().values().iterator();
        try {
            while (it.hasNext()) {
                CubeTableSource key = it.next();
                loader.getTableIndex(key);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 数据连接
     * 业务包管理
     * 多路径设置
     * 权限配置管理
     * FineIndex更新
     *
     * @param userId
     * @return
     */
    public static JSONObject getAuthDataConfigNodes(long userId) throws Exception {
        JSONObject jo = new JSONObject();
        if (ComparatorUtils.equals(userId, UserControl.getInstance().getSuperManagerID())) {
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION.PAGE, true);
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER.PAGE, true);
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING, true);
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY, true);
            jo.put(DBConstant.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE, true);
        } else {
            Set<BIDataConfigAuthority> authoritySet = BIConfigureManagerCenter.getDataConfigAuthorityManager().getDataConfigAuthoritiesByUserId(userId);
            for (BIDataConfigAuthority authority : authoritySet) {
                String id = authority.getId();
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.DATA_CONNECTION.PAGE)) {
                    jo.put(id, true);
                }
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER.PAGE)) {
                    jo.put(id, true);
                }
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING)) {
                    jo.put(id, true);
                }
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY)) {
                    jo.put(id, true);
                }
                if (ComparatorUtils.equals(id, DBConstant.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE)) {
                    jo.put(id, true);
                }
            }
        }
        return jo;
    }


    public static JSONObject createRelationJSONWithName(BITableRelation relation) throws Exception {
        long userId = UserControl.getInstance().getSuperManagerID();
        JSONObject jo = new JSONObject();
        BusinessField primaryField = relation.getPrimaryField();
        BusinessField foreignField = relation.getForeignField();
        JSONObject primaryJson = primaryField.createJSON();
        String pId = primaryField.getFieldID().getIdentityValue();
        BusinessTable pTable = BusinessFieldHelper.getBusinessTable(primaryField);
        String pTId = pTable.getID().getIdentityValue();
        primaryJson.put(BIJSONConstant.JSON_KEYS.FIELD_ID, pId);
        primaryJson.put(BIJSONConstant.JSON_KEYS.FIELD_TRAN_NAME, BICubeConfigureCenter.getAliasManager().getAliasName(pId, userId));
        primaryJson.put(BIJSONConstant.JSON_KEYS.TABLE_TRAN_NAME, BICubeConfigureCenter.getAliasManager().getAliasName(pTId, userId));
        primaryJson.put(BIJSONConstant.JSON_KEYS.TABLE_NAME, pTable.getTableSource().getTableName());

        JSONObject foreignJson = foreignField.createJSON();
        String fId = foreignField.getFieldID().getIdentityValue();
        BusinessTable fTable = BusinessFieldHelper.getBusinessTable(foreignField);
        String fTId = fTable.getID().getIdentityValue();
        foreignJson.put(BIJSONConstant.JSON_KEYS.FIELD_ID, foreignField.getFieldID().getIdentityValue());
        foreignJson.put(BIJSONConstant.JSON_KEYS.FIELD_TRAN_NAME, BICubeConfigureCenter.getAliasManager().getAliasName(fId, userId));
        foreignJson.put(BIJSONConstant.JSON_KEYS.TABLE_TRAN_NAME, BICubeConfigureCenter.getAliasManager().getAliasName(fTId, userId));
        foreignJson.put(BIJSONConstant.JSON_KEYS.TABLE_NAME, fTable.getTableSource().getTableName());

        jo.put("primaryKey", primaryJson);
        jo.put("foreignKey", foreignJson);
        return jo;
    }

    //TODO 临时判断字段是否存在，有待提供相关接口
    public static boolean isFieldExist(BusinessField field) {
        boolean exist = true;
        try {
            BusinessFieldHelper.getBusinessTable(field);
        } catch (Exception e) {
            exist = false;
            BILoggerFactory.getLogger().info("field " + field.getFieldName() + " may be not exist !");
        }
        return exist;
    }

    public static JSONObject getAvailableGroupsPackages(long userId) throws Exception {
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
        return jo;
    }

    private static void dealWithGroups(JSONObject groupsJO, JSONObject packagesJO) throws Exception {
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