package com.fr.bi.conf.utils;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.module.BIModule;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/16.
 */
public class BIModuleUtils {

    public static JSONObject createPackJSON(long userId, Locale locale) throws Exception {
        JSONObject jo = new JSONObject();
        for (BIModule module : BIModuleManager.getModules()) {
            BISystemPackageConfigurationProvider provider = module.getBusiPackManagerProvider();
            jo.join(provider.createPackageJSON(userId, locale));
        }
        return jo;
    }

    public static JSONObject createGroupJSON(long userId) throws JSONException {
        JSONObject jo = new JSONObject();
        for (BIModule module : BIModuleManager.getModules()) {
            BISystemPackageConfigurationProvider provider = module.getBusiPackManagerProvider();
            jo.join(provider.createGroupJSON(userId));
        }
        return jo;
    }

    public static ICubeTableService getTableIndex(BusinessTable td, BIUser user, Map<String, ICubeDataLoader> childLoaderMap) {
        for (BIModule module : BIModuleManager.getModules()) {
            return childLoaderMap.get(module.getModuleName()).getTableIndex(td.getTableSource());
        }
        return null;
    }

    public static ICubeTableService getTableIndex(CubeTableSource tableSource, BIUser user, Map<String, ICubeDataLoader> childLoaderMap) {
        for (BIModule module : BIModuleManager.getModules()) {
            return childLoaderMap.get(module.getModuleName()).getTableIndex(tableSource);
        }
        return null;
    }


    public static BIKey getFieldIndex(BusinessField column, BIUser user, Map<String, ICubeDataLoader> childLoaderMap) {
        ICubeTableService ti = getTableIndex(column.getTableBelongTo(), user, childLoaderMap);
        return ti == null ? null : ti.getColumnIndex(column);
    }


    public static Set<IBusinessPackageGetterService> getAllPacks(long userId) {
        Set<IBusinessPackageGetterService> set = new HashSet<IBusinessPackageGetterService>();
        for (BIModule module : BIModuleManager.getModules()) {
            BISystemPackageConfigurationProvider provider = module.getBusiPackManagerProvider();
            set.addAll(provider.getAllPackages(userId));
        }
        return set;
    }

    public static CubeTableSource getSourceByID(BITableID id, BIUser user) {
        for (BIModule module : BIModuleManager.getModules()) {
            BIDataSourceManagerProvider provider = module.getDataSourceManagerProvider();
            CubeTableSource source = null;
            try {
                source = provider.getTableSource(id);
            } catch (BIKeyAbsentException e) {
                throw BINonValueUtils.beyondControl(e);
            }
            if (source != null) {
                return source;
            }
        }
        return null;
    }
}