package com.fr.swift.cloud.result;

import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.util.Closable;

import java.util.Iterator;

/**
 * @author Lyon
 * @date 2018/6/13
 */
public interface SwiftRowIterator extends Iterator<Row>, Closable {
    @Override
    void close();
}
