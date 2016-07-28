package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.AbstractCubeBuild;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BITableRelationConfigurationProvider;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.*;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by kary on 16/5/30.
 */
public class CubeBuildSingleTable extends AbstractCubeBuild implements CubeBuild {

    private Set<IBusinessPackageGetterService> packs;
    private Set<CubeTableSource> sources;
    private Set<CubeTableSource> allSingleSources;
    private Set<BIBusinessTable> allBusinessTable = new HashSet<BIBusinessTable>();
    private BIUser biUser;
    private Set<List<Set<CubeTableSource>>> dependTableResource;
    private Set<BITableSourceRelation> biTableSourceRelationSet = new HashSet<BITableSourceRelation>();
    private Set<BITableSourceRelationPath> biTableSourceRelationPathSet = new HashSet<BITableSourceRelationPath>();
    private Set<BICubeGenerateRelationPath> cubeGenerateRelationPathSet = new HashSet<BICubeGenerateRelationPath>();
    private Set<BICubeGenerateRelation> cubeGenerateRelationSet = new HashSet<BICubeGenerateRelation>();


    public CubeBuildSingleTable(BusinessTable businessTable, long userId) {
        super(userId);
        this.biUser = new BIUser(userId);
        init(businessTable);
    }

    public void init(BusinessTable businessTable) {

        try {
            setAllSources(businessTable);
            Set<List<Set<CubeTableSource>>> depends = calculateTableSource(getSources());
            setDependTableResource(depends);
            setAllSingleSources(set2Set(depends));
            calculateRelationsAndPaths(businessTable);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private void calculateRelationsAndPaths(BusinessTable businessTable) {
        //获取生成过的关联，没生成的不管
        Set<BITableRelation> generatedRelations = getGeneratedRelations();
        Set<BITableRelationPath> generatedPaths = getGeneratedPaths(generatedRelations);
        //遍历所有路径，能链到该表的关联（路径）都要被更新
        Set<BITableRelation> inUseRelations = new HashSet<BITableRelation>();
        Set<BITableRelationPath> inUsePaths = new HashSet<BITableRelationPath>();
        for (BITableRelation tableRelation : generatedRelations) {
            if (tableRelation.getPrimaryTable().getID().getIdentity().equals(businessTable.getID().getIdentity()) || tableRelation.getForeignTable().getID().getIdentity().equals(businessTable.getID().getIdentity())) {
                inUseRelations.add(tableRelation);
            }
        }
        for (BITableRelationPath path : generatedPaths) {
            for (BITableRelation tableRelation : path.getAllRelations()) {
                if (inUseRelations.contains(tableRelation)) {
                    break;
                }
            }
            inUsePaths.add(path);
            for (BITableRelation biTableRelation : path.getAllRelations()) {
                inUseRelations.add(biTableRelation);
            }
        }
        //设置路径（关联）的依赖关系
        setCubeGenerateRelationSet(inUseRelations, businessTable);
        setCubeGenerateRelationPathSet(inUsePaths);
    }

    public void setCubeGenerateRelationSet(Set<BITableRelation> inUseRelations, BusinessTable businessTable) {
        for (BITableRelation tableRelation : inUseRelations) {
            if (isRelationValid(tableRelation)) {
                BITableSourceRelation convertRelation = convertRelation(tableRelation);
                BICubeGenerateRelation generateRelation;
                if (null != convertRelation) {
                    this.biTableSourceRelationSet.add(convertRelation);
                    if (tableRelation.getPrimaryTable().getID().getIdentity().equals(businessTable.getID().getIdentity()) || tableRelation.getForeignTable().getID().getIdentity().equals(businessTable.getID().getIdentity())) {
                        Set<CubeTableSource> dependTableSourceSet = new HashSet<CubeTableSource>();
                        dependTableSourceSet.add(businessTable.getTableSource());
                        generateRelation = new BICubeGenerateRelation(convertRelation, dependTableSourceSet);
                    } else {
                        generateRelation = new BICubeGenerateRelation(convertRelation);
                    }
                    this.cubeGenerateRelationSet.add(generateRelation);
                }
            }
        }

    }

    public void setCubeGenerateRelationPathSet(Set<BITableRelationPath> inUsePaths) {
        for (BITableRelationPath path : inUsePaths) {
            try {
                this.biTableSourceRelationPathSet.add(convertPath(path));
                this.cubeGenerateRelationPathSet.add(new BICubeGenerateRelationPath(convertPath(path)));
            } catch (BITablePathConfusionException e) {
                BILogger.getLogger().error(e.getMessage());
            }
        }

    }

    private Set<BITableRelationPath> getGeneratedPaths(Set<BITableRelation> generatedRelations) {
        Set<BITableRelationPath> generatedRelationPaths = new HashSet<BITableRelationPath>();
        for (BITableRelationPath tableRelationPath : allRelationPathSet) {
            boolean flag = true;
            if (tableRelationPath.size() < 2) {
                flag = false;
            }
            for (BITableRelation tableRelation : tableRelationPath.getAllRelations()) {
                if (!generatedRelations.contains(tableRelation)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                generatedRelationPaths.add(tableRelationPath);
            }
        }
        return generatedRelationPaths;
    }

    private Set<BITableRelation> getGeneratedRelations() {
        BITableRelationConfigurationProvider relationManager = BICubeConfigureCenter.getTableRelationManager();
        Set<BITableRelation> generatedRelations = new HashSet<BITableRelation>();
        for (BITableRelation relation : relationManager.getAllTableRelation(biUser.getUserId())) {
            try {
                if (relationManager.isRelationGenerated(biUser.getUserId(), relation)) {
                    generatedRelations.add(relation);
                }
            } catch (BITableAbsentException e) {
                BILogger.getLogger().error(e.getMessage());
            } catch (BIRelationAbsentException e) {
                BILogger.getLogger().error(e.getMessage());
            }
        }
        return generatedRelations;
    }

    private void setAllSources(BusinessTable businessTable) {
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(biUser.getUserId());
        this.packs = packs;
        this.sources = new HashSet<CubeTableSource>();
        allBusinessTable = new HashSet<BIBusinessTable>();
        for (IBusinessPackageGetterService pack : packs) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                BIBusinessTable table = tIt.next();
                if (ComparatorUtils.equals(table.getID(), businessTable.getID())) {
                    allBusinessTable.add(table);
                    sources.add(table.getTableSource());
                }
            }
        }
    }

    /**
     * @return the packs
     */
    public Set<IBusinessPackageGetterService> getPacks() {
        return packs;
    }

    /**
     * @return sources
     */
    public Set<CubeTableSource> getSources() {
        return sources;
    }


    public Set<BITableSourceRelationPath> getBiTableSourceRelationPathSet() {
        return new HashSet<BITableSourceRelationPath>();
    }


    @Override
    public Set<CubeTableSource> getAllSingleSources() {
        return allSingleSources;
    }

    public void setAllSingleSources(Set<CubeTableSource> allSingleSources) {
        this.allSingleSources = allSingleSources;
    }


    public Set<List<Set<CubeTableSource>>> getDependTableResource() {
        return dependTableResource;
    }

    @Override
    public Set<BITableRelation> getTableRelationSet() {
        return new HashSet<BITableRelation>();
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
    public boolean isSingleTable() {
        return true;
    }

    public void setDependTableResource(Set<List<Set<CubeTableSource>>> dependTableResource) {
        this.dependTableResource = dependTableResource;
    }


    /**
     * @return the tableSourceRelationSet
     */
    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return new HashSet<BITableSourceRelation>();
    }


    /**
     * TODO改变层级结构
     *
     * @param set
     * @return
     */
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

}
