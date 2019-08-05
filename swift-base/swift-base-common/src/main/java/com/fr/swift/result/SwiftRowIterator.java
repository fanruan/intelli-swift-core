package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.util.Closable;

import java.util.Iterator;

/**
 * @author Lyon
 * @date 2018/6/13
 */
public interface SwiftRowIterator extends Iterator<Row>, Closable {
    @Override
    void close();
}
