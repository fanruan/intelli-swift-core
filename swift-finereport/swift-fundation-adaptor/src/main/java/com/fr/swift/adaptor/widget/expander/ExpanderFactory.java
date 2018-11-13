package com.fr.swift.adaptor.widget.expander;

import com.finebi.conf.algorithm.common.DMUtils;
import com.finebi.conf.constant.BIDesignConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.expander.ExpanderBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.fr.swift.query.group.info.cursor.Expander;
import com.fr.swift.query.group.info.cursor.ExpanderImpl;
import com.fr.swift.query.group.info.cursor.ExpanderType;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lyon on 2018/4/19.
 */
public class ExpanderFactory {

    public static Expander createRowExpander(TableWidgetBean bean, List<FineDimension> rowDimensions) {
        // 如果是挖掘，则展开所有节点
        boolean openRowNode = bean.isOpenRowNode();
        Map<String, Boolean> headerExpand = bean.getHeaderExpand();
        if (!DMUtils.isEmptyAlgorithm(bean.getDataMining())) {
            openRowNode = true;
            headerExpand = null;
        }

        // TODO: 2018/5/23 交叉表的行表头是bean.getvPage还是bean.getPage?
        return create(isOperationNode(bean.getPage(), openRowNode), rowDimensions, bean.getRowExpand(),
                headerExpand);
    }

    public static Expander createColExpander(TableWidgetBean bean, List<FineDimension> colDimensions) {
        return create(isOperationNode(bean.gethPage(), bean.isOpenColNode()), colDimensions, bean.getColExpand(),
                bean.getCrossHeaderExpand());
    }

    private static boolean isOperationNode(int pageOperator, boolean isOpenNode) {
        if (pageOperator == BIDesignConstants.DESIGN.TABLE_PAGE_OPERATOR.ALL_PAGE) {
            isOpenNode = true;
        }
        return isOpenNode;
    }

    private static Expander create(boolean isOpenNode, List<FineDimension> dimensions,
                                  List<ExpanderBean> beanList, Map<String, Boolean> headerExpander) {
        ExpanderType type = isOpenNode ? ExpanderType.ALL_EXPANDER : ExpanderType.LAZY_EXPANDER;
        type = headerExpander == null || headerExpander.isEmpty() ? type : ExpanderType.N_LEVEL_EXPANDER;
        beanList = beanList == null ? new ArrayList<ExpanderBean>(0) : beanList;
        Iterator<List<BeanTree>> iterator = new Tree2RowIterator<BeanTree>(dimensions.size(), new MapperIterator<ExpanderBean, BeanTree>(beanList.iterator(), new Function<ExpanderBean, BeanTree>() {
            @Override
            public BeanTree apply(ExpanderBean p) {
                return new BeanTree(p);
            }
        }), new Function<BeanTree, Iterator<BeanTree>>() {
            @Override
            public Iterator<BeanTree> apply(BeanTree p) {
                return p.iterator();
            }
        });
        final Function<BeanTree, String> fn = new Function<BeanTree, String>() {
            @Override
            public String apply(BeanTree p) {
                return p == null ? null : p.getBean().getText();
            }
        };
        Iterator<RowIndexKey<String[]>> keyIt = new MapperIterator<List<BeanTree>, RowIndexKey<String[]>>(iterator, new Function<List<BeanTree>, RowIndexKey<String[]>>() {
            @Override
            public RowIndexKey<String[]> apply(List<BeanTree> p) {
                return new RowIndexKey<String[]>(IteratorUtils.list2Array(p, fn));
            }
        });
        Set<RowIndexKey<String[]>> indexKeys = new HashSet<RowIndexKey<String[]>>();
        while (keyIt.hasNext()) {
            indexKeys.add(keyIt.next());
        }
        if (type == ExpanderType.N_LEVEL_EXPANDER) {
            int level = getLevel(dimensions, headerExpander);
            if (level == 0) {
                type = ExpanderType.LAZY_EXPANDER;
            } else if (level == dimensions.size() - 1) {
                type = ExpanderType.ALL_EXPANDER;
            } else {
                return new ExpanderImpl(level, type, indexKeys);
            }
        }
        return new ExpanderImpl(type, indexKeys);
    }

    private static int getLevel(List<FineDimension> dimensions, Map<String, Boolean> headerExpander) {
        int level = 0;
        for (int i = 0; i < dimensions.size(); i++) {
            String id = dimensions.get(i).getId();
            if (headerExpander.containsKey(id) && headerExpander.get(id)) {
                level = i + 1;
            }
        }
        return level;
    }

    private static class BeanTree implements Iterable<BeanTree> {

        private ExpanderBean root;

        public BeanTree(ExpanderBean root) {
            this.root = root;
        }

        public ExpanderBean getBean() {
            return root;
        }

        @Override
        public Iterator<BeanTree> iterator() {
            List<ExpanderBean> children = root.getChildren();
            children = children == null ? new ArrayList<ExpanderBean>(0) : children;
            return new MapperIterator<ExpanderBean, BeanTree>(children.iterator(), new Function<ExpanderBean, BeanTree>() {
                @Override
                public BeanTree apply(ExpanderBean p) {
                    return new BeanTree(p);
                }
            });
        }
    }
}
