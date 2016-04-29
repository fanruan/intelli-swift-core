package com.fr.bi.common.factory;

import com.fr.bi.stable.utils.program.BIStringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2016/3/14.
 *
 * @author Connery
 * @since 4.0
 */
public class BIModuleFactoryManager {

    private Map<String, IModuleFactory> moduleTag2ModuleFactory;
    private Map<String, IModuleFactory> beanName2ModuleFactory;
    private static BIModuleFactoryManager instance;

    public static BIModuleFactoryManager getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BIFactory.class) {
                if (instance == null) {
                    instance = new BIModuleFactoryManager();
                }
                return instance;
            }
        }
    }

    private BIModuleFactoryManager() {
        moduleTag2ModuleFactory = new ConcurrentHashMap<String, IModuleFactory>();
        moduleTag2ModuleFactory.put(IModuleFactory.CONF_MODULE, new BIConfFactory());
        moduleTag2ModuleFactory.put(IModuleFactory.CUBE_BASE_MODULE, new BICubeBaseFactory());
        moduleTag2ModuleFactory.put(IModuleFactory.CUBE_BUILD_MODULE, new BICubeBuildFactory());

        beanName2ModuleFactory = new ConcurrentHashMap<String, IModuleFactory>();
    }

    /**
     * 由于不知道factoryTag来自哪个模块。
     * 所以传递factoryTag，如果当前的Module接受了，那么
     * useFactory返回true，于是认为更换成功。否则继续传递。
     *
     * @param factoryTag 工厂实例
     * @return 是否被某一个工厂模块接受
     */
    public boolean changeModuleFactory(String factoryTag) {
        Iterator<IModuleFactory> moduleFactoryIterator = moduleTag2ModuleFactory.values().iterator();
        while (moduleFactoryIterator.hasNext()) {
            IModuleFactory factory = moduleFactoryIterator.next();
            if (factory.useFactory(factoryTag)) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }

    /**
     * 记录Bean的名字所在的模块信息。
     *
     * @param name      Bean的名字
     * @param moduleTag 模块标签
     * @throws BIFactoryKeyAbsentException 模块module的名字可能缺少
     */
    public void registerModuleTag(String name, String moduleTag) throws BIFactoryKeyAbsentException {
        if (moduleTag2ModuleFactory.containsKey(moduleTag)) {
            if (!beanName2ModuleFactory.containsKey(name)) {
                beanName2ModuleFactory.put(name, moduleTag2ModuleFactory.get(moduleTag));
            } else {
                /**
                 * 如果当前Manager已经包含name了，那么不做处理。
                 * 因为一个name可能对应module下面的多个实现
                 */
            }
        } else {
            throw new BIFactoryKeyAbsentException(BIStringUtils.append("the module factory absent:", moduleTag));
        }
    }

    public IModuleFactory getModuleFactoryByName(String registeredName) {
        return beanName2ModuleFactory.get(registeredName);
    }

    public IModuleFactory getModuleFactoryByModuleTag(String moduleTag) {
        return moduleTag2ModuleFactory.get(moduleTag);
    }

    public void registerClass(String moduleTag, String name, Class clazz) throws BIFactoryKeyDuplicateException, BIFactoryKeyAbsentException {
        IModuleFactory factory = getModuleFactoryByModuleTag(moduleTag);
        factory.registerClass(moduleTag, name, clazz);
        registerModuleTag(name, moduleTag);
    }

    public void registerObject(String moduleTag, String name, Object object) throws BIClassNonsupportException, BIFactoryKeyAbsentException {
        IModuleFactory factory = getModuleFactoryByModuleTag(moduleTag);
        factory.registerObject(moduleTag, name, object);
        registerModuleTag(name, moduleTag);
    }
}
