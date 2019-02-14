package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.relation.RelationPathSourceImpl;
import com.fr.swift.source.relation.RelationSourceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationSourceFactory {

    public static RelationSource transformRelationSourcesFromPath(FineBusinessTableRelationPath path) {
        try {
            List<RelationSource> relationSources = transformRelationSources(path.getFineBusinessTableRelations());
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

    private static SourcePath transformSourcePath(FineBusinessTableRelationPath path) throws Exception {
        List<RelationSource> relationSources = transformRelationSources(path.getFineBusinessTableRelations());
        if (relationSources.isEmpty()) {
            return null;
        }
        return new RelationPathSourceImpl(relationSources);
    }

    public static List<RelationSource> transformRelationSources(List<FineBusinessTableRelation> relations) throws Exception {
        List<RelationSource> list = new ArrayList<RelationSource>();
        for (FineBusinessTableRelation relation : relations) {
            if (relation.getRelationType() == BICommonConstants.RELATION_TYPE.MANY_TO_ONE) {
                list.add(0, transformRelationSourcesFromRelation(relation));
            } else {
                list.add(transformRelationSourcesFromRelation(relation));
            }
        }
        return list;
    }

    public static List<SourcePath> transformSourcePaths(List<FineBusinessTableRelationPath> relations) throws Exception {
        List<SourcePath> list = new ArrayList<SourcePath>();
        for (FineBusinessTableRelationPath relation : relations) {
            list.add(transformSourcePath(relation));
        }
        return list;
    }

    public static RelationSource transformRelationSourcesFromRelation(FineBusinessTableRelation relation) throws Exception {
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
        SourceKey primary = DataSourceFactory.getDataSourceInCache(primaryTable).getSourceKey();
        SourceKey foreign = DataSourceFactory.getDataSourceInCache(foreignTable).getSourceKey();
        List<String> primaryKey = new ArrayList<String>();
        List<String> foreignKey = new ArrayList<String>();

        for (FineBusinessField field : primaryFields) {
            primaryKey.add(field.getName());
        }

        for (FineBusinessField field : foreignFields) {
            foreignKey.add(field.getName());
        }
        return new RelationSourceImpl(primary, foreign, primaryKey, foreignKey);
    }

}
