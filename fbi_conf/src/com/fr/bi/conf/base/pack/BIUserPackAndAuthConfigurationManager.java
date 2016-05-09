package com.fr.bi.conf.base.pack;

import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.base.pack.data.BIBasicBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Connery on 2015/12/25.
 */

/**
 * TODO factory要修改，现在对象和持久化分开处理。那么不需要多个按照持久化的factory
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BIUserPackAndAuthConfigurationManager.class)
public class BIUserPackAndAuthConfigurationManager {

    protected BIUser user;
    private BIPackAndAuthConfigManager packAndAuthConfigManager;
    protected BIPackagesManagerService currentPackageManager;



    public BIUserPackAndAuthConfigurationManager(long userId) {
        user = new BIUser(userId);
        packAndAuthConfigManager=BIFactoryHelper.getObject(BIPackAndAuthConfigManager.class,userId);
        currentPackageManager = BIFactoryHelper.getObject(BIPackagesManagerService.class, userId);

    }

    public BIPackagesManagerService getCurrentPackageManager() {
        return currentPackageManager;
    }
    public BIPackAndAuthConfigManager getPackAndAuthConfigManager() {
        return packAndAuthConfigManager;
    }

    public BIUser getUser() {
        return user;
    }

    public void setUser(BIUser user) {
        this.user = user;
    }



    /**
     * 更新
     */
    public void envChanged() {
        packAndAuthConfigManager.clear();
    }



    public Set<BIBusinessPackage> getCurrentPackage4Generating() {
        Set<BIBusinessPackage> clone = new HashSet<BIBusinessPackage>();
        for (BIBusinessPackage pack : packAndAuthConfigManager.getAllPackages()) {
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
