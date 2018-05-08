package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.group.by2.node.iterator.TopGroupNodeIterator;
import com.fr.swift.query.group.by2.node.iterator.XLeftNodeIterator;
import com.fr.swift.query.group.by2.node.mapper.TopGroupNodeRowMapper;
import com.fr.swift.query.group.by2.node.mapper.XLeftNodeRowMapper;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSetImpl;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.Filter;
import com.fr.swift.structure.iterator.FilteredIterator;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/28.
 */
public class XNodeGroupByUtils {

    public static NodeResultSet<XLeftNode> groupBy(GroupByInfo rowGroupByInfo, GroupByInfo colGroupByInfo,
                                                   MetricInfo metricInfo) {
        TopGroupNode topNodeRoot = new TopGroupNode(-1, null);
        Iterator<TopGroupNode[]> topIt = new TopGroupNodeIterator(colGroupByInfo, topNodeRoot, new TopGroupNodeRowMapper());
        CacheIterable topItCreator = new CacheIterable(topIt, colGroupByInfo);
        XLeftNodeRowMapper xLeftNodeRowMapper = new XLeftNodeRowMapper(metricInfo, topItCreator);
        XLeftNode xLeftRoot = new XLeftNode(-1, null);
        Iterator<XLeftNode[]> leftIt = new XLeftNodeIterator(rowGroupByInfo, xLeftRoot, xLeftNodeRowMapper);
        List<Map<Integer, Object>> rowGlobalDictionaries = NodeGroupByUtils.initDictionaries(rowGroupByInfo.getDimensions().size());
        List<DictionaryEncodedColumn> dictionaries = IteratorUtils.iterator2List(new MapperIterator<Column, DictionaryEncodedColumn>(rowGroupByInfo.getDimensions().iterator(), new Function<Column, DictionaryEncodedColumn>() {
            @Override
            public DictionaryEncodedColumn apply(Column p) {
                return p.getDictionaryEncodedColumn();
            }
        }));
        while (leftIt.hasNext()) {
            XLeftNode[] leftRow = leftIt.next();
            // 更新行表头的索引和转化节点的字典序号
            NodeGroupByUtils.updateGlobalDictionariesAndGlobalIndex(leftRow, rowGlobalDictionaries, dictionaries);
        }
        // 表头的叶子节点对应列的所有聚合值，用于交叉表XLeftNode行合并的基础上继续进行列向的合并
        setValues2topGroupNodeAndClearCachedIndex(rowGroupByInfo.getDimensions().size(), colGroupByInfo.getDimensions().size(),
                xLeftRoot, topNodeRoot);
        return new XNodeMergeResultSetImpl(xLeftRoot, topNodeRoot, rowGlobalDictionaries,
                topItCreator.getColGlobalDictionaries(), metricInfo.getAggregators());
    }

    private static void setValues2topGroupNodeAndClearCachedIndex(int rowDimensionSize, int colDimensionSize,
                                                                  XLeftNode leftRoot, TopGroupNode topRoot) {
        List<XLeftNode> leftLeafNodes = getLeafNodes(rowDimensionSize, leftRoot);
        List<TopGroupNode> topLeafNodes = getLeafNodes(colDimensionSize, topRoot);
        // 清理表头叶子节点缓存的索引
        for (TopGroupNode node : topLeafNodes) {
            node.setTraversal(null);
        }
        updateValues2TopGroupNode(leftLeafNodes, topLeafNodes);
    }

    public static void updateValues2TopGroupNode(List<XLeftNode> leftLeafNodes, List<TopGroupNode> topLeafNodes) {
        for (int col = 0; col < topLeafNodes.size(); col++) {
            List<AggregatorValue[]> values = new ArrayList<AggregatorValue[]>();
            // topLeaf保存xLeftNode的所有行对应的xLeftNode#valueArrayList某一列的数据。就是二维数组转换
            for (int row = 0; row < leftLeafNodes.size(); row++) {
                assert leftLeafNodes.get(row).getValueArrayList().size() == topLeafNodes.size();
                List<AggregatorValue[]> rowValues = leftLeafNodes.get(row).getValueArrayList();
                // 添加(row, col)
                values.add(rowValues.get(col));
            }
            topLeafNodes.get(col).setTopGroupValues(values);
        }
    }

    /**
     * 找到树转为行，每一行对应一个叶子节点。返回这些叶子节点的集合。
     *
     * @param dimensionSize 维度个数
     * @param root          根节点
     * @param <Node>
     * @return
     */
    public static <Node extends GroupNode<Node>> List<Node> getLeafNodes(int dimensionSize, Node root) {
        // 树转为行
        Iterator<List<Node>> topIt = new Tree2RowIterator<Node>(dimensionSize, root.iterator());
        // 找出每一行中叶子节点
        Iterator<Node> leafNodeIt = new MapperIterator<List<Node>, Node>(topIt, new Function<List<Node>, Node>() {
            @Override
            public Node apply(List<Node> p) {
                for (int i = p.size() - 1; i > -1; i--) {
                    if (p.get(i) != null) {
                        // 一行的叶子节点
                        return p.get(i);
                    }
                }
                return null;    // p.isEmpty()
            }
        });
        // 过滤掉空值
        return IteratorUtils.iterator2List(new FilteredIterator<Node>(leafNodeIt, new Filter<Node>() {
            @Override
            public boolean accept(Node node) {
                return node != null;
            }
        }));
    }

    private static class CacheIterable implements Iterable<TopGroupNode[]> {

        private Iterator<TopGroupNode[]> iterator;
        private GroupByInfo colGroupByInfo;
        private List<TopGroupNode[]> cacheList = new ArrayList<TopGroupNode[]>();

        private List<Map<Integer, Object>> colGlobalDictionaries;
        private List<DictionaryEncodedColumn> dictionaries;

        public CacheIterable(Iterator<TopGroupNode[]> iterator, GroupByInfo colGroupByInfo) {
            this.iterator = iterator;
            this.colGroupByInfo = colGroupByInfo;
            init();
        }

        private void init() {
            colGlobalDictionaries = NodeGroupByUtils.initDictionaries(colGroupByInfo.getDimensions().size());
            dictionaries = IteratorUtils.iterator2List(new MapperIterator<Column, DictionaryEncodedColumn>(colGroupByInfo.getDimensions().iterator(), new Function<Column, DictionaryEncodedColumn>() {
                @Override
                public DictionaryEncodedColumn apply(Column p) {
                    return p.getDictionaryEncodedColumn();
                }
            }));
        }

        public List<Map<Integer, Object>> getColGlobalDictionaries() {
            return colGlobalDictionaries;
        }

        @Override
        public Iterator<TopGroupNode[]> iterator() {
            // 通过代理的方式缓存索引
            return cacheList.isEmpty() ? new ProxyIterator() : cacheList.iterator();
        }

        private class ProxyIterator implements Iterator<TopGroupNode[]> {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public TopGroupNode[] next() {
                TopGroupNode[] ret = iterator.next();
                // 更新表头字典和索引
                NodeGroupByUtils.updateGlobalDictionariesAndGlobalIndex(ret, colGlobalDictionaries, dictionaries);
                cacheList.add(ret);
                return ret;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
