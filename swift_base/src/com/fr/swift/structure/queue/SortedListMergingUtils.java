package com.fr.swift.structure.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Lyon on 2018/3/30.
 */
public class SortedListMergingQueue {

    /**
     * 多个有序列表合并的通用方法，这边采用类库里面java.util.PriorityQueue的堆实现
     * 因为维护堆性质的复杂度是
     *
     * @param lists 要合并的列表集合
     * @param <E> 列表元素
     * @return 多个列表列表的合并结果
     */
    public static <E extends Comparable<? super E>> List<E> merge(Collection<? extends List<? extends E>> lists) {
        // E为列表元素，因为要比较，所以要实现Comparable
        // 这边构建优先队列没有传入队列元素的comparator
        // 那么PriorityQueue的实现里面是默认队列元素(IteratorComparator<E>)已经实现了Comparable接口的！
        PriorityQueue<IteratorComparator<E>> queue = new PriorityQueue<IteratorComparator<E>>();
        for (List<? extends E> list : lists) {
            if (!list.isEmpty()) {
                queue.add(new IteratorComparator<E>(list.iterator()));
            }
        }
        List<E> merged = new ArrayList<E>();
        while (!queue.isEmpty()) {
            IteratorComparator<E> next = queue.remove();
            merged.add(next.next());
            if (next.hasNext()) {
                queue.add(next);
            }
        }
        return merged;
    }

    private static class IteratorComparator<E extends Comparable<? super E>> implements Iterator<E>,
            Comparable<IteratorComparator<E>> {
        E peekElement;
        Iterator<? extends E> it;

        public IteratorComparator(Iterator<? extends E> it) {
            this.it = it;
            peekElement = it.hasNext() ? it.next() : null;
        }

        @Override
        public boolean hasNext() {
            return peekElement != null;
        }

        @Override
        public E next() {
            E ret = peekElement;
            peekElement = it.hasNext() ? it.next() : null;
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(IteratorComparator<E> o) {
            // 优先队列里面的元素(IteratorComparator<E>)之间的比较，只需比较有序列表的"栈顶"元素(E)
            return peekElement == null ? 1 : peekElement.compareTo(o.peekElement);
        }
    }
}
