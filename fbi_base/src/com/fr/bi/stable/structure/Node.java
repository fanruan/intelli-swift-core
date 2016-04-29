package com.fr.bi.stable.structure;

import com.fr.bi.common.inter.Release;

import java.util.Iterator;
import java.util.List;

/**
 * Created by GUY on 2015/4/27.
 */
public interface Node<T> extends Release {
    int getChildLength();

    <K extends Node> List<K> getChilds();

    Iterator getChildIterator();

    <K extends Node> K getFirstChild();

    <K extends Node> K getLastChild();

    <K extends Node> K getLeft();

    void setLeft(Node before);

    <K extends Node> K getRight();

    void setRight(Node behind);

    <K extends Node> K getParent();

    void setParent(Node parent);

    <K extends Node> K getChild(int index);

    <K extends Node> K getChild(Object value);

    void removeChild(int index);

    int getChildIndex(Object value);

    void addChild(Node child);

    void addChild(int index, Node child);

    T getValue();

    void setValue(T value);

    Object getData();

    void setData(Object data);
}