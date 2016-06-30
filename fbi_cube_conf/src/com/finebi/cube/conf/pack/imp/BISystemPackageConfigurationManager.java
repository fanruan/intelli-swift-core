package com.finebi.cube.conf.pack.imp;

import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.IPackagesManagerService;
import com.finebi.cube.conf.pack.data.*;
import com.finebi.cube.conf.pack.group.IBusinessGroupGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIGroupDuplicateException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class BISystemPackageConfigurationManager extends BISystemDataManager<BIUserPackageConfigurationManager> implements BISystemPackageConfigurationProvider {


    @Override
    public BIUserPackageConfigurationManager constructUserManagerValue(Long userId) {
        return BIFactoryHelper.getObject(BIUserPackageConfigurationManager.class, UserControl.getInstance().getSuperManagerID());
    }

    public BIUserPackageConfigurationManager getUserGroupConfigManager(long userId) {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID());
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }


    @Override
    public String managerTag() {
        return "BusinessPackage";
    }

    @Override
    public String persistUserDataName(long key) {
        return managerTag();
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
    public Set<IBusinessPackageGetterService> getAllPackages(long userId) {
        return getUserGroupConfigManager(userId).getCurrentPackage4Generating();
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
        getUserGroupConfigManager(userId).getPackageConfigManager().renamePack(packageID, packageName);
    }

    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }

