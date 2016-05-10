package com.fr.bi.conf.base.pack;

import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.base.BISystemDataManager;
import com.fr.bi.conf.base.pack.data.BIPackAndAuthority;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.provider.BISystemPackAndAuthConfigurationProvider;

import java.util.HashSet;
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
        Set<BIPackAndAuthority> allPackages = getUserGroupConfigManager(userId).getBiPackAndAuthContainer().getAllPackages();
        Set<BIPackAndAuthority> clone = new HashSet<BIPackAndAuthority>();
        for (BIPackAndAuthority pack : allPackages) {
            clone.add(pack);
        }
        return clone;

    }



    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }

    @Override
    public void updateAuthority(long userId, BIPackAndAuthority biPackAndAuthority) throws Exception {
        getUserGroupConfigManager(userId).getBiPackAndAuthContainer().removePackage(biPackAndAuthority);
        getUserGroupConfigManager(userId).getBiPackAndAuthContainer().addPackage(biPackAndAuthority);
    }

    @Override
    public boolean containPackage(long userId,BIPackAndAuthority biPackAndAuthority) {
        return getUserGroupConfigManager(userId).getBiPackAndAuthContainer().containPackage(biPackAndAuthority);
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
    public void addPackage(long userId, BIPackAndAuthority biPackAndAuthority) throws Exception {
        getUserGroupConfigManager(userId).getBiPackAndAuthContainer().addPackage(biPackAndAuthority);
    }




}
