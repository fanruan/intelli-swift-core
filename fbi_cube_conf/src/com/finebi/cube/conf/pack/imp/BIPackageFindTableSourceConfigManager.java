package com.finebi.cube.conf.pack.imp;

import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.conf.pack.IPackagesManagerService;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuk on 16/6/8.
 */
public class BIPackageFindTableSourceConfigManager extends BISystemDataManager<BIUserPackageConfigurationManager> {
    protected IPackagesManagerService currentPackageManager;
    protected IPackagesManagerService analysisPackageManager;
    private BIPackageConfigManager packageConfigManager;
    public BIPackageFindTableSourceConfigManager(long userId) {
        packageConfigManager = BIFactoryHelper.getObject(BIPackageConfigManager.class, userId);
    }
    public Set<BIBusinessPackage> getUnGeneratedPackages(){
        Set<BIBusinessPackage> analysisPackageManagerAllPackages = analysisPackageManager.getAllPackages();
        Set<BIBusinessPackage> currentPackageManagerAllPackages = currentPackageManager.getAllPackages();
        Set<BIBusinessPackage> packageSet=new HashSet<BIBusinessPackage>();
        for (BIBusinessPackage currentPackageManagerAllPackage : currentPackageManagerAllPackages) {
            if (!analysisPackageManagerAllPackages.contains(currentPackageManagerAllPackage)){
                try {
                    packageSet.add((BIBusinessPackage) currentPackageManagerAllPackage.clone());
                } catch (CloneNotSupportedException e) {
                    BILogger.getLogger().error(e.getMessage());
                }
            }
        }
        return packageSet;
    }

    @Override
    public BIUserPackageConfigurationManager constructUserManagerValue(Long userId) {
        return null;
    }

    @Override
    public String managerTag() {
        return null;
    }

    @Override
    public String persistUserDataName(long key) {
        return null;
    }
}
