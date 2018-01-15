package com.fr.swift.adaptor.update;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.internalimp.update.UpdateLog;
import com.finebi.conf.internalimp.update.UpdateStatus;
import com.finebi.conf.service.engine.update.EngineUpdateManager;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.fs.control.UserControl;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.manager.ProviderManager;
import com.fr.swift.provider.IndexStuffInfoProvider;
import com.fr.swift.source.container.SourceContainer;
import com.fr.swift.source.manager.IndexStuffProvider;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.stuff.TotalIndexStuffImpl;

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
    public TableUpdateInfo getTableUpdateInfo(FineBusinessTable table) {
        return null;
    }

    @Override
    public boolean saveUpdateSetting(TableUpdateInfo updateInfo, FineBusinessTable table) throws Exception {
        Map<FineBusinessTable, TableUpdateInfo> infoMap = new HashMap<FineBusinessTable, TableUpdateInfo>();
        infoMap.put(table, updateInfo);
        return saveUpdateSetting(infoMap);
    }

    @Override
    public boolean saveUpdateSetting(Map<FineBusinessTable, TableUpdateInfo> infoMap) throws Exception {
        List<String> updateTableSourceKeys = new ArrayList<String>();
        List<String> updateRelationSourceKeys = new ArrayList<String>();
        List<String> updatePathSourceKeys = new ArrayList<String>();
        SourceContainer updateSourceContainer = new SourceContainer();

        IndexingDataSourceFactory.transformDataSources(infoMap, updateTableSourceKeys, updateSourceContainer);

        IndexingStuff indexingStuff = new TotalIndexStuffImpl(updateTableSourceKeys, updateRelationSourceKeys, updatePathSourceKeys);
        IndexStuffProvider indexStuffProvider = new IndexStuffInfoProvider(indexingStuff, updateSourceContainer);
        ProviderManager.getManager().registProvider(userId, indexStuffProvider);
        return true;
    }

    @Override
    public UpdateStatus getTableUpdateStatus(FineBusinessTable table) {
        return null;
    }

    @Override
    public UpdateLog getTableUpdateLog(FineBusinessTable table) {
        return null;
    }

    @Override
    public void updateAll(TableUpdateInfo info) {

    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
