package com.fr.swift.segment;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SourceKey;

import java.net.URI;

/**
 * @author anchore
 * @date 2018/5/23
 */
public interface SegmentKey {
    String getId();

    SourceKey getTable();

    URI getUri();

    Integer getOrder();

    StoreType getStoreType();

    SwiftDatabase getSwiftSchema();
}