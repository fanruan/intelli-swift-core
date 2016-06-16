package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CalculateDependTool;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.*;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.*;

import static com.finebi.cube.conf.BICubeConfigureCenter.getTableRelationManager;

/**
 * Created by kary on 2016/6/8.
 * 增量更新，尽量减少依赖，最大化提升效率
 */
public class CubeBuildStuffManagerIncremental implements CubeBuildStuff {

    private Set<CubeTableSource> allSingleSources;
    private Set<CubeTableSource> sources;
    private Set<BIBusinessTable> newBiBusinessTableSet;
    private ICubeConfiguration cubeConfiguration;
    private BIUser biUser;
    private Set<List<Set<CubeTableSource>>> dependTableResource;
    private Set<BITableRelation> tableRelationSet;
    private Set<BITableSourceRelation> biTableSourceRelationSet;
    private Set<BITableSourceRelationPath> biTableSourceRelationPathSet;
    private Set<BICubeGenerateRelationPath> cubeGenerateRelationPathSet;
    private Set<BICubeGenerateRelation> cubeGenerateRelationSet;

    public CubeBuildStuffManagerIncremental(Set<BIBusinessTable> newTables, long userId) {
        this.biUser = new BIUser(userId);
        this.cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
        this.newBiBusinessTableSet = newTables;
        init();
        try {
            setSources();
            setResourcesAndDepends();
            setRealationAndPath();
            calculateRelationDepends();
        } catch (BITableAbsentException e) {
            BILogger.getLogger().error(e.getMessage());
        }
    }

