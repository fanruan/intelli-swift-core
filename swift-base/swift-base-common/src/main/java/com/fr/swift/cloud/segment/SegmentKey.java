package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.cube.io.Types.StoreType;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.source.SourceKey;

import java.util.Date;

/**
 * @author anchore
 * @date 2018/5/23
 */
public interface SegmentKey {

    SourceKey getTable();

    Integer getOrder();

    String getSegmentUri();

    StoreType getStoreType();

    SwiftDatabase getSwiftSchema();

    String getId();

    Date getCreateTime();

    Date getFinishTime();

    void markFinish(int rows);

    int getRows();

    SegmentSource segmentSource();
}