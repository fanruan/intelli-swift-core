package com.fr.swift.result;

import com.fr.swift.source.SwiftResultSet;

/**
 * Created by pony on 2018/4/19.
 */
public interface NodeResultSet<T extends SwiftNode> extends SwiftResultSet {
    SwiftNode<T> getNode();
}
