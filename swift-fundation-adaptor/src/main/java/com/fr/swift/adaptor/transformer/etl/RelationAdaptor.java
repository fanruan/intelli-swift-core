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
import com.fr.swift.adaptor.transformer.RelationSourceFactory;
import com.fr.swift.conf.dashboard.DashboardRelationPathService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.RelationSource;
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
class RelationAdaptor {
    static String getBaseTable(DashboardRelationPathService relationProvider, List<SelectFieldBeanItem> selectFieldBeanItemList) throws Exception {
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
        List<FineBusinessTableRelationPath> allPaths = relationProvider.getAllAnalysisRelationPaths();
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
            //todo 这里crash的话，就会影响更新..但是有的错的表其实是不是应该中断的，不crash的话，就会错误的走下去？
            throw new Exception("wrong relation, foreign table size is" + tables.size());
//            return Crasher.crash("wrong relation, foreign table size is" + tables.size());
        }
        return tables.iterator().next();
    }

    static RelationSource getRelation(List<SelectFieldPathItem> path, String baseTable, String table, DashboardRelationPathService relationProvider) {
        if (path != null && !path.isEmpty()) {
            List<FineBusinessTableRelation> targetRelations = new ArrayList<FineBusinessTableRelation>();
            try {
                for (SelectFieldPathItem item : path) {
                    handleSelectFieldPath(relationProvider, item, targetRelations);
                }
                return RelationSourceFactory.transformRelationSourcesFromPath(new FineBusinessTableRelationPathImp(targetRelations));
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
        List<FineBusinessTableRelationPath> relation = relationProvider.getRelationPathsByTables(table, baseTable);
        if (relation == null || relation.isEmpty()) {
            return Crasher.crash("invalid relation tables");
        }
        FineBusinessTableRelationPath p = relation.get(0);
        return RelationSourceFactory.transformRelationSourcesFromPath(p);
    }

    private static void handleSelectFieldPath(DashboardRelationPathService relationProvider, SelectFieldPathItem item, List<FineBusinessTableRelation> targetRelations) throws FineEngineException {
        RelationshipBean bean = item.getRelationship();
        List<String> from = bean.getFrom();
        List<String> to = bean.getTo();
        if (null == from || null == to) {
            return;
        }
        FineBusinessTable fromTable = BusinessTableUtils.getTableByFieldId(from.get(0));
        FineBusinessTable toTable = BusinessTableUtils.getTableByFieldId((to.get(0)));
        List<FineBusinessTableRelation> relations = relationProvider.getAnalysisRelationsByTables(fromTable.getName(), toTable.getName());
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