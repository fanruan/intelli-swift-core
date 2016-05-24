package com.fr.bi.cal.generate.relation;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.base.TemplateUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.loader.CubeGeneratingTableIndexLoader;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIPackUtils;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.Inter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by GUY on 2015/3/24.
 */
public class RelationsGetter {

    protected BIUser biUser;

    public RelationsGetter(long userId) {
        biUser = new BIUser(userId);
    }

    public Set<BITableSourceRelation> getGenerateRelations() {
        Set<String> tableMD5s = createGenerateTables();
        return generateRelations(tableMD5s);
    }

    private Set<String> createGenerateTables() {
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getPacks();
        Set<BusinessTable> generateTable = BIPackUtils.getAllBusiTableKeys(packs);
        /**
         * TODO Connery：LoginInfo Mark
         */
//        BILoginUserInfo info = BICubeConfigureCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getUserInfo();
//        if (info != null && info.getTableKey() != null) {
//            generateTable.add(info.getTableKey());
//        }
        Set<String> genereteTableMD5s = new HashSet<String>();
        for (BusinessTable table : generateTable) {
            genereteTableMD5s.add(BIConfigureManagerCenter.getDataSourceManager().getCoreByTableID(table.getID(), biUser).getIDValue());
        }
        return genereteTableMD5s;
    }

    private Set<BITableSourceRelation> generateRelations(Set<String> tableMD5s) {
        Set<BITableSourceRelation> relations = new HashSet<BITableSourceRelation>();
        Set<BITableSourceRelation> connectionSet = BICubeConfigureCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getTableSourceRelationSet();
        Map<CubeTableSource, Set<BITableSourceRelation>> primKeyMap = BICubeConfigureCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getPrimaryKeyMap();
        Map<CubeTableSource, Set<BITableSourceRelation>> foreignKeyMap = BICubeConfigureCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getForeignKeyMap();
        ICubeDataLoader loader = CubeGeneratingTableIndexLoader.getInstance(biUser.getUserId());
        for (BITableSourceRelation tableSourceRelation : BICubeConfigureCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getTableSourceRelationSet()) {
            ICubeFieldSource primaryKey = tableSourceRelation.getPrimaryKey();
            ICubeFieldSource foreignKey = tableSourceRelation.getForeignKey();
            CubeTableSource primaryTableSource = tableSourceRelation.getPrimaryTable();
            CubeTableSource foreignTableSource = tableSourceRelation.getForeignTable();
            ICubeTableService primaryCubeTable = loader.getTableIndex(primaryKey.getTableBelongTo());
            ICubeTableService foreignCubeTable = loader.getTableIndex(foreignKey.getTableBelongTo());
            if (primaryCubeTable == null || foreignCubeTable == null) {
                continue;
            }
            if (primaryCubeTable.getColumnIndex(primaryKey.getFieldName()) == null || foreignCubeTable.getColumnIndex(foreignKey.getFieldName()) == null) {
                continue;
            }
            if (!tableMD5s.contains(primaryTableSource.fetchObjectCore().getIDValue()) || !tableMD5s.contains(foreignTableSource.fetchObjectCore().getIDValue())) {
                continue;
            }
            if (!findDistinctPIDToAdd(loader, relations, connectionSet, primKeyMap, foreignKeyMap, tableSourceRelation)) {
                String text = TemplateUtils.render(
                        Inter.getLocText("BI-A_And_B_All_Distinct"),
                        new String[]{"one", "other"},
                        new String[]{primaryKey.toString(), foreignKey.toString()});
                BILogger.getLogger().info(text);
            }
        }
        return relations;
    }

    private boolean findDistinctPIDToAdd(ICubeDataLoader loader,
                                         Set<BITableSourceRelation> relations,
                                         Set<BITableSourceRelation> connectionSet,
                                         Map<CubeTableSource, Set<BITableSourceRelation>> primKeyMap,
                                         Map<CubeTableSource, Set<BITableSourceRelation>> foreignKeyMap,
                                         BITableSourceRelation relation) {
        if (loader.getTableIndex(relation.getPrimaryField().getTableBelongTo()).isDistinct(relation.getPrimaryField().getFieldName())) {
            relations.add(relation);
            addKeyToGenerateRelationMap(relation, connectionSet, primKeyMap, foreignKeyMap);
            return true;
        }
        return false;
    }

    private void addKeyToGenerateRelationMap(BITableSourceRelation relation, Set<BITableSourceRelation> connectionSet, Map<CubeTableSource, Set<BITableSourceRelation>> primKeyMap, Map<CubeTableSource, Set<BITableSourceRelation>> foreignKeyMap) {
        connectionSet.add(relation);
        CubeTableSource primaryKeyTable = relation.getPrimaryKey().getTableBelongTo();
        CubeTableSource foreignKeyTable = relation.getForeignKey().getTableBelongTo();
        Set<BITableSourceRelation> childSet = primKeyMap.get(primaryKeyTable);
        if (childSet == null) {
            childSet = new HashSet<BITableSourceRelation>();
            primKeyMap.put(primaryKeyTable, childSet);
        }
        childSet.add(relation);
        Set<BITableSourceRelation> parentSet = foreignKeyMap.get(foreignKeyTable);
        if (parentSet == null) {
            parentSet = new HashSet<BITableSourceRelation>();
            foreignKeyMap.put(foreignKeyTable, parentSet);
        }
        parentSet.add(relation);
    }


}