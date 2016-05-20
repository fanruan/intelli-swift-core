package com.fr.bi.conf.engine;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.manager.userInfo.BILoginUserInfo;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.BITableSourceRelationPath;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.Serializable;
import java.util.*;


public class CubeBuildStuffManager implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2315016175890907748L;
    private Set<BIBusinessPackage> packs;
    private Set<ICubeTableSource> sources;
    private Set<ICubeTableSource> allSingleSources;

    private BILoginUserInfo userInfo;
    private String rootPath;
    private String buildTempPath;
    private Set<BITableSourceRelation> tableSourceRelationSet;
    private Set<BIBusinessTable> allBusinessTable = new HashSet<BIBusinessTable>();
    private Set<BITableRelation> tableRelationSet;
    private Map<ICubeTableSource, Map<String, BICubeFieldSource>> tableDBFieldMaps = new HashMap<ICubeTableSource, Map<String, BICubeFieldSource>>();
    private Map<Table, Set<BITableSourceRelation>> primaryKeyMap;
    private Map<Table, Set<BITableSourceRelation>> foreignKeyMap;
    private BIUser biUser;
    private Set<BITableSourceRelationPath> relationPaths;
    /**
     * TableSource之间存在依赖关系，这一点很合理。
     * 这个结构肯定是不好的。
     * 不合理的在于为何要把这个依赖关系用一个List(原来是个Map)，把间接依赖的统统获得。
     * 开发的时候封装一下即可，如果当时不封装，这个结构就镶嵌代码了，随着开发替换代价越高。
     */
    private Set<List<Set<ICubeTableSource>>> dependTableResource;

    public CubeBuildStuffManager(BIUser biUser) {
        this.biUser = biUser;
    }

    /**
     * @return the packs
     */
    public Set<BIBusinessPackage> getPacks() {
        return packs;
    }

    /**
     * @return sources
     */
    public Set<ICubeTableSource> getSources() {
        return sources;
    }

    public String getRootPath() {
        return rootPath;
    }

    public Set<BITableRelation> getTableRelationSet() {
        Set<BITableRelation> set = new HashSet<BITableRelation>();
        for (BITableRelation relation : tableRelationSet) {
            BIField pField = relation.getPrimaryField();
            BIField fField = relation.getForeignField();
            if (tableSourceRelationSet.contains(
                    new BITableSourceRelation(
                            pField,
                            fField,
                            BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(pField.getTableID(), biUser),
                            BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(fField.getTableID(), biUser)
                    ))) {
                set.add(relation);
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


        ICubeTableSource primaryTable = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(relation.getPrimaryField().getTableID(), biUser);
        ICubeTableSource foreignTable = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(relation.getForeignField().getTableID(), biUser);
        BICubeFieldSource primaryField = tableDBFieldMaps.get(primaryTable).get(relation.getPrimaryField().getFieldName());
        BICubeFieldSource foreignField = tableDBFieldMaps.get(foreignTable).get(relation.getForeignField().getFieldName());
        if (primaryField == null || foreignField == null) {
            throw new NullPointerException();
        }
        /**
         * 设置成业务包里面的Table
         * 原因：外部无法区分当前是getTableBelongTo获得的是什么Table。
         * 所有这里同一设置成为业务包表。DBField和BIField没有彻底分开。
         */
        primaryField.setTableBelongTo(relation.getPrimaryField().getTableBelongTo());
        foreignField.setTableBelongTo(relation.getForeignField().getTableBelongTo());
        return new BITableSourceRelation(
                primaryField,
                foreignField,
                primaryTable,
                foreignTable
        );
    }


    public Set<BITableSourceRelationPath> convertPaths(Set<BITableRelationPath> paths) {
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

    public Set<ICubeTableSource> getAllSingleSources() {
        return allSingleSources;
    }

    public void setAllSingleSources(Set<ICubeTableSource> allSingleSources) {
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

    public Set<List<Set<ICubeTableSource>>> getDependTableResource() {
        return dependTableResource;
    }

    public void setDependTableResource(Set<List<Set<ICubeTableSource>>> dependTableResource) {
        this.dependTableResource = dependTableResource;
    }

    /**
     * @param packs the packs to set
     */
    public void setPacks(Set<BIBusinessPackage> packs, long userId) {
        this.packs = packs;
        this.sources = new HashSet<ICubeTableSource>();
        for (BIBusinessPackage pack : packs) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                BIBusinessTable table = tIt.next();
                allBusinessTable.add(table);
                sources.add(table.getSource());
            }
        }
        BILoginUserInfo info = BIConfigureManagerCenter.getUserLoginInformationManager().getUserInfoManager(userId).getCurrentUserInfo();
        if (info.getTableKey() != null) {
            ICubeTableSource source = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByCore(info.fetchObjectCore(), new BIUser(userId));
            if (source != null) {
                sources.add(source);
            }
        }
        fullTableDBFields();
    }

    private void fullTableDBFields() {
        Iterator<ICubeTableSource> tableSourceIterator = sources.iterator();
        while (tableSourceIterator.hasNext()) {
            ICubeTableSource tableSource = tableSourceIterator.next();
            BICubeFieldSource[] BICubeFieldSources = tableSource.getFieldsArray(sources);
            Map<String, BICubeFieldSource> name2Field = new HashMap<String, BICubeFieldSource>();
            for (int i = 0; i < BICubeFieldSources.length; i++) {
                BICubeFieldSource field = BICubeFieldSources[i];
                name2Field.put(field.getFieldName(), field);
            }
            tableDBFieldMaps.put(tableSource, name2Field);
        }
    }

    private boolean isRelationValid(BITableRelation relation) {
        Table primaryTable = relation.getPrimaryTable();
        Table foreignTable = relation.getForeignTable();
        return allBusinessTable.contains(primaryTable) && allBusinessTable.contains(foreignTable);
    }

    /**
     * @return the currentUserInfo
     */
    public BILoginUserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * @param currentUserInfo the currentUserInfo to set
     */
    public void setUserInfo(BILoginUserInfo currentUserInfo) {
        this.userInfo = currentUserInfo;
    }

    /**
     * @return the tableSourceRelationSet
     */
    public Set<BITableSourceRelation> getTableSourceRelationSet() {
        return tableSourceRelationSet;
    }

    /**
     * @return the primaryKeyMap
     */
    public Map<Table, Set<BITableSourceRelation>> getPrimaryKeyMap() {
        return primaryKeyMap;
    }

    /**
     * @param primaryKeyMap the primaryKeyMap to set
     */
    public void setPrimaryKeyMap(Map<Table, Set<BITableSourceRelation>> primaryKeyMap) {
        this.primaryKeyMap = primaryKeyMap;
    }

    /**
     * @return the foreignKeyMap
     */
    public Map<Table, Set<BITableSourceRelation>> getForeignKeyMap() {
        return foreignKeyMap;
    }

    /**
     * @param foreignKeyMap the foreignKeyMap to set
     */
    public void setForeignKeyMap(Map<Table, Set<BITableSourceRelation>> foreignKeyMap) {
        this.foreignKeyMap = foreignKeyMap;
    }

    public void initialCubeStuff() {
        try {
            Set<BIBusinessPackage> packs = BIConfigureManagerCenter.getPackageManager().getAllPackages(biUser.getUserId());
            setPacks(packs, biUser.getUserId());
            Set<List<Set<ICubeTableSource>>> depends = calculateTableSource(getSources());
            setDependTableResource(depends);
            setAllSingleSources(set2Set(depends));
            BIConfigureManagerCenter.getPackageManager().startBuildingCube(biUser.getUserId());
            BILoginUserInfo info = BIConfigureManagerCenter.getUserLoginInformationManager().getUserInfoManager(biUser.getUserId()).getCurrentUserInfo();
            setUserInfo(info);

            setTableRelationSet(BIConfigureManagerCenter.getTableRelationManager().getAllTableRelation(biUser.getUserId()));
            Map<Table, Set<BITableSourceRelation>> primaryKeyMap = new HashMap<Table, Set<BITableSourceRelation>>();
            setPrimaryKeyMap(primaryKeyMap);
            Map<Table, Set<BITableSourceRelation>> foreignKeyMap = new HashMap<Table, Set<BITableSourceRelation>>();
            setForeignKeyMap(foreignKeyMap);
            setRelationPaths(convertPaths(BIConfigureManagerCenter.getTableRelationManager().getAllTablePath(biUser.getUserId())));

            rootPath = BIPathUtils.createBasePath();
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private Set<List<Set<ICubeTableSource>>> calculateTableSource(Set<ICubeTableSource> tableSources) {
        Iterator<ICubeTableSource> it = tableSources.iterator();
        Set<List<Set<ICubeTableSource>>> depends = new HashSet<List<Set<ICubeTableSource>>>();
        while (it.hasNext()) {
            ICubeTableSource tableSource = it.next();
            depends.add(tableSource.createGenerateTablesList());
        }
        return depends;
    }

    private List<Set<ICubeTableSource>> map2List(Map<Integer, Set<ICubeTableSource>> map) {
        List<Set<ICubeTableSource>> tableList = new ArrayList<Set<ICubeTableSource>>();

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
    public static Set<ICubeTableSource> set2Set(Set<List<Set<ICubeTableSource>>> set) {
        Set<ICubeTableSource> result = new HashSet<ICubeTableSource>();
        Iterator<List<Set<ICubeTableSource>>> outIterator = set.iterator();
        while (outIterator.hasNext()) {
            Iterator<Set<ICubeTableSource>> middleIterator = outIterator.next().iterator();
            while (middleIterator.hasNext()) {
                result.addAll(middleIterator.next());
            }
        }
        return result;
    }
}
