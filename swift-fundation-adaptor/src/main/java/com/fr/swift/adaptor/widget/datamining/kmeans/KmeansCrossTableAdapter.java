package com.fr.swift.adaptor.widget.datamining.kmeans;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.kmeans.KmeansBean;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.fr.engine.compare.CompareUtil;
import com.fr.swift.adaptor.widget.datamining.DMErrorWrap;
import com.fr.swift.adaptor.widget.datamining.SwiftAlgorithmResultAdapter;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.info.group.XGroupQueryInfo;
import com.fr.swift.query.post.group.XGroupPostQuery;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSet;
import com.fr.swift.result.XNodeMergeResultSetImpl;
import com.fr.swift.result.node.iterator.NLevelGroupNodeIterator;
import com.fr.swift.result.node.iterator.PostOrderNodeIterator;
import com.fr.swift.result.node.xnode.XNodeUtils;
import com.fr.swift.source.SwiftResultSet;
import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author qingj
 */
public class KmeansCrossTableAdapter extends SwiftAlgorithmResultAdapter<KmeansBean, CrossTableWidget, NodeResultSet, XGroupQueryInfo> {

    public KmeansCrossTableAdapter(KmeansBean bean, CrossTableWidget widget, NodeResultSet result, XGroupQueryInfo info, DMErrorWrap errorWrap) {
        super(bean, widget, result, info, errorWrap);
    }

    @Override
    public SwiftResultSet getResult() {

//        if (info.getColDimensionInfo().getDimensions().length == 0 || info.getDimensionInfo().getDimensions().length == 0) {
//            KmeansGroupTableAdapter adapter = new KmeansGroupTableAdapter(bean, widget, result, info, errorWrap);
//            return adapter.getResult();
//        } else {
            // 行列表头都不为空
            return handleCrossTable();
//        }
    }

