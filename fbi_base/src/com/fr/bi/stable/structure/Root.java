package com.fr.bi.stable.structure;

import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by GUY on 2015/4/22.
 */
public class Root<T> implements Node<T> {

    protected T value;//节点key值
    protected Object data;//储存数据
    protected Node parent;
    protected Node left;//前一个
    protected Node right;//后一个

    protected List<Node> childs;

    public Root(T value) {
        this.value = value;
        childs = new ArrayList<Node>();
    }

    public Root(T value, Object data) {
        this.value = value;
        this.data = data;
    }

    @Override
    public int getChildLength() {
        return childs.size();
    }

    @Override
    public <K extends Node> List<K> getChilds() {
        return (List<K>) childs;
    }

    @Override
    public Iterator getChildIterator() {
        return childs.iterator();
    }

    @Override
    public Node<T> getFirstChild() {
        if (childs.isEmpty()) {
            return null;
        }
        return childs.get(0);
    }

    @Override
    public Node<T> getLastChild() {
        if (childs.isEmpty()) {
            return null;
        }
        return childs.get(childs.size() - 1);
    }

    @Override
    public Node<T> getLeft() {
        return left;
    }

    @Override
    public void setLeft(Node before) {
        this.left = before;
    }

    @Override
    public Node<T> getRight() {
        return right;
    }

    @Override
    public void setRight(Node behind) {
        this.right = behind;
    }

    @Override
    public Node<T> getParent() {
        return parent;
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public Node<T> getChild(int index) {
        if (index < 0 || index >= getChildLength()) {
            return null;
        }
        return childs.get(index);
    }

    @Override
    public <K extends Node> K getChild(Object value) {
        Iterator iterator = childs.iterator();
        while (iterator.hasNext()) {
            K node = (K) iterator.next();
            if (ComparatorUtils.equals(node.getValue(), value)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void removeChild(int index) {
        Node cur = getChild(index);
        Node before = getChild(index - 1);
        Node behind = getChild(index + 1);
        if (before != null) {
            before.setRight(behind);
        }
        if (behind != null) {
            behind.setLeft(before);
        }
        if (cur != null) {
            childs.remove(index);
            cur = null;
        }
    }

    @Override
    public int getChildIndex(Object value) {
        Iterator iterator = childs.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Root node = (Root) iterator.next();
            if (ComparatorUtils.equals(node.value, value)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public void addChild(Node child) {
        child.setParent(this);
        getLastChild().setRight(child);
        child.setLeft(getLastChild());
        childs.add(child);
    }

    @Override
    public void addChild(int index, Node child) {
        if (index < 0 || index > getChildLength()) {
            return;
        }
        Node before = getChild(index - 1);
        Node behind = getChild(index + 1);
        if (before != null) {
            before.setRight(child);
            child.setLeft(before);
        }
        if (behind != null) {
            child.setRight(behind);
            behind.setLeft(child);
        }
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T data) {
        this.value = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Root)) {
            return false;
        }

        Root treeNode = (Root) o;

        if (!ComparatorUtils.equals(value, treeNode.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public void releaseResource() {
        childs.clear();
        parent = null;
        left = null;
        right = null;
    }
}