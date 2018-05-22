package com.fr.swift.adaptor.space;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.space.SpaceInfo;
import com.finebi.conf.provider.SwiftAnalysisConfManager;
import com.finebi.conf.service.engine.space.EngineSpaceManager;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.cube.space.SpaceUnit;
import com.fr.swift.cube.space.SpaceUsageService;
import com.fr.swift.cube.space.impl.SpaceUsageServiceImpl;
import com.fr.swift.source.SourceKey;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class SwiftSpaceManager implements EngineSpaceManager {
    @Autowired
    private SwiftAnalysisConfManager analysisConfManager;

    private SpaceUsageService spaceUsageService = SpaceUsageServiceImpl.getInstance();

    private static SpaceInfo newSpaceInfo(long size) {
        SpaceInfo info = new SpaceInfo();
        info.setSpace(SpaceUnit.MB.of(size));
        return info;
    }

    @Override
    public SpaceInfo getTableSpaceInfo(String table) throws Exception {
        FineBusinessTable fineTable = analysisConfManager.getBusinessTable(table);
        long used = spaceUsageService.getTableUsedSpace(new SourceKey(fineTable.getName()));
        return newSpaceInfo(used);
    }

    @Override
    public SpaceInfo getPackageSpaceInfo(String pack) throws Exception {
        List<FineBusinessTable> fineTables = analysisConfManager.getBusinessTables(pack);
        List<SourceKey> tables = new ArrayList<SourceKey>();
        for (FineBusinessTable fineTable : fineTables) {
            tables.add(DataSourceFactory.getDataSourceInCache(fineTable).getSourceKey());
        }
        long used = spaceUsageService.getTableUsedSpace(tables);
        return newSpaceInfo(used);
    }

    @Override
    public SpaceInfo getAllOccupySpaceInfo() throws Exception {
        return newSpaceInfo(spaceUsageService.getUsedOverall());
    }

    @Override
    public SpaceInfo getDiskSpaceInfo() throws Exception {
        return newSpaceInfo(spaceUsageService.getTotalOverall());
    }

    @Override
    public SpaceInfo getUsefulSpaceInfo() throws Exception {
        return newSpaceInfo(spaceUsageService.getUsableOverall());
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}