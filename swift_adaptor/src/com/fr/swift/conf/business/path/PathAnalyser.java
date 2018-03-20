package com.fr.swift.conf.business.path;

import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.swift.conf.business.relation.SwiftRelationDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/3/19
 */
public class PathAnalyser {
    private SwiftRelationDao relationDao;
    private ConcurrentHashMap<String, PathAnalyserNode> tableAnalyserMap = new ConcurrentHashMap<String, PathAnalyserNode>();

    public PathAnalyser(SwiftRelationDao relationDao) {
        this.relationDao = relationDao;
    }

    public boolean registRelation(List<FineBusinessTableRelation> relations) {
        boolean registed = false;
        for (FineBusinessTableRelation relation : relations) {
            FineBusinessTable primary = relation.getPrimaryBusinessTable();
            FineBusinessTable foreign = relation.getForeignBusinessTable();
            PathAnalyserNode primaryNode = getPathAnalyserNode(primary.getName());
            PathAnalyserNode foreignNode = getPathAnalyserNode(foreign.getName());

            if (!primaryNode.containChild(foreignNode)) {
                primaryNode.addChildNode(foreignNode);
                registed = true;
            }
        }
        return registed;
    }

    public List<FineBusinessTableRelationPath> getAllPaths() {
        Map<String, PathAnalyserNode> pathAnalyserNodeMap = new HashMap<String, PathAnalyserNode>(tableAnalyserMap);
        Iterator<String> primary = pathAnalyserNodeMap.keySet().iterator();
        List<FineBusinessTableRelationPath> result = new ArrayList<FineBusinessTableRelationPath>();
        while (primary.hasNext()) {
            String primaryTable = primary.next();
            Iterator<String> foreign = pathAnalyserNodeMap.keySet().iterator();
            while (foreign.hasNext()) {
                List<FineBusinessTableRelationPath> paths = getAllPathsBetweenTables(primaryTable, foreign.next());
                result.addAll(paths);
            }
        }
        return result;
    }

    public List<FineBusinessTableRelationPath> getAllPathsBetweenTables(String primary, String foreign) {
        PathAnalyserNode primaryNode = getPathAnalyserNode(primary);
        return primaryNode.getTargetPath(new HashMap<PathAnalyserNode, Integer>(), foreign);
    }

    private PathAnalyserNode getPathAnalyserNode(String table) {
        synchronized (tableAnalyserMap) {
            if (tableAnalyserMap.containsKey(table)) {
                return tableAnalyserMap.get(table);
            } else {
                PathAnalyserNode node = new PathAnalyserNode(table, relationDao);
                tableAnalyserMap.put(table, node);
                return node;
            }
        }
    }
}
