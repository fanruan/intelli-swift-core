package com.fr.bi.conf.base.pack;

import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.base.BISystemDataManager;
import com.fr.bi.conf.base.pack.data.*;
import com.fr.bi.conf.base.pack.group.BIBusinessGroupGetterService;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIGroupDuplicateException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.conf.manager.singletable.SingleTableUpdateManager;
import com.fr.bi.conf.manager.timer.UpdateFrequencyManager;
import com.fr.bi.conf.provider.BISystemPackAndAuthConfigurationProvider;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Set;

/**
 * Created by Connery on 2016/1/4.
 */
public class BISystemPackAndAuthConfigurationManager extends BISystemDataManager<BIUserPackAndAuthConfigurationManager> implements BISystemPackAndAuthConfigurationProvider {



    @Override
    public BIUserPackAndAuthConfigurationManager constructUserManagerValue(Long userId) {
        return BIFactoryHelper.getObject(BIUserPackAndAuthConfigurationManager.class, userId);
    }

    @Override
    public String managerTag() {
        return "packageAndAuthority";
    }


    /**
     * 完成生成cube
     *
     * @param userId 用户id
     */
    @Override
    public void finishGenerateCubes(long userId) {
        getUserGroupConfigManager(userId).finishGenerateCubes();
    }


    @Override
    public Set<BIBusinessPackage> getAllPackages(long userId) {
        return getUserGroupConfigManager(userId).getCurrentPackage4Generating();
    }

    @Override
    public UpdateFrequencyManager getUpdateManager(long userId) {
        return getUserGroupConfigManager(userId).getUpdateManager();
    }


    @Override
    public SingleTableUpdateManager getSingleTableUpdateManager(long userId) {
        return getUserGroupConfigManager(userId).getSingleTableUpdateManager();
    }

    /**
     * 更新
     */
    @Override
    public void envChanged() {
        clear();
    }


    @Override
    public void renamePackage(long userId, BIPackageID packageID, BIPackageName packageName) throws BIPackageAbsentException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().renamePack(packageID, packageName);
    }

    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }

//    @Override
//    public BIGroupTagManagerService getCurrent(long userId) {
//        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getCurrentPackageManager();
//    }


    @Override
    public BIBusinessPackageGetterService getPackage(long userId, BIPackageID packageID) throws BIPackageAbsentException {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getPackage(packageID);
    }

    @Override
    public void renameGroup(long userId, BIGroupTagName oldName, BIGroupTagName newName) throws BIGroupAbsentException, BIGroupDuplicateException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().renameGroup(oldName, newName);
    }

    @Override
    public void addPackage(long userId, BIBusinessPackage biBusinessPackage) throws BIPackageDuplicateException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().addPackage(biBusinessPackage);
    }

    @Override
    public Boolean containPackage(long userId, BIBusinessPackage biPackage) {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().containPackage(biPackage);
    }

    @Override
    public Boolean containPackageID(long userId, BIPackageID packageID) {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().containPackageID(packageID);
    }

    @Override
    public Boolean containGroup(long userId, BIGroupTagName groupTagName) {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getGroupCollectionManager().existGroupTag(groupTagName);
    }

    @Override
    public void removePackage(long userId, BIPackageID packageID) throws BIPackageAbsentException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().removePackage(packageID);
    }

    @Override
    public void removeGroup(long userId, BIGroupTagName groupTagName) throws BIGroupAbsentException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getGroupCollectionManager().removeGroup(groupTagName);
    }

    @Override
    public void createEmptyGroup(long userId, BIGroupTagName groupTagName) throws BIGroupDuplicateException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getGroupCollectionManager().createEmptyGroup(groupTagName);
    }

    @Override
    public void stickGroupTagOnPackage(long userId, BIPackageID packageID, BIGroupTagName groupTagName) throws BIPackageAbsentException, BIGroupAbsentException, BIPackageDuplicateException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().stickGroupTagOnPackage(packageID, groupTagName);
    }

    @Override
    public void removeGroupTagFromPackage(long userId, BIPackageID packageID, BIGroupTagName groupTagName) throws BIPackageAbsentException, BIGroupAbsentException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().removeGroupTagFromPackage(packageID, groupTagName);
    }

    @Override
    public BIBusinessGroupGetterService getGroup(long userId, BIGroupTagName groupTagName) throws BIGroupAbsentException {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getGroupCollectionManager().getReadableGroup(groupTagName);
    }

    @Override
    public Set<BIBusinessPackage> getPackageByName(long userId, BIPackageName packName) {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getPackageByName(packName);
    }

    @Override
    public void startBuildingCube(long userId) {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().setStartBuildCube();
    }

    @Override
    public void endBuildingCube(long userId) {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().setEndBuildCube();
    }

    @Override
    public JSONObject createGroupJSON(long userId) throws JSONException {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().createGroupJSON();
    }

    @Override
    public void parseGroupJSON(long userId, JSONObject jo) throws JSONException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().parseGroupJSON(userId, jo);

    }

    @Override
    public JSONObject createPackageJSON(long userId) throws Exception {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().createPackageJSON();
    }

    @Override
    public boolean isPackageDataChanged(long userId) {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().isPackageDataChanged();
    }

    @Override
    public Boolean isPackagesEmpty(long userId) {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().isCurrentPackageEmpty();
    }

    @Override
    public void removeTable(long userId, BIPackageID packageID, BITableID biTableID) throws BIPackageAbsentException, BITableAbsentException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().removeBusinessTableByID(packageID, biTableID);
    }

    @Override
    public Boolean isPackageTaggedSomeGroup(long userId, BIPackageID packageID) {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().isPackageTaggedSomeGroup(packageID);
    }

    @Override
    public Boolean isPackageTaggedSpecificGroup(long userId, BIPackageID packageID, BIGroupTagName groupTagName) throws BIGroupAbsentException {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().isPackageTaggedSpecificGroup(packageID, groupTagName);
    }

    @Override
    public Set<Table> getAllTables(long userId) {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getAllTables();
    }
}
