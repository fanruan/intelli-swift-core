package com.fr.bi.conf.utils;

import com.fr.bi.module.BIModule;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public class BIModuleManager {

    private static LinkedHashMap<String, BIModule> modules = new LinkedHashMap<String, BIModule>();

    public static Collection<BIModule> getModules() {
        return modules.values();
    }

    public static void registModule(BIModule module){
        if(modules.containsKey(module.getModuleName())){
            return;
        }
        modules.put(module.getModuleName(), module);
    }

    public static boolean isModuleAllAdmin(String name){
        return modules.get(name).isAllAdmin();
    }

}