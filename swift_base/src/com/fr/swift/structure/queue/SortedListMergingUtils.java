package com.fr.swift.structure.queue;

import com.fr.swift.query.aggregator.Combiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Lyon on 2018/3/30.
 */
public class SortedListMergingUtils {

    /**
     * 多个有序列表合并的通用方法，这边采用类库里面java.util.PriorityQueue的堆实现
     * java.util.PriorityQueue实现中维护堆性质的复杂度是O(log(n))，构造堆的复杂度(目测)是O(n*log(n))
     * 假设有m个待合并的列表，这些列表总共有n个元素，那么最终合并的效率是O(n*log(m) + m*log(m))
     * 考虑到待合并列表的个数不会太多，所以效率可以约等于O(n*log(m))
     *
     * @param lists 要合并的列表集合
     * @param <E> 列表元素
     * @return 多个列表列表的合并结果
     */
    public static <E> List<E> merge(Collection<? extends List<? extends E>> lists,
                                    Comparator<E> comparator, Combiner<E> combiner) {
        Iterator<E> iterator = new ElementIterator<E>(lists, comparator);
        List<E> mergedList = new ArrayList<E>();
        E lastE = null;
        while (iterator.hasNext()) {
            E item = iterator.next();
            if (lastE != null && comparator.compare(lastE, item) == 0) {
                // 合并相同元素
                combiner.combine(lastE, item);
                continue;
            }
            mergedList.add(item);
            lastE = item;
        }
        return mergedList;
    }

    public static <E> List<E> merge(List<Iterator<E>> iterators, Comparator<E> comparator, Combiner<E> combiner) {
        return null;
    }

    private static class ElementIterator<E> implements Iterator<E> {

        private PriorityQueue<IteratorComparator<E>> queue;
        private E next = null;

        public ElementIterator(Collection<? extends List<? extends E>> lists, Comparator<E> comparator) {
            // 这边构建优先队列没有传入队列元素的comparator
            // 那么PriorityQueue的实现里面是默认队列元素(IteratorComparator<E>)已经实现了Comparable接口的！
            this.queue = new PriorityQueue<IteratorComparator<E>>();
            init(lists, comparator);
        }

        private void init(Collection<? extends List<? extends E>> lists, Comparator<E> comparator) {
            // 构造queue
            for (List<? extends E> list : lists) {
                if (!list.isEmpty()) {
                    queue.add(new IteratorComparator<E>(list.iterator(), comparator));
                }
            }
            next = getNext();
        }

        private E getNext() {
            E n = null;
            if (!queue.isEmpty()) {
                IteratorComparator<E> nextIt = queue.remove();
                if (nextIt.hasNext()) {
                    n = nextIt.next();
                }
                if (nextIt.hasNext()) {
                    // 当前迭代器还有元素，要把迭代器仍回队列
                    queue.add(nextIt);
                }
            }
            return n;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            E old = next;
            next = getNext();
            return old;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 作为队列元素的迭代器，并且迭代器本身实现Comparable
     * @param <E>
     */
    private static class IteratorComparator<E> implements Iterator<E>, Comparable<IteratorComparator<E>> {

        private Comparator<E> comparator;
        private E peekElement;
        private Iterator<? extends E> it;

        public IteratorComparator(Iterator<? extends E> it, Comparator<E> comparator) {
            this.comparator = comparator;
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
            return peekElement == null ? 1 : comparator.compare(peekElement, o.peekElement);
        }
    }
}
