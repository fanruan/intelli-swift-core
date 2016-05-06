package com.fr.bi.conf.engine;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.manager.userInfo.BILoginUserInfo;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.BITableSourceRelationPath;
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
    private Set<ITableSource> sources;
    private Set<ITableSource> allSingleSources;

    private BILoginUserInfo userInfo;
    private String rootPath;
    private String buildTempPath;
    private Set<BITableSourceRelation> tableSourceRelationSet;

    private Set<BITableRelation> TableRelationSet;
    private Map<ITableSource, Map<String, DBField>> tableDBFieldMaps = new HashMap<ITableSource, Map<String, DBField>>();
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
    private Set<List<Set<ITableSource>>> dependTableResource;

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
    public Set<ITableSource> getSources() {
        return sources;
    }

    public String getRootPath() {
        return rootPath;
    }

    public Set<BITableRelation> getTableRelationSet() {
        Set<BITableRelation> set = new HashSet<BITableRelation>();
        for (BITableRelation relation : TableRelationSet) {
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

    public void setTableRelationSet(Set<BITableRelation> tableRelationSet) {
        this.TableRelationSet = tableRelationSet;
        this.tableSourceRelationSet = convertRelations(tableRelationSet);
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
            set.add(convert(relation));
        }
        return set;
    }

    private BITableSourceRelation convert(BITableRelation relation) {


        ITableSource primaryTable = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(relation.getPrimaryField().getTableID(), biUser);
        ITableSource foreignTable = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(relation.getForeignField().getTableID(), biUser);
        DBField primaryField = tableDBFieldMaps.get(primaryTable).get(relation.getPrimaryField().getFieldName());
        DBField foreignField = tableDBFieldMaps.get(foreignTable).get(relation.getForeignField().getFieldName());
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


    private Set<BITableSourceRelationPath> convertPaths(Set<BITableRelationPath> paths) {
        Set<BITableSourceRelationPath> set = new HashSet<BITableSourceRelationPath>();
        for (BITableRelationPath path : paths) {

            set.add(convert(path));
        }
        return set;
    }

    public Set<ITableSource> getAllSingleSources() {
        return allSingleSources;
    }

    public void setAllSingleSources(Set<ITableSource> allSingleSources) {
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

    public Set<List<Set<ITableSource>>> getDependTableResource() {
        return dependTableResource;
    }

    public void setDependTableResource(Set<List<Set<ITableSource>>> dependTableResource) {
        this.dependTableResource = dependTableResource;
    }

    /**
     * @param packs the packs to set
     */
    public void setPacks(Set<BIBusinessPackage> packs, long userId) {
        this.packs = packs;
        this.sources = new HashSet<ITableSource>();
        for (BIBusinessPackage pack : packs) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                sources.add(tIt.next().getSource());
            }
        }
        BILoginUserInfo info = BIConfigureManagerCenter.getUserLoginInformationManager().getUserInfoManager(userId).getCurrentUserInfo();
        if (info.getTableKey() != null) {
            ITableSource source = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByCore(info.fetchObjectCore(), new BIUser(userId));
            if (source != null) {
                sources.add(source);
            }
        }
        fullTableDBFields();
    }

    private void fullTableDBFields() {
        Iterator<ITableSource> tableSourceIterator = sources.iterator();
        while (tableSourceIterator.hasNext()) {
            ITableSource tableSource = tableSourceIterator.next();
            DBField[] dbFields = tableSource.getFieldsArray(sources);
            Map<String, DBField> name2Field = new HashMap<String, DBField>();
            for (int i = 0; i < dbFields.length; i++) {
                DBField field = dbFields[i];
                name2Field.put(field.getFieldName(), field);
            }
            tableDBFieldMaps.put(tableSource, name2Field);
        }
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
            Set<List<Set<ITableSource>>> depends = calculateTableSource(getSources());
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

    private Set<List<Set<ITableSource>>> calculateTableSource(Set<ITableSource> tableSources) {
        Iterator<ITableSource> it = tableSources.iterator();
        Set<List<Set<ITableSource>>> depends = new HashSet<List<Set<ITableSource>>>();
        while (it.hasNext()) {
            ITableSource tableSource = it.next();
            depends.add(map2List(tableSource.createGenerateTablesMap()));
        }
        return depends;
    }

    private List<Set<ITableSource>> map2List(Map<Integer, Set<ITableSource>> map) {
        List<Set<ITableSource>> tableList = new ArrayList<Set<ITableSource>>();

        for (int i = 0; i < map.size(); i++) {
            tableList.add(map.get(i));
        }
        return tableList;
    }

    /**
     * TODO改变层级结构
     *
     * @param map
     * @return
     */
    public static Set<ITableSource> set2Set(Set<List<Set<ITableSource>>> set) {
        Set<ITableSource> result = new HashSet<ITableSource>();
        Iterator<List<Set<ITableSource>>> outIterator = set.iterator();
        while (outIterator.hasNext()) {
            Iterator<Set<ITableSource>> middleIterator = outIterator.next().iterator();
            while (middleIterator.hasNext()) {
                result.addAll(middleIterator.next());
            }
        }
        return result;
    }
}
