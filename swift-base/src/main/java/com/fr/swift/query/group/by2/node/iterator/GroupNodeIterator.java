package com.fr.swift.query.group.by2.node.iterator;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by2.DFTIterator;
import com.fr.swift.query.group.by2.GroupByController;
import com.fr.swift.query.group.by2.ItCreator;
import com.fr.swift.query.group.by2.MultiGroupByV2;
import com.fr.swift.query.group.by2.node.ProxyNodeCreatorStack;
import com.fr.swift.query.group.by2.node.expander.NodeAllExpanderController;
import com.fr.swift.query.group.by2.node.expander.NodeLazyExpanderController;
import com.fr.swift.query.group.by2.node.expander.NodeNLevelExpanderController;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.cursor.Expander;
import com.fr.swift.query.group.info.cursor.ExpanderType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.BiFunction;
import com.fr.swift.util.function.Function;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/4/27.
 */
public class GroupNodeIterator<Node extends GroupNode> implements Iterator<Node[]> {

    private GroupByInfo groupByInfo;
    private Node root;
    private BiFunction<Integer, GroupByEntry, Node> itemMapper;
    private BiFunction<GroupByEntry, LimitedStack<Node>, Node[]> rowMapper;
    private Iterator<Node[]> iterator;

    public GroupNodeIterator(GroupByInfo groupByInfo, Node root,
                             BiFunction<Integer, GroupByEntry, Node> itemMapper,
                             BiFunction<GroupByEntry, LimitedStack<Node>, Node[]> rowMapper) {
        this.groupByInfo = groupByInfo;
        this.root = root;
        this.itemMapper = itemMapper;
        this.rowMapper = rowMapper;
        init();
    }

    private void init() {
        List<Column> dimensions = groupByInfo.getDimensions();
        final List<DictionaryEncodedColumn> dictionaries = IteratorUtils.iterator2List(new MapperIterator<Column, DictionaryEncodedColumn>(dimensions.iterator(), new Function<Column, DictionaryEncodedColumn>() {
            @Override
            public DictionaryEncodedColumn apply(Column p) {
                return p.getDictionaryEncodedColumn();
            }
        }));
        ProxyNodeCreatorStack<Node> proxyStack = new ProxyNodeCreatorStack<Node>(dimensions.size(), root);
        DFTIterator dftIterator = new DFTIterator(dimensions.size(), new ItCreator(groupByInfo), proxyStack);
        GroupByController controller = createController(dictionaries);

        iterator = new MultiGroupByV2<Node>(dftIterator, proxyStack, controller, itemMapper, rowMapper);
    }

    private GroupByController<GroupNode> createController(List<DictionaryEncodedColumn> dictionaries) {
        Expander expander = groupByInfo.getExpander();
        Set<RowIndexKey<int[]>> indexKeys = strKey2IntKey(expander.getStringIndexKeys(), dictionaries);
        ExpanderType type = expander.getType();
        if (type == ExpanderType.LAZY_EXPANDER) {
            return new NodeLazyExpanderController(indexKeys);
        } else if (type == ExpanderType.N_LEVEL_EXPANDER) {
            return new NodeNLevelExpanderController(expander.getNLevel());
        } else {
            return new NodeAllExpanderController(indexKeys);
        }
    }

    private static Set<RowIndexKey<int[]>> strKey2IntKey(Set<RowIndexKey<String[]>> strKeys,
                                                         List<DictionaryEncodedColumn> dictionaries) {
        Set<RowIndexKey<int[]>> indexKeys = new HashSet<RowIndexKey<int[]>>();
        for (RowIndexKey<String[]> strKey : strKeys) {
            Object[] keys = strKey.getKey();
            int[] indexes = new int[keys.length];
            Arrays.fill(indexes, -1);
            for (int i = 0; i < keys.length; i++) {
                assert keys[0] != null;
                if (keys[i] == null) {
                    break;
                }
                int index = dictionaries.get(i).getIndex(keys[i]);
                if (index == -1) {
                    // 没找到，后面的keys[i]即使存在也不用管了
                    break;
                }
                indexes[i] = index;
            }
            if (indexes[0] != -1) {
                indexKeys.add(new RowIndexKey<int[]>(indexes));
            }
        }
        return indexKeys;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Node[] next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