//    @Override
//    public BIGroupTagManagerService getCurrent(long userId) {
//        return getUserGroupConfigManager(userId).getPackageConfigManager().getCurrentPackageManager();
//    }


    @Override
    public IBusinessPackageGetterService getPackage(long userId, BIPackageID packageID) throws BIPackageAbsentException {
        return getUserGroupConfigManager(userId).getPackageConfigManager().getPackage(packageID);
    }

    @Override
    public void renameGroup(long userId, BIGroupTagName oldName, BIGroupTagName newName) throws BIGroupAbsentException, BIGroupDuplicateException {
        getUserGroupConfigManager(userId).getPackageConfigManager().renameGroup(oldName, newName);
    }

    @Override
    public void addPackage(long userId, BIBusinessPackage biBusinessPackage) throws BIPackageDuplicateException {
        getUserGroupConfigManager(userId).getPackageConfigManager().addPackage(biBusinessPackage);
    }

    @Override
    public Boolean containPackage(long userId, BIBusinessPackage biPackage) {
        return getUserGroupConfigManager(userId).getPackageConfigManager().containPackage(biPackage);
    }

    @Override
    public Boolean containPackageID(long userId, BIPackageID packageID) {
        return getUserGroupConfigManager(userId).getPackageConfigManager().containPackageID(packageID);
    }

    @Override
    public Boolean containGroup(long userId, BIGroupTagName groupTagName) {
        return getUserGroupConfigManager(userId).getPackageConfigManager().getGroupCollectionManager().existGroupTag(groupTagName);
    }

    @Override
    public void removePackage(long userId, BIPackageID packageID) throws BIPackageAbsentException {
        getUserGroupConfigManager(userId).getPackageConfigManager().removePackage(packageID);
    }

    @Override
    public void removeGroup(long userId, BIGroupTagName groupTagName) throws BIGroupAbsentException {
        getUserGroupConfigManager(userId).getPackageConfigManager().getGroupCollectionManager().removeGroup(groupTagName);
    }

    @Override
    public void createEmptyGroup(long userId, BIGroupTagName groupTagName, long position) throws BIGroupDuplicateException {
        getUserGroupConfigManager(userId).getPackageConfigManager().getGroupCollectionManager().createEmptyGroup(groupTagName, position);
    }

    @Override
    public void stickGroupTagOnPackage(long userId, BIPackageID packageID, BIGroupTagName groupTagName) throws BIPackageAbsentException, BIGroupAbsentException, BIPackageDuplicateException {
        getUserGroupConfigManager(userId).getPackageConfigManager().stickGroupTagOnPackage(packageID, groupTagName);
    }

    @Override
    public void removeGroupTagFromPackage(long userId, BIPackageID packageID, BIGroupTagName groupTagName) throws BIPackageAbsentException, BIGroupAbsentException {
        getUserGroupConfigManager(userId).getPackageConfigManager().removeGroupTagFromPackage(packageID, groupTagName);
    }

    @Override
    public IBusinessGroupGetterService getGroup(long userId, BIGroupTagName groupTagName) throws BIGroupAbsentException {
        return getUserGroupConfigManager(userId).getPackageConfigManager().getGroupCollectionManager().getReadableGroup(groupTagName);
    }

    @Override
    public Set<BIBusinessPackage> getPackageByName(long userId, BIPackageName packName) {
        return getUserGroupConfigManager(userId).getPackageConfigManager().getPackageByName(packName);
    }

    @Override
    public void startBuildingCube(long userId) {
        getUserGroupConfigManager(userId).getPackageConfigManager().setStartBuildCube();
    }

    @Override
    public void endBuildingCube(long userId) {
        getUserGroupConfigManager(userId).getPackageConfigManager().setEndBuildCube();
    }

    @Override
    public JSONObject createGroupJSON(long userId) throws JSONException {
        return getUserGroupConfigManager(userId).getPackageConfigManager().createGroupJSON();
    }

    @Override
    public void parseGroupJSON(long userId, JSONObject jo) throws JSONException {
        getUserGroupConfigManager(userId).getPackageConfigManager().parseGroupJSON(userId, jo);

    }

    @Override
    public JSONObject createPackageJSON(long userId) throws Exception {
        return getUserGroupConfigManager(userId).getPackageConfigManager().createPackageJSON();
    }

    @Override
    public JSONObject createPackageJSON(long userId, Locale locale) throws Exception {
        return getUserGroupConfigManager(userId).getPackageConfigManager().createPackageJSON();
    }

    @Override
    public boolean isPackageDataChanged(long userId) {
        return getUserGroupConfigManager(userId).getPackageConfigManager().isPackageDataChanged();
    }

    @Override
    public Boolean isPackagesEmpty(long userId) {
        return getUserGroupConfigManager(userId).getPackageConfigManager().isCurrentPackageEmpty();
    }

    @Override
    public void removeTable(long userId, BIPackageID packageID, BITableID biTableID) throws BIPackageAbsentException, BITableAbsentException {
        getUserGroupConfigManager(userId).getPackageConfigManager().removeBusinessTableByID(packageID, biTableID);
    }

    @Override
    public Boolean isPackageTaggedSomeGroup(long userId, BIPackageID packageID) {
        return getUserGroupConfigManager(userId).getPackageConfigManager().isPackageTaggedSomeGroup(packageID);
    }

    @Override
    public Boolean isPackageTaggedSpecificGroup(long userId, BIPackageID packageID, BIGroupTagName groupTagName) throws BIGroupAbsentException {
        return getUserGroupConfigManager(userId).getPackageConfigManager().isPackageTaggedSpecificGroup(packageID, groupTagName);
    }

    @Override
    public Set<BusinessTable> getAllTables(long userId) {
        return getUserGroupConfigManager(userId).getPackageConfigManager().getAllTables();
    }

    @Override
    public Set<BIBusinessPackage> getPackages4CubeGenerate(long userId){
        IPackagesManagerService analysisPackageManager = getUserGroupConfigManager(userId).getPackageConfigManager().getAnalysisPackageManager();
        IPackagesManagerService currentPackageManager = getUserGroupConfigManager(userId).getPackageConfigManager().getCurrentPackageManager();
        Set<BIBusinessPackage> analysisPackages = analysisPackageManager.getAllPackages();
        Set<BIBusinessPackage> currentPackages = currentPackageManager.getAllPackages();
        Set<BIBusinessPackage> packageSet=new HashSet<BIBusinessPackage>();
        for (BIBusinessPackage biBusinessPackage : currentPackages) {
            if (!analysisPackages.contains(biBusinessPackage)){
                try {
                    packageSet.add((BIBusinessPackage) biBusinessPackage.clone());
                } catch (CloneNotSupportedException e) {
                    BILogger.getLogger().error(e.getMessage());
                }
            }
        }
        return packageSet;
    }

}
