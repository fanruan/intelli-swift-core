package com.fr.bi.conf.utils;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.provider.BIDataSourceManagerProvider;
import com.fr.bi.conf.provider.BISystemPackageConfigurationProvider;
import com.fr.bi.module.BIModule;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.engine.index.AbstractTIPathLoader;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/16.
 */
public class BIModuleUtils {

    public static JSONObject createPackJSON(long userId) throws Exception {
        JSONObject jo = new JSONObject();
        for (BIModule module : BIModuleManager.getModules()) {
            BISystemPackageConfigurationProvider provider = module.getBusiPackManagerProvider();
            jo.join(provider.createPackageJSON(userId));
        }
        return jo;
    }

    public static JSONObject createGroupJSON(long userId) throws JSONException {
        JSONObject jo = new JSONObject();
        for (BIModule module : BIModuleManager.getModules()) {
            BISystemPackageConfigurationProvider provider = module.getBusiPackManagerProvider();
            jo.join( provider.createGroupJSON(userId));
        }
        return jo;
    }

    public static ICubeTableService getTableIndex(Table td, BIUser user, Map<String, AbstractTIPathLoader> childLoaderMap) {
        for (BIModule module : BIModuleManager.getModules()) {
            BIDataSourceManagerProvider provider = module.getDataSourceManagerProvider();
            ITableSource source = provider.getTableSourceByID(td.getID(), user);
            if (source != null) {
                return childLoaderMap.get(module.getModuleName()).getTableIndexByPath(source.getSourceFile());
            }
        }
        return null;
    }

    public static ICubeTableService getTableIndex(BICore md5, BIUser user, Map<String, AbstractTIPathLoader> childLoaderMap) {
        for (BIModule module : BIModuleManager.getModules()) {
            BIDataSourceManagerProvider provider = module.getDataSourceManagerProvider();
            ITableSource source = provider.getTableSourceByCore(md5, user);
            if (source != null) {
                return childLoaderMap.get(module.getModuleName()).getTableIndexByPath(source.getSourceFile());
            }
        }
        return null;
    }

    public static ITableSource getSourceByCore(BICore md5, BIUser user) {
        for (BIModule module : BIModuleManager.getModules()) {
            BIDataSourceManagerProvider provider = module.getDataSourceManagerProvider();
            ITableSource source = provider.getTableSourceByCore(md5, user);
            if (source != null) {
                return source;
            }
        }
        return null;
    }

    public static BIKey getFieldIndex(BIField column, BIUser user, Map<String, AbstractTIPathLoader> childLoaderMap) {
        ICubeTableService ti = getTableIndex(column.getTableBelongTo(), user, childLoaderMap);
        return ti == null ? null : ti.getColumnIndex(column);
    }
    

    public static Set<BIBusinessPackage> getAllPacks(long userId) {
        Set<BIBusinessPackage> set = new HashSet<BIBusinessPackage>();
        for (BIModule module : BIModuleManager.getModules()) {
            BISystemPackageConfigurationProvider provider = module.getBusiPackManagerProvider();
            set.addAll(provider.getAllPackages(userId));
        }
        return set;
    }

    public static ITableSource getSourceByID(BITableID id, BIUser user) {
        for (BIModule module : BIModuleManager.getModules()) {
            BIDataSourceManagerProvider provider = module.getDataSourceManagerProvider();
            ITableSource source = provider.getTableSourceByID(id, user);
            if (source != null) {
                return source;
            }
        }
        return null;
    }
}