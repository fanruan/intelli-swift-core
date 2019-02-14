package com.fr.swift.structure.queue;

import com.fr.swift.query.aggregator.Combiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 多个有序列表(迭代器)合并的通用方法，实现流式合并&组合，丝般顺滑
 * <p>
 * 这边采用类库里面java.util.PriorityQueue的堆实现
 * java.util.PriorityQueue实现中维护堆性质的复杂度是O(log(n))，构造堆的复杂度(目测)是O(n*log(n))
 * 假设有m个待合并的列表，这些列表总共有n个元素，那么最终合并的效率是O(n*log(m) + m*log(m))
 * 考虑到待合并列表的个数不会太多，所以效率可以约等于O(n*log(m))
 * <p>
 * Created by Lyon on 2018/3/30.
 */
public class SortedListMergingUtils {

    /**
     * 多个有序列表合并
     *
     * @param lists      多个有序列表
     * @param comparator 元素比较器
     * @param combiner   元素组合器
     * @param <E>        元素
     * @return 返回合并后的元素列表
     */
    public static <E> List<E> merge(Collection<? extends List<E>> lists,
                                    Comparator<E> comparator, Combiner<E> combiner) {
        List<Iterator<E>> iterators = new ArrayList<Iterator<E>>();
        for (List<E> list : lists) {
            if (!list.isEmpty()) {
                iterators.add(list.iterator());
            }
        }
        return merge(iterators, comparator, combiner);
    }

    /**
     * 多个有序迭代器合并
     *
     * @param iterators  多个有序列表
     * @param comparator 元素比较器
     * @param combiner   元素合并器
     * @param <E>        元素
     * @return 返回合并后的元素列表
     */
    public static <E> List<E> merge(List<Iterator<E>> iterators, Comparator<E> comparator, Combiner<E> combiner) {
        Iterator<E> iterator = mergeIterator(iterators, comparator, combiner);
        List<E> mergedList = new ArrayList<E>();
        while (iterator.hasNext()) {
            mergedList.add(iterator.next());
        }
        return mergedList;
    }

    /**
     * 流式合并&组合，尤其适用于有序且内存开销比较大的迭代器。比如group by索引迭代器，明显不能把所有索引算完放列表里面
     *
     * @param iterators  多个有序迭代器
     * @param comparator 元素比较器
     * @param combiner   元素组合器
     * @param <E>        元素
     * @return 返回丝般顺滑的迭代器
     */
    public static <E> Iterator<E> mergeIterator(List<Iterator<E>> iterators,
                                                Comparator<E> comparator, Combiner<E> combiner) {
        Iterator<E> iterator = new ElementIterator<E>(iterators, comparator);
        return new StreamCombinerIterator<E>(iterator, comparator, combiner);
    }

    /**
     * 只进行流式合并，不组合相同元素
     *
     * @param iterators  多个有序迭代器
     * @param comparator 元素比较器
     * @param <E>        元素
     * @return 返回丝般顺滑的迭代器
     */
    public static <E> Iterator<E> mergeIterator(List<Iterator<E>> iterators, Comparator<E> comparator) {
        return new ElementIterator<E>(iterators, comparator);
    }

    /**
     * 只进行流式合并，不组合相同元素
     *
     * @param lists      多个有序迭代器
     * @param comparator 元素比较器
     * @param <E>        元素
     * @return 返回丝般顺滑的迭代器
     */
    public static <E> Iterator<E> merge(List<List<E>> lists, Comparator<E> comparator) {
        List<Iterator<E>> iterators = new ArrayList<Iterator<E>>();
        for (List<E> list : lists) {
            if (!list.isEmpty()) {
                iterators.add(list.iterator());
            }
        }
        return mergeIterator(iterators, comparator);
    }

    private static class StreamCombinerIterator<E> implements Iterator<E> {

        private Iterator<E> iterator;
        private Comparator<E> comparator;
        private Combiner<E> combiner;
        private E next;

        public StreamCombinerIterator(Iterator<E> iterator, Comparator<E> comparator, Combiner<E> combiner) {
            this.iterator = iterator;
            this.comparator = comparator;
            this.combiner = combiner;
            next = iterator.hasNext() ? iterator.next() : null;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            E e4Return = next;
            next = null;
            while (iterator.hasNext()) {
                E item = iterator.next();
                // 判断item是否和lastE相同，相同则要合并并e4Return继续检查下一个
                if (comparator.compare(e4Return, item) == 0) {
                    combiner.combine(e4Return, item);
                } else {
                    next = item;
                    break;
                }
            }
            return e4Return;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class ElementIterator<E> implements Iterator<E> {

        private PriorityQueue<IteratorComparator<E>> queue;
        private E next = null;

        public ElementIterator(List<Iterator<E>> iterators, Comparator<E> comparator) {
            // 这边构建优先队列没有传入队列元素的comparator
            // 那么PriorityQueue的实现里面是默认队列元素(IteratorComparator<E>)已经实现了Comparable接口的！
            this.queue = new PriorityQueue<IteratorComparator<E>>();
            init(iterators, comparator);
        }

        private void init(List<Iterator<E>> iterators, Comparator<E> comparator) {
            // 构造queue
            for (Iterator<E> iterator : iterators) {
                queue.add(new IteratorComparator<E>(iterator, comparator));
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
                    // 当前迭代器还有元素，要把迭代器扔回队列
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
     *
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
            return comparator.compare(peekElement, o.peekElement);
        }
    }
}
