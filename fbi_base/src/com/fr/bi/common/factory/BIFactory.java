package com.fr.bi.common.factory;

/**
 * This class created on 2016/3/14.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFactory implements IModuleFactory {
    private static BIFactory instance;
    private BIModuleFactoryManager moduleManager;

    public static BIFactory getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BIFactory.class) {
                if (instance == null) {
                    instance = new BIFactory();
                }
                return instance;
            }
        }
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
        return moduleManager.changeModuleFactory(factoryTag);
    }

    private BIFactory() {
        moduleManager = BIModuleFactoryManager.getInstance();
        /**
         * 载入全部标注的Factory
         * BIFactoryAutoLoader.load();
         * 改用生成所有注册代码，这样加快启动，
         * 否则遍历耗时太久。参考
         * BIFactoryAutoRegisterCode
         */
        BIFactoryAutoLoader.load();

    }

    private IModuleFactory getModuleFactoryByName(String registeredName) {
        return moduleManager.getModuleFactoryByName(registeredName);
    }

    private IModuleFactory getModuleFactoryByModuleTag(String moduleTag) {
        return moduleManager.getModuleFactoryByModuleTag(moduleTag);
    }

    @Override
    public String getFactoryTag() {
        return "BI_Factory";
    }

    @Override
    public Object getObject(String registeredName, Object... parameter) throws Exception {
        IModuleFactory module = getModuleFactoryByName(registeredName);
        synchronized (module) {
            return module.getObject(registeredName, parameter);
        }
    }

    @Override
    public Object getObject(String registeredName) throws Exception {
        IModuleFactory module = getModuleFactoryByName(registeredName);
        synchronized (module) {
            return module.getObject(registeredName);
        }
    }

    @Override
    public boolean useFactory(String tag) {
        return false;
    }

    @Override
    public void registerClass(String moduleTag, String name, Class clazz) throws BIFactoryKeyDuplicateException, BIFactoryKeyAbsentException {
        moduleManager.registerClass(moduleTag, name, clazz);
    }

    @Override
    public void registerObject(String moduleTag, String name, Object object) throws BIClassNonsupportException, BIFactoryKeyAbsentException {
        moduleManager.registerObject(moduleTag, name, object);
    }
}
