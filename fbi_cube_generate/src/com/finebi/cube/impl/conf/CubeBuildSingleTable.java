package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.*;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by wuk on 16/5/30.
 */
public class CubeBuildSingleTable implements CubeBuild {

    private Set<IBusinessPackageGetterService> packs;
    private Set<CubeTableSource> sources;
    private Set<CubeTableSource> allSingleSources;
    private ICubeConfiguration cubeConfiguration;
    private Set<BIBusinessTable> allBusinessTable = new HashSet<BIBusinessTable>();
    private BIUser biUser;

    public CubeBuildSingleTable(BusinessTable businessTable, ICubeConfiguration cubeConfiguration, long userId) {
        this.biUser = new BIUser(userId);
        this.cubeConfiguration = cubeConfiguration;
        init(businessTable);
    }

    public CubeBuildSingleTable(BusinessTable businessTable, long userId) {
        this.biUser = new BIUser(userId);
        this.cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
        init(businessTable);
    }

    public void init(BusinessTable businessTable) {

        try {
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
            Set<List<Set<CubeTableSource>>> depends = calculateTableSource(getSources());
            setDependTableResource(depends);
            setAllSingleSources(set2Set(depends));

        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
    /**
     *
     */

    /**
     * TableSource之间存在依赖关系，这一点很合理。
     * 这个结构肯定是不好的。
     * 不合理的在于为何要把这个依赖关系用一个List(原来是个Map)，把间接依赖的统统获得。
     * 开发的时候封装一下即可，如果当时不封装，这个结构就镶嵌代码了，随着开发替换代价越高。
     */
    private Set<List<Set<CubeTableSource>>> dependTableResource;


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

    public void setDependTableResource(Set<List<Set<CubeTableSource>>> dependTableResource) {
        this.dependTableResource = dependTableResource;
    }

    /**
     * @param packs the packs to set
     */
    public void setPacks(Set<IBusinessPackageGetterService> packs, long userId) {
        this.packs = packs;
        this.sources = new HashSet<CubeTableSource>();
        allBusinessTable = new HashSet<BIBusinessTable>();
        for (IBusinessPackageGetterService pack : packs) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                BIBusinessTable table = tIt.next();
                allBusinessTable.add(table);
                sources.add(table.getTableSource());
            }
        }
    }


    /**
     * @return the tableSourceRelationSet
     */
    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return new HashSet<BITableSourceRelation>();
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
