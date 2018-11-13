package com.fr.swift.adaptor.widget.datamining.kmeans;

import com.finebi.conf.algorithm.kmeans.KmeansPredict;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.kmeans.KmeansBean;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.fr.swift.adaptor.widget.datamining.DMErrorWrap;
import com.fr.swift.adaptor.widget.datamining.SwiftAlgorithmResultAdapter;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.info.element.dimension.DimensionInfo;
import com.fr.swift.query.info.element.target.TargetInfo;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.GroupNodeAggregateUtils;
import com.fr.swift.result.node.NodeType;
import com.fr.swift.source.SwiftResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qingj
 */
public class KmeansGroupTableAdapter extends SwiftAlgorithmResultAdapter<KmeansBean, AbstractTableWidget, NodeResultSet, GroupQueryInfoImpl> {


    public KmeansGroupTableAdapter(KmeansBean bean, AbstractTableWidget widget, NodeResultSet result, GroupQueryInfoImpl info, DMErrorWrap errorWrap) {
        super(bean, widget, result, info, errorWrap);
    }

    @Override
    public SwiftResultSet getResult() throws Exception {

        List<FineTarget> targetList = widget.getTargetList();
//        TargetInfo targetInfo = info.getTargetInfo();
//        DimensionInfo dimensionInfo = info.getDimensionInfo();
//        List<Aggregator> aggregators = info.getTargetInfo().getResultAggregators();

        TargetInfo targetInfo = null;
        DimensionInfo dimensionInfo = null;
        List<Aggregator> aggregators = null;

        GroupNode rootNode = (GroupNode) result.getNode();
        List<double[]> resultSummary = getResultSummary(rootNode);

//        KmeansPredict kmeans = new KmeansPredict(bean, resultSummary, targetList);
        KmeansPredict kmeans = null;
        GroupNode resultRootNode = addFirstDimension(rootNode, kmeans, info);
        GroupNodeAggregateUtils.aggregate(NodeType.GROUP, dimensionInfo.getDimensions().length, resultRootNode, targetInfo.getResultAggregators());

        return new NodeMergeResultSetImpl(resultRootNode, new ArrayList<Map<Integer, Object>>());

    }

    /**
     * 分组表指标转换二维表
     *
     * @param rootNode 分组表指标
     * @return 指标数据表
     */
    private List<double[]> getResultSummary(GroupNode rootNode) {
        List<double[]> numbers = new ArrayList<double[]>();
        if (rootNode.getChildrenSize() == 0) {
            AggregatorValue[] aggregatorValue = rootNode.getAggregatorValue();
            double[] targets = new double[aggregatorValue.length];
            for (int i = 0; i < aggregatorValue.length; i++) {
                targets[i] = aggregatorValue[i].calculate();
            }
            numbers.add(targets);
        } else {
            for (int i = 0; i < rootNode.getChildrenSize(); i++) {
                numbers.addAll(getResultSummary(rootNode.getChild(i)));
            }
        }
        return numbers;
    }


    /**
     * 根据聚类算法添加第一维度聚类
     *
     * @param rootNode 分组表根节点
     * @param kmeans   聚类
     * @return 添加后的根节点
     */
    private GroupNode addFirstDimension(GroupNode rootNode, KmeansPredict kmeans, GroupQueryInfoImpl info) {
        GroupNode resultRootNode = new GroupNode(-1, null);
        Map<Integer, GroupNode> childNodes = new HashMap<Integer, GroupNode>(kmeans.getCluster());
        for (int i = 0; i < rootNode.getChildrenSize(); i++) {
            Map<Integer, GroupNode> node = changeNode(rootNode.getChild(i), kmeans);
            childNodes.putAll(mergeRootNode(node, childNodes));
        }

        // 聚类维度排序
        for (int i : childNodes.keySet()) {
            resultRootNode.addChild(childNodes.get(i));
        }
        resultRootNode.setData(rootNode.getData());
        resultRootNode.setAggregatorValue(rootNode.getAggregatorValue());
        return resultRootNode;
    }

