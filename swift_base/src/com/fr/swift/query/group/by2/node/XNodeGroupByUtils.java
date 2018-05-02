package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSetImpl;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
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
        return new XNodeMergeResultSetImpl(xLeftRoot, topNodeRoot, rowGlobalDictionaries, topItCreator.getColGlobalDictionaries());
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
