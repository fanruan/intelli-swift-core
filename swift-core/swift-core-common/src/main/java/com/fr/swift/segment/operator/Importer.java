package com.fr.swift.segment.operator;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.SegmentKey;

import java.util.List;

/**
 * This class created on 2018/12/24
 *
 * @author Lucifer
 * @description
 */
public interface Importer<R extends SwiftResultSet> {

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
