package com.fr.bi.conf.base.pack;

import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.base.BISystemDataManager;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessPackageGetterService;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.base.pack.data.BIPackageName;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.conf.provider.BISystemPackAndAuthConfigurationProvider;
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




    @Override
    public Set<BIBusinessPackage> getAllPackages(long userId) {
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
    public void persistData(long userId) {
        persistUserData(userId);
    }


    @Override
    public BIBusinessPackageGetterService getPackage(long userId, BIPackageID packageID) throws BIPackageAbsentException {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getPackage(packageID);
    }


    @Override
    public void addPackage(long userId, BIBusinessPackage biBusinessPackage) throws BIPackageDuplicateException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().addPackage(biBusinessPackage);
    }




    @Override
    public void removePackage(long userId, BIPackageID packageID) throws BIPackageAbsentException {
        getUserGroupConfigManager(userId).getPackAndAuthConfigManager().removePackage(packageID);
    }


    @Override
    public Set<BIBusinessPackage> getPackageByName(long userId, BIPackageName packName) {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().getPackageByName(packName);
    }


    @Override
    public JSONObject createPackageJSON(long userId) throws Exception {
        return getUserGroupConfigManager(userId).getPackAndAuthConfigManager().createPackageJSON();
    }

}
