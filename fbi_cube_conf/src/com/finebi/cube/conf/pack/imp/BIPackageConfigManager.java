package com.finebi.cube.conf.pack.imp;

import com.finebi.cube.conf.pack.IPackagesManagerService;
import com.finebi.cube.conf.pack.BIStatusChaosException;
import com.finebi.cube.conf.pack.data.*;
import com.finebi.cube.conf.pack.group.IGroupTagsManagerService;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.inter.Release;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIGroupDuplicateException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * 主要记录分组，业务包，表等变动。
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BIPackageConfigManager.class)
public class BIPackageConfigManager implements Release {

    public static Integer PACKAGE_MANAGER_STATUS_BUILDING = 1;
    public static Integer PACKAGE_MANAGER_STATUS_FREE = 2;

    /**
     * TODO group加入状态管理，减少通过比较Group来计算是否改变。
     */
    protected IPackagesManagerService currentPackageManager;
    protected IPackagesManagerService analysisPackageManager;
    protected BIPackageContainer buildingCubePackages;
    protected IGroupTagsManagerService groupCollectionManager;
    protected BIUser user;
    @BIIgnoreField
    protected Integer status = PACKAGE_MANAGER_STATUS_FREE;

    public BIPackageConfigManager(long userId) {
        user = BIFactoryHelper.getObject(BIUser.class, userId);
        analysisPackageManager = BIFactoryHelper.getObject(IPackagesManagerService.class, userId);
        currentPackageManager = BIFactoryHelper.getObject(IPackagesManagerService.class, userId);
        groupCollectionManager = BIFactoryHelper.getObject(IGroupTagsManagerService.class, userId);
    }

    public IPackagesManagerService getAnalysisPackageManager() {
        return analysisPackageManager;
    }

    protected IPackagesManagerService getCurrentPackageManager() {
        return currentPackageManager;
    }

