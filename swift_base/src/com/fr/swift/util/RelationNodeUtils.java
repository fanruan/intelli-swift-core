package com.fr.swift.util;

import com.fr.swift.config.IMetaData;
import com.fr.swift.config.IMetaDataColumn;
import com.fr.swift.config.conf.service.SwiftConfigService;
import com.fr.swift.config.conf.service.SwiftConfigServiceProvider;
import com.fr.swift.reliance.RelationNode;
import com.fr.swift.reliance.RelationPathNode;
import com.fr.swift.reliance.RelationPathReliance;
import com.fr.swift.reliance.RelationReliance;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.Source;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.relation.FieldRelationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationNodeUtils {
    public static void calculateRelationNode(RelationReliance relationReliance) {
        SwiftConfigService service = SwiftConfigServiceProvider.getInstance();
        Map<SourceKey, DataSource> dataSourceList = relationReliance.getAllDataSourceList();
        Map<SourceKey, RelationSource> relationSourceMap = relationReliance.getAllRelationSource();
        Iterator<Map.Entry<SourceKey, RelationSource>> iterator = relationSourceMap.entrySet().iterator();
        while (iterator.hasNext()) {
            RelationSource source = iterator.next().getValue();
            DataSource primary = dataSourceList.get(source.getPrimarySource());
            DataSource foreign = dataSourceList.get(source.getForeignSource());
            // 更新子表时如果包含关联则尝试更新关联
            if (null != foreign) {
                if (null != primary) {
                    relationReliance.addNode(new RelationNode(source, Arrays.<Source>asList(primary, foreign)));
                } else {
                    relationReliance.addNode(new RelationNode(source, Arrays.<Source>asList(foreign)));
                }
                IMetaData metaData = service.getMetaDataByKey(source.getPrimarySource().getId());
                List<IMetaDataColumn> columns = metaData.getFieldList();
                for (IMetaDataColumn column : columns) {
                    ColumnKey columnKey = new ColumnKey(column.getName());
                    columnKey.setRelation(source);
                    relationReliance.addNode(new RelationNode(new FieldRelationSource(columnKey), Arrays.<Source>asList(source)));
                }
            }
        }
    }

    public static void calculateRelationPathNode(RelationPathReliance relationReliance) {
        SwiftConfigService service = SwiftConfigServiceProvider.getInstance();
        Map<SourceKey, RelationNode> dataSourceList = relationReliance.getRelationNodeMap();
        Map<SourceKey, SourcePath> relationSourceMap = relationReliance.getAllRelationSource();
        Iterator<Map.Entry<SourceKey, SourcePath>> iterator = relationSourceMap.entrySet().iterator();
        List<RelationPathNode> nodes = new ArrayList<RelationPathNode>();
        while (iterator.hasNext()) {
            SourcePath source = iterator.next().getValue();
            nodes.addAll(calculateRelationNode(source, dataSourceList).values());
        }
        for (RelationPathNode node : nodes) {
            relationReliance.addNode(node);
            RelationSource path = node.getNode();
            IMetaData metaData = service.getMetaDataByKey(path.getPrimarySource().getId());
            List<IMetaDataColumn> columns = metaData.getFieldList();
            for (IMetaDataColumn column : columns) {
                ColumnKey columnKey = new ColumnKey(column.getName());
                columnKey.setRelation(path);
                relationReliance.addNode(new RelationPathNode(new FieldRelationSource(columnKey), Arrays.asList(path)));
            }
        }
    }

    private static Map<SourceKey, RelationPathNode> calculateRelationNode(SourcePath source, Map<SourceKey, RelationNode> dataSourceList) {
        Map<SourceKey, RelationPathNode> nodes = new HashMap<SourceKey, RelationPathNode>();
        List<RelationSource> relations = source.getRelations();
        RelationSource lastRelation = relations.get(relations.size() - 1);
        SourcePath pre = source.clone().removeLastRelation();
        if (dataSourceList.containsKey(lastRelation.getSourceKey())) {
            if (!nodes.containsKey(source.getSourceKey())) {
                if (pre.getRelations().size() > 1) {
                    nodes.put(source.getSourceKey(), new RelationPathNode(source, Arrays.asList(lastRelation, pre)));
                    // 递归去算防止传单个路径时无法计算子依赖
                    Map<SourceKey, RelationPathNode> map = calculateRelationNode(pre, dataSourceList);
                    Iterator<Map.Entry<SourceKey, RelationPathNode>> iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<SourceKey, RelationPathNode> entry = iterator.next();
                        if (!nodes.containsKey(entry.getKey())) {
                            nodes.put(entry.getValue().getKey(), entry.getValue());
                        }
                    }
                } else {
                    // 如果prepath只有一个关联，直接将该关联作为依赖
                    nodes.put(source.getSourceKey(), new RelationPathNode(source, Arrays.asList(lastRelation, pre.getRelations().get(0))));
                }
            }
        }
        return nodes;
    }
}
