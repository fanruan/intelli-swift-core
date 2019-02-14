package com.fr.swift.segment;

import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/5/23
 */
public interface SegmentKey extends ObjectConverter {
    SourceKey getTable();

    Integer getOrder();

    StoreType getStoreType();

    SwiftDatabase getSwiftSchema();

    String getId();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}