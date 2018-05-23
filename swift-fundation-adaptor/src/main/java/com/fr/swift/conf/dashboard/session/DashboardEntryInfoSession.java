package com.fr.swift.conf.dashboard.session;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.base.config.utils.Utils;
import com.finebi.common.internalimp.config.driver.CommonDataSourceDriverFactory;
import com.finebi.common.internalimp.config.entryinfo.EntryInfoWrapper;
import com.finebi.common.internalimp.config.session.CommonConfigManager;
import com.finebi.common.structure.config.driver.CommonDataSourceDriver;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.entryinfo.EntryInfoIdNameMap;
import com.finebi.common.structure.config.intercept.EntryInfoInterceptor;
import com.finebi.common.structure.config.intercept.InterceptorFactory;
import com.finebi.common.structure.config.session.EntryInfoSession;
import com.finebi.common.structure.config.store.Store;
import com.fr.engine.config.constants.DefaultDriverTypes;
import com.fr.stable.StableUtils;
import com.fr.swift.conf.dashboard.store.DashboardTableIdNameStore;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fr.engine.constant.Null.isNull;

/**
 * @author yee
 * @date 2018/5/22
 */
public class DashboardEntryInfoSession implements EntryInfoSession {
    private Store<String, EntryInfoWrapper> store;
    private FineEngineType type = FineEngineType.Cube;
    private EntryInfoIdNameMap idNameMap;

    public DashboardEntryInfoSession(Store<String, EntryInfoWrapper> store) {
        this.store = store;
        createEntryInfoIdNameMap();
    }

    @Override
    public void save(EntryInfo entryInfo) {
        synchronized (this) {
            store.put(entryInfo.getID(), new EntryInfoWrapper(entryInfo, type.getType()));
            idNameMap.put(entryInfo.getID(), entryInfo.getAlias());
        }
    }

    @Override
    public void update(EntryInfo entryInfo) {
        save(entryInfo);
    }


    @Override
    public void delete(EntryInfo entryInfo) {
        delete(entryInfo.getID());
    }

    @Override
    public void delete(String id) {
        synchronized (this) {
//            delScheduler(id);
            store.remove(id);
            idNameMap.remove(id);
        }
    }


    @Override
    public EntryInfo find(String id) {
        EntryInfoWrapper wrapper = store.get(id);
        if (wrapper != null) {
            return wrapper.getEntryInfo();
        }
        return null;
    }

    @Override
    public EntryInfo findByName(String name) {
        name = Utils.encodeName(name);
        String id = idNameMap.getId(name);
        return id != null ? find(id) : null;
    }

    @Override
    public EntryInfo findInterceptEntryInfo(String id) {
        EntryInfoWrapper entryInfoWrapper = store.get(id);
        EntryInfo entryInfo = null;
        if (!StableUtils.isNull(entryInfoWrapper)) {
            entryInfo = entryInfoWrapper.getEntryInfo();
        }
        List<EntryInfoInterceptor> entryInfoInterceptors = InterceptorFactory.getInstance().getEntryInfoInterceptors();
        for (EntryInfoInterceptor interceptor : entryInfoInterceptors) {
            entryInfo = interceptor.intercept(entryInfo);
        }
        return entryInfo;
    }

    @Override
    public Map<String, EntryInfo> getAll() {
        Map<String, EntryInfo> map = new HashMap<String, EntryInfo>();
        for (Map.Entry<String, EntryInfoWrapper> entryInfoEntry : store.getAll().entrySet()) {
            map.put(entryInfoEntry.getKey(), entryInfoEntry.getValue().getEntryInfo());
        }
        return map;

    }


    @Override
    public EntryInfo getByPackageIdAndEntryInfoName(String packageId, String entryInfoName) {
        List<EntryInfo> entryInfosByPackageId = CommonConfigManager.getPackageSession(type).getAllTableByPackageId(packageId);
        for (EntryInfo entryInfo : entryInfosByPackageId) {
            if (entryInfo.getAlias().equals(entryInfoName)) {
                return entryInfo;
            }
        }
        return null;
    }


    @Override
    public void refresh() {
        synchronized (this) {
            initIdNameMap();
        }
    }


    private void initIdNameMap() {
        //idNameMap.clear();
        for (Map.Entry<String, EntryInfoWrapper> entry : store.getAll().entrySet()) {
            String entryInfoName = entry.getValue().getName();
            idNameMap.put(entry.getKey(), entryInfoName);
        }
    }

    @Override
    public void refreshByDriver() {
        synchronized (this) {
            refreshFromRegisterConfig();
        }
    }

    @Override
    public EntryInfoIdNameMap getIdNameMap() {
        return idNameMap;
    }


    /**
     * 从其他配置文件读数据
     */
    private void refreshFromRegisterConfig() {
        List<CommonDataSourceDriver> refreshDrivers = CommonDataSourceDriverFactory.getInstance(type).getDrivers();
        for (CommonDataSourceDriver driver : refreshDrivers) {
            if (needAdaptOldVersion(driver)) {
                List<EntryInfo> list = driver.initEntryInfo();
                for (EntryInfo entryInfo : list) {
                    if (!store.containsKey(entryInfo.getID())) {
                        store.put(entryInfo.getID(), new EntryInfoWrapper(entryInfo, type.getType()));
                    }
                }
            } else {
                List<EntryInfo> list = driver.initEntryInfo();
                if (isNull(list)) {
                    continue;
                }
                for (EntryInfo entryInfo : list) {
                    store.put(entryInfo.getID(), new EntryInfoWrapper(entryInfo, type.getType()));
                }
            }
        }
    }


    private boolean needAdaptOldVersion(CommonDataSourceDriver driver) {
        return driver.getType().equals(DefaultDriverTypes.SQL) || driver.getType().equals(DefaultDriverTypes.JOIN) || driver.getType().equals(DefaultDriverTypes.CIRCLE);
    }

    private void delFieldInfoByTable(String id) {
        CommonConfigManager.getFieldSession(type).delete(id);
    }

    private void createEntryInfoIdNameMap() {
        try {
            Constructor<EntryInfoIdNameMap> constructor = EntryInfoIdNameMap.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            idNameMap = constructor.newInstance();
        } catch (Exception e) {
            idNameMap = EntryInfoIdNameMap.getInstance();
        }
        idNameMap.setTableIdNameStore(DashboardTableIdNameStore.getInstance());
    }
}