    /**
     * 使用递归将聚类维度加入第一维度中
     *
     * @param rootNode 分组表某节点
     * @param kmeans   聚类
     * @return 节点与对应的聚类值map
     * @throws Exception
     */
    private Map<Integer, GroupNode> changeNode(GroupNode rootNode, KmeansPredict kmeans) {
        if (rootNode.getChildrenSize() == 0) {
            GroupNode resultRootNode = new GroupNode(rootNode.getDepth() + 1, null);
            resultRootNode.setData(rootNode.getData());
            AggregatorValue[] summaryValue = rootNode.getAggregatorValue();
            resultRootNode.setAggregatorValue(summaryValue);

            double[] targets = new double[summaryValue.length];
            for (int i = 0; i < summaryValue.length; i++) {
                targets[i] = summaryValue[i].calculate();
            }
//            int predict = kmeans.predict(targets);
            int predict = 0;
            HashMap<Integer, GroupNode> map = new HashMap<Integer, GroupNode>(1);
            map.put(predict, resultRootNode);
            return map;
        } else {
            Map<Integer, GroupNode> childNodes = new HashMap<Integer, GroupNode>(kmeans.getCluster());
            for (int i = 0; i < rootNode.getChildrenSize(); i++) {
                Map<Integer, GroupNode> node = changeNode(rootNode.getChild(i), kmeans);
                childNodes.putAll(mergeNode(node, childNodes, rootNode.getData()));
            }
            return childNodes;
        }
    }

    private Map<Integer, GroupNode> mergeRootNode(Map<Integer, GroupNode> subLevelMap, Map<Integer, GroupNode> sameLevelMap) {
        Map<Integer, GroupNode> resultNode = new HashMap<Integer, GroupNode>();

        for (int clusterNum : subLevelMap.keySet()) {
            GroupNode subGroupNode = subLevelMap.get(clusterNum);
            if (sameLevelMap.keySet().contains(clusterNum)) {
                GroupNode sameGroupNode = sameLevelMap.get(clusterNum);
                sameGroupNode.addChild(subGroupNode);
                sameGroupNode.setAggregatorValue(subGroupNode.getAggregatorValue());
                resultNode.put(clusterNum, sameGroupNode);
            } else {
                GroupNode resultRootNode = new GroupNode(0, (Integer) clusterNum);
                resultRootNode.addChild(subGroupNode);
                resultNode.put(clusterNum, resultRootNode);
            }
        }


        return resultNode;
    }


    /**
     * @param subLevelMap  儿子结点的信息
     * @param sameLevelMap 同级结点的信息
     * @param parentData
     * @return
     */
    private Map<Integer, GroupNode> mergeNode(Map<Integer, GroupNode> subLevelMap, Map<Integer, GroupNode> sameLevelMap, Object parentData) {
        Map<Integer, GroupNode> resultNode = new HashMap<Integer, GroupNode>();

        // 在子节点的时候调用，相当于clone一遍元素，放到对应的聚类数里面
        for (int clusterNum : subLevelMap.keySet()) {
            GroupNode subGroupNode = subLevelMap.get(clusterNum);
            if (sameLevelMap.keySet().contains(clusterNum)) {

                GroupNode sameGroupNode = sameLevelMap.get(clusterNum);
                if (sameGroupNode.getData() == parentData) {
                    sameGroupNode.addChild(subGroupNode);
                    sameGroupNode.setAggregatorValue(subGroupNode.getAggregatorValue());
                    resultNode.put(clusterNum, sameGroupNode);
                }
            } else {

                GroupNode resultRootNode = new GroupNode(subGroupNode.getDepth() - 1, parentData);
                resultRootNode.addChild(subGroupNode);
                resultRootNode.setAggregatorValue(subGroupNode.getAggregatorValue());
                resultNode.put(clusterNum, resultRootNode);
            }
        }
        return resultNode;
    }

}