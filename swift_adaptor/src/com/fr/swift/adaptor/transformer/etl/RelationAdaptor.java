package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.analysis.bean.operator.select.RelationshipBean;
import com.finebi.conf.internalimp.analysis.bean.operator.select.SelectFieldBeanItem;
import com.finebi.conf.internalimp.analysis.bean.operator.select.SelectFieldPathItem;
import com.finebi.conf.internalimp.path.FineBusinessTableRelationPathImp;
import com.finebi.conf.provider.SwiftRelationPathConfProvider;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.relation.RelationPathSourceImpl;
import com.fr.swift.source.relation.RelationSourceImpl;
import com.fr.swift.util.Crasher;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class RelationAdaptor {
    public static String getBaseTable(SwiftRelationPathConfProvider relationProvider, List<SelectFieldBeanItem> selectFieldBeanItemList) {
        Set<String> tables = new HashSet<String>();
        for (SelectFieldBeanItem selectFieldBeanItem : selectFieldBeanItemList) {
            tables.add(selectFieldBeanItem.getTableName());
            //设置了关联与子表的都直接返回
            List<String> baseTables = selectFieldBeanItem.getCommonTable();
            if (baseTables != null && !baseTables.isEmpty()) {
                return baseTables.get(0);
            }
            List<SelectFieldPathItem> path = selectFieldBeanItem.getPath();
            if (path != null) {
                return path.get(path.size() - 1).getTable();
            }
        }
        List<FineBusinessTableRelationPath> allPaths = relationProvider.getAllRelationPaths();
        for (FineBusinessTableRelationPath path : allPaths) {
            if (tables.size() == 1) {
                break;
            }
            List<FineBusinessTableRelation> relations = path.getFineBusinessTableRelations();
            FineBusinessTableRelation firstRelation = relations.get(0);
            FineBusinessTableRelation lastRelation = relations.get(relations.size() - 1);
            String prim;
            if (firstRelation.getRelationType() == BICommonConstants.RELATION_TYPE.MANY_TO_ONE) {
                prim = firstRelation.getForeignBusinessTable().getId();
            } else {
                prim = firstRelation.getPrimaryBusinessTable().getId();
            }
            String foreign;
            if (lastRelation.getRelationType() == BICommonConstants.RELATION_TYPE.MANY_TO_ONE) {
                foreign = lastRelation.getPrimaryBusinessTable().getId();
            } else {
                foreign = lastRelation.getForeignBusinessTable().getId();
            }
            if (tables.contains(prim) && tables.contains(foreign)) {
                tables.remove(prim);
            }
        }
        if (tables.size() != 1) {
            return Crasher.crash("wrong relation, foreign table size is" + tables.size());
        }
        return tables.iterator().next();
    }

    public static RelationSource getRelation(List<SelectFieldPathItem> path, String baseTable, String table, SwiftRelationPathConfProvider relationProvider) {
        if (path != null) {
            List<FineBusinessTableRelation> targetRelations = new ArrayList<FineBusinessTableRelation>();
            try {
                for (SelectFieldPathItem item : path) {
                    handleSelectFieldPath(relationProvider, item, targetRelations);
                }
                return getRelation(new FineBusinessTableRelationPathImp(targetRelations));
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("Cannot find relation, use default. ", e);
            }
        }
        List<FineBusinessTableRelationPath> relation = relationProvider.getRelationPaths(table, baseTable);
        if (relation == null || relation.isEmpty()) {
            return Crasher.crash("invalid relation tables");
        }
        FineBusinessTableRelationPath p = relation.get(0);
        return getRelation(p);
    }

    private static RelationSource getRelation(FineBusinessTableRelationPath path) {
        try {
            List<RelationSource> relationSources = pathConvert2RelationSource(path);
            if (relationSources.isEmpty()) {
                return null;
            }
            if (1 == relationSources.size()) {
                return relationSources.get(0);
            }
            return new RelationPathSourceImpl(relationSources);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<RelationSource> pathConvert2RelationSource(FineBusinessTableRelationPath path) throws Exception {
        List<FineBusinessTableRelation> relations = path.getFineBusinessTableRelations();
        List<RelationSource> result = new ArrayList<RelationSource>();
        for (FineBusinessTableRelation relation : relations) {
            FineBusinessTable primaryTable;
            FineBusinessTable foreignTable;
            List<FineBusinessField> primaryFields;
            List<FineBusinessField> foreignFields;
            if (relation.getRelationType() == BICommonConstants.RELATION_TYPE.MANY_TO_ONE) {
                primaryTable = relation.getForeignBusinessTable();
                foreignTable = relation.getPrimaryBusinessTable();
                primaryFields = relation.getForeignBusinessField();
                foreignFields = relation.getPrimaryBusinessField();
            } else {
                primaryTable = relation.getPrimaryBusinessTable();
                foreignTable = relation.getForeignBusinessTable();
                primaryFields = relation.getPrimaryBusinessField();
                foreignFields = relation.getForeignBusinessField();
            }
            SourceKey primary = DataSourceFactory.getDataSource(primaryTable).getSourceKey();
            SourceKey foreign = DataSourceFactory.getDataSource(foreignTable).getSourceKey();
            List<String> primaryKey = new ArrayList<String>();
            List<String> foreignKey = new ArrayList<String>();

            for (FineBusinessField field : primaryFields) {
                primaryKey.add(field.getName());
            }

            for (FineBusinessField field : foreignFields) {
                foreignKey.add(field.getName());
            }
            result.add(new RelationSourceImpl(primary, foreign, primaryKey, foreignKey));
        }
        return result;
    }

    private static void handleSelectFieldPath(SwiftRelationPathConfProvider relationProvider, SelectFieldPathItem item, List<FineBusinessTableRelation> targetRelations) throws FineEngineException {
        RelationshipBean bean = item.getRelationship();
        List<String> from = bean.getFrom();
        List<String> to = bean.getTo();
        FineBusinessTable fromTable = BusinessTableUtils.getTableByFieldId(from.get(0));
        FineBusinessTable toTable = BusinessTableUtils.getTableByFieldId((to.get(0)));
        List<FineBusinessTableRelation> relations = relationProvider.getRelationsByTables(fromTable.getName(), toTable.getName());
        if (!relations.isEmpty()) {
            int lastSize = targetRelations.size();
            for (FineBusinessTableRelation relation : relations) {
                List<FineBusinessField> primaryFields;
                List<FineBusinessField> foreignFields;
                if (relation.getRelationType() == BICommonConstants.RELATION_TYPE.MANY_TO_ONE) {
                    primaryFields = relation.getForeignBusinessField();
                    foreignFields = relation.getPrimaryBusinessField();
                } else {
                    primaryFields = relation.getPrimaryBusinessField();
                    foreignFields = relation.getForeignBusinessField();
                }
                if (from.size() != primaryFields.size() || to.size() != foreignFields.size()) {
                    continue;
                }
                boolean equals = true;
                for (int i = 0; i < from.size(); i++) {
                    // 按照常理主表字段和子表字段数是一样的
                    if (!ComparatorUtils.equals(from.get(i), primaryFields.get(i).getId()) || !ComparatorUtils.equals(to.get(i), foreignFields.get(i).getId())) {
                        equals = false;
                        break;
                    }
                }
                if (equals) {
                    targetRelations.add(relation);
                    break;
                }
            }
            if (lastSize == targetRelations.size()) {
                selectFieldPathCrash(from, to);
            }
        } else {
            selectFieldPathCrash(from, to);
        }
    }

    private static void selectFieldPathCrash(List<String> from, List<String> to) {
        StringBuilder builder = new StringBuilder("Cannot find relation: ").append("{");
        StringBuilder foreign = new StringBuilder();
        for (int i = 0; i < from.size(); i++) {
            builder.append(from.get(i)).append(",");
            foreign.append(to.get(i)).append(",");
        }
        builder.setLength(builder.length() - 1);
        foreign.setLength(foreign.length() - 1);
        builder.append("}->").append(foreign).append("}");
        Crasher.crash(builder.toString());
    }
}