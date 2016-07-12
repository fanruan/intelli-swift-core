package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.relation.*;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wuk on 16/5/30.
 */
public interface CubeBuild {


    Set<BITableSourceRelationPath> getBiTableSourceRelationPathSet();

    Set<CubeTableSource> getAllSingleSources();

    Set<BITableSourceRelation> getTableSourceRelationSet();

    Set<CubeTableSource> getSources();

    Set<List<Set<CubeTableSource>>> getDependTableResource();

    ICubeConfiguration getCubeConfiguration();

    Set<BITableRelation> getTableRelationSet();

    Map<CubeTableSource, Long> getVersions();

    Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet();

    Set<BICubeGenerateRelation> getCubeGenerateRelationSet();

    boolean preConditionsCheck();
}
