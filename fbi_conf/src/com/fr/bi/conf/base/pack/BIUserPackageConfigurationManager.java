package com.fr.bi.conf.base.pack;

import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.base.pack.data.BIBasicBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.manager.singletable.SingleTableUpdateManager;
import com.fr.bi.conf.manager.timer.UpdateFrequencyManager;
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
    private UpdateFrequencyManager updateManager;
    private SingleTableUpdateManager singleManager;

    public BIUserPackageConfigurationManager(long userId) {
        user = new BIUser(userId);
        packageConfigManager = BIFactoryHelper.getObject(BIPackageConfigManager.class, userId);
        updateManager = new UpdateFrequencyManager(userId);
        singleManager = new SingleTableUpdateManager(userId);
    }

    public BIPackageConfigManager getPackageConfigManager() {
        return packageConfigManager;
    }

    public UpdateFrequencyManager getUpdateManager() {
        return updateManager;
    }

    public BIUser getUser() {
        return user;
    }

    public void setUser(BIUser user) {
        this.user = user;
    }

    public SingleTableUpdateManager getSingleTableUpdateManager() {
        return singleManager;
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
//        updateManager.clear();
        singleManager.clear();
    }

//    public Map<String, Set<String>> getCurrentPackageGroup4Generating() {
//        Map<String, Set<String>> clone = new HashMap<String, Set<String>>();
//        for (Map.Entry<String, Set<String>> entry : groupConfigureChangeManager.getAllGroups4Generating().entrySet()) {
//            Set<String> set = new HashSet<String>();
//            for (String name : entry.getValue()) {
//                set.add(name);
//            }
//            clone.put(entry.getKey(), set);
//        }
//        return clone;
//    }

    public void startGenerateCubes() {
        packageConfigManager.setStartBuildCube();
    }

    public Set<BIBusinessPackage> getCurrentPackage4Generating() {
        Set<BIBusinessPackage> clone = new HashSet<BIBusinessPackage>();
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