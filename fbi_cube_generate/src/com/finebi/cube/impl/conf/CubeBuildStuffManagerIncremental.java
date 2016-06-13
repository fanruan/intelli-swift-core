package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.*;
import com.finebi.cube.conf.pack.imp.BIPackageTableSourceConfigManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.base.BIUser;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;

import java.util.*;

import static com.finebi.cube.conf.BICubeConfigureCenter.getTableRelationManager;

/**
 * Created by kary on 2016/6/8.
 */
public class CubeBuildStuffManagerIncremental implements CubeBuildStuff {

    private Set<CubeTableSource> allSingleSources;
    private Set<BIBusinessTable> newTables;
    private ICubeConfiguration cubeConfiguration;
    private BIUser biUser;
    private Set<List<Set<CubeTableSource>>> dependTableResource;
    private Set<BITableRelation> tableRelationSet;
    private Set<BITableRelationPath> tableSourceRelationPaths;

    public CubeBuildStuffManagerIncremental(Set<BIBusinessTable> newTables, long userId) {
        this.allSingleSources = new HashSet<CubeTableSource>();
        this.biUser = new BIUser(userId);
        this.newTables = newTables;
        this.cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
        this.tableRelationSet = new HashSet<BITableRelation>();
        tableSourceRelationPaths = new HashSet<BITableRelationPath>();
        try {
            init();
        } catch (BITableAbsentException e) {
            BILogger.getLogger().error(e.getMessage());
        } catch (BITablePathConfusionException e) {
            BILogger.getLogger().error(e.getMessage());
        } catch (BITableRelationConfusionException e) {
            BILogger.getLogger().error(e.getMessage());
        }
    }

    private void init() throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        setResourcesAndDepends();
        setRealationAndPath();
        calculateDepends();
    }

    private void calculateDepends() {
        BIPackageTableSourceConfigProvider biPackageFindTableSourceConfigManager = new BIPackageTableSourceConfigManager();
        Set<BIBusinessTable> analysisTables = biPackageFindTableSourceConfigManager.getTable4Generate(UserControl.getInstance().getSuperManagerID());
        CalculateDepend cal = new CalculateDependManager() {
            @Override
            public void setOriginal(Set<BIBusinessTable> analysisTables) {
                for (BIBusinessTable analysisTable : analysisTables) {
                    analysisTableSources.add(analysisTable.getTableSource());
                }
            }
        };
//        cal.setOriginal(analysisTables);
        cal.calRelations(getTableSourceRelationSet());
        cal.calRelationPath(this.getRelationPaths(), this.getTableSourceRelationSet());
    }

    protected void setResourcesAndDepends() throws BITableAbsentException {
        Set<CubeTableSource> cubeTableSourceHashSet = new HashSet<CubeTableSource>();
        for (BIBusinessTable biBusinessTable : newTables) {
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
            for (BITableRelation biTableRelation : tableRelationSet) {
                Set<BITableRelationPath> allTablePath = BICubeConfigureCenter.getTableRelationManager().getAllTablePath(biUser.getUserId());
                for (BITableRelationPath biTableRelationPath : allTablePath) {
                    if (biTableRelationPath.getAllRelations().contains(biTableRelation)) {
                        tableSourceRelationPaths.add(biTableRelationPath);
                    }
                }
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
        try {
            primaryTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(biTableRelation.getPrimaryField().getTableBelongTo());
            foreignTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(biTableRelation.getForeignField().getTableBelongTo());
            primaryTable = biTableRelation.getPrimaryTable().getTableSource();
            foreignTable = biTableRelation.getForeignTable().getTableSource();
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
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
        Set<BITableSourceRelation> biTableSourceRelationSet = new HashSet<BITableSourceRelation>();
        for (BITableRelation biTableRelation : this.tableRelationSet) {
            BITableSourceRelation biTableSourceRelation = convetTableRealtionToTableSourceRealtion(biTableRelation);
            biTableSourceRelationSet.add(biTableSourceRelation);
        }
        return biTableSourceRelationSet;
    }

    @Override
    public Set<CubeTableSource> getSources() {
        return null;
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
