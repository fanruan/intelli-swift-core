package com.fr.swift.adaptor.widget.datamining.kmeans;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.algorithm.kmeans.KmeansPredict;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.kmeans.KmeansBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.dashboard.widget.dimension.kmeans.FineKmeansDimension;
import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.dashboard.widget.target.FineTarget;
import com.fr.swift.adaptor.widget.datamining.SwiftAlgorithmResultAdapter;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.query.adapter.dimension.DimensionInfo;
import com.fr.swift.query.adapter.target.TargetInfo;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
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
public class KmeansGroupTableAdapter implements SwiftAlgorithmResultAdapter<KmeansBean, AbstractTableWidget, NodeResultSet, GroupQueryInfo> {


    @Override
    public SwiftResultSet getResult(KmeansBean bean, AbstractTableWidget widget, NodeResultSet result, GroupQueryInfo info) throws Exception {


        // List<FineDimension> dimensionList = widget.getDimensionList();
        List<FineTarget> targetList = widget.getTargetList();
        TargetInfo targetInfo = info.getTargetInfo();
        DimensionInfo dimensionInfo = info.getDimensionInfo();
        List<Aggregator> aggregators = targetInfo.getResultAggregators();

        GroupNode rootNode = (GroupNode) result.getNode();
        List<double[]> resultSummary = getResultSummary(rootNode);

        KmeansPredict kmeans = new KmeansPredict(bean, resultSummary, targetList);
        GroupNode resultRootNode = addFirstDimension(rootNode, kmeans, info);
        GroupNodeAggregateUtils.aggregate(NodeType.GROUP, dimensionInfo.getDimensions().length, resultRootNode, aggregators);

        return new NodeMergeResultSetImpl(resultRootNode, new ArrayList<Map<Integer, Object>>(), new ArrayList<Aggregator>());

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
     * @throws Exception
     */
    private GroupNode addFirstDimension(GroupNode rootNode, KmeansPredict kmeans, GroupQueryInfo info) throws Exception {
        GroupNode resultRootNode = new GroupNode();
        Map<Integer, GroupNode> childNodes = new HashMap<Integer, GroupNode>(kmeans.getCluster());
        for (int i = 0; i < rootNode.getChildrenSize(); i++) {
            Map<Integer, GroupNode> node = changeNode(rootNode.getChild(i), kmeans);
            childNodes.putAll(mergeNode(node, childNodes, info));
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
    private Map<Integer, GroupNode> changeNode(GroupNode rootNode, KmeansPredict kmeans) throws Exception {
        if (rootNode.getChildrenSize() == 0) {
            GroupNode resultRootNode = new GroupNode();
            resultRootNode.setData(rootNode.getData());
            AggregatorValue[] summaryValue = rootNode.getAggregatorValue();
            resultRootNode.setAggregatorValue(summaryValue);

            double[] targets = new double[summaryValue.length];
            for (int i = 0; i < summaryValue.length; i++) {
                targets[i] = summaryValue[i].calculate();
            }
            int predict = kmeans.predict(targets);
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

    private Map<Integer, GroupNode> mergeNode(Map<Integer, GroupNode> node1, Map<Integer, GroupNode> node2, GroupQueryInfo info) {
        Map<Integer, GroupNode> resultNode = new HashMap<Integer, GroupNode>();

        if (node2.isEmpty()) {
            for (int index : node1.keySet()) {
                GroupNode resultRootNode = new GroupNode();
                resultRootNode.setData(index);
                resultRootNode.addChild(node1.get(index));
                resultRootNode.setAggregatorValue(node1.get(index).getAggregatorValue());
                resultNode.put(index, resultRootNode);
            }
            return resultNode;
        }

        for (int index : node1.keySet()) {
            if (node2.keySet().contains(index)) {
                GroupNode fineGroupNode1 = node1.get(index);
                GroupNode fineGroupNode2 = node2.get(index);
                if (fineGroupNode2.getData() == (Object) index) {
                    fineGroupNode2.addChild(fineGroupNode1);
                    fineGroupNode2.setAggregatorValue(fineGroupNode1.getAggregatorValue());
                    resultNode.put(index, fineGroupNode2);
                }
            } else {
                GroupNode resultRootNode = new GroupNode();
                resultRootNode.setData(index);
                resultRootNode.addChild(node1.get(index));
                resultRootNode.setAggregatorValue(node1.get(index).getAggregatorValue());
                resultNode.put(index, resultRootNode);
            }
        }
        return resultNode;
    }


    private Map<Integer, GroupNode> mergeNode(Map<Integer, GroupNode> node1, Map<Integer, GroupNode> node2, Object parentData) {
        Map<Integer, GroupNode> resultNode = new HashMap<Integer, GroupNode>();

        if (node2.isEmpty()) {
            for (int index : node1.keySet()) {
                GroupNode resultRootNode = new GroupNode();
                resultRootNode.setData(parentData);
                resultRootNode.addChild(node1.get(index));
                resultRootNode.setAggregatorValue(node1.get(index).getAggregatorValue());
                resultNode.put(index, resultRootNode);
            }
            return resultNode;
        }

        for (int index : node1.keySet()) {
            if (node2.keySet().contains(index)) {
                GroupNode fineGroupNode1 = node1.get(index);
                GroupNode fineGroupNode2 = node2.get(index);
                if (fineGroupNode2.getData() == parentData) {
                    fineGroupNode2.addChild(fineGroupNode1);
                    fineGroupNode2.setAggregatorValue(fineGroupNode1.getAggregatorValue());
                    resultNode.put(index, fineGroupNode2);
                }
            } else {
                GroupNode resultRootNode = new GroupNode();
                resultRootNode.setData(parentData);
                resultRootNode.addChild(node1.get(index));
                resultRootNode.setAggregatorValue(node1.get(index).getAggregatorValue());
                resultNode.put(index, resultRootNode);
            }
        }
        return resultNode;
    }

}