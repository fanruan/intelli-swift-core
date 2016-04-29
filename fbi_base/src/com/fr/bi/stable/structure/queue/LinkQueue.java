package com.fr.bi.stable.structure.queue;

import com.fr.bi.stable.structure.Node;
import com.fr.bi.stable.structure.Root;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 链式队列, 还是用LinkedList吧
 * Created by GUY on 2015/4/22.
 */
public class LinkQueue<T> implements BIQueue<T> {

    protected Root<T> root = new Root<T>(null);//虚的根节点，第一个元素从root.getBehind()开始

    protected Root<T> tail = new Root<T>(null);//虚的尾节点，最后一个元素为tail.getBefore()

    protected int size;


    public LinkQueue() {
        clear();
    }

    @Override
    public Iterator<T> iterator() {
        List<T> list = new ArrayList<T>();
        Node<T> t = root.getRight();
        while (t != tail) {
            list.add(t.getValue());
            t = t.getRight();
        }
        return list.iterator();
    }

    @Override
    public boolean contains(T obj) {
        Node t = root.getRight();
        while (t != tail) {
            if (ComparatorUtils.equals(t.getValue(), obj)) {
                return true;
            }
            t = t.getRight();
        }
        return false;
    }

    @Override
    public void remove(T obj) {
        Node t = root.getRight();
        while (t != tail) {
            if (ComparatorUtils.equals(t.getValue(), obj)) {
                t.getLeft().setRight(t.getRight());
                t.getRight().setLeft(t.getLeft());
                t = null;
                return;
            }
            t = t.getRight();
        }
    }

    @Override
    public boolean add(T obj) {
        Root<T> node = new Root<T>(obj);
        tail.getLeft().setRight(node);
        node.setLeft(tail.getLeft());
        node.setRight(tail);
        tail.setLeft(node);
        size++;
        return true;
    }

    @Override
    public boolean isEmpty() {
        return root.getRight() == tail;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T peek() {
        return tail.getLeft().getValue();
    }

    @Override
    public T poll() {
        T data = peek();
        Node node = tail.getLeft();
        node.getLeft().setRight(tail);
        tail.setLeft(node.getLeft());
        node = null;
        return data;
    }

    @Override
    public void clear() {
        root.setRight(tail);
        tail.setLeft(root);
    }
}