    private SwiftResultSet handleCrossTable() {
        try {

//            int targetSize = info.getTargetInfo().getMetrics().size();
            int targetSize = 0;
//            List<Aggregator> aggregators = info.getTargetInfo().getResultAggregators();

            XNodeMergeResultSet XResultSet = (XNodeMergeResultSet) result;
            TopGroupNode topNode = XResultSet.getTopGroupNode();
            int topDepth = info.getColDimensionInfo().getDimensions().length;


            XLeftNode leftNode = (XLeftNode) XResultSet.getNode();
//            int leftDepth = info.getDimensionInfo().getDimensions().length;
            int leftDepth = 0;
            PostOrderNodeIterator<GroupNode> xLeftNodePostOrderNodeIterator = new PostOrderNodeIterator<GroupNode>(leftDepth, leftNode);
            Iterator<GroupNode> leftIterator = XNodeUtils.excludeAllSummaryRow(xLeftNodePostOrderNodeIterator);

            // 把TopGroup节点上的值，放到XLeftNode上，这时XLeft结点变成类似于分组表的结点，所有计算在这个上面进行
            while (leftIterator.hasNext()) {
                GroupNode lNode = leftIterator.next();
                PostOrderNodeIterator<TopGroupNode> topGroupNodePostOrderNodeIterator = new PostOrderNodeIterator<TopGroupNode>(topDepth, topNode);
                Iterator<TopGroupNode> topIterator = XNodeUtils.excludeAllSummaryRow(topGroupNodePostOrderNodeIterator);

                while (topIterator.hasNext()) {
                    TopGroupNode tNode = topIterator.next();
                    GroupNode newNode = new GroupNode();

                    List<AggregatorValue[]> topGroupValues = tNode.getTopGroupValues();
                    AggregatorValue[] aggregatorValues = topGroupValues.get(lNode.getIndex());
                    tNode.setAggregatorValue(aggregatorValues);

                    newNode.setData(tNode.getData());
                    newNode.setAggregatorValue(aggregatorValues);

                    newNode.setParent(lNode);
                    lNode.addChild(newNode);
                }
            }


            // 构造出聚类的GroupNode的分组表结果
            GroupNode resultNode = new GroupNode();
            int combineDepth = leftDepth + info.getColDimensionInfo().getDimensions().length;
            PostOrderNodeIterator<GroupNode> leftNodeIterator = new PostOrderNodeIterator<GroupNode>(combineDepth, leftNode);
            Iterator<GroupNode> leftAllSummaryNodeIterator = XNodeUtils.excludeAllSummaryRow(leftNodeIterator);
            while (leftAllSummaryNodeIterator.hasNext()) {
                GroupNode lNode = leftAllSummaryNodeIterator.next();
                int random = (int) (Math.random() * 3);

                addDimensionNode(resultNode, lNode, random);

            }

            // 获取TopGroupNode第一组全部结点,为了填充值,使用LinkedHashMap保持Key值和放入的顺序一致
            LinkedHashMap<Object, GroupNode> firstGroupNodeMap = new LinkedHashMap<Object, GroupNode>();
            NLevelGroupNodeIterator topNLevelNodeIterator = new NLevelGroupNodeIterator(topDepth - 1 >= 0 ? topDepth - 1 : 0, topNode);
            while (topNLevelNodeIterator.hasNext()) {
                GroupNode leafNode = topNLevelNodeIterator.next();
                List<GroupNode> children = leafNode.getChildren();
                for (GroupNode child : children) {
                    GroupNode copy = copyGroupNode(child);
                    AggregatorValue[] aggregatorValues = new AggregatorValue[targetSize];
                    Arrays.fill(aggregatorValues, null);
                    copy.setAggregatorValue(aggregatorValues);
                    firstGroupNodeMap.put(copy.getData(), copy);
                }
                break;
            }

            // 把resultNode所有叶子结点填充，满足转换成topNode的结构
            NLevelGroupNodeIterator nLevelResultNodeIterator = new NLevelGroupNodeIterator(leftDepth + topDepth, resultNode);
            while (nLevelResultNodeIterator.hasNext()) {
                GroupNode leafNode = nLevelResultNodeIterator.next();
                List<GroupNode> children = leafNode.getChildren();
                LinkedHashMap<Object, GroupNode> cloneHashMap = cloneHashMap(firstGroupNodeMap);
                for (GroupNode child : children) {
                    cloneHashMap.put(child.getData(), child);
                }
                leafNode.clearChildren();
                for (GroupNode node : cloneHashMap.values()) {
                    leafNode.addChild(node);
                }
            }

            // 遍历resultNode，把结果放入resultNode的aggregatorValue里面，因为是聚类所以+1
            int clusterDepth = leftDepth + 1;
            NLevelGroupNodeIterator nLevelGroupNodeIterator = new NLevelGroupNodeIterator(clusterDepth, resultNode);
            while (nLevelGroupNodeIterator.hasNext()) {
                GroupNode nLevelNode = nLevelGroupNodeIterator.next();
                PostOrderNodeIterator<GroupNode> groupNodePostOrderNodeIterator = new PostOrderNodeIterator<GroupNode>(topDepth, nLevelNode);
                List<AggregatorValue> list = new ArrayList<AggregatorValue>();
                while (groupNodePostOrderNodeIterator.hasNext()) {
                    GroupNode groupNode = groupNodePostOrderNodeIterator.next();
                    AggregatorValue[] aggregatorValue = groupNode.getAggregatorValue();
                    list.addAll(Arrays.asList(aggregatorValue));
                }
                AggregatorValue[] arr = new AggregatorValue[list.size()];
                nLevelNode.setAggregatorValue(list.toArray(arr));
                nLevelNode.getChildMap().clear();
            }

            // 把GroupNode转化为XLeftNode结构
            XLeftNode xLeftResultNode = cloneGroupNodeToXLeftNode(resultNode, targetSize);

            // 计算XLeft汇总值，并刷新到TopGroup上
//            GroupNodeAggregateUtils.aggregate(NodeType.X_LEFT, clusterDepth, xLeftResultNode, info.getTargetInfo().getResultAggregators());
            XNodeUtils.updateTopGroupNodeValues(topDepth, clusterDepth,
                    topNode, xLeftResultNode);

            // 计算TopGroup汇总值，并刷新到xLeft上
            topNode.setTopGroupValues(null); // 不设置为null他不计算根节点
//            GroupNodeAggregateUtils.aggregate(NodeType.TOP_GROUP, topDepth, topNode, info.getTargetInfo().getResultAggregators());
            // 先更新一下xLeftNode#valueArrayList（包含topGroupNode所有列（包括汇总列）的某一行)
            XGroupPostQuery.updateXLeftNode(clusterDepth, topDepth, xNodeResultAdapter(xLeftResultNode, topNode));

            // 把XLeft的中间值，转化成XValue最终结果
            XNodeUtils.setValues2XLeftNode(info.getColDimensionInfo().isShowSum(), topDepth, clusterDepth,
                    topNode, xLeftResultNode);

            return xNodeResultAdapter(xLeftResultNode, topNode);

        } catch (Exception e) {
            errorWrap.setError(e.getMessage());
            SwiftLoggers.getLogger().error(e.getMessage(), e);
        }

        return result;
    }

