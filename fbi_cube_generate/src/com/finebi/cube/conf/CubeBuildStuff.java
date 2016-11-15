package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.relation.*;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kary on 16/5/30.
 */
public interface CubeBuildStuff {


    Set<BITableSourceRelationPath> getTableSourceRelationPathSet();

    Set<CubeTableSource> getSingleSourceLayers();

    Set<BITableSourceRelation> getTableSourceRelationSet();

    Set<CubeTableSource> getSystemTableSources();

    Set<List<Set<CubeTableSource>>> getDependTableResource();

    ICubeConfiguration getCubeConfiguration();


    Map<CubeTableSource, Long> getVersions();

    Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet();

    Set<BICubeGenerateRelation> getCubeGenerateRelationSet();

    boolean preConditionsCheck();

    boolean isSingleTable();

    boolean copyFileFromOldCubes();

    boolean replaceOldCubes();

    Map<CubeTableSource, UpdateSettingSource> getUpdateSettingSources();


   String getCubeTaskId();
}