    private void setSources() {
        this.sources = new HashSet<CubeTableSource>();
        for (IBusinessPackageGetterService pack : BICubeConfigureCenter.getPackageManager().getAllPackages(biUser.getUserId())) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                BIBusinessTable table = tIt.next();
                sources.add(table.getTableSource());
            }
        }
    }

    private void init() {
        this.sources = new HashSet<CubeTableSource>();
        this.allSingleSources = new HashSet<CubeTableSource>();
        this.tableRelationSet = new HashSet<BITableRelation>();
        biTableSourceRelationSet = new HashSet<BITableSourceRelation>();
        biTableSourceRelationPathSet = new HashSet<BITableSourceRelationPath>();
    }


    private void calculateRelationDepends() {
        CalculateDependTool cal = new CalculateDependManager();
        cal.setOriginal(this.sources);
        cubeGenerateRelationSet = new HashSet<BICubeGenerateRelation>();
        for (BITableSourceRelation biTableSourceRelation : this.getTableSourceRelationSet()) {
            this.cubeGenerateRelationSet.add(cal.calRelations(biTableSourceRelation));
        }
        cubeGenerateRelationPathSet = new HashSet<BICubeGenerateRelationPath>();
        for (BITableSourceRelationPath biTableSourceRelationPath : this.getBiTableSourceRelationPathSet()) {
            BICubeGenerateRelationPath path = cal.calRelationPath(biTableSourceRelationPath, this.biTableSourceRelationSet);
            if (null != path) {
                cubeGenerateRelationPathSet.add(path);
            }
        }
    }

    protected void setResourcesAndDepends() throws BITableAbsentException {
        Set<CubeTableSource> cubeTableSourceHashSet = new HashSet<CubeTableSource>();
        for (BIBusinessTable biBusinessTable : newBiBusinessTableSet) {
            sources.add(biBusinessTable.getTableSource());
            cubeTableSourceHashSet.add(biBusinessTable.getTableSource());
            Set<BITableRelation> primaryRelaitons = getTableRelationManager().getPrimaryRelation(biUser.getUserId(), biBusinessTable).getContainer();
            Set<BITableRelation> foreignRelaitons = getTableRelationManager().getForeignRelation(biUser.getUserId(), biBusinessTable).getContainer();
            tableRelationSet.addAll(primaryRelaitons);
            tableRelationSet.addAll(foreignRelaitons);
        }
        Set<List<Set<CubeTableSource>>> depends = calculateTableSource(cubeTableSourceHashSet);
        this.dependTableResource = depends;
        this.allSingleSources = set2Set(depends);
    }

    private void setRealationAndPath() {
        try {
            Set<BITableRelationPath> allTablePath = BICubeConfigureCenter.getTableRelationManager().getAllTablePath(biUser.getUserId());
            for (BITableRelationPath biTableRelationPath : allTablePath) {
                for (BITableRelation biTableRelation : tableRelationSet) {
                    if (biTableRelationPath.getAllRelations().contains(biTableRelation)) {
                        biTableSourceRelationPathSet.add(convertBITableRelationPathToBITableSourceRelationPath(biTableRelationPath));
                        break;
                    }
                }
            }
            for (BITableRelation biTableRelation : this.tableRelationSet) {
                BITableSourceRelation biTableSourceRelation = convetTableRealtionToTableSourceRealtion(biTableRelation);
                biTableSourceRelationSet.add(biTableSourceRelation);
            }

        } catch (BITableRelationConfusionException e) {
            BILogger.getLogger().error(e.getMessage());
        } catch (BITablePathConfusionException e) {
            BILogger.getLogger().error(e.getMessage());
        }
    }


    private Set<CubeTableSource> set2Set(Set<List<Set<CubeTableSource>>> set) {
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

    private BITableSourceRelation convetTableRealtionToTableSourceRealtion(BITableRelation biTableRelation) {
        CubeTableSource primaryTable = null;
        CubeTableSource foreignTable = null;
        ICubeFieldSource primaryField = null;
        ICubeFieldSource foreignField = null;
        primaryTable = biTableRelation.getPrimaryTable().getTableSource();
        foreignTable = biTableRelation.getForeignTable().getTableSource();
        for (ICubeFieldSource iCubeFieldSource : primaryTable.getSelfFields(allSingleSources)) {
            if (iCubeFieldSource.getFieldName().equals(biTableRelation.getPrimaryField().getFieldName())) {
                primaryField = iCubeFieldSource;
                break;
            }
        }
        for (ICubeFieldSource iCubeFieldSource : foreignTable.getSelfFields(allSingleSources)) {
            if (iCubeFieldSource.getFieldName().equals(biTableRelation.getForeignField().getFieldName())) {
                foreignField = iCubeFieldSource;
                break;
            }
        }
        primaryField.setTableBelongTo(primaryTable);
        foreignField.setTableBelongTo(foreignTable);
        return new BITableSourceRelation(
                primaryField,
                foreignField,
                primaryTable,
                foreignTable
        );
    }

    private BITableSourceRelationPath convertBITableRelationPathToBITableSourceRelationPath(BITableRelationPath path) throws BITablePathConfusionException {
        BITableSourceRelationPath tableSourceRelationPath = new BITableSourceRelationPath();
        for (BITableRelation biTableRelation : path.getAllRelations()) {
            BITableSourceRelation biTableSourceRelation = convetTableRealtionToTableSourceRealtion(biTableRelation);
            tableSourceRelationPath.addRelationAtTail(biTableSourceRelation);
        }
        return tableSourceRelationPath;
    }

    public Set<BITableSourceRelationPath> getBiTableSourceRelationPathSet() {
        return this.biTableSourceRelationPathSet;
    }


    @Override
    public Set<CubeTableSource> getAllSingleSources() {
        return allSingleSources;
    }

    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return this.biTableSourceRelationSet;
    }

    @Override
    public Set<CubeTableSource> getSources() {
        return this.sources;
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
        return this.tableRelationSet;
    }

    @Override
    public Map<CubeTableSource, Long> getVersions() {
        Set<CubeTableSource> allTable = getAllSingleSources();
        Map<CubeTableSource, Long> result = new HashMap<CubeTableSource, Long>();
        Long version = System.currentTimeMillis();
        for (CubeTableSource table : allTable) {
            result.put(table, version);
        }
        return result;
    }

    public Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathSet() {
        return this.cubeGenerateRelationPathSet;
    }

    public Set<BICubeGenerateRelation> getCubeGenerateRelationSet() {
        return this.cubeGenerateRelationSet;
    }
}