    private XNodeMergeResultSet xNodeResultAdapter(XLeftNode xLeftNode, TopGroupNode topGroupNode) {
        return new XNodeMergeResultSetImpl(xLeftNode, topGroupNode, null, null, null);
    }

    private XLeftNode cloneGroupNodeToXLeftNode(GroupNode groupNode, int targetLength) {
        XLeftNode xLeftNode = new XLeftNode();
        xLeftNode.setData(groupNode.getData());

        AggregatorValue[] aggregatorValue = groupNode.getAggregatorValue();
        List<AggregatorValue[]> xValue = new ArrayList<AggregatorValue[]>();
        int groupNum = targetLength == 0 ? 0 : aggregatorValue.length / targetLength;
        for (int i = 0; i < groupNum; i++) {
            int n = i * targetLength;
            AggregatorValue[] aggArr = new AggregatorValue[targetLength];
            for (int j = 0; j < targetLength; j++) {
                aggArr[j] = aggregatorValue[n + j];
            }
            xValue.add(aggArr);
        }
        xLeftNode.setValueArrayList(xValue);

        List<GroupNode> children = groupNode.getChildren();
        for (GroupNode child : children) {
            XLeftNode cloneChild = cloneGroupNodeToXLeftNode(child, targetLength);
            cloneChild.setParent(xLeftNode);
            xLeftNode.addChild(cloneChild);
        }

        return xLeftNode;
    }

    private GroupNode addDimensionNode(GroupNode resultNode, GroupNode leafNode, int clusterNum) {
        LinkedList<GroupNode> nodeList = new LinkedList<GroupNode>();
        nodeList.add(leafNode);
        GroupNode parent = leafNode.getParent();
        while (parent != null) {
            nodeList.add(parent);
            parent = parent.getParent();
        }
        // 移除掉root结点，因为它的值为null
        nodeList.remove(nodeList.size() - 1);
        GroupNode findNode = findChildrenGroupNode(resultNode, clusterNum);
        if (findNode == null) {
            GroupNode newGroupNode = new GroupNode();
            newGroupNode.setData(clusterNum);
            resultNode.addChild(newGroupNode);
            findNode = newGroupNode;
        }
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            GroupNode leftParentNode = nodeList.get(i);
            GroupNode lastNode = findNode;
            findNode = findChildrenGroupNode(findNode, leftParentNode.getData());
            if (findNode == null) {
                GroupNode newNode = copyGroupNode(leftParentNode);
                lastNode.addChild(newNode);
                findNode = newNode;
            }
        }
        return resultNode;
    }


    private LinkedHashMap<Object, GroupNode> cloneHashMap(LinkedHashMap<Object, GroupNode> originMap) {
        LinkedHashMap<Object, GroupNode> newMap = new LinkedHashMap<Object, GroupNode>();
        for (Object key : originMap.keySet()) {
            newMap.put(key, copyGroupNode(originMap.get(key)));
        }
        return newMap;
    }

    private GroupNode findChildrenGroupNode(GroupNode resultNode, Object clusterNum) {
        for (int i = 0; i < resultNode.getChildrenSize(); i++) {
            GroupNode child = resultNode.getChild(i);
            if (CompareUtil.isEqual(child.getData(), clusterNum)) {
                return child;
            }
        }
        return null;
    }

    private GroupNode copyGroupNode(GroupNode node) {
        GroupNode newNode = new GroupNode();
        newNode.setData(node.getData());
        newNode.setAggregatorValue(node.getAggregatorValue());
        return newNode;
    }
}