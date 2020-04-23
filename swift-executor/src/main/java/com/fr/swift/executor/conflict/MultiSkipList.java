
package com.fr.swift.executor.conflict;

import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 非线程安全的可重复有序列表，底层实现为限制层数的跳表
 *
 * @param <E>
 */
public class MultiSkipList<E> {

    // 最高层数
    private static final int MAX_LEVEL = 32;
    protected Comparator<E> comparator;
    // 分别是 头、尾、底层头
    protected SkipListNode<E> head, tail, bottomHead;
    // 总节点数
    protected transient int size = 0;
    // 总层数
    protected transient int levels = 0;
    protected Random random;

    public MultiSkipList(Comparator<E> comparator) {
        this.comparator = comparator;
        random = new Random();
        clear();
    }

    /**
     * 插队后，不上升，直接插入到最底层的某个位置
     * 需要限制插队的频次
     *
     * @param element
     * @param position
     * @return
     */
    public boolean add(E element, int position) {
        if (size < position) {
            return add(element);
        } else {
            SkipListNode<E> newNode = new SkipListNode<>(element);
            SkipListNode curNode = bottomHead.right;
            while (curNode.right != null && position > 0) {
                curNode = curNode.right;
                position--;
            }
            if (curNode.left != null) {
                SkipListNode leftNode = curNode.left;
                horizontalLink(leftNode, newNode);
                horizontalLink(newNode, curNode);
                size++;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean add(E element) {
        // 拿到的节点一定是最底层的节点
        SkipListNode<E> target = find(element);
        // 无论是不是相同值的节点，都可以直接插入到 target 后面
        SkipListNode<E> newNode = new SkipListNode<>(element);
        backLink(target, newNode);
        // 通过抛硬币决定是否向上提升
        int curLevel = 0;
        while (random.nextBoolean()) {
            // 如果超出了现有高度, 但还在最高允许
            if (curLevel >= levels && curLevel < MAX_LEVEL) {
                levels++;
                SkipListNode<E> topHead = new SkipListNode<>(null);
                SkipListNode<E> topTail = new SkipListNode<>(null);
                horizontalLink(topHead, topTail);
                verticalLink(topHead, head);
                verticalLink(topTail, tail);
                // 保证 head tail 永远是最高层的
                head = topHead;
                tail = topTail;
            }
            // 往左走找到第一个有上指针的节点
            while (target.left != null && target.up == null) {
                target = target.left;
            }
            target = target.up;
            SkipListNode<E> upNewNode = new SkipListNode<>(element);
            backLink(target, upNewNode);
            verticalLink(upNewNode, newNode);
            newNode = upNewNode;
            curLevel++;
        }
        size++;
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {
        if (c == null || c.size() == 0) {
            return false;
        }
        for (E e : c) {
            add(e);
        }
        return true;
    }

    public Iterator<E> iterator() {
        return new SkipListItr<>(bottomHead);
    }

    public int size() {
        return size;
    }

    /**
     * 在该节点出现过的所有层都删除掉
     *
     * @param target
     */
    private void removeNode(SkipListNode<E> target) {
        List<SkipListNode<E>> nodeList = new ArrayList<>();
        while (target != null) {
            nodeList.add(target);
            target = target.up;
        }
        for (SkipListNode<E> node : nodeList) {
            horizontalLink(node.left, node.right);
        }
    }

    /**
     * equal() 判断值是否相等
     *
     * @param element
     * @return
     */
    public boolean remove(E element) {
        if (element == null) {
            return false;
        }
        // 拿到的节点是 comparator 值相等的第一个节点，需要往后遍历到 equal 的节点进行删除
        Pair<Boolean, SkipListNode<E>> target = search(element);
        if (!target.getKey()) {
            return false;
        }
        while (target.getValue().right != null && !element.equals(target.getValue().element)) {
            target.setValue(target.getValue().right);
        }
        if (element.equals(target.getValue().element)) {
            // 删除这个节点
            removeNode(target.getValue());
            size--;
            return true;
        }
        return false;
    }

    /**
     * 非线程安全toList
     *
     * @return
     */
    public List<E> toList() {
        List<E> list = new ArrayList<>();
        SkipListNode curNode = bottomHead;
        while (curNode != null) {
            if (curNode.element != null) {
                list.add((E) curNode.element);
            }
            curNode = curNode.right;
        }
        return list;
    }

    public void clear() {
        head = new SkipListNode<>(null);
        tail = new SkipListNode<>(null);
        horizontalLink(head, tail);
        levels = 0;
        size = 0;
        bottomHead = head;
    }

    /**
     * 水平双向连接
     */
    private void horizontalLink(SkipListNode<E> node1, SkipListNode<E> node2) {
        node1.right = node2;
        node2.left = node1;
    }

    /**
     * 垂直双向连接
     */
    private void verticalLink(SkipListNode<E> node1, SkipListNode<E> node2) {
        node1.down = node2;
        node2.up = node1;
    }

    /**
     * node1后面插入node2
     */
    private void backLink(SkipListNode<E> node1, SkipListNode<E> node2) {
        node2.left = node1;
        node2.right = node1.right;
        node1.right.left = node2;
        node1.right = node2;
    }

    /**
     * 找到最后一个比 element 小的节点
     * 使用 comparator 判断
     *
     * @param element
     * @return
     */
    private SkipListNode<E> find(E element) {
        if (element == null) {
            return null;
        }
        SkipListNode<E> cur = head;
        while (true) {
            // 只要右节点的值 >= element，就继续往右走（值越大越靠前）
            // 保证离开循环的 cur 一定 >= element, 这样就可以直接插入 cur 后面了
            while (cur.right.right != null && comparator.compare(cur.right.element, element) >= 0) {
                cur = cur.right;
            }
            // 所有的节点都是在最底层找到的
            if (cur.down != null) {
                cur = cur.down;
            } else {
                break;
            }
        }
        return cur;
    }

    /**
     * 查找 comparator 相同值的第一个节点，拿到后可以往后遍历删除
     *
     * @param element
     * @return
     */
    private Pair<Boolean, SkipListNode<E>> search(E element) {
        if (element == null) {
            return null;
        }
        SkipListNode<E> cur = head;
        while (true) {
            // 只要右节点的值还比 element 大，就继续往右走（值越大越靠前）
            while (cur.right.right != null && comparator.compare(cur.right.element, element) > 0) {
                cur = cur.right;
            }
            // 所有的节点都是在最底层找到的
            if (cur.down != null) {
                cur = cur.down;
            } else {
                break;
            }
        }
        if (cur.element == null) {
            // 说明这个是头节点
            cur = cur.right;
            if (cur.element == null) {
                // 说明只剩下了头尾
                return new Pair<>(false, null);
            }
        }
        while (comparator.compare(cur.element, element) > 0) {
            cur = cur.right;
            // 已经到尾节点了, 没找到
            if (cur.element == null) {
                return new Pair<>(false, null);
            }
        }
        return new Pair<>(true, cur);
    }

    private static class SkipListNode<E> {
        public E element;
        // 上下左右四个指针
        public SkipListNode<E> up = null;
        public SkipListNode<E> down = null;
        public SkipListNode<E> left = null;
        public SkipListNode<E> right = null;

        public SkipListNode(E element) {
            this.element = element;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (!(o instanceof SkipListNode<?>)) {
                return false;
            }
            SkipListNode<E> ent;
            try {
                ent = (SkipListNode<E>) o; // 检测类型
            } catch (ClassCastException ex) {
                return false;
            }
            return (ent.element.equals(element));
        }

//        @Override
//        public String toString() {
//            return element.toString();
//        }
    }

    private static class SkipListItr<E> implements Iterator<E> {
        SkipListNode<E> last;

        public SkipListItr(SkipListNode<E> node) {
            last = node;
        }

        @Override
        public boolean hasNext() {
            return last.right.element != null;
        }

        @Override
        public E next() {
            last = last.right;
            return last.element;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 打印当前skiplist结构，非线程安全
     */
    public void print() {
        SkipListNode[][] arrays = new SkipListNode[levels + 1][size + 2];
        SkipListNode curNode = bottomHead;
        arrays[levels][0] = curNode;
        for (int i = 0; i < size; i++) {
            curNode = curNode.right;
            arrays[levels][i + 1] = curNode;
        }
        arrays[levels][size + 1] = curNode.right;
        for (int i = levels; i >= 0; i--) {
            for (int j = 0; j < size + 2; j++) {
                if (arrays[i][j] != null) {
                    if (arrays[i][j].up != null) {
                        arrays[i - 1][j] = arrays[i][j].up;
                    }
                }

            }
        }
        for (SkipListNode[] array : arrays) {
            for (SkipListNode node : array) {
                if (node == null) {
                    System.out.print(String.format("%-6s", "") + "--");
                } else if (node.element != null) {
                    System.out.print(String.format("%-6s", node.element) + "--");
                } else {
                    System.out.print(String.format("%-12s", node.hashCode()) + "--");
                }
            }
            System.out.println();
        }
        System.out.println("----------");
    }
}
