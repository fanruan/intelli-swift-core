package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.*;

import static com.finebi.cube.conf.BICubeConfigureCenter.*;

/**
 * Created by 49597 on 2016/6/8.
 */
public class CubeBuildStuffManagerByTableSources implements CubeBuildStuff {

    private Set<CubeTableSource> allSingleSources;
    private Set<BIBusinessTable> tableSources4Genrate;
    private ICubeConfiguration cubeConfiguration;
    private BIUser biUser;
    private Set<List<Set<CubeTableSource>>> dependTableResource;
    private Set<BITableRelation> tableRelationSet;


    public CubeBuildStuffManagerByTableSources(Set<BIBusinessTable> tableSources4Genrate, long userId) {
        this.biUser = new BIUser(userId);
        this.tableSources4Genrate=tableSources4Genrate;
        this.cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
        this.dependTableResource = calculateTableSource(set2Set(calculateTableSource(allSingleSources)));
        try {
            init();
        } catch (BITableAbsentException e) {
            BILogger.getLogger().error(e.getMessage());
        }
    }

    private void init() throws BITableAbsentException {
          Set<CubeTableSource> cubeTableSourceHashSet=new HashSet<CubeTableSource>();
        for (BIBusinessTable biBusinessTable : tableSources4Genrate) {
            cubeTableSourceHashSet.add(biBusinessTable.getTableSource());
            Set<BITableRelation> primaryContainer = getTableRelationManager().getPrimaryRelation(biUser.getUserId(), biBusinessTable).getContainer();
            Set<BITableRelation> foreignContainer = getTableRelationManager().getForeignRelation(biUser.getUserId(), biBusinessTable).getContainer();
            tableRelationSet.add(primaryRelation)
        }
        this.allSingleSources = set2Set(calculateTableSource(cubeTableSourceHashSet));
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
