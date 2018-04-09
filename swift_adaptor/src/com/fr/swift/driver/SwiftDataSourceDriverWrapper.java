package com.fr.swift.driver;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.fieldinfo.SimpleFieldInfoPersist;
import com.finebi.common.structure.config.driver.CommonDataSourceDriver;
import com.finebi.common.structure.config.driver.PersistDriver;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.entryinfo.EntryInfoFactory;
import com.finebi.common.structure.config.entryinfo.EntryInfoStoreManager;
import com.finebi.common.structure.config.fieldinfo.FieldInfoPersist;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.engine.config.constants.DriverType;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/9
 */
public class SwiftDataSourceDriverWrapper implements CommonDataSourceDriver {

    private CommonDataSourceDriver driver;

    public SwiftDataSourceDriverWrapper(CommonDataSourceDriver driver) {
        this.driver = driver;
    }

    @Override
    public DriverType getType() {
        return driver.getType();
    }

    @Override
    public PersistDriver getPersistDriver() {
        return driver.getPersistDriver();
    }

    @Override
    public JSONObject getEntryInfoJSONObject(EntryInfo entryInfo) {
        return driver.getEntryInfoJSONObject(entryInfo);
    }

    @Override
    public List<EntryInfo> initEntryInfo() {
        return driver.initEntryInfo();
    }

    @Override
    public void init() {
        driver.init();
    }

    /**
     * TODO 返回simple
     * @return
     */
    @Override
    public FieldInfoPersist getFieldInfoPersist() {
        return SimpleFieldInfoPersist.INSTANCE;
    }

    @Override
    public EntryInfo createEntryInfo(FineBusinessTable table) {
        return driver.createEntryInfo(table);
    }

    @Override
    public FineBusinessTable createBusinessTable(EntryInfo entryInfo, FineEngineType type) {
        return driver.createBusinessTable(entryInfo, type);
    }

    @Override
    public EntryInfoFactory getEntryInfoFactory() {
        return driver.getEntryInfoFactory();
    }

    @Override
    public EntryInfoStoreManager getEntryInfoStoreSession() {
        return driver.getEntryInfoStoreSession();
    }
}
