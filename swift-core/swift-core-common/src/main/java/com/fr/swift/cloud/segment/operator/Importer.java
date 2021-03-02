package com.fr.swift.cloud.segment.operator;

import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.segment.SegmentKey;

import java.util.List;

/**
 * This class created on 2018/12/24
 *
 * @author Lucifer
 * @description
 */
public interface Importer<R extends SwiftResultSet, T, S> extends RollBackable<T, S> {

    /**
     * 导入数据
     *
     * @param swiftResultSet 承载数据的resultSet
     * @throws Exception
     */
    void importResultSet(R swiftResultSet) throws Exception;

    /**
     * 获取字段
     *
     * @return
     */
    List<String> getFields();

    /**
     * 导入的Segment
     *
     * @return
     */
    List<SegmentKey> getImportSegments();
}
