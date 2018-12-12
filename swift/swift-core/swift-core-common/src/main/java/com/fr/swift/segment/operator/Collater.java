package com.fr.swift.segment.operator;

import com.fr.swift.result.SwiftResultSet;

/**
 * This class created on 2018/7/9
 *
 * @author Lucifer
 * @description 整理segment
 * @since Advanced FineBI 5.0
 */
public interface Collater {

    void collate(SwiftResultSet swiftResultSet) throws Exception;

}