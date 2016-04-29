package com.fr.bi.stable.report;

import com.fr.bi.stable.structure.Node;

/**
 * Created by GUY on 2015/4/30.
 */
public interface SummaryNode<T> extends Node<T> {

    void setSummaryValue(T key, Object value);

    Number getSummaryValue(T key);
}