    public BIUser getUser() {
        return user;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void clear() {
        synchronized (currentPackageManager) {
            analysisPackageManager.clearPackages();
            currentPackageManager.clearPackages();
        }
    }

    /**
     *
     */
    public void setStartBuildCube() throws BIStatusChaosException {
        try {
            synchronized (currentPackageManager) {
                if (isFree()) {
                    buildingCubePackages = currentPackageManager.clonePackageContainer();
                    setBusy();
                } else {
                    throw new BIStatusChaosException("Please check current status of building cube,here happened a fatal problem");
                }
            }
        } catch (CloneNotSupportedException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public boolean isPackageDataChanged() {
        return currentPackageManager.isNeed2BuildCube(analysisPackageManager.getAllPackages());
    }

    public boolean isCurrentPackageEmpty() {
        return currentPackageManager.isPackageEmpty();
    }

    public boolean isFree() {
        return status == PACKAGE_MANAGER_STATUS_FREE;
    }

    private void setBusy() {
        status = PACKAGE_MANAGER_STATUS_BUILDING;
    }

    private void setFree() {
        status = PACKAGE_MANAGER_STATUS_FREE;
    }

    public void setEndBuildCube() throws BIStatusChaosException {
        synchronized (analysisPackageManager) {
            if (!isFree()) {
                analysisPackageManager.parsePackageContainer(buildingCubePackages);
                buildingCubePackages.clearPackages();
                setFree();
            } else {
                throw new BIStatusChaosException("Please check current status of building cube,here happened a fatal problem");
            }
        }
    }

    public Set<BIBusinessPackage> getAllPackages() {
        synchronized (currentPackageManager) {
            return currentPackageManager.getAllPackages();
        }
    }


    public void renamePack(BIPackageID id, BIPackageName newName) throws BIPackageAbsentException {
        synchronized (currentPackageManager) {
            currentPackageManager.rename(id, newName);
        }
    }

    public void renameGroup(BIGroupTagName oldName, BIGroupTagName newName) throws BIGroupAbsentException, BIGroupDuplicateException {
        synchronized (groupCollectionManager) {
            groupCollectionManager.rename(oldName, newName);
        }
    }

    public IBusinessPackageGetterService getPackage(String packageId) throws BIPackageAbsentException {
        return getPackage(new BIPackageID(packageId));
    }

    public IBusinessPackageGetterService getPackage(BIPackageID packageId) throws BIPackageAbsentException {
        return currentPackageManager.getPackage(packageId);
    }

    public void addPackage(BIBusinessPackage biBasicBusinessPackage) throws BIPackageDuplicateException {
        synchronized (currentPackageManager) {
            currentPackageManager.addPackage(biBasicBusinessPackage);
        }
    }

    public Boolean containPackage(BIBusinessPackage biBasicBusinessPackage) {
        return currentPackageManager.containPackage(biBasicBusinessPackage);
    }

    public Boolean containPackageID(BIPackageID biPackageID) {
        return currentPackageManager.containPackage(biPackageID);
    }

    protected IGroupTagsManagerService getGroupCollectionManager() {
        return groupCollectionManager;
    }

    public void removePackage(BIPackageID packageID) throws BIPackageAbsentException {
        synchronized (currentPackageManager) {
            currentPackageManager.removePackage(packageID);
        }
        synchronized (groupCollectionManager) {
            groupCollectionManager.removePackage(packageID);
        }
    }

    public void stickGroupTagOnPackage(BIPackageID packageID, BIGroupTagName groupTagName) throws BIPackageAbsentException, BIGroupAbsentException, BIPackageDuplicateException {
        IBusinessPackageGetterService biBusinessPackage = getPackage(packageID);
        groupCollectionManager.addPackage(groupTagName, (BIBusinessPackage) biBusinessPackage);
    }

    public void removeGroupTagFromPackage(BIPackageID packageID, BIGroupTagName groupTagName) throws BIPackageAbsentException, BIGroupAbsentException {
        IBusinessPackageGetterService biBusinessPackage = getPackage(packageID);
        groupCollectionManager.removeSpecificGroupPackageTag(groupTagName, (BIBusinessPackage) biBusinessPackage);
    }

    public Set<BIBusinessPackage> getPackageByName(BIPackageName packName) {
        HashSet<BIBusinessPackage> packages = new HashSet<BIBusinessPackage>();
        Iterator<BIBusinessPackage> it = currentPackageManager.getAllPackages().iterator();
        while (it.hasNext()) {
            BIBusinessPackage biBasicBusinessPackage = it.next();
            if (ComparatorUtils.equals(packName, biBasicBusinessPackage.getName())) {
                packages.add(biBasicBusinessPackage);
            }
        }
        return packages;
    }

    public JSONObject createGroupJSON() throws JSONException {
        synchronized (groupCollectionManager) {
            JSONObject jo = new JSONObject();
            Iterator<BIGroupTagName> groupNameIte = groupCollectionManager.getAllGroupTagName().iterator();
            while (groupNameIte.hasNext()) {
                JSONObject t = new JSONObject();
                BIGroupTagName groupName = groupNameIte.next();
                if ("".equals(groupName.getName())) {
                    continue;
                }
                try {
                    Set<BIBusinessPackage> packList = groupCollectionManager.getReadableGroup(groupName).getPackages();

                    Map<Long, BIBusinessPackage> packageMap = new TreeMap<Long, BIBusinessPackage>(new Comparator<Long>() {
                        @Override
                        public int compare(Long o1, Long o2) {
                            return (o2 - o1) > 0 ? -1 : 1;
                        }
                    });
                    for (BIBusinessPackage pack : packList) {
                        packageMap.put(pack.getPosition(), pack);
                    }

                    t.put("name", groupName.getValue());
                    JSONArray names = new JSONArray();
                    Iterator<Map.Entry<Long, BIBusinessPackage>> iterator = packageMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        names.put(new JSONObject().put("id", iterator.next().getValue().getID().getIdentityValue()));
                    }
                    t.put("init_time", groupCollectionManager.getReadableGroup(groupName).getPosition());
                    t.put("children", names);
                    String groupID = UUID.randomUUID().toString();
                    t.put("id", groupID);
                    jo.put(groupID, t);
                } catch (BIGroupAbsentException e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                    continue;
                }
            }
            return jo;
        }
    }

    public JSONObject createPackageJSON() throws Exception {
        JSONObject jo = new JSONObject();
        Iterator<Map.Entry<Long, BIBusinessPackage>> iterator = sortPackages();
        while (iterator.hasNext()) {
            BIBusinessPackage pack = iterator.next().getValue();
            jo.put(pack.getID().getIdentity(), pack.createJSON());
        }
        return jo;
    }

    private Iterator<Map.Entry<Long, BIBusinessPackage>> sortPackages() {
        Map<Long, BIBusinessPackage> packageMap = new TreeMap<Long, BIBusinessPackage>(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return (o2 - o1) > 0 ? -1 : 1;
            }
        });
        for (BIBusinessPackage pack : currentPackageManager.getAllPackages()) {
            packageMap.put(pack.getPosition(), pack);
        }
        return packageMap.entrySet().iterator();
    }

