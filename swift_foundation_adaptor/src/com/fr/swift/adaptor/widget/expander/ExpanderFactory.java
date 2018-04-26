package com.fr.swift.adaptor.widget.expander;

import com.finebi.conf.internalimp.bean.dashboard.widget.expander.ExpanderBean;
import com.fr.swift.query.adapter.dimension.Expander;
import com.fr.swift.query.adapter.dimension.ExpanderType;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/4/19.
 */
public class ExpanderFactory {

    public static Expander create(boolean isOpenNode, final int dimensionSize, List<ExpanderBean> beanList) {
        ExpanderType type = isOpenNode ? ExpanderType.ALL_EXPANDER : ExpanderType.LAZY_EXPANDER;
        Iterator<List<BeanTree>> iterator = new Tree2RowIterator<BeanTree>(dimensionSize, new MapperIterator<ExpanderBean, BeanTree>(beanList.iterator(), new Function<ExpanderBean, BeanTree>() {
            @Override
            public BeanTree apply(ExpanderBean p) {
                return new BeanTree(p);
            }
        }));
        final Function<BeanTree, String> fn = new Function<BeanTree, String>() {
            @Override
            public String apply(BeanTree p) {
                return p == null ? null : p.getBean().getText();
            }
        };
        Iterator<RowIndexKey<String>> keyIt = new MapperIterator<List<BeanTree>, RowIndexKey<String>>(iterator, new Function<List<BeanTree>, RowIndexKey<String>>() {
            @Override
            public RowIndexKey<String> apply(List<BeanTree> p) {
                return new RowIndexKey<String>(IteratorUtils.list2Array(p, fn));
            }
        });
        Set<RowIndexKey<String>> indexKeys = new HashSet<RowIndexKey<String>>();
        while (keyIt.hasNext()) {
            indexKeys.add(keyIt.next());
        }
        return new Expander(type, indexKeys);
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
