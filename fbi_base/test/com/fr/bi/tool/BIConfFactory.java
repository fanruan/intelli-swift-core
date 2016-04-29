package com.fr.bi.tool;


import com.fr.bi.common.factory.BIClassNonsupportException;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.BIFactoryKeyDuplicateException;

import java.util.Map;

/**
 * Created by Connery on 2015/12/7.
 */
public class BIConfFactory implements IFactoryService {
    static {
        try {
//            BIFactory xmlFactory = ((BIFactory) BIMateFactory.getInstance().getObject(BIFactory.CONF_XML, new Object[]{}));
//            xmlFactory.registerClass("com.fr.bi.base.BIUser", com.fr.bi.base.BIUser.class);
//            xmlFactory.registerClass("key", com.fr.bi.common.factory.annotation.BIRegisterObject4test.class);
//            xmlFactory.registerClass("com.fr.bi.common.factory.annotation.BIRegisterObject4test", com.fr.bi.common.factory.annotation.BIRegisterObject4test.class);
//            xmlFactory.registerClass("basic", com.fr.bi.common.factory.BIRegister4TestBasic.class);
//            xmlFactory.registerClass("com.fr.bi.common.factory.BIRegister4TestBasic", com.fr.bi.common.factory.BIRegister4TestBasic.class);
//            xmlFactory.registerClass("one", com.fr.bi.common.factory.BIRegister4TestOne.class);
//            xmlFactory.registerClass("com.fr.bi.common.factory.BIRegister4TestOne", com.fr.bi.common.factory.BIRegister4TestOne.class);
//            xmlFactory.registerClass("com.fr.bi.common.factory.BIRegister4TestBasic", com.fr.bi.common.factory.BIRegister4TestTwo.class);
//            xmlFactory.registerClass("com.fr.bi.conf.base.datasource.BIDataSource", com.fr.bi.conf.base.datasource.BIXMLDataSource.class);
//            xmlFactory.registerClass("com.fr.bi.conf.base.pack.BIPackageConfigManager", com.fr.bi.conf.base.pack.BIPackageConfigManager.class);
//            xmlFactory.registerClass("com.fr.bi.conf.base.pack.BIPackagesManagerService", com.fr.bi.conf.base.pack.BIPackageContainer.class);
//            xmlFactory.registerClass("com.fr.bi.conf.base.pack.BIUserPackageConfigurationManager", com.fr.bi.conf.base.pack.BIUserPackageConfigurationManager.class);
//            xmlFactory.registerClass("com.fr.bi.conf.base.pack.group.BIGroupTagsManagerService", com.fr.bi.conf.base.pack.group.BIGroupTagContainer.class);
//            xmlFactory.registerClass("com.fr.bi.conf.base.relation.BITableRelationAnalysisService", com.fr.bi.conf.base.relation.BITableRelationAnalyser.class);
//            xmlFactory.registerClass("com.fr.bi.conf.base.relation.BIUserTableRelationManager", com.fr.bi.conf.base.relation.BIUserTableRelationManager.class);
//            xmlFactory.registerClass("com.fr.bi.conf.manager.userInfo.manager.LoginUserInfoManager", com.fr.bi.conf.manager.userInfo.manager.LoginUserInfoManager.class);
//            xmlFactory.registerClass("com.fr.bi.stable.relation.BITableRelationPath", com.fr.bi.stable.relation.BITableRelationPath.class);
        } catch (Exception e) {

        }
    }


    private static BIConfFactory instance;
    private Map<String, IFactoryService> units;
    private IFactoryService currentFactory;


    public void registerFactory(IFactoryService factory) {
        units.put(factory.getFactoryTag(), factory);
    }

    public void useFactory(String tag) {
        synchronized (units) {
            currentFactory = units.get(tag);
        }
    }

    @Override
    public Object getObject(String registeredName, Object... parameter) throws Exception {
        synchronized (currentFactory) {
            return currentFactory.getObject(registeredName, parameter);
        }
    }

    @Override
    public Object getObject(String registeredName) throws Exception {
        synchronized (currentFactory) {
            return currentFactory.getObject(registeredName);
        }
    }

    @Override
    public void registerClass(String name, Class clazz) throws BIFactoryKeyDuplicateException {
        synchronized (currentFactory) {
            currentFactory.registerClass(name, clazz);
        }
    }

    @Override
    public String getFactoryTag() {
        return "conf";
    }

    @Override
    public void registerObject(String name, Object object) throws BIClassNonsupportException {
        currentFactory.registerObject(name, object);
    }

}