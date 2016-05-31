package com.finebi.cube.conf.build;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.base.BIUser;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.Serializable;
import java.util.*;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class CubeBuildStuffManager implements Serializable, CubeBuildStuff {

    /**
     *
     */
    private static final long serialVersionUID = -2315016175890907748L;
    private Set<IBusinessPackageGetterService> packs;
    private Set<CubeTableSource> sources;
    private Set<CubeTableSource> allSingleSources;

    private String rootPath;
    private String buildTempPath;
    private Set<BITableSourceRelation> tableSourceRelationSet;
    private Set<BIBusinessTable> allBusinessTable = new HashSet<BIBusinessTable>();
    private Set<BITableRelation> tableRelationSet;
    private Map<CubeTableSource, Map<String, ICubeFieldSource>> tableDBFieldMaps = new HashMap<CubeTableSource, Map<String, ICubeFieldSource>>();
    private Map<CubeTableSource, Set<BITableSourceRelation>> primaryKeyMap;
    private Map<CubeTableSource, Set<BITableSourceRelation>> foreignKeyMap;
    private BIUser biUser;
    private Set<BITableSourceRelationPath> relationPaths;
    /**
     * TableSource之间存在依赖关系，这一点很合理。
     * 这个结构肯定是不好的。
     * 不合理的在于为何要把这个依赖关系用一个List(原来是个Map)，把间接依赖的统统获得。
     * 开发的时候封装一下即可，如果当时不封装，这个结构就镶嵌代码了，随着开发替换代价越高。
     */
    private Set<List<Set<CubeTableSource>>> dependTableResource;

    public CubeBuildStuffManager(BIUser biUser) {
        this.biUser = biUser;
        initialCubeStuff();
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

    public String getRootPath()
    {
        return rootPath;
    }
@Override
    public Set<BITableRelation> getTableRelationSet() {
        Set<BITableRelation> set = new HashSet<BITableRelation>();
        for (BITableRelation relation : tableRelationSet) {
            try {
                CubeTableSource primaryTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getPrimaryField().getTableBelongTo());
                CubeTableSource foreignTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getForeignField().getTableBelongTo());
                ICubeFieldSource primaryField = tableDBFieldMaps.get(primaryTable).get(relation.getPrimaryField().getFieldName());
                ICubeFieldSource foreignField = tableDBFieldMaps.get(foreignTable).get(relation.getForeignField().getFieldName());
                if (tableSourceRelationSet.contains(
                        new BITableSourceRelation(
                                primaryField,
                                foreignField,
                                primaryTable,
                                foreignTable
                        ))) {
                    set.add(relation);
                }
            } catch (BIKeyAbsentException e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        return set;
    }

    private Set<BITableRelation> filterRelation(Set<BITableRelation> tableRelationSet) {
        Iterator<BITableRelation> iterator = tableRelationSet.iterator();
        Set<BITableRelation> result = new HashSet<BITableRelation>();
        while (iterator.hasNext()) {
            BITableRelation tableRelation = iterator.next();
            if (isRelationValid(tableRelation)) {
                result.add(tableRelation);
            }
        }
        return tableRelationSet;
    }

    public void setTableRelationSet(Set<BITableRelation> tableRelationSet) {
        this.tableRelationSet = filterRelation(tableRelationSet);
        this.tableSourceRelationSet = convertRelations(this.tableRelationSet);
    }

    @Override
    public Set<BITableSourceRelationPath> getRelationPaths() {
        return relationPaths;
    }

    public void setRelationPaths(Set<BITableSourceRelationPath> relationPaths) {
        this.relationPaths = relationPaths;
    }

    private Set<BITableSourceRelation> convertRelations(Set<BITableRelation> connectionSet) {
        Set<BITableSourceRelation> set = new HashSet<BITableSourceRelation>();
        for (BITableRelation relation : connectionSet) {
            try {
                set.add(convert(relation));
            } catch (NullPointerException e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        return set;
    }

    private BITableSourceRelation convert(BITableRelation relation) {


        CubeTableSource primaryTable = null;
        CubeTableSource foreignTable = null;
        try {
            primaryTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getPrimaryField().getTableBelongTo());
            foreignTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getForeignField().getTableBelongTo());
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
        ICubeFieldSource primaryField = tableDBFieldMaps.get(primaryTable).get(relation.getPrimaryField().getFieldName());
        ICubeFieldSource foreignField = tableDBFieldMaps.get(foreignTable).get(relation.getForeignField().getFieldName());
        if (primaryField == null || foreignField == null) {
            throw new NullPointerException();
        }
        /**
         * 设置成业务包里面的Table
         * 原因：外部无法区分当前是getTableBelongTo获得的是什么Table。
         * 所有这里同一设置成为业务包表。DBField和BIField没有彻底分开。
         */
        primaryField.setTableBelongTo(primaryTable);
        foreignField.setTableBelongTo(foreignTable);
        return new BITableSourceRelation(
                primaryField,
                foreignField,
                primaryTable,
                foreignTable
        );
    }


    private Set<BITableSourceRelationPath> convertPaths(Set<BITableRelationPath> paths) {
        Set<BITableSourceRelationPath> set = new HashSet<BITableSourceRelationPath>();
        for (BITableRelationPath path : paths) {

            try {
                set.add(convert(path));
            } catch (Exception e) {
                continue;
            }
        }
        return set;
    }

    @Override
    public Set<CubeTableSource> getAllSingleSources() {
        return allSingleSources;
    }

    public void setAllSingleSources(Set<CubeTableSource> allSingleSources) {
        this.allSingleSources = allSingleSources;
    }

    private BITableSourceRelationPath convert(BITableRelationPath path) {
        BITableSourceRelationPath tableSourceRelationPath = new BITableSourceRelationPath();
        try {
            for (BITableRelation relation : path.getAllRelations()) {
                tableSourceRelationPath.addRelationAtTail(convert(relation));
            }
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.beyondControl(e);
        }
        return tableSourceRelationPath;
    }

    public Set<List<Set<CubeTableSource>>> getDependTableResource() {
        return dependTableResource;
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
        fullTableDBFields();
    }

    private void fullTableDBFields() {
        Iterator<CubeTableSource> tableSourceIterator = sources.iterator();
        while (tableSourceIterator.hasNext()) {
            CubeTableSource tableSource = tableSourceIterator.next();
            ICubeFieldSource[] BICubeFieldSources = tableSource.getFieldsArray(sources);
            Map<String, ICubeFieldSource> name2Field = new HashMap<String, ICubeFieldSource>();
            for (int i = 0; i < BICubeFieldSources.length; i++) {
                ICubeFieldSource field = BICubeFieldSources[i];
                name2Field.put(field.getFieldName(), field);
            }
            tableDBFieldMaps.put(tableSource, name2Field);
        }
    }

    private boolean isRelationValid(BITableRelation relation) {
        BusinessTable primaryTable = relation.getPrimaryTable();
        BusinessTable foreignTable = relation.getForeignTable();
        return allBusinessTable.contains(primaryTable) && allBusinessTable.contains(foreignTable);
    }


    /**
     * @return the tableSourceRelationSet
     */
    @Override
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return tableSourceRelationSet;
    }

    /**
     * @return the primaryKeyMap
     */
    public Map<CubeTableSource, Set<BITableSourceRelation>> getPrimaryKeyMap() {
        return primaryKeyMap;
    }

    /**
     * @param primaryKeyMap the primaryKeyMap to set
     */
    public void setPrimaryKeyMap(Map<CubeTableSource, Set<BITableSourceRelation>> primaryKeyMap) {
        this.primaryKeyMap = primaryKeyMap;
    }

    /**
     * @return the foreignKeyMap
     */
    public Map<CubeTableSource, Set<BITableSourceRelation>> getForeignKeyMap() {
        return foreignKeyMap;
    }

    /**
     * @param foreignKeyMap the foreignKeyMap to set
     */
    public void setForeignKeyMap(Map<CubeTableSource, Set<BITableSourceRelation>> foreignKeyMap) {
        this.foreignKeyMap = foreignKeyMap;
    }

    public void initialCubeStuff() {
        try {
            Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(biUser.getUserId());
            setPacks(packs, biUser.getUserId());
            Set<List<Set<CubeTableSource>>> depends = calculateTableSource(getSources());
            setDependTableResource(depends);
            setAllSingleSources(set2Set(depends));
            BICubeConfigureCenter.getPackageManager().startBuildingCube(biUser.getUserId());
            setTableRelationSet(BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(biUser.getUserId()));
            Map<CubeTableSource, Set<BITableSourceRelation>> primaryKeyMap = new HashMap<CubeTableSource, Set<BITableSourceRelation>>();
            setPrimaryKeyMap(primaryKeyMap);
            Map<CubeTableSource, Set<BITableSourceRelation>> foreignKeyMap = new HashMap<CubeTableSource, Set<BITableSourceRelation>>();
            setForeignKeyMap(foreignKeyMap);
            setRelationPaths(convertPaths(BICubeConfigureCenter.getTableRelationManager().getAllTablePath(biUser.getUserId())));
            rootPath = BIPathUtils.createBasePath();
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
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

    private List<Set<CubeTableSource>> map2List(Map<Integer, Set<CubeTableSource>> map) {
        List<Set<CubeTableSource>> tableList = new ArrayList<Set<CubeTableSource>>();

        for (int i = 0; i < map.size(); i++) {
            tableList.add(map.get(i));
        }
        return tableList;
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
