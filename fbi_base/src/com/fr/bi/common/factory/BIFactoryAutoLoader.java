package com.fr.bi.common.factory;

import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.utils.code.BILogDelegate;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIClassUtils;
import com.fr.general.ComparatorUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Connery on 2015/12/8.
 */
class BIFactoryAutoLoader {
    private static boolean loadFlag = false;

    public static void load() {
        synchronized (BIFactoryAutoLoader.class) {
            if (!loadFlag) {
                Set<Class<?>> set = BIClassUtils.getClasses("com.fr.bi");
                set.addAll(BIClassUtils.getClasses("com.finebi"));
                Iterator<Class<?>> it = set.iterator();
                while (it.hasNext()) {
                    try {
                        load(it.next());
                    } catch (Exception ex) {
                        BILogDelegate.errorDelegate(ex.getMessage(), ex);
                    }
                }
                loadFlag = true;
            }
        }
    }

    public static void reload() {
        synchronized (BIFactoryAutoLoader.class) {
            loadFlag = true;
            load();
        }
    }

    private static void load(Class clazz) throws Exception {
        if (clazz.isAnnotationPresent(BIMandatedObject.class)) {
//            if (clazz.getName().equals("com.finebi.cube.router.IRouter")){
//                System.out.println("find");
//            }
            BIMandatedObject registerObject = (BIMandatedObject) clazz.getAnnotation(BIMandatedObject.class);
            if (!ComparatorUtils.equals(registerObject.factory(), "default_factory")) {
                IFactoryService factory = (IFactoryService) BIMateFactory.getInstance().getObject(registerObject.factory(), new Object[]{});
                if (!ComparatorUtils.equals(registerObject.key(), "default_key")) {
                    registerClass(factory, registerObject.key(), clazz);
                    registerModule(registerObject.key(), registerObject.module());
                }
                if (!ComparatorUtils.equals(registerObject.implement(), Object.class)) {
                    registerClass(factory, registerObject.implement().getName(), clazz);
                    registerModule(registerObject.implement().getName(), registerObject.module());
                } else {
                    registerClass(factory, clazz.getName(), clazz);
                    registerModule(clazz.getName(), registerObject.module());
                }
            }
        }
    }

    private static void registerClass(IFactoryService factory, String key, Class clazz) throws BIFactoryKeyDuplicateException {
        factory.registerClass(key, clazz);
    }

    private static void registerModule(String key, String module) {
        try {
            BIModuleFactoryManager.getInstance().registerModuleTag(key, module);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

}