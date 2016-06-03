package com.finebi.cube.conf;


import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.*;

/**
 * Created by wuk on 16/6/1.
 */
public class CubeBuildStuffManagerTableSource implements CubeBuildStuff {

    private Set<CubeTableSource> allSingleSources;
    private ICubeConfiguration cubeConfiguration;
    private BIUser biUser;
    Set<List<Set<CubeTableSource>>> dependTableResource;

    public CubeBuildStuffManagerTableSource(CubeTableSource cubeTableSource, ICubeConfiguration cubeConfiguration, long userId) {
        this.biUser = new BIUser(userId);
        this.cubeConfiguration = cubeConfiguration;
        Set<CubeTableSource> sourceSet = new HashSet<CubeTableSource>();
        sourceSet.add(cubeTableSource);
        this.allSingleSources = set2Set(calculateTableSource(sourceSet));
        init();
    }

    private void init() {
        this.dependTableResource = calculateTableSource(set2Set(calculateTableSource(allSingleSources)));

    }

    public CubeBuildStuffManagerTableSource(CubeTableSource cubeTableSource, long userId) {
        this.biUser = new BIUser(userId);
        this.cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
        Set<CubeTableSource> sourceSet = new HashSet<CubeTableSource>();
        sourceSet.add(cubeTableSource);
        this.allSingleSources = set2Set(calculateTableSource(sourceSet));
        init();
    }


    public static Set<CubeTableSource> set2Set(Set<List<Set<CubeTableSource>>> set) {
        Set<CubeTableSource> result = new HashSet<CubeTableSource>();
        Iterator<List<Set<CubeTableSource>>> outIterator = set.iterator();
        while (outIterator.hasNext()) {
            Iterator<Set<CubeTableSource>> middleIterator = outIterator.next().iterator();
            while (middleIterator.hasNext()) {
                result.addAll(middleIterator.next());
            }
        }
        return result;
    }

    private Set<List<Set<CubeTableSource>>> calculateTableSource(Set<CubeTableSource> tableSources) {
        Iterator<CubeTableSource> it = tableSources.iterator();
        Set<List<Set<CubeTableSource>>> depends = new HashSet<List<Set<CubeTableSource>>>();
        while (it.hasNext()) {
            CubeTableSource tableSource = it.next();
            depends.add(tableSource.createGenerateTablesList());
        }
        return depends;
    }

    @Override
    public Set<BITableSourceRelationPath> getRelationPaths() {
        return new HashSet<BITableSourceRelationPath>();
    }


    @Override
    public Set<CubeTableSource> getAllSingleSources() {
        return allSingleSources;
    }

    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return new HashSet<BITableSourceRelation>();
    }

    @Override
    public Set<CubeTableSource> getSources() {
        return allSingleSources;
    }

    @Override
    public Set<List<Set<CubeTableSource>>> getDependTableResource() {
        return dependTableResource;
    }

    @Override
    public ICubeConfiguration getCubeConfiguration() {
        return cubeConfiguration;
    }

    @Override
    public Set<BITableRelation> getTableRelationSet() {
        return new HashSet<BITableRelation>();
    }

    @Override
    public Map<CubeTableSource, Long> getVersions() {
        return null;
    }
}
