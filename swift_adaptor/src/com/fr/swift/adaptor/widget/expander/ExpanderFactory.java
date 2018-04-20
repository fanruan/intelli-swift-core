package com.fr.swift.adaptor.widget.expander;

import com.finebi.conf.internalimp.bean.dashboard.widget.expander.ExpanderBean;
import com.fr.swift.cal.info.Expander;
import com.fr.swift.cal.info.ExpanderType;
import com.fr.swift.query.group.by.paging.MapperIterator;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.result.node.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;
import com.fr.swift.util.function.Supplier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/4/19.
 */
public class ExpanderFactory {

    public static Expander create(boolean isOpenNode, final int dimensionSize, ExpanderBean bean) {
        ExpanderType type = isOpenNode ? ExpanderType.ALL_EXPANDER : ExpanderType.LAZY_EXPANDER;
        Iterator<List<String>> iterator = new Tree2RowIterator<String, BeanTree>(dimensionSize, new BeanTree(bean));
        Iterator<RowIndexKey<String>> iterator1 = new MapperIterator<List<String>, RowIndexKey<String>>(iterator, new Function<List<String>, RowIndexKey<String>>() {
            @Override
            public RowIndexKey<String> apply(List<String> p) {
                return new RowIndexKey<String>(p.toArray(new String[dimensionSize]));
            }
        });
        Set<RowIndexKey<String>> indexKeys = new HashSet<RowIndexKey<String>>();
        while (iterator1.hasNext()) {
            indexKeys.add(iterator1.next());
        }
        return new Expander(type, indexKeys);
    }

    private static class BeanTree implements Supplier<String>, Iterable<BeanTree> {

        private ExpanderBean root;

        public BeanTree(ExpanderBean root) {
            this.root = root;
        }

        @Override
        public String get() {
            return root.getText();
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
