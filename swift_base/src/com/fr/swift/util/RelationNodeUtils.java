package com.fr.swift.util;

import com.fr.swift.reliance.RelationNode;
import com.fr.swift.reliance.RelationPathNode;
import com.fr.swift.reliance.RelationPathReliance;
import com.fr.swift.reliance.RelationReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationNodeUtils {
    public static void calculateRelationNode(RelationReliance relationReliance) {
        Map<SourceKey, DataSource> dataSourceList = relationReliance.getAllDataSourceList();
        Map<SourceKey, RelationSource> relationSourceMap = relationReliance.getAllRelationSource();
        Iterator<Map.Entry<SourceKey, RelationSource>> iterator = relationSourceMap.entrySet().iterator();
        while (iterator.hasNext()) {
            RelationSource source = iterator.next().getValue();
            DataSource primary = dataSourceList.get(source.getPrimarySource());
            DataSource foreign = dataSourceList.get(source.getForeignSource());
            if (null != primary && null != foreign) {
                relationReliance.addNode(new RelationNode(source, Arrays.asList(primary, foreign)));
            }
        }
    }

    public static void calculateRelationPathNode(RelationPathReliance relationReliance) {
        Map<SourceKey, DataSource> dataSourceList = relationReliance.getAllDataSourceList();
        Map<SourceKey, SourcePath> relationSourceMap = relationReliance.getAllRelationSource();
        Iterator<Map.Entry<SourceKey, SourcePath>> iterator = relationSourceMap.entrySet().iterator();
        List<RelationPathNode> nodes = new ArrayList<RelationPathNode>();
        while (iterator.hasNext()) {
            SourcePath source = iterator.next().getValue();
            nodes.addAll(calculateRelationNode(source, dataSourceList));
        }
        for (RelationPathNode node : nodes) {
            relationReliance.addNode(node);
        }
    }

    private static List<RelationPathNode> calculateRelationNode(SourcePath source, Map<SourceKey, DataSource> dataSourceList) {
        List<RelationPathNode> nodes = new ArrayList<RelationPathNode>();
        DataSource primary = dataSourceList.get(source.getPrimarySource());
        DataSource foreign = dataSourceList.get(source.getForeignSource());
        if (null != primary && null != foreign) {
            List<RelationSource> relations = source.getRelations();
            RelationSource lastRelation = relations.get(relations.size() - 1);
            SourcePath pre = source.clone().removeLastRelation();
            if (dataSourceList.containsKey(lastRelation.getPrimarySource())) {
                if (pre.getRelations().size() > 1) {
                    nodes.add(new RelationPathNode(source, Arrays.asList(lastRelation, pre)));
                } else {
                    nodes.add(new RelationPathNode(source, Arrays.asList(lastRelation, pre.getRelations().get(0))));
                }
            }
        }
        return nodes;
    }
}
