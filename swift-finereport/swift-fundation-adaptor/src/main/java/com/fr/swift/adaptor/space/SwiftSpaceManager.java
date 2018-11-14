package com.fr.swift.adaptor.space;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.exception.FinePackageAbsentException;
import com.finebi.conf.exception.FineTableAbsentException;
import com.finebi.conf.internalimp.space.SpaceInfo;
import com.finebi.conf.provider.SwiftAnalysisConfManager;
import com.finebi.conf.service.engine.space.EngineSpaceManager;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.cube.space.SpaceUnit;
import com.fr.swift.cube.space.SpaceUsageService;
import com.fr.swift.cube.space.impl.SwiftSpaceUsageService;
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

    private SpaceUsageService spaceUsageService = SwiftSpaceUsageService.getInstance();

    private static SpaceInfo newSpaceInfo(long size) {
        SpaceInfo info = new SpaceInfo();
        info.setSpace(SpaceUnit.MB.of(size));
        return info;
    }

    @Override
    public SpaceInfo getTableSpaceInfo(String table) throws Exception {
        long used;
        try {
            FineBusinessTable fineTable = analysisConfManager.getBusinessTable(table);
            used = spaceUsageService.getTableUsedSpace(DataSourceFactory.getDataSourceInCache(fineTable).getSourceKey());
        } catch (FineTableAbsentException e) {
            used = 0;
        }
        return newSpaceInfo(used);
    }

    @Override
    public SpaceInfo getPackageSpaceInfo(String pack) throws Exception {
        long used;
        try {
            List<FineBusinessTable> fineTables = analysisConfManager.getBusinessTables(pack);
            List<SourceKey> tables = new ArrayList<SourceKey>();
            for (FineBusinessTable fineTable : fineTables) {
                tables.add(DataSourceFactory.getDataSourceInCache(fineTable).getSourceKey());
            }
            used = spaceUsageService.getTableUsedSpace(tables);
        } catch (FinePackageAbsentException e) {
            used = 0;
        }
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