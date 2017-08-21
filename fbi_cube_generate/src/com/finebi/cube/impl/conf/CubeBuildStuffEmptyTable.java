package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.AbstractCubeBuildStuff;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTaskType;

import java.util.*;

/**
 * This class created on 2016/11/9.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class CubeBuildStuffEmptyTable extends AbstractCubeBuildStuff {
    private static BILogger logger = BILoggerFactory.getLogger(CubeBuildStuffEmptyTable.class);

    public CubeBuildStuffEmptyTable(long userId, Set<CubeTableSource> allTableSources) {
        super(userId, allTableSources);
    }

    @Override
    public Set<BITableSourceRelationPath> getTableSourceRelationPathSet() {
        return new HashSet<BITableSourceRelationPath>();
    }

    @Override
    public Set<CubeTableSource> getSingleSourceLayers() {
        return new HashSet<CubeTableSource>();
    }

    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return new HashSet<BITableSourceRelation>();
    }

    @Override
    public Set<CubeTableSource> getSystemTableSources() {
        return new HashSet<CubeTableSource>();
    }

    @Override
    public Set<List<Set<CubeTableSource>>> getDependTableResource() {
        return new HashSet<List<Set<CubeTableSource>>>();
    }

    @Override
    public Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet() {
        return new HashSet<BICubeGenerateRelationPath>();
    }

    @Override
    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return new HashSet<BICubeGenerateRelation>();
    }

    @Override
    public boolean preConditionsCheck() {
        return true;
    }

    @Override
    public boolean copyFileFromOldCubes() {
        return true;
    }

    @Override
    public boolean replaceOldCubes() {
        return true;
    }

    @Override
    public Map<CubeTableSource, UpdateSettingSource> getUpdateSettingSources() {
        return new HashMap<CubeTableSource, UpdateSettingSource>();
    }

    @Override
    public String getCubeTaskId() {
        return DBConstant.GLOBAL_UPDATE_TYPE.META_UPDATE;

    }

    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.EMPTY;
    }

    @Override
    public Set<String> getTaskTableSourceIds() {
        return new HashSet<String>();
    }

    @Override
    public boolean isNeed2Update() {
        return true;
    }
}
