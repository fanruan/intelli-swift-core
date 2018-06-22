package com.fr.swift.segment.operator;

import com.fr.swift.source.Row;

/**
 * This class created on 2018/5/23
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Deprecated
public interface Recorder {

    void recordData(Row row, int segIndex);

    void end();
}
