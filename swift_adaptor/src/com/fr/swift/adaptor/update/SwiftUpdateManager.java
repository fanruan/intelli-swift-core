package com.fr.swift.adaptor.update;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.response.update.TableUpdateSetting;
import com.finebi.conf.internalimp.update.GlobalUpdateInfo;
import com.finebi.conf.internalimp.update.GlobalUpdateLog;
import com.finebi.conf.internalimp.update.GlobalUpdateSetting;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.internalimp.update.UpdateLog;
import com.finebi.conf.internalimp.update.UpdateNeedSpace;
import com.finebi.conf.internalimp.update.UpdateStatus;
import com.finebi.conf.provider.SwiftTableConfProvider;
import com.finebi.conf.service.engine.update.EngineUpdateManager;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.fs.control.UserControl;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.increment.Increment;
import com.fr.swift.manager.ProviderManager;
import com.fr.swift.provider.IndexStuffInfoProvider;
import com.fr.swift.source.container.SourceContainer;
import com.fr.swift.source.manager.IndexStuffProvider;
import com.fr.swift.stuff.HistoryIndexStuffImpl;
import com.fr.swift.stuff.IndexingStuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-12 14:17:13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftUpdateManager implements EngineUpdateManager {

    private long userId = UserControl.getInstance().getSuperManagerID();

    @Override
    public Map<FineBusinessTable, TableUpdateInfo> getTableUpdateInfo() {
        return null;
    }

    @Override
    public List<TableUpdateInfo> getTableUpdateInfo(FineBusinessTable table) {
        TableUpdateInfo tableUpdateInfo = new TableUpdateInfo();
        tableUpdateInfo.setTableName(table.getName());
        List<TableUpdateInfo> tableUpdateInfoList = new ArrayList<TableUpdateInfo>();
        tableUpdateInfoList.add(tableUpdateInfo);
        return tableUpdateInfoList;
    }

    @Override
    public TableUpdateInfo getPackageUpdateInfo(String packageId) throws Exception {
        return null;
    }

    @Override
    public void saveUpdateSetting(TableUpdateInfo updateInfo, FineBusinessTable table) throws Exception {
        Map<FineBusinessTable, TableUpdateInfo> infoMap = new HashMap<FineBusinessTable, TableUpdateInfo>();
        infoMap.put(table, updateInfo);
        saveUpdateSetting(infoMap);
    }

    @Override
    public void triggerTableUpdate(TableUpdateInfo updateInfo, FineBusinessTable table) throws Exception {

    }

    @Override
    public void saveUpdateSetting(Map<FineBusinessTable, TableUpdateInfo> infoMap) throws Exception {
        if (infoMap == null) {
        }
        List<String> updateTableSourceKeys = new ArrayList<String>();
        List<String> updateRelationSourceKeys = new ArrayList<String>();
        List<String> updatePathSourceKeys = new ArrayList<String>();
        SourceContainer updateSourceContainer = new SourceContainer();

        Map<String, List<Increment>> incrementMap = new HashMap<String, List<Increment>>();
        IndexingDataSourceFactory.transformDataSources(infoMap, updateTableSourceKeys, updateSourceContainer, incrementMap);

        IndexingStuff indexingStuff = new HistoryIndexStuffImpl(updateTableSourceKeys, updateRelationSourceKeys, updatePathSourceKeys);
        IndexStuffProvider indexStuffProvider = new IndexStuffInfoProvider(indexingStuff, updateSourceContainer, incrementMap);
        ProviderManager.getManager().registProvider(userId, indexStuffProvider);
    }

    @Override
    public void saveTableUpdateSetting(TableUpdateSetting tableUpdateSetting) throws Exception {
        FineBusinessTable fineBusinessTable = new SwiftTableConfProvider().getSingleTable(tableUpdateSetting.getTableName());
        Map<FineBusinessTable, TableUpdateInfo> infoMap = new HashMap<FineBusinessTable, TableUpdateInfo>();
        infoMap.put(fineBusinessTable, tableUpdateSetting.getSettings().get(tableUpdateSetting.getTableName()));
        this.saveUpdateSetting(infoMap);
    }

    @Override
    public void savePackageUpdateSetting(String packId, TableUpdateInfo info) throws Exception {
    }

    @Override
    public void triggerPackageUpdate(String packId) throws Exception {

    }

    @Override
    public Map<String, UpdateStatus> getTableUpdateStatus(FineBusinessTable table) {
        return null;
    }

    @Override
    public UpdateStatus getTableUpdateState(String tableName) throws Exception {
        return null;
    }

    @Override
    public UpdateStatus getPackUpdateStatus(String packId) throws Exception {
        return null;
    }

    @Override
    public List<UpdateLog> getTableUpdateLog(FineBusinessTable table) {
        return null;
    }

    @Override
    public void updateAll(GlobalUpdateSetting info) throws Exception {

    }


    @Override
    public GlobalUpdateSetting getUpdateInfo() throws Exception {
        return null;
    }

    @Override
    public void triggerAllUpdate(TableUpdateInfo info) throws Exception {

    }

    @Override
    public GlobalUpdateInfo checkGlobalUpdateInfo() {
        return null;
    }

    @Override
    public GlobalUpdateLog getGlobalUpdateLog() {
        return null;
    }

    @Override
    public UpdateNeedSpace getUpdateNeedSpace() {
        return null;
    }

    @Override
    public boolean shouldUpdate() {
        return false;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}