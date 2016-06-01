package com.fr.bi.conf.utils;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BIDataSourceManagerProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.module.BIModule;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
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

    public static ICubeTableService getTableIndex(BusinessTable td, BIUser user, Map<String, ICubeDataLoader> childLoaderMap) {
        for (BIModule module : BIModuleManager.getModules()) {
            return childLoaderMap.get(module.getModuleName()).getTableIndex(td.getTableSource());
        }
        return null;
    }

    public static ICubeTableService getTableIndex(CubeTableSource tableSource,  Map<String, ICubeDataLoader> childLoaderMap) {
        for (BIModule module : BIModuleManager.getModules()) {
            BIDataSourceManagerProvider provider = module.getDataSourceManagerProvider();
            if (provider.isRecord(tableSource)) {
                return childLoaderMap.get(module.getModuleName()).getTableIndex(tableSource);
            }
        }
        BINonValueUtils.beyondControl();
        return null;
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
        CubeTableSource source = null;
        for (BIModule module : BIModuleManager.getModules()) {
            BIDataSourceManagerProvider provider = module.getDataSourceManagerProvider();
            try {
                BusinessTable table = provider.getBusinessTable(id);
                if (table != null){
                    source = table.getTableSource();
                }
            } catch (BIKeyAbsentException e) {
            }
            if (source != null) {
                return source;
            }
        }
        if (source == null){
            BINonValueUtils.beyondControl();
        }
        return null;
    }

    public static BusinessField getBusinessFieldById(BIFieldID id) {
        BusinessField field = null;
        for (BIModule module : BIModuleManager.getModules()) {
            BIDataSourceManagerProvider provider = module.getDataSourceManagerProvider();
            try {
                field = provider.getBusinessField(id);
            } catch (BIKeyAbsentException e) {
            }
            if (field != null) {
                return field;
            }
        }
        if (field == null){
            BINonValueUtils.beyondControl();
        }
        return null;
    }

    public static BusinessTable getBusinessTableById(BITableID id) {
        BusinessTable field = null;
        for (BIModule module : BIModuleManager.getModules()) {
            BIDataSourceManagerProvider provider = module.getDataSourceManagerProvider();
            try {
                field = provider.getBusinessTable(id);
            } catch (BIKeyAbsentException e) {
            }
            if (field != null) {
                return field;
            }
        }
        if (field == null){
            BINonValueUtils.beyondControl();
        }
        return null;
    }

}