    /**
     * 更新业务包分组设置
     *
     * @param jo JSON数组
     * @throws com.fr.json.JSONException
     */
    public void parseGroupJSON(long userId, JSONObject jo) throws JSONException {
        synchronized (groupCollectionManager) {
            groupCollectionManager.clear();
            if (jo.has("changedPackage")) {
                JSONObject changedPackage = jo.getJSONObject("changedPackage");
                if (changedPackage != null) {
                    String newPackageName = changedPackage.getString("newPackageName");
                    String packageID = changedPackage.getString("packageID");
                    BISystemPackageConfigurationProvider packageConfigProvider = BICubeConfigureCenter.getPackageManager();
                    IBusinessPackageGetterService pack = null;
                    BIPackageID packID = new BIPackageID(packageID);
                    try {
                        pack = packageConfigProvider.getPackage(userId, packID);
                    } catch (BIPackageAbsentException e) {
                        BILogger.getLogger().error(e.getMessage(), e);
                    }
                    if (!ComparatorUtils.equals(newPackageName, pack.getName().getValue())) {
                        try {
                            BICubeConfigureCenter.getPackageManager().renamePackage(userId, packID, new BIPackageName(newPackageName));
                        } catch (BIPackageAbsentException e) {
                            BILogger.getLogger().error(e.getMessage(), e);
                        } catch (BIPackageDuplicateException e) {
                            BILogger.getLogger().error(e.getMessage(), e);
                        }
                    }
                }
                jo.remove("changedPackage");
            }
            JSONArray ja = jo.names() == null ? new JSONArray() : jo.names();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject t = jo.optJSONObject(ja.optString(i));
                String name = t.optString("name");
                JSONArray jt = t.optJSONArray("children");
                BIGroupTagName groupTagName = new BIGroupTagName(name);
                try {
                    long position = t.optLong("init_time");
                    groupCollectionManager.createEmptyGroup(groupTagName, position);
                    Set<String> list = new HashSet<String>();
                    for (int j = 0; j < jt.length(); j++) {
                        JSONObject g = jt.optJSONObject(j);
                        BIPackageID id = new BIPackageID(g.optString("id"));
                        IBusinessPackageGetterService biPackage = currentPackageManager.getPackage(id);
                        groupCollectionManager.addPackage(groupTagName, (BIBusinessPackage) biPackage);
                    }
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    public Boolean isPackageTaggedSpecificGroup(BIPackageID packageID, BIGroupTagName name) throws BIGroupAbsentException {
        return groupCollectionManager.isPackageTaggedSpecificGroup(name, packageID);
    }

    public Boolean isPackageTaggedSomeGroup(BIPackageID packageID) {
        return groupCollectionManager.isPackageTaggedSomeGroup(packageID);
    }

    public void removeBusinessTableByID(BIPackageID packageID, BITableID tableID) throws BIPackageAbsentException, BITableAbsentException {
        currentPackageManager.removeTable(packageID, tableID);
    }

    public Set<BusinessTable> getAllTables() {
        Set<BusinessTable> result = new HashSet<BusinessTable>();
        Iterator<BIBusinessPackage> it = getAllPackages().iterator();
        while (it.hasNext()) {
            result.addAll(it.next().getBusinessTables());
        }
        return result;
    }
}
