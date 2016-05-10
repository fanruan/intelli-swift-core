package com.fr.bi.conf.base.pack;

import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.base.BISystemDataManager;
import com.fr.bi.conf.base.pack.data.*;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.conf.provider.BISystemPackAndAuthConfigurationProvider;

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
    public String persistUserDataName(long key) {
        return managerTag();
    }



    @Override
    public Set<BIPackAndAuthority> getAllPackages(long userId) {
        return getUserGroupConfigManager(userId).getCurrentAuthority4Generating();
    }



    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }


    public BIPackAndAuthority getPackageByID(long userId, String packageID) throws BIPackageAbsentException {
        Set<BIPackAndAuthority> allPackages = getAllPackages(userId);
        for (BIPackAndAuthority packAndAuthority : allPackages) {
            if (packAndAuthority.getBiPackageID().equals(packageID)){
                return  packAndAuthority;
            }
        }
        return null;
    }


    @Override
    public void addPackage(long userId, BIPackAndAuthority biPackAndAuthority) throws BIPackageDuplicateException {
        getUserGroupConfigManager(userId).getBiPackAndAuthContainer().addPackage(biPackAndAuthority);
    }




}
