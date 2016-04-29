package com.fr.bi.conf.utils;

import com.fr.bi.module.BIModule;
import com.fr.bi.stable.engine.index.AbstractTIPathLoader;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public class BIModuleManager {

    private static LinkedHashMap<String, BIModule> modules = new LinkedHashMap<String, BIModule>();

    private static Map<String, Class<? extends AbstractTIPathLoader>> loaderClassMap = new ConcurrentHashMap<String, Class<? extends AbstractTIPathLoader>>();

    public static Collection<BIModule> getModules() {
        return modules.values();
    }

    public static Map<String, Class<? extends AbstractTIPathLoader>> getLoaderClassMap() {
        return loaderClassMap;
    }

    public static void registModule(BIModule module){
        if(modules.containsKey(module.getModuleName())){
            return;
        }
        modules.put(module.getModuleName(), module);
        loaderClassMap.put(module.getModuleName(), module.getTIPathLoaderClass());
    }

    public static boolean isModuleAllAdmin(String name){
        return modules.get(name).isAllAdmin();
    }

}