package com.fr.swift.service.handler.history.rule;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.DataSyncRule;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/7/16
 */
@SwiftBean(name = "defaultDataSyncRule")
public class DefaultDataSyncRule implements DataSyncRule {
    @Override
    public Map<String, Set<SegmentKey>> getNeedLoadAndUpdateDestinations(Set<String> nodeIds, Set<SegmentKey> needLoads,
                                                                         Map<SourceKey, List<SegmentDestination>> destinations) {
        int maxSegBakCount = getMaxSegBakCount(nodeIds);
        NodeSegmentsPair[] nodeSegs = getNodeSegmentPair(nodeIds, destinations);
        Map<String, Set<String>> segmentLoadedCountMap = getSegmentLoadedCountMap(nodeSegs);

        Map<String, Set<SegmentKey>> result = new HashMap<String, Set<SegmentKey>>();

        for (SegmentKey needLoad : needLoads) {
            SourceKey sourceKey = needLoad.getTable();
            if (null == destinations.get(sourceKey)) {
                destinations.put(sourceKey, new ArrayList<SegmentDestination>());
            }
            int lessCount = maxSegBakCount;
            //减去已经存在的份数
            if (segmentLoadedCountMap.containsKey((needLoad.getId()))){
                lessCount -= segmentLoadedCountMap.get(needLoad.getId()).size();
            }

            for (NodeSegmentsPair pair : getSegmentParis(lessCount, nodeSegs, needLoad.getId())){
                if (null == result.get(pair.getClusterId())) {
                    result.put(pair.getClusterId(), new HashSet<SegmentKey>());
                }
                result.get(pair.getClusterId()).add(needLoad);
                destinations.get(sourceKey).add(new SegmentDestinationImpl(pair.getClusterId(),
                        needLoad.getId(), needLoad.getOrder()));
            }
        }

        return result;
    }

    //从seg最少的node开始，取less个不含有当前segId的node
    private List<NodeSegmentsPair> getSegmentParis(int lessCount, NodeSegmentsPair[] nodeSegs, String id) {
        if (nodeSegs.length == 0) {
            throw new RuntimeException("NodeSortMap is empty");
        }
        Arrays.sort(nodeSegs);
        List<NodeSegmentsPair> result = new ArrayList<NodeSegmentsPair>();
        for (NodeSegmentsPair pair : nodeSegs){
            if (lessCount <= 0){
                break;
            }
            if (!pair.containsSeg(id)){
                pair.addSeg(id);
                result.add(pair);
                lessCount--;
            }
        }
        return result;
    }

    //获取segment在哪些节点上面
    private Map<String, Set<String>> getSegmentLoadedCountMap(NodeSegmentsPair[] nodeSegs) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        for (NodeSegmentsPair pair: nodeSegs){
            for (String segId : pair.getSegIds()){
                if (!result.containsKey(segId)){
                    result.put(segId, new HashSet<String>());
                }
                result.get(segId).add(pair.getClusterId());
            }
        }
        return result;
    }

    //获取每个节点上已经有哪些segments
    private NodeSegmentsPair[] getNodeSegmentPair(Set<String> nodeIds, Map<SourceKey, List<SegmentDestination>> destinations) {
        Map<String, Set<String>> nodeSegsMap = new HashMap<String, Set<String>>();
        for (String nodeId : nodeIds){
            nodeSegsMap.put(nodeId, new HashSet<String>());
        }
        for (Map.Entry<SourceKey, List<SegmentDestination>> entry : destinations.entrySet()){
            for (SegmentDestination dest : entry.getValue()){
                if (nodeSegsMap.containsKey(dest.getClusterId())){
                    nodeSegsMap.get(dest.getClusterId()).add(dest.getSegmentId());
                }
            }
        }
        NodeSegmentsPair[] nodeSegs = new NodeSegmentsPair[nodeSegsMap.size()];
        int index = 0;
        for (Map.Entry<String, Set<String>> nodeSeg : nodeSegsMap.entrySet()){
            nodeSegs[index++] = new NodeSegmentsPair(nodeSeg.getKey(), nodeSeg.getValue());
        }
        return nodeSegs;
    }

    private int getMaxSegBakCount(Set<String> nodeIds) {
        // 最多存3份，最少存node份
        return nodeIds.size() > 3 ? 3 : nodeIds.size();
    }

}
