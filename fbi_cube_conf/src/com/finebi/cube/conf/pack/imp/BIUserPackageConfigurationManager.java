package com.finebi.cube.conf.pack.imp;

import com.finebi.cube.conf.pack.data.BIBasicBusinessPackage;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Manager包含Set<BIGroup>，BIGroup包含Set<BIPackage>，BIPackage包含List<BITable>
 * Created by Connery on 2015/12/25.
 */

/**
 * TODO factory要修改，现在对象和持久化分开处理。那么不需要多个按照持久化的factory
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BIUserPackageConfigurationManager.class)
public class BIUserPackageConfigurationManager {

    protected BIUser user;
    private BIPackageConfigManager packageConfigManager;


    public BIUserPackageConfigurationManager(long userId) {
        user = new BIUser(userId);
        packageConfigManager = BIFactoryHelper.getObject(BIPackageConfigManager.class, userId);

    }

    public BIPackageConfigManager getPackageConfigManager() {
        return packageConfigManager;
    }


    public BIUser getUser() {
        return user;
    }

    public void setUser(BIUser user) {
        this.user = user;
    }

    /**
     * 完成生成cube
     */
    public void finishGenerateCubes() {
        synchronized (this) {
            packageConfigManager.setEndBuildCube();
        }
    }

    /**
     * 更新
     */
    public void envChanged() {
        packageConfigManager.clear();
    }



    public void startGenerateCubes() {
        packageConfigManager.setStartBuildCube();
    }

    public Set<IBusinessPackageGetterService> getCurrentPackage4Generating() {
        Set<IBusinessPackageGetterService> clone = new HashSet<IBusinessPackageGetterService>();
        for (BIBusinessPackage pack : packageConfigManager.getAllPackages()) {
            try {
                clone.add((BIBasicBusinessPackage) pack.clone());
            } catch (CloneNotSupportedException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return false;
    }


}