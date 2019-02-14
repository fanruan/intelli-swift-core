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
public interface Importer {

    void importData(SwiftResultSet swiftResultSet) throws Exception;

    List<String> getFields();

    List<SegmentKey> getImportSegments();